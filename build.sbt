lazy val root = (project in file(".")).aggregate(gvCoreJs, gvCoreJvm, gvClientServerJs, gvClientServerJvm).settings(
  publish := {},
  publishLocal := {}
)

lazy val sharedSettings = Seq(
  organization := "uk.co.turingatemyhamster",
  scalaVersion := "2.11.7",
  scalacOptions ++= Seq("-deprecation", "-unchecked"),
  version := "0.4.1-SNAPSHOT",
  licenses += ("Apache-2.0", url("http://www.apache.org/licenses/LICENSE-2.0.html"))
)

lazy val gvCore = crossProject.in(file("gv-core")).settings(sharedSettings :_*).settings(
  name := "gv-core"
)

lazy val gvCoreJs = gvCore.js

lazy val gvCoreJvm = gvCore.jvm


lazy val gvClientServer = crossProject.in(file("gv-clientServer")).settings(sharedSettings :_*).settings(
  name := "gv-clientServer"
).dependsOn(gvCore)

lazy val gvClientServerJs = gvClientServer.js

lazy val gvClientServerJvm = gvClientServer.jvm.settings(
  (resources in Compile) += {
        (fastOptJS in (gvClientServerJs, Compile)).value
        (artifactPath in (gvClientServerJs, Compile, fastOptJS)).value
      }
)