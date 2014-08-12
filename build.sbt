import play.Project._

name := "hr"

version := "1.0"

libraryDependencies ++= Seq(
  "org.webjars" %% "webjars-play" % "2.2.0",
  "org.webjars" % "bootstrap" % "3.1.1-2"
)

playScalaSettings