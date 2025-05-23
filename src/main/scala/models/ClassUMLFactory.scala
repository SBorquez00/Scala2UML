package scalauml
package models

object ClassUMLFactory {
  private var counter = 0

  def create(name: String,
             classType: ClassType,
             fields: List[FieldUML],
             methods: List[MethodUML]): ClassUML = {
    val id = counter + 1
    val spacing = 50
    val x = id * spacing
    val y = id * spacing
    counter += 1
    ClassUML(name, classType, fields, methods, x, y, id)
  }
}
