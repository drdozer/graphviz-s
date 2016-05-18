//// REQUIRED SETUP

// Setup bintray resolver - needed for many sbt plugins
resolvers += Resolver.url(
  "bintray-sbt-plugin-releases",
  url("http://dl.bintray.com/content/sbt/sbt-plugin-releases"))(
  Resolver.ivyStylePatterns)

libraryDependencies ++= Seq(
  "org.eclipse.jgit" % "org.eclipse.jgit" % "3.0.0.201306101825-r"
)

resolvers += Classpaths.sbtPluginReleases

resolvers += Classpaths.typesafeReleases

addSbtPlugin("me.lessis" % "bintray-sbt" % "0.1.2")

addSbtPlugin("com.typesafe.sbt" % "sbt-pgp" % "0.8.3")

addSbtPlugin("org.scala-js" % "sbt-scalajs" % "0.6.6")

addSbtPlugin("org.xerial.sbt" % "sbt-pack" % "0.7.9")