package scalauml
package models

case class ClassUML(
                   name: String,
                   classType: ClassType,
                   fields: List[FieldUML],
                   methods: List[MethodUML],
                   xCord: Double = 0,
                   yCord: Double = 0,
                   id: Int = 1
                   )
