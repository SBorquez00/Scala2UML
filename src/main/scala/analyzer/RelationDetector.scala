package scalauml
package analyzer

import models.{ClassUML, Handle, RelationType, RelationUML}

class RelationDetector
{

  def generateInheritanceRelations(nodes: List[ClassUML]): List[RelationUML] = {

    val mapaNodos = createMapFromNodesArray(nodes);

    nodes.flatMap { nodo =>
      nodo.parentClassesNames.flatMap { nombreDestino =>
        mapaNodos.get(nombreDestino).map { destino =>
          newRelation(nodo.id, destino.id, RelationType.Inheritance)
        }
      }
    }

  }

  def newRelation(sourceId: Int, targetId: Int, relType: RelationType): RelationUML = {
    new RelationUML(sourceId, targetId, Handle.Top1.value, Handle.Top1.value, RelationType.Inheritance)
  }

  def createMapFromNodesArray(nodes: List[ClassUML]): Map[String, ClassUML] = {
    nodes.map(n => n.name -> n).toMap
  }
}
