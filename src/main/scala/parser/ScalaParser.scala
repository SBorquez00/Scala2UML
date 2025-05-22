package scalauml
package parser

import scalauml.models.ClassType

import scala.meta._
import scala.io.{Source => FileSource}
class ScalaParser {

  val fileTest = "data/scala-de-prueba.txt";

  def processOneClassProgram(): Unit = {
    val source = FileSource.fromFile(fileTest)
    val program = source.mkString
    source.close()

    val tree = program.parse[Source].get

    val classObject = getClasses(tree)(0)

    val name = classObject.name
    val classType = getClassType(classObject)

    val methods = getMethods(tree)

    val nameMethods = methods.map(m => m.name)

    val paramsMethods = methods.map(m => {
      m.paramClauseGroups.head.paramClauses.head.values.map(p => p.decltpe.get)
    })

    val returnTypeMethods = methods.map(m =>{
      m.decltpe.get
    })

    println(nameMethods)

  }

  private def getClassType(classDef: Defn.Class): ClassType = {
    val mods = classDef.mods

    if (mods.length == 0) {
      return ClassType.ConcreteClass
    }
    else {
      val isAbstract = mods.exists {
        case Mod.Abstract() => true
        case _ => false
      }
      return if (isAbstract) ClassType.Abstract else ClassType.ConcreteClass
    }
  }

  private def getClasses(tree: Tree): List[Defn.Class] = {
    val classes = tree.collect {
      case cls: Defn.Class => cls
    }
    return classes
  }

  private def getMethods(tree: Tree): List[Defn.Def] = {
    val methods = tree.collect {
      case met: Defn.Def => met
    }
    return methods
  }

}
