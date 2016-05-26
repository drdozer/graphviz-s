#GRAPHVIZ bindings from scala.

This projects has two modules: core and clientServer. Core provides an API to parse and emit graphviz files, and also
to execute dot. ClientServer provides a scala.js/spray.io client-server environment for exposing the dot binary
as a web service.

To call the graphviz binaries, you must have graphviz installed for your system.

## gv-core

Use:

Add a resolver to `build.sbt`:
```scala
resolvers += "bintray-drdozer" at "http://dl.bintray.com/content/drdozer/maven"
```
and the dependency to your `libraryDependencies`:
```scala
libraryDependencies += "uk.co.turingatemyhamster" %% "gv-core" % "0.4.1"
```

```scala
import uk.co.turingatemyhamster.graphvizs._
import uk.co.turingatemyhamster.graphvizs.dsl._
import uk.co.turingatemyhamster.graphvizs.exec._

val g = StrictDigraph(
  id = "g",
  "lacI" :| ("shape" := "box"),
  "tetR" :| ("shape" := "box"),
  "lacI" --> "tetR" :| ("label" := "represses")
)

println("Input graph:")
renderGraph(g, System.out)

println("Piping through dot...")
val gg = dot2dot[Graph, Graph](g)

println("Resulting graph:")
renderGraph(gg, System.out)

println("Rendering as svg...")
val gSvg = dot2dot[Graph, String](g, format = DotFormat.svg)
println(gSvg)
```

## gv-clientServer

The server exposes the following endpoints:

```
/graphviz/$layout.$format
```

Post to this to receive back the output of dot. For example, `/graphviz/dot.dot` will invoke dot with the dot layout and
return the result as dot text while `/graphviz/neato.svg` will invoke dot with the neato layout and svg output.

## dockerised service

Firstly build the dockerised web service:
```
$sbt
>project gvClientServerJVM
>docker
```

Then run using docker:
```
$docker run -d  -p 127.0.0.1:10080:10080 uk.co.turingatemyhamster/gv-clientserver
```

Then open up [http://localhost:10080/index.html] and enjoy an entirely dockerised dot editing experience.
