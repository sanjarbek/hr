import play.Project._

name := "HR"

version := "1.0"

libraryDependencies ++= Seq(
  jdbc,
  cache,
  "org.squeryl" %% "squeryl" % "0.9.5-6",
  "org.postgresql" % "postgresql" % "9.3-1101-jdbc41",
  "org.webjars" %% "webjars-play" % "2.2.1",
  "org.webjars" % "bootstrap" % "3.1.1-2",
  "org.webjars" % "jquery" % "2.1.1",
  "org.webjars" % "pnotify" % "2.0.1",
  "org.webjars" % "angularjs" % "1.2.22",
  "org.webjars" % "angular-ui-router" % "0.2.10-1",
  "org.webjars" % "ng-table" % "0.3.3",
  "org.webjars" % "angular-ui-tree" % "2.1.5",
  "org.webjars" % "angular-ui-bootstrap" % "0.11.0-2",
  "org.webjars" % "angular-file-upload" % "1.6.6",
  "org.webjars" % "angular-tree-control" % "0.2.2",
  //  "org.jopendocument" % "jOpenDocument" % "1.3",
  "org.docx4j" % "docx4j" % "3.2.0",
  "org.apache.poi" % "poi-ooxml" % "3.10.1",
  "org.apache.poi" % "poi" % "3.10.1",
  "org.webjars" % "font-awesome" % "4.2.0",
  "io.github.cloudify" %% "spdf" % "1.0.0",
  "org.webjars" % "angular-ckeditor" % "0.2.0"
)

playScalaSettings