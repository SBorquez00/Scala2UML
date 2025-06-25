package scalauml

import scalauml.parser.ScalaParser

import scala.meta._
import scala.io.{Source => FileSource}
object Main {
  def main(args: Array[String]): Unit = {
    val parser = new ScalaParser();
    parser.processOneFileOfClasses()
  }
}