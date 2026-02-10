package scalauml
package analyzer

import models.{ClassType, ClassUML, Handle, RelationType, RelationUML}

class RelationDetector
{

  def generateRelations(nodes: List[ClassUML]): List[RelationUML] = {
    val mapaNodos = createMapFromNodesArray(nodes);

    val inheritance = generateInheritanceRelations(nodes, mapaNodos)
    val implementation = generateImplementationRelations(nodes, mapaNodos)
    val aggregation = generateAggregationRelation(nodes, mapaNodos)
    val association = generateAssociationRelations(nodes, mapaNodos)

    inheritance ++ implementation ++ aggregation ++ association

  }

  def generateAssociationRelations(nodes: List[ClassUML], mapaNodos: Map[String, ClassUML]): List[RelationUML] = {

    nodes.flatMap { nodo =>
      nodo.methods.flatMap { metodo =>
        val relCodom = mapaNodos.get(metodo.codomBaseType)
                        .map { destino =>
                          newRelation(nodo.id, destino.id, RelationType.Association)
                        }
        val relDom = metodo.domBaseType.flatMap { param =>
                      mapaNodos.get(param)
                        .map { destino =>
                          newRelation(nodo.id, destino.id, RelationType.Association)
                        }
                    }
        List(relCodom, relDom).flatten
      }
    }
  }

  def generateAggregationRelation(nodes: List[ClassUML], mapaNodos: Map[String, ClassUML]): List[RelationUML] = {

    nodes.flatMap { nodo =>
        nodo.fields.flatMap { nombreDestino =>
          mapaNodos.get(nombreDestino.baseType)
            .map { destino =>
              newRelation(nodo.id, destino.id, RelationType.Aggregation)
            }
        }
      }
  }

  def generateImplementationRelations(nodes: List[ClassUML], mapaNodos: Map[String, ClassUML]): List[RelationUML] = {

    nodes.filter(nodo => nodo.classType != ClassType.Trait)
      .flatMap { nodo =>
      nodo.parentClassesNames.flatMap { nombreDestino =>
        mapaNodos.get(nombreDestino)
          .filter(dest => dest.classType == ClassType.Trait)
          .map { destino =>
          newRelation(nodo.id, destino.id, RelationType.Implementation)
        }
      }
    }
  }

  def generateInheritanceRelations(nodes: List[ClassUML], mapaNodos: Map[String, ClassUML]): List[RelationUML] = {

    nodes
      .flatMap { nodo =>
      nodo.parentClassesNames.flatMap { nombreDestino =>
        mapaNodos.get(nombreDestino)
          .filterNot(dest => (dest.classType == ClassType.Trait) && nodo.classType != ClassType.Trait)
          .map { destino =>
          newRelation(nodo.id, destino.id, RelationType.Inheritance)
        }
      }
    }

  }

  def newRelation(sourceId: Int, targetId: Int, relType: RelationType): RelationUML = {
    val handle = relType match {
      case RelationType.Association => Handle.Top1
      case RelationType.Inheritance => Handle.Top2
      case RelationType.Implementation => Handle.Top2
      case RelationType.Aggregation => Handle.Top3
    }
    new RelationUML(sourceId, targetId, handle.value, handle.value, relType)
  }

  def createMapFromNodesArray(nodes: List[ClassUML]): Map[String, ClassUML] = {
    nodes.map(n => n.name -> n).toMap
  }
}
