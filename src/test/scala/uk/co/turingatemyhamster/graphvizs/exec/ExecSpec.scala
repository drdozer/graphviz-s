package uk.co.turingatemyhamster.graphvizs.exec

import org.specs2.mutable.Specification
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner


/**
 * Created by IntelliJ IDEA.
 * User: nmrp3
 * Date: 24/01/12
 * Time: 12:59
 * To change this template use File | Settings | File Templates.
 */

@RunWith(classOf[JUnitRunner])
class ExecSpec extends Specification {

  "DOT Exec" should {

    "run DOT on string input and generate string output" in {
      val dotOut = dot2dotS("""
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

}