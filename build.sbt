ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.16"

lazy val root = (project in file("."))
  .settings(
    name := "Scala2UML",
    idePackagePrefix := Some("scalauml")
  )

libraryDependencies ++= Seq(
  "org.scalameta" %% "scalameta" % "4.13.5"
)
