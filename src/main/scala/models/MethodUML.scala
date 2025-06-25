package scalauml
package models

case class MethodUML(
  name: String,
  domType: List[String],
  domBaseType: List[String],
  codomType: String,
  codomBaseType: String,
  visibility: AccessModifier,
  isAbstract: Boolean
)
