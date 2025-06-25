package scalauml
package models

object ClassUMLFactory {
  private var counter = 1

  def create(name: String,
             classType: ClassType,
             fields: List[FieldUML],
             methods: List[MethodUML],
             parentClassesNames: List[String]): ClassUML = {
    val id = counter
    val spacing = 50
    val x = id * spacing
    val y = id * spacing
    counter += 1
    ClassUML(name, classType, fields, methods, parentClassesNames, x, y, id)
  }
}
