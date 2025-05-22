package scalauml
package models

case class MethodUML(
  name: String,
  domType: List[String],
  codomType: String,
  visibility: AccessModifier,
  isAbstract: Boolean
)
