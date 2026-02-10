package scalauml
package models

case class UMLJsonDocument(
                          nodes: List[ClassUML],
                          edges: List[RelationUML] = Nil
                          )
