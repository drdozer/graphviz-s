package uk.co.turingatemyhamster.graphvizs

import java.io.Reader

import uk.co.turingatemyhamster.graphvizs.dsl.EdgeOp.{--, ->}

/**
 * Created by IntelliJ IDEA.
 * User: nmrp3
 * Date: 18/01/12
 * Time: 12:10
 * To change this template use File | Settings | File Templates.
 */

package object dsl {

  def parseAsGraph(in: CharSequence): Graph = {
    val parser = DotAstParser
    import parser._
    parser.parseAll(graph, in) match {
      case Success(g, _) => g
      case NoSuccess(msg, input) => sys.error("Unable to parse input: " + msg + " at " + input.pos);
    }
  }

  def parseAsGraph(in: Reader): Graph = {
    val parser = DotAstParser
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


  // implicits to stop you gouging your own eyes out
  implicit def stringAsId(s: String): ID = ID(s)

  implicit def doubleAsId(d: Double): ID = ID.Numeral(d)

  implicit def intAsId(i: Int): ID = ID.Numeral(i.toDouble)

  implicit def longAsId(l: Long): ID = ID.Numeral(l.toDouble)

//  implicit def elementAsId(html: Elem): ID = ID.Html(html)

  implicit def idableAsNodeId[A](a: A)(implicit a2Id: A => ID): NodeId = NodeId(a2Id(a), None)

  implicit def idableAsAttributeAssignment[A](a: A)(implicit a2Id: A => ID): AttributeAssignment
  = AttributeAssignment(a2Id(a))

  implicit def idableAsNodeStatement[A](a: A)(implicit a2Id: A => ID): NodeStatement
  = NodeStatement(a2Id(a), None)

  implicit class NodeStatementSyntax[N](n: N)(implicit asNs: N => NodeStatement) {
    def :| [AL](as: AttributeAssignment*) = {
      val ns = asNs(n)
      ns.copy(attributes = Some(ns.attributes.getOrElse(AttributeList()) ++ as))
    }
  }

  implicit class NodeSyntax[N](n: N)(implicit asN: N => Node) {
    def --> [N2](n2: N2)(implicit asN2: N2 => Node) =
      EdgeStatement(node = asN(n), nodes = Seq((->, asN2(n2))))
    def --- [N2](n2: N2)(implicit asN2: N2 => Node) =
      EdgeStatement(node = asN(n), nodes = Seq((--, asN2(n2))))
  }
  
   implicit class IdSyntax[D](id: D)(implicit asId: D => ID) {
     def := [ID2](id2: ID2)(implicit asId2: ID2 => ID) = AttributeAssignment(name = asId(id), value = asId2(id2))
   }

  implicit class EdgeStatementSyntax[ES](es: ES)(implicit asES: ES => EdgeStatement) {
    def :| [AL](as: AttributeAssignment*) = {
          val e = asES(es)
          e.copy(attributes = Some(e.attributes.getOrElse(AttributeList()) ++ as))
        }
  }

  // other constructors and things
  def NonStrictGraph(id: ID, statements: Statement*): Graph =
    Graph(false, GraphType.Graph, Some(id), statements)

  def NonStrictDigraph(id: ID, statements: Statement*): Graph =
    Graph(false, GraphType.Digraph, Some(id), statements)

  def StrictGraph(id: ID, statements: Statement*): Graph =
    Graph(true, GraphType.Graph, Some(id), statements)

  def StrictDigraph(id: ID, statements: Statement*): Graph =
    Graph(true, GraphType.Digraph, Some(id), statements)
}
