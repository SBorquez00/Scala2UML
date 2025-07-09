package scalauml
package serializers

import io.circe.{Encoder, Json}
import models._

import io.circe.generic.semiauto.deriveEncoder
import io.circe.syntax.EncoderOps

object UMLJsonDocumentSerializer {
  implicit val classTypeEncoder: Encoder[ClassType] =
    Encoder.encodeString.contramap(_.value)

  implicit val accessModifierEncoder: Encoder[AccessModifier] =
    Encoder.encodeString.contramap(_.value)

  implicit val relationType: Encoder[RelationType] =
    Encoder.encodeString.contramap(_.value)

  implicit val methodUMLEncoder: Encoder[MethodUML] =
    Encoder.instance { m =>
      Json.obj(
        "name" -> Json.fromString(m.name),
        "domType" -> m.domType.asJson,
        "codType" -> Json.fromString(m.codomType),
        "visibility" -> m.visibility.asJson,
        "abstract" -> Json.fromBoolean(m.isAbstract)
      )
    }

  implicit val fieldUMLEncoder: Encoder[FieldUML] =
    Encoder.instance{f =>
      Json.obj(
        "name" -> Json.fromString(f.name),
        "type" -> Json.fromString(f.dataType),
        "visibility" -> f.visibility.asJson
      )
    }

  implicit val ClassUMLEncoder: Encoder[ClassUML] =
    Encoder.instance{c =>
      Json.obj(
        "id" -> Json.fromString(c.id.toString),
        "name" -> Json.fromString(c.name),
        "classType" -> c.classType.asJson,
        "methods" -> c.methods.asJson,
        "fields" -> c.fields.asJson,
        "x" -> Json.fromDoubleOrString(c.xCord),
        "y" -> Json.fromDoubleOrString(c.yCord)
      )
    }

  implicit val RelationUMLEncoder: Encoder[RelationUML] =
    Encoder.instance{e =>
      Json.obj(
        "source" -> Json.fromString(e.source.toString()),
        "target" -> Json.fromString(e.target.toString()),
        "sourceHandle" -> Json.fromString(e.sourceHandle),
        "targetHandle" -> Json.fromString(e.targetHandle),
        "type" -> e.relationType.asJson
      )
    }


  implicit val UMLJsonDocumentEncoder: Encoder[UMLJsonDocument] = deriveEncoder
}
