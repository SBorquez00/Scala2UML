ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.16"

lazy val root = (project in file("."))
  .settings(
    name := "Scala2UML",
    idePackagePrefix := Some("scalauml")
  )

libraryDependencies ++= Seq(
  "org.scalameta" %% "scalameta" % "4.13.5",
  "com.lihaoyi" %% "cask" % "0.9.7"
)

val circeVersion = "0.14.13"

libraryDependencies ++= Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-parser"
).map(_ % circeVersion)
