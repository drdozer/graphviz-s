
import sbt._
import sbt.Keys._
import com.inthenow.sbt.scalajs._
import com.inthenow.sbt.scalajs.SbtScalajs._
import scala.scalajs.sbtplugin.ScalaJSPlugin._
import ScalaJSKeys._


object GraphvizSBuild extends Build {

  val module = XModule(id = "graphviz-s", defaultSettings = buildSettings)

  val logger = ConsoleLogger()

  lazy val buildSettings: Seq[Setting[_]] = Seq(
    organization := "uk.co.turingatemyhamster",
    scalaVersion := "2.11.4",
    crossScalaVersions := Seq("2.11.4", "2.11.2"),
    scalacOptions ++= Seq("-deprecation", "-unchecked"),
    version := "0.3.0",
    resolvers += Resolver.url(
      "bintray-scalajs-releases",
      url("http://dl.bintray.com/scala-js/scala-js-releases/"))(
        Resolver.ivyStylePatterns),
    resolvers ++= Seq("snapshots", "releases").map(Resolver.sonatypeRepo)
  )

  lazy val graphvizS            = module.project(graphvizSPlatformJvm, graphvizSPlatformJs)
  lazy val graphvizSPlatformJvm = module.jvmProject(graphvizSSharedJvm).settings(graphvizSPlatformJvmSettings : _*)
  lazy val graphvizSPlatformJs  = module.jsProject(graphvizSSharedJs)
  lazy val graphvizSSharedJvm   = module.jvmShared().settings(graphvizSSharedSettings : _*)
  lazy val graphvizSSharedJs    = module.jsShared(graphvizSSharedJvm).settings(graphvizSSharedSettings : _*)

  lazy val graphvizSSharedSettings = Seq(
    libraryDependencies ++= Seq("org.scalajs" %%%! "scala-parser-combinators" % "1.0.2")
  )

  lazy val graphvizSPlatformJvmSettings = Seq(
    libraryDependencies ++= Seq("org.specs2" %% "specs2-core" % "2.4.13" % "test")
  )
}
