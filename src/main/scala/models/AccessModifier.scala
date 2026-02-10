package scalauml
package models

sealed trait AccessModifier {
  def value: String
}

object AccessModifier {
  case object Public extends AccessModifier {
    val value = "public"
  }
  case object Protected extends AccessModifier {
    val value = "protected"
  }
  case object Private extends AccessModifier {
    val value = "private"
  }
}