package scalauml
package models

case class ClassUML(
                   name: String,
                   classType: ClassType,
                   fields: List[FieldUML],
                   methods: List[MethodUML]
                   )
