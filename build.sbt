import play.Project._

name := "HR"

version := "1.0"

libraryDependencies ++= Seq(
  jdbc,
  "org.squeryl" %% "squeryl" % "0.9.5-6",
  "postgresql" % "postgresql" % "9.2-1002.jdbc4",
  "org.webjars" %% "webjars-play" % "2.2.1",
  "org.webjars" % "bootstrap" % "3.1.1-2",
  "org.webjars" % "jquery" % "2.1.1",
  "org.webjars" % "pnotify" % "2.0.1",
  "org.webjars" % "angularjs" % "1.2.22",
  "org.webjars" % "angular-ui-router" % "0.2.10-1",
  "org.webjars" % "ng-table" % "0.3.3",
  "org.webjars" % "angular-ui-tree" % "2.1.5"
)

playScalaSettings