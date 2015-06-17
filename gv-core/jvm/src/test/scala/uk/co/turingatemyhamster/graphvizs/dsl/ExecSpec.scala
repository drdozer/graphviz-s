package uk.co.turingatemyhamster.graphvizs.dsl

import org.specs2.mutable.Specification
import org.specs2.specification.Scope

import uk.co.turingatemyhamster.graphvizs.exec._
import java.io.File


/**
 * Created by IntelliJ IDEA.
 * User: nmrp3
 * Date: 24/01/12
 * Time: 12:59
 * To change this template use File | Settings | File Templates.
 */

class ExecSpec extends Specification {

  "DOT Exec" should {

    "run DOT on string input and generate string output" in {
      val dotOut = dot2dot[String, String]("""
              digraph G {

              	subgraph cluster_0 {
              		style=filled;
              		color=lightgrey;
              		node [style=filled,color=white];
              		a0 -> a1 -> a2 -> a3;
              		label = "process #1";
              	}

              	subgraph cluster_1 {
              		node [style=filled];
              		b0 -> b1 -> b2 -> b3;
              		label = "process #2";
              		color=blue
              	}
              	start -> a0;
              	start -> b0;
              	a1 -> b3;
              	b2 -> a3;
              	a3 -> a0;
              	a3 -> end;
              	b3 -> end;

              	start [shape=Mdiamond];
              	end [shape=Msquare];
              }""")

      dotOut contains "pos="
    }

  }

  "run DOT on graph input and generate graph output" in {
    val gOut = dot2dot[Graph, Graph](StrictDigraph("g",
      "a1" --> "a2" --> "a3" --> "a4",
      "a1" --> "a4"
    ))

    val annotated = gOut.statements.collect { case es@EdgeStatement(_, _, Some(_)) => es }
    ! annotated.isEmpty
  }

  "allow dot location to be overriden" in {
    val path = new File("/usr/local/bin/doc")
    val customExec = Exec(path)
    import customExec._
    dotBinary ==== path
  }

//  "run DOT on graph input and generate SVG XML output" in {
//    val svgOut = dot2dot[Graph, String](StrictDigraph("g",
//          EdgeStatement("a1") --> "a2" --> "a3" --> "a4",
//          EdgeStatement("a1") --> "a4"
//        ), format = DotFormat.svg)
//
//    svgOut.label must_== "svg"
//  }

}
