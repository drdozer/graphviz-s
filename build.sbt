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
  name := "gv-core",
  libraryDependencies += "com.lihaoyi" %%% "fastparse" % "0.3.4",
  libraryDependencies += "com.lihaoyi" %% "utest" % "0.4.3" % "test",
  testFrameworks += new TestFramework("utest.runner.Framework")
)

lazy val gvCoreJs = gvCore.js

lazy val gvCoreJvm = gvCore.jvm.settings(
  libraryDependencies ++= Seq("org.specs2" %% "specs2-core" % "3.8.3" % "test"),
  scalacOptions in Test ++= Seq("-Yrangepos")
)


lazy val gvClientServer = crossProject.in(file("gv-clientServer")).settings(sharedSettings :_*).settings(
  name := "gv-clientServer"
).dependsOn(gvCore)

lazy val gvClientServerJs = gvClientServer.js.settings(
  persistLauncher in Compile := true,
  persistLauncher in Test := false,
  libraryDependencies += "io.github.widok" %%% "widok" % "0.3.0-SNAPSHOT"
)

lazy val gvClientServerJvm = gvClientServer.jvm
  .enablePlugins(SbtWeb, sbtdocker.DockerPlugin, JavaAppPackaging)
  .dependsOn(gvClientServerJs)
  .settings(
  libraryDependencies += "com.typesafe.akka" %% "akka-http-experimental" % "2.4.5",
  (resources in Assets) += {
    (fastOptJS in (gvClientServerJs, Compile)).value
    (artifactPath in (gvClientServerJs, Compile, fastOptJS)).value
  },
  (resources in Assets) += {
    (fastOptJS in (gvClientServerJs, Compile)).value
    (artifactPath in (gvClientServerJs, Compile, packageScalaJSLauncher)).value
  },
  (resources in Assets) += {
    (fastOptJS in (gvClientServerJs, Compile)).value
    (artifactPath in (gvClientServerJs, Compile, packageJSDependencies)).value
  },
  (managedClasspath in Runtime) += (packageBin in Assets).value,

  dockerfile in docker := {
    val appDir: File = stage.value
    val targetDir = "/app"

    new Dockerfile {
      from("java:8-jre")
      entryPoint(s"$targetDir/bin/${executableScriptName.value}")
      copy(appDir, targetDir)
      expose(10080)
    }
  }
)
