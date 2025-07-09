package scalauml
package api

import cask.{get, options, post, postForm}
import io.circe.Json
import scalauml.parser.ScalaParser
import scalauml.utils.JsonInterop.circeToUjson

import java.nio.file.Files
import scala.io.{Source => FileSource}

object MainApi extends cask.MainRoutes {

  // Endpoint simple de prueba
  @get("/")
  def hello(): String = {
    "API is running very well"
  }

  // Endpoint POST que recibe un archivo, pero aún no lo procesa
  @postForm("/upload")
  def upload(file: cask.FormFile): cask.Response[ujson.Value] = {

    val fileTest = file.filePath.get.toFile

    val parser = new ScalaParser()
    val jsonAns = circeToUjson(parser.processOneFileOfClasses(fileTest))

    cask.Response(
      data = jsonAns,
      statusCode = 200,
      headers = Seq(
        "Access-Control-Allow-Origin" -> "http://localhost:3000",
        "Access-Control-Allow-Headers" -> "*",
        "Access-Control-Allow-Methods" -> "POST, OPTIONS"
      )
    )

  }

  @options("/upload")
  def uploadOptions(): cask.Response[String] = {
    cask.Response(
      data = "",
      statusCode = 200,
      headers = Seq(
        "Access-Control-Allow-Origin" -> "http://localhost:3000",
        "Access-Control-Allow-Headers" -> "*",
        "Access-Control-Allow-Methods" -> "POST, OPTIONS"
      )
    )
  }


  initialize()
}
