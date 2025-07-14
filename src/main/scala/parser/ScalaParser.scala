package scalauml
package parser

import scalauml.models.{AccessModifier, ClassType, ClassUML, ClassUMLFactory, FieldUML, MethodUML, RelationUML, UMLJsonDocument}
import io.circe.syntax.EncoderOps
import io.circe.Json
import scalauml.serializers.UMLJsonDocumentSerializer.UMLJsonDocumentEncoder
import scalauml.analyzer.RelationDetector
import scalauml.utils.FileUtils

import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}
import scala.meta.{io, _}
import scala.io.{Source => FileSource}

class ScalaParser {

  private val fileTest = new File("data/scala-de-prueba.txt");

  def processASetOfFiles(): Json = {
    val carpeta = new File("src/main/scala")

    var res: List[ClassUML] = List()

    if(carpeta.exists() && carpeta.isDirectory){
      val archivos = FileUtils.listAllFiles(carpeta)

      res = archivos.flatMap { file =>
        classesFromFile(file)
      }
    }
    val relations = new RelationDetector();
    val edges = relations.generateRelations(res);

    val umlDocument = UMLJsonDocument(res, edges)


    val json = umlDocument.asJson
    val path = Paths.get("output.json")
    Files.write(path, json.spaces2.getBytes(StandardCharsets.UTF_8))
    json

  }

  def classesFromFile(uploadFile: File = fileTest): List[ClassUML] = {
    val source = FileSource.fromFile(uploadFile)
    val program = source.mkString
    source.close()

    val tree = program.parse[Source].get
    val classObject = getClasses(tree)

    val classList = classObject.map(c => {
      processOneClass(c)
    })

    val traitObject = getTraits(tree)
    val traitList = traitObject.map(t => {
      processOneTrait(t)
    })

    classList ++ traitList
  }


    def processOneFileOfClasses(uploadFile: File = fileTest): Json = {
    val source = FileSource.fromFile(uploadFile)
    val program = source.mkString
    source.close()

    val tree = program.parse[Source].get
    val classObject = getClasses(tree)

    val classList = classObject.map(c => {
       processOneClass(c)
    })

    val traitObject = getTraits(tree)
    val traitList = traitObject.map(t => {
      processOneTrait(t)
    })

    //classList ++ traitList

    val relations = new RelationDetector();
    val edges = relations.generateRelations(classList ++ traitList);

    val umlDocument = UMLJsonDocument(classList ++ traitList, edges)


    val json = umlDocument.asJson
    val path = Paths.get("output.json")
    Files.write(path, json.spaces2.getBytes(StandardCharsets.UTF_8))
    return json
  }

  def processOneClass(classObject: Defn.Class): ClassUML = {

    val name = classObject.name.toString()

    val classType = getClassType(classObject)

    val methodsUML = getMethods(classObject)
    val fieldsUML = getFields(classObject)

    val parentClasses = getParentClasses(classObject)

    val classRes = ClassUMLFactory.create(name, classType, fieldsUML, methodsUML, parentClasses)
    classRes
  }

  def processOneTrait(traitObject: Defn.Trait): ClassUML = {

    val name = traitObject.name.toString()
    val classType = ClassType.Trait

    val methodsUML = getMethods(traitObject)
    val fieldsUML = getFields(traitObject)

    val parentClasses = getParentClasses(traitObject)

    val classRes = ClassUMLFactory.create(name, classType, fieldsUML, methodsUML, parentClasses)
    classRes
  }

  private def getClassType(classDef: Defn.Class): ClassType = {
    val mods = classDef.mods

    if (mods.isEmpty) {
      ClassType.ConcreteClass
    }
    else {
      val isAbstract = mods.exists {
        case Mod.Abstract() => true
        case _ => false
      }
      if (isAbstract) ClassType.Abstract else ClassType.ConcreteClass
    }
  }

  private def getClasses(tree: Tree): List[Defn.Class] = {
    val classes = tree.collect {
      case cls: Defn.Class => cls
    }
    classes
  }

  private def getTraits(tree: Tree): List[Defn.Trait] = {
    val traits = tree.collect {
      case cls: Defn.Trait => cls
    }
    traits
  }

  private def getAccessModifierFromMods(mods: List[Mod]): AccessModifier = {
    val mod = mods match {
      case Nil => AccessModifier.Public
      case Mod.Protected(_) :: _ => AccessModifier.Protected
      case Mod.Private(_) :: _ => AccessModifier.Private
      case _ => AccessModifier.Public
    }
    mod
  }

