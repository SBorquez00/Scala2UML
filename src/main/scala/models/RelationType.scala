package scalauml
package models

sealed trait RelationType {
  def value: String
}

object RelationType{
  case object Association extends RelationType {
    val value = "association"
  }
  case object Inheritance extends RelationType {
    val value = "inheritance"
  }

  case object Implementation extends RelationType {
    val value = "implementation"
  }

  case object Aggregation extends RelationType {
    val value = "aggregation"
  }
}