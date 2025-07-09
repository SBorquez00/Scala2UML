package scalauml

import io.circe
import io.circe.Json
import org.scalatest.funsuite.AnyFunSuite
import parser.ScalaParser

import org.scalatest.prop.TableFor2
import org.scalatest.prop.TableDrivenPropertyChecks._

import java.io.File
import scala.io.Source

class ProcessorTest extends AnyFunSuite {

  def readFile(resourcePath: String): String = {
    val file = new File(getClass.getResource(resourcePath).toURI)
    val source = Source.fromFile(file)
    try source.mkString
    finally source.close()
  }

  def jsonEquals(resulted: Json, expected: Json): Boolean = {

    val nodesEqual = compareNodes(resulted, expected)

    val relationsEqual = compareRelations(resulted, expected)

    nodesEqual && relationsEqual
  }

  def compareNodes(resulted: Json, expected: Json): Boolean = {
    val resultedNodes = resulted.hcursor.downField("nodes").as[List[Json]].getOrElse(List())
    val expectedNodes = expected.hcursor.downField("nodes").as[List[Json]].getOrElse(List())

    val expectedNodeSet = expectedNodes.map(cleanNode).toSet
    val resultedNodeSet = resultedNodes.map(cleanNode).toSet

    expectedNodeSet == resultedNodeSet
  }

  def cleanNode(node: Json): Json = {
    val cursor = node.hcursor

    val name = cursor.get[String]("name").getOrElse("")
    val classType = cursor.get[String]("classType").getOrElse("")

    val fields = cursor.downField("fields").as[List[Json]].getOrElse(List())
      .sortBy(_.hcursor.get[String]("name").getOrElse(""))

    val methods = cursor.downField("methods").as[List[Json]].getOrElse(List())
      .map(cleanMethod).sortBy(_.hcursor.get[String]("name").getOrElse(""))

    Json.obj(
      "name" -> Json.fromString(name),
      "classType" -> Json.fromString(classType),
      "methods" -> Json.fromValues(methods),
      "fields" -> Json.fromValues(fields)
    )
  }

  def cleanMethod(method: Json): Json = {
    val cursor = method.hcursor

    val domType = cursor.downField("domType").as[List[String]].getOrElse(List()).sorted

    Json.obj(
      "name" -> cursor.get[String]("name").map(Json.fromString).getOrElse(Json.Null),
      "codType" -> cursor.get[String]("codType").map(value => if(value.isEmpty) "Unit" else value).map(Json.fromString).getOrElse(Json.Null),
      "visibility" -> cursor.get[String]("visibility").map(Json.fromString).getOrElse(Json.Null),
      "abstract" -> cursor.get[Boolean]("abstract").map(Json.fromBoolean).getOrElse(Json.False),
      "domType" -> Json.fromValues(domType.map(Json.fromString))
    )
  }

  def compareRelations(expected: Json, actual: Json): Boolean = {
    val expectedRelations = extractRelations(expected)
    val actualRelations = extractRelations(actual)


    expectedRelations == actualRelations
  }

  def extractRelations(json: Json): Set[(String, String, String)] = {
    val cursor = json.hcursor

    // Map node IDs to names
    val nodes = cursor.downField("nodes").as[List[Json]].getOrElse(Nil)
    val idToName: Map[String, String] = nodes.flatMap { node =>
      for {
        id <- node.hcursor.get[String]("id").toOption
        name <- node.hcursor.get[String]("name").toOption
      } yield id -> name
    }.toMap

    // Extract edges using the correct field names
    val edges = cursor.downField("edges").as[List[Json]].getOrElse(Nil)
    edges.flatMap { edge =>
      for {
        sourceId <- edge.hcursor.get[String]("source").toOption
        targetId <- edge.hcursor.get[String]("target").toOption
        relationType <- edge.hcursor.get[String]("type").toOption
        sourceName <- idToName.get(sourceId)
        targetName <- idToName.get(targetId)
      } yield (sourceName, targetName, relationType)
    }.toSet
  }


  val cases: TableFor2[String, String] = Table(
    ("inputPath", "expectedPath"),
    ("/concrete_class_simple/input.scala", "/concrete_class_simple/output.json"),
    ("/abstract_class_mixed_methods/input.scala", "/abstract_class_mixed_methods/output.json"),
    ("/trait_partial_implementation/input.scala", "/trait_partial_implementation/output.json"),
    ("/field_visibility/input.scala", "/field_visibility/output.json"),
    ("/composite_field_types/input.scala", "/composite_field_types/output.json"),
    ("/method_parameters_and_return/input.scala", "/method_parameters_and_return/output.json"),
    ("/method_visibility/input.scala", "/method_visibility/output.json"),
    ("/class_inheritance/input.scala", "/class_inheritance/output.json"),
    ("/trait_implementation/input.scala", "/trait_implementation/output.json"),
    ("/direct_aggregation/input.scala", "/direct_aggregation/output.json"),
    ("/trait_chain/input.scala", "/trait_chain/output.json"),
    ("/complete_mix/input.scala", "/complete_mix/output.json")
  )

  forAll(cases) { (inputPath, expectedPath) =>
    test(s"Procesa correctamente el caso $inputPath") {

      val inputFile = new File(getClass.getResource(inputPath).toURI)
      val expected = readFile(expectedPath)

      val parser = new ScalaParser();

      val resultJson = parser.processOneFileOfClasses(inputFile)
      val expectedJson = circe.parser.parse(expected).getOrElse(Json.Null)

      assert(jsonEquals(resultJson, expectedJson))
    }
  }
}