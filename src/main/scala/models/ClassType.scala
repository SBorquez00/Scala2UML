package scalauml
package models

sealed trait ClassType {
  def value: String
}

object ClassType{
  case object Abstract extends ClassType {
    val value = "abstractClass"
  }
  case object Trait extends ClassType {
    val value = "trait"
  }

  case object ConcreteClass extends ClassType {
    val value = "concreteClass"
  }
}