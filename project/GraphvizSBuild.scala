
import sbt._
import sbt.Keys._
import com.inthenow.sbt.scalajs._
import com.inthenow.sbt.scalajs.SbtScalajs._
import scala.scalajs.sbtplugin.ScalaJSPlugin._
import ScalaJSKeys._
import xerial.sbt.Pack._


object GraphvizSBuild extends Build {

  val logger = ConsoleLogger()

  val gvCore = XModule(id = "gv-core", defaultSettings = buildSettings, baseDir = "gv-core")

  lazy val core            = gvCore.project(corePlatformJvm, corePlatformJs)
  lazy val corePlatformJvm = gvCore.jvmProject(coreSharedJvm).settings(corePlatformJvmSettings : _*)
  lazy val corePlatformJs  = gvCore.jsProject(coreSharedJs)
  lazy val coreSharedJvm   = gvCore.jvmShared().settings(coreSharedSettingsJvm : _*)
  lazy val coreSharedJs    = gvCore.jsShared(coreSharedJvm).settings(coreSharedSettingsJs : _*)

  val gvClientServer = XModule(id = "gv-clientServer", defaultSettings = buildSettings, baseDir = "gv-clientServer")

  lazy val clientServer             = gvClientServer.project(clientServerPlatformJvm, clientServerPlatformJs)
  lazy val clientServerPlatformJvm  = gvClientServer.jvmProject(clientServerSharedJvm).dependsOn(corePlatformJvm).settings(clientServerPlatformJvmSettings : _*)
  lazy val clientServerPlatformJs   = gvClientServer.jsProject(clientServerSharedJs).dependsOn(corePlatformJs).settings(clientServerPlatformJsSettings : _*)
  lazy val clientServerSharedJvm    = gvClientServer.jvmShared().dependsOn(coreSharedJvm)
  lazy val clientServerSharedJs     = gvClientServer.jsShared(clientServerSharedJvm).dependsOn(coreSharedJs)

  lazy val coreSharedSettingsJvm = Seq(
    libraryDependencies ++= Seq("org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.2")
  )

  lazy val coreSharedSettingsJs = Seq(
    libraryDependencies ++= Seq("org.scalajs" %%% "scala-parser-combinators" % "1.0.2")
  )

  lazy val buildSettings: Seq[Setting[_]] = Seq(
    organization := "uk.co.turingatemyhamster",
    scalaVersion := "2.11.4",
    crossScalaVersions := Seq("2.11.4", "2.11.2"),
    scalacOptions ++= Seq("-deprecation", "-unchecked"),
    version := "0.3.1",
    resolvers += Resolver.url(
      "bintray-scalajs-releases",
      url("http://dl.bintray.com/scala-js/scala-js-releases/"))(
        Resolver.ivyStylePatterns),
    resolvers ++= Seq("snapshots", "releases").map(Resolver.sonatypeRepo),
    resolvers += "spray repo" at "http://repo.spray.io",
    scalacOptions ++= Seq("-Ylog-classpath")
  )

  lazy val corePlatformJvmSettings = Seq(
    libraryDependencies ++= Seq("org.specs2" %% "specs2-core" % "2.4.13" % "test")
  )

  lazy val clientServerPlatformJvmSettings = packAutoSettings ++ Seq(
    libraryDependencies ++= Seq(
      "io.spray" %% "spray-routing" % "1.3.2",
      "io.spray" %% "spray-can" % "1.3.2",
      "com.typesafe.akka" %% "akka-actor" % "2.3.7",
      "com.scalatags" %% "scalatags" % "0.4.2"
    ),
    (resources in Compile) += {
      (fastOptJS in (clientServerPlatformJs, Compile)).value
      (artifactPath in (clientServerPlatformJs, Compile, fastOptJS)).value
    }
  )

  lazy val clientServerPlatformJsSettings = Seq(
    libraryDependencies ++= Seq(
      "uk.co.turingatemyhamster" %%% "scalatags-ext" % "0.1.1",
      "com.scalatags" %%% "scalatags" % "0.4.2",
      "com.scalarx" %%% "scalarx" % "0.2.6",
      "org.scala-lang.modules.scalajs" %%% "scalajs-dom" % "0.6"
    )
  )
}
