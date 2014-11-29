package uk.co.turingatemyhamster.graphvizs
package demo

import exec._
import dsl._

/**
 *
 *
 * @author Matthew Pocock
 */
object SimpleGraph {
  def main(args: Array[String]): Unit = {

    import Predef.{ArrowAssoc => _}

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
  }
}
