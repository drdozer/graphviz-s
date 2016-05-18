package uk.co.turingatemyhamster.graphvizs.dsl

import fastparse.all

/**
 * Constructors defining the DOT data structure.
 *
 * @author Matthew Pocock
 */

trait DotConstructors extends Dot {

  // handlers

  def handle_graph(strict: Boolean, graphType: T_GraphType, id: Option[T_ID], statements: Seq[T_Statement]): T_Graph

  def handle_nodeStatement(node: T_NodeId, attributes: Option[T_AttributeList]): T_NodeStatement
  def handle_edgeStatement(node: T_Node, nodes: Seq[(T_EdgeOp, T_Node)], attributes: Option[T_AttributeList]): T_EdgeStatement
  def handle_attributeStatement(statementType: T_StatementType, attributes: T_AttributeList): T_AttributeStatement
  def handle_assignmentStatement(name: T_ID, value: T_ID): T_AssignmentStatement

  def handle_nodeId(id: T_ID, port: Option[T_Port]): T_NodeId
  def handle_port(id: Option[T_ID], compassPt: Option[T_CompassPt]): T_Port
  def handle_attributeList(attrs: Seq[T_AttributeAssignment], next: Option[T_AttributeList]): T_AttributeList
  def handle_attributeAssignment(name: T_ID, value: Option[T_ID]): T_AttributeAssignment
  def handle_subgraph(id: Option[T_ID], statements: Seq[T_Statement]): T_Subgraph
}

/**
 * P for the DOT file format.
 *
 * @author Matthew Pocock
 */
trait DotParser extends DotConstructors {

  import fastparse.all._

  // symbol literals
  val l_bracket: P0 = "{"
  val r_bracket: P0 = "}"
  val l_box: P0 = "["
  val r_box: P0 = "]"
  val colon: P0 = ":"
  val semi_colon: P0 = ";"
  val comma: P0 = ","
  val period: P0 = "."
  val equ: P0 = "="
  val hash: P0 = "#"
  val notNewline: P0 = !("\n")
  val dblQuote: P0 = "\""

  val directed_edge: P0 = "->"
  val undirected_edge: P0 = "--"
  
  // keywords
  val STRICT = IgnoreCase("strict")
  val GRAPH = IgnoreCase("graph")
  val DIGRAPH = IgnoreCase("digraph")
  val NODE = IgnoreCase("node")
  val EDGE = IgnoreCase("edge")
  val SUBGRAPH = IgnoreCase("subgraph")

  val whiteSpace: P0 = (spaces_? ~ lineComment ~ spaces).rep(1)
  val spaces = space.rep(1)
  val spaces_? = space.rep
  val lineComment = hash ~ notNewline.rep
  val space = CharIn(Array('\n', '\r', ' '))


  // identifier types
  val digit: P0 = P(CharIn('0' to '9'))
  val identifier: P[String] = P(CharIn('a' to 'Z', "_") ~  P(CharIn('a' to 'Z', "_", '0' to '9')).rep).!
  val numeral: P[String] = P(CharIn("-+").? ~ ((period ~ digit.rep(1))) | (digit.rep(1) ~ (period ~ digit.rep).?)).!
  val dblquoted: P[String] = P(dblQuote ~ (!dblQuote).rep.! ~ dblQuote)

  // compass points
  val n = IgnoreCase("n")
  val ne = IgnoreCase("ne")
  val e = IgnoreCase("e")
  val se = IgnoreCase("se")
  val s = IgnoreCase("s")
  val sw = IgnoreCase("sw")
  val w = IgnoreCase("w")
  val nw = IgnoreCase("nw")
  val c = IgnoreCase("c")

  // productions
  lazy val graph: P[T_Graph] = P(STRICT.!.? ~ graph_type ~ id.? ~ l_bracket ~ statement_list ~ r_bracket) map {
    case (str, gt, id, stmts) => handle_graph(str.isDefined, gt, id, stmts)
  }

  lazy val statement_list: P[Seq[T_Statement]] = P((statement ~ semi_colon.?).rep)

  lazy val statement: P[T_Statement]
  = assignment_statement |
    attribute_statement |
    subgraph |
    edge_statement |
    node_statement

  lazy val attribute_statement: P[T_AttributeStatement] = P(statement_type ~ attribute_list) map {
    case (st, al) => handle_attributeStatement(st, al)
  }

  lazy val attribute_list: P[T_AttributeList] = P(l_box ~ attributes ~ r_box ~ attribute_list.?) map {
    case (al1, nxt) => handle_attributeList(al1, nxt) }

  lazy val attributes: P[Seq[T_AttributeAssignment]] = attribute_assignment.rep(sep = comma)

  lazy val attribute_assignment: P[T_AttributeAssignment] = id ~ (equ ~ id).? map {
    case (name, value) => handle_attributeAssignment(name, value)
  }

  lazy val edge_statement: P[T_EdgeStatement] = node ~ edge_rhs ~ attribute_list.? map
    { case (n, ns, as) => handle_edgeStatement(n, ns, as) }

  lazy val edge_rhs: P[Seq[(T_EdgeOp, T_Node)]] = ((edge_op ~ node) map { case (eo, n) => (eo, n) }).rep(1)

  lazy val node_statement: P[T_NodeStatement] = node_id ~ attribute_list.? map { case (id, as) => handle_nodeStatement(id, as) }

  lazy val node_id: P[T_NodeId] = id ~ port.? map { case (id, port) => handle_nodeId(id, port) }

  lazy val node: P[T_Node] = P(node_id | subgraph)

  lazy val port: P[T_Port] =
    (colon ~ id  ~ ( colon ~ compass_pt).? map { case (id, cp) => handle_port(Some(id), cp) }) |
    (colon ~ compass_pt map { case cp => handle_port(None, Some(cp)) } )

  lazy val subgraph: P[T_Subgraph] = P(SUBGRAPH ~ id.? ~ l_bracket ~ statement_list ~ r_bracket) map
    { case (id, ss) => handle_subgraph(id, ss) }

  lazy val assignment_statement: P[T_AssignmentStatement] = P(id ~ equ ~ id) map
    { case (name, value) => handle_assignmentStatement(name, value) }

  def id: P[T_ID] // (identifier | numeral | quoted_string | html)
  def statement_type: P[T_StatementType] // (GRAPH | NODE | EDGE)
  def graph_type: P[T_GraphType] // (GRAPH | DIGRAPH)
  def edge_op: P[T_EdgeOp] // (directed_edge | undirected_edge)
  def compass_pt: P[T_CompassPt] // (n | ne | e | se | s | sw | w | nw | id)

  private def noneOnEmpty[T](ts: Seq[T]): Option[Seq[T]] = if(ts.isEmpty) None else Some(ts)

}