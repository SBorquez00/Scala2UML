package scalauml
package models

case class RelationUML (
                  source: Int,
                  target: Int,
                  sourceHandle: String,
                  targetHandle: String,
                  relationType: RelationType
                  )

