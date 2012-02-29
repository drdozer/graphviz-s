package uk.co.turingatemyhamster.graphvizs

import java.io.Reader

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

  def parseAsGraph(in: Reader): Graph = {
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


  // implicits to stop you gouging your own eyes out
  implicit def stringAsId(s: String): ID = ID(s)

  implicit def doubleAsId(d: Double): ID = ID.Numeral(d)

  implicit def intAsId(i: Int): ID = ID.Numeral(i.toDouble)

  implicit def longAsId(l: Long): ID = ID.Numeral(l.toDouble)

  implicit def idableAsNodeId[A](a: A)(implicit a2Id: A => ID): NodeId = NodeId(a2Id(a), None)

  implicit def idableAsAttributeAssignment[A](a: A)(implicit a2Id: A => ID): AttributeAssignment
  = AttributeAssignment(a2Id(a))

  implicit def idableAsNodeStatement[A](a: A)(implicit a2Id: A => ID): NodeStatement
  = NodeStatement(a2Id(a), None)

  implicit def idablePairAsAttributeAssignment[A, B](ab: (A, B))(implicit a2Id: A => ID, b2Id: B => ID): AttributeAssignment
  = AttributeAssignment(a2Id(ab._1), b2Id(ab._2))

  implicit def idablePairAsAssignmentStatement[A, B](ab: (A, B))(implicit a2Id: A => ID, b2Id: B => ID): AssignmentStatement
  = AssignmentStatement(a2Id(ab._1), b2Id(ab._2))
  
  implicit def assignmentsAsAttributeList[AA](ass: Seq[AA])(implicit f: AA => AttributeAssignment): AttributeList =
    AttributeList((ass map f) : _*)

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