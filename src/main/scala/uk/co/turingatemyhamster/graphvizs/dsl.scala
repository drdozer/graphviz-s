package uk.co.turingatemyhamster.graphvizs

/**
 * Created by IntelliJ IDEA.
 * User: nmrp3
 * Date: 18/01/12
 * Time: 12:10
 * To change this template use File | Settings | File Templates.
 */

package object dsl {

  def parseAsGraph(in: CharSequence): Graph = {
    val parser = new DotAstParser
    import parser._
    parser.parseAll(graph, in) match {
      case Success(g, _) => g
      case NoSuccess(msg, input) => sys.error("Unable to parse input: " + msg + " at " + input.pos);
    }
  }
  
  def renderGraph(g: Graph, out: Appendable) {
    val renderer = new DotAstRenderer(out)
    import renderer._

    render_graph(g)
  }


}