  private def getMethods(tree: Tree): List[MethodUML] = {
    val methodsDef = tree.collect {
      case met: Defn.Def => met
    }

    val methodsDecl = tree.collect {
      case met: Decl.Def => met
    }

    val methodsUML = methodsDef.map(m => {
      MethodUML(
        name = m.name.toString(),
        domType = m.paramClauseGroups.head.paramClauses.head.values.map(p => p.decltpe.get.toString()),
        domBaseType = m.paramClauseGroups.head.paramClauses.head.values.map(p => getBaseType(p.decltpe.get)),
        codomType = m.decltpe.getOrElse(t"Any").toString(),
        codomBaseType = getBaseType(m.decltpe.getOrElse(t"Any")),
        visibility = getAccessModifierFromMods(m.mods),
        isAbstract = false
      )
    })

    val methodsDeclUml = methodsDecl.map(m => {
      MethodUML(
        name = m.name.toString(),
        domType = if (m.paramClauseGroups.nonEmpty && m.paramClauseGroups.head.paramClauses.nonEmpty)
                    m.paramClauseGroups.head.paramClauses.head.values.map(p => p.decltpe.map(_.toString()).getOrElse("Any"))
                  else
                    List(),
        domBaseType = if (m.paramClauseGroups.nonEmpty && m.paramClauseGroups.head.paramClauses.nonEmpty)
                        m.paramClauseGroups.head.paramClauses.head.values.map(p => getBaseType(p.decltpe.get))
                      else
                        List(),
        codomType = m.decltpe.toString(),
        codomBaseType = getBaseType(m.decltpe),
        visibility = getAccessModifierFromMods(m.mods),
        isAbstract = true
      )
    })

    methodsUML ++ methodsDeclUml
  }

  private def getFields(tree: Tree): List[FieldUML] = {

    val fieldsDeclared = tree match{
      case cls: Defn.Class => processField(cls.templ)
      case tr: Defn.Trait => processField(tr.templ)
      case _ => Nil
    }

    if(tree.isInstanceOf[Defn.Trait]){
      return fieldsDeclared
    }

    val ctorFields: List[FieldUML] = tree.collect {
      case cto: Ctor.Primary => cto.paramClauses.headOption.toList.flatMap(_.values)
    }.headOption
      .getOrElse(Nil)
      .map(cf => FieldUML(
        name = cf.name.toString(),
        dataType = cf.decltpe.map(_.toString()).getOrElse("Any"),
        baseType = cf.decltpe.map(getBaseType(_)).getOrElse("Any"),
        visibility = getAccessModifierFromMods(cf.mods)
      ))


    fieldsDeclared ++ ctorFields
  }

  def processField(t: Template): List[FieldUML] = {
    t.body.children
      .collect {
        case fieldVar: Decl.Var => FieldUML(
          fieldVar.pats.head.text,
          fieldVar.decltpe.toString(),
          getBaseType(fieldVar.decltpe),
          getAccessModifierFromMods(fieldVar.mods)
        )
        case fieldVal: Decl.Val => FieldUML(
          fieldVal.pats.head.text,
          fieldVal.decltpe.toString(),
          getBaseType(fieldVal.decltpe),
          getAccessModifierFromMods(fieldVal.mods)
        )
        case fieldVar: Defn.Var => FieldUML(
          fieldVar.pats.head.text,
          fieldVar.decltpe.getOrElse(t"Any").toString(),
          getBaseType(fieldVar.decltpe.getOrElse(t"Any")),
          getAccessModifierFromMods(fieldVar.mods)
        )
        case fieldVal: Defn.Val => FieldUML(
          fieldVal.pats.head.text,
          fieldVal.decltpe.getOrElse(t"Any").toString(),
          getBaseType(fieldVal.decltpe.getOrElse(t"Any")),
          getAccessModifierFromMods(fieldVal.mods)
        )
      }
  }

  def getParentClasses(tree: Tree): List[String] = {

    val parent = tree match{
      case c: Defn.Class
      => c.templ.children.collect {
            case extended: Init => extended.tpe.toString()
          }
      case t: Defn.Trait
      => t.templ.children.collect {
            case extended: Init => extended.tpe.toString()
          }
    }

    parent
  }

  def getBaseType(param: Type): String = {
    param match {
      case apply: Type.Apply => {
        if(apply.tpe.toString() == "Map"){
          getBaseType(apply.argClause.values(1))
        }
        else{
          getBaseType(apply.argClause.head)
        }
      }
      case _ => param.toString()
    }
  }

}
