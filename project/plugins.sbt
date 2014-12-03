//// REQUIRED SETUP

// Setup bintray resolver - needed for many sbt plugins
resolvers += Resolver.url(
  "bintray-sbt-plugin-releases",
  url("http://dl.bintray.com/content/sbt/sbt-plugin-releases"))(
  Resolver.ivyStylePatterns)

resolvers += Resolver.sonatypeRepo("releases")

resolvers += Resolver.sonatypeRepo("snapshots")

resolvers += "spray repo" at "http://repo.spray.io"

// Wrapper plugin for scalajs
addSbtPlugin("com.github.inthenow" % "sbt-scalajs" % "0.56.6")

//// END REQUIRED SETUP

// Uncomment to get more information during initialization
//logLevel := Level.Warn

// Uncomment to use bintray publishing
//addSbtPlugin("me.lessis" % "bintray-sbt" % "0.1.1")

// Uncomment to add the sbt-release plugin - see https://github.com/sbt/sbt-release
//addSbtPlugin("com.github.gseitz" % "sbt-release" % "0.8.5")

// Uncomment to use utest - see https://github.com/lihaoyi/utest
//addSbtPlugin("com.lihaoyi" % "utest-js-plugin" % "0.2.4")

addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "1.6.0")

addSbtPlugin("org.xerial.sbt" % "sbt-pack" % "0.6.5")
