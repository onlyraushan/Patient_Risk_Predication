ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.12.18"

lazy val root = (project in file("."))
  .settings(
    name := "Risk"
  )

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-sql" % "3.3.1",
  "org.apache.spark" %% "spark-core" % "3.3.1",
  "org.apache.spark" %% "spark-mllib" % "3.3.1",
  "org.postgresql" % "postgresql" % "42.7.2"
)
