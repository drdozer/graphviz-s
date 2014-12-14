
import sbt._
import sbt.Keys._
import com.inthenow.sbt.scalajs._
import com.inthenow.sbt.scalajs.SbtScalajs._
import scala.scalajs.sbtplugin.ScalaJSPlugin._
import ScalaJSKeys._
import bintray.Plugin._
import bintray.Keys._
import org.eclipse.jgit.lib._
import xerial.sbt.Pack._
import spray.revolver.RevolverPlugin._


object GraphvizSBuild extends Build {

  val logger = ConsoleLogger()

  val baseVersion = "0.3.1"

  val gvCore = XModule(id = "gv-core", defaultSettings = buildSettings, baseDir = "gv-core")

  lazy val core            = gvCore.project(corePlatformJvm, corePlatformJs)
  lazy val corePlatformJvm = gvCore.jvmProject(coreSharedJvm)
    .settings(corePlatformJvmSettings : _*)
  lazy val corePlatformJs  = gvCore.jsProject(coreSharedJs)
  lazy val coreSharedJvm   = gvCore.jvmShared().settings(coreSharedSettingsJvm : _*)
  lazy val coreSharedJs    = gvCore.jsShared(coreSharedJvm).settings(coreSharedSettingsJs : _*)

  val gvClientServer = XModule(id = "gv-clientServer", defaultSettings = buildSettings, baseDir = "gv-clientServer")

  lazy val clientServer             = gvClientServer.project(clientServerPlatformJvm, clientServerPlatformJs)
  lazy val clientServerPlatformJvm  = gvClientServer.jvmProject(clientServerSharedJvm).dependsOn(corePlatformJvm).settings(clientServerPlatformJvmSettings : _*)
  lazy val clientServerPlatformJs   = gvClientServer.jsProject(clientServerSharedJs).dependsOn(corePlatformJs).settings(clientServerPlatformJsSettings : _*)
  lazy val clientServerSharedJvm    = gvClientServer.jvmShared().dependsOn(coreSharedJvm)
  lazy val clientServerSharedJs     = gvClientServer.jsShared(clientServerSharedJvm).dependsOn(coreSharedJs)

  lazy val buildSettings: Seq[Setting[_]] = bintrayPublishSettings ++ Seq(
    organization := "uk.co.turingatemyhamster",
    scalaVersion := "2.11.4",
    crossScalaVersions := Seq("2.11.4", "2.10.4"),
    scalacOptions ++= Seq("-deprecation", "-unchecked"),
    version := makeVersion(baseVersion),
    resolvers += Resolver.url(
      "bintray-scalajs-releases",
      url("http://dl.bintray.com/scala-js/scala-js-releases/"))(
        Resolver.ivyStylePatterns),
    resolvers ++= Seq("snapshots", "releases").map(Resolver.sonatypeRepo),
    resolvers += "spray repo" at "http://repo.spray.io",
    resolvers += "Scalaz Bintray Repo" at "http://dl.bintray.com/scalaz/releases",
    resolvers += "drdozer Bintray Repo" at "http://dl.bintray.com/content/drdozer/maven",
    publishMavenStyle := true,
    repository in bintray := "maven",
    bintrayOrganization in bintray := None,
    licenses +=("Apache-2.0", url("http://www.apache.org/licenses/LICENSE-2.0.html"))
  )

  lazy val coreSharedSettingsJvm = Seq(
    libraryDependencies ++= Seq(
      "org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.2",
      "com.lihaoyi" %% "utest" % "0.2.4" % "test"
    )
  )

  lazy val coreSharedSettingsJs = utest.jsrunner.Plugin.utestJsSettings ++ Seq(
    libraryDependencies ++= Seq(
      "org.scalajs" %%% "scala-parser-combinators" % "1.0.2",
      "com.lihaoyi" %% "utest" % "0.2.4" % "test"
    )
  )

  lazy val corePlatformJvmSettings = Seq(
    libraryDependencies ++= Seq(
      "org.specs2" %% "specs2-core" % "2.4.14" % "test",
      "org.specs2" %% "specs2-matcher-extra" % "2.4.14" % "test",
      "org.scalacheck" %% "scalacheck" % "1.12.1" % "test",
      "junit" % "junit" % "4.12" % "test")
  )

  lazy val clientServerPlatformJvmSettings = packAutoSettings ++ Revolver.settings ++ Seq(
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

  def fetchGitBranch(): String = {
    val builder = new RepositoryBuilder()
    builder.setGitDir(file(".git"))
    val repo = builder.readEnvironment().findGitDir().build()
    val gitBranch = repo.getBranch
    logger.info(s"Git branch reported as: $gitBranch")
    repo.close()
    val travisBranch = Option(System.getenv("TRAVIS_BRANCH"))
    logger.info(s"Travis branch reported as: $travisBranch")

    travisBranch getOrElse gitBranch

    val branch = (travisBranch getOrElse gitBranch) replaceAll ("/", "_")
    logger.info(s"Computed branch is $branch")
    branch
  }

  def makeVersion(baseVersion: String): String = {
    val branch = fetchGitBranch()
    if(branch == "master") {
      baseVersion
    } else {
      val tjn = Option(System.getenv("TRAVIS_JOB_NUMBER"))
      s"$branch-$baseVersion${
        tjn.map("." + _) getOrElse ""
      }"
    }
  }
}
