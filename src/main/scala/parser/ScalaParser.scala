package scalauml
package parser

import scalauml.models.{AccessModifier, ClassType, ClassUML, FieldUML, MethodUML, UMLJsonDocument}
import scalauml.serializers.UMLJsonDocumentSerializer

import io.circe.{Encoder, Json}
import io.circe.generic.semiauto.deriveEncoder

import io.circe.syntax.EncoderOps
import scalauml.serializers.UMLJsonDocumentSerializer.UMLJsonDocumentEncoder

import scala.meta.{io, _}
import scala.io.{Source => FileSource}

class ScalaParser {

  private val fileTest = "data/scala-de-prueba.txt";

  def processOneClassProgram(): Unit = {
    val source = FileSource.fromFile(fileTest)
    val program = source.mkString
    source.close()

    val tree = program.parse[Source].get

    val classObject = getClasses(tree).head

    val name = classObject.name.toString()
    val classType = getClassType(classObject)

    val methodsUML = getMethods(tree)

    val fieldsUML = getFields(tree)

    val classRes = ClassUML(name, classType, fieldsUML, methodsUML)
    val jsonDocument = UMLJsonDocument(List(classRes))
    val json = jsonDocument.asJson

    print(json.spaces2)

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
    val methods = tree.collect {
      case met: Defn.Def => met
    }

    val methodsUML = methods.map(m => {
      MethodUML(
        name = m.name.toString(),
        domType = m.paramClauseGroups.head.paramClauses.head.values.map(p => p.decltpe.get.toString()),
        codomType = m.decltpe.get.toString(),
        visibility = getAccessModifierFromMods(m.mods),
        isAbstract = m.mods.exists {
          case Mod.Abstract() => true
          case _ => false
        }
      )
    })
    methodsUML
  }

  private def getFields(tree: Tree): List[FieldUML] = {
    val fieldsDeclared = tree.collect {
      case fieldVar: Decl.Var => FieldUML(
        fieldVar.pats.head.text,
        fieldVar.decltpe.toString(),
        getAccessModifierFromMods(fieldVar.mods)
      )
      case fieldVal: Decl.Val => FieldUML(
        fieldVal.pats.head.text,
        fieldVal.decltpe.toString(),
        getAccessModifierFromMods(fieldVal.mods)
      )
    }

    val ctorFields = tree.collect {
      case cto: Ctor.Primary => cto.paramClauses.head.values
    }.head.map(cf => {
      FieldUML(
        name = cf.name.toString(),
        dataType = cf.decltpe.get.toString(),
        visibility = getAccessModifierFromMods(
          cf.mods
        )
      )
    })

    fieldsDeclared ++ ctorFields
  }

}
