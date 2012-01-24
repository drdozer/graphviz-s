package uk.co.turingatemyhamster.graphvizs.dsl

/**
 * An AST that binds the DOT constructors to concrete datatypes.
 *
 * @author Matthew Pocock
 */

trait DotAst extends Dot {

  // bind concrete types
  type T_Graph = Graph

  type T_Statement = Statement
  type T_NodeStatement = NodeStatement
  type T_EdgeStatement = EdgeStatement
  type T_AttributeStatement = AttributeStatement
  type T_AssignmentStatement = AssignmentStatement

  type T_AttributeList = AttributeList
  type T_AttributeAssignment = AttributeAssignment

  type T_NodeId = NodeId
  type T_Port = Port
  type T_Subgraph = Subgraph

  type T_EdgeOp = EdgeOp
  type T_Node = Node
  type T_CompassPt = CompassPt
  type T_StatementType = StatementType
  type T_GraphType = GraphType
  type T_ID = ID

}

trait DotAstConstructors extends DotAst with DotConstructors {

  // implicits to stop you gouging your own eyes out
  implicit def stringAsId(s: String): ID = ID.Identifier(s)
  
  implicit def doubleAsId(d: Double): ID = ID.Numeral(d)

  implicit def intAsId(i: Int): ID = ID.Numeral(i.toDouble)
  
  implicit def idAsNodeId(id: ID): NodeId = NodeId(id, None)

  implicit def idableAsNodeId[A](a: A)(implicit a2Id: A => ID): NodeId = idAsNodeId(a2Id(a))

  implicit def idableAsAttributeAssignment[A](a: A)(implicit a2Id: A => ID): AttributeAssignment
  = AttributeAssignment(a2Id(a))

  implicit def idableAsNodeStatement[A](a: A)(implicit a2Id: A => ID): NodeStatement
  = NodeStatement(a2Id(a), None)

  implicit def idablePairAsAttributeAssignment[A, B](ab: (A, B))(implicit a2Id: A => ID, b2Id: B => ID): AttributeAssignment
  = AttributeAssignment(a2Id(ab._1), b2Id(ab._2))

  implicit def idablePairAsAssignmentStatement[A, B](ab: (A, B))(implicit a2Id: A => ID, b2Id: B => ID): AssignmentStatement
  = AssignmentStatement(a2Id(ab._1), b2Id(ab._2))

}

/**
 * Builder that knows how to make an AST representation.
 *
 * @author Matthew Pocock
 */
trait DotAstBuilder extends DotAstConstructors {

  def handle_graph(strict: Boolean, graphType: GraphType, id: Option[ID], statements: Seq[Statement]) =
    Graph(strict, graphType, id, statements)

  def handle_nodeStatement(node: NodeId, attributes: Option[AttributeList]) =
    NodeStatement(node, attributes)

  def handle_edgeStatement(node: Node, nodes: Seq[(EdgeOp, Node)], attributes: Option[AttributeList]) =
    EdgeStatement(node, nodes, attributes)

  def handle_attributeStatement(statementType: StatementType, attributes: AttributeList) =
    AttributeStatement(statementType, attributes)

  def handle_assignmentStatement(name: ID, value: ID) =
    AssignmentStatement(name, value)

  def handle_nodeId(id: ID, port: Option[Port]) =
    NodeId(id, port)

  def handle_port(id: Option[ID], compassPt: Option[CompassPt]) =
    Port(id, compassPt)

  def handle_attributeList(attrs1: Option[Seq[AttributeAssignment]], attrs2: Option[Seq[AttributeAssignment]]) =
    AttributeList(attrs1, attrs2)

  def handle_attributeAssignment(name: ID, value: Option[ID]) =
    AttributeAssignment(name, value)

  def handle_subgraph(id: Option[ID], statements: Seq[Statement]) =
    Subgraph(id, statements)

}

class DotAstParser extends DotAstBuilder with DotParser {

  lazy val id: Parser[ID]
  = id_identifier | id_numeral | id_quoted_string


  lazy val id_identifier: Parser[ID.Identifier]
  = identifier ^^ { x => ID.Identifier(x) }

  lazy val id_numeral: Parser[ID.Numeral]
  = numeral ^^ { x => ID.Numeral(x.toDouble) }

  lazy val id_quoted_string: Parser[ID.Quoted]
  = dblquoted ^^ { x => ID.Quoted(x) }



  lazy val statement_type: Parser[StatementType]
  = (GRAPH ^^^ StatementType.Graph) |
    (NODE ^^^ StatementType.Node) |
    (EDGE ^^^ StatementType.Edge)


  lazy val graph_type: Parser[GraphType]
  = (GRAPH ^^^ GraphType.Graph) |
    (DIGRAPH ^^^ GraphType.Digraph)


  lazy val edge_op: Parser[EdgeOp]
  = (directed_edge ^^^ EdgeOp.->) |
    (undirected_edge ^^^ EdgeOp.--)


  lazy val compass_pt: Parser[T_CompassPt]
  = (n  ^^^ CompassPt.N)  |
    (ne ^^^ CompassPt.NE) |
    (e  ^^^ CompassPt.E)  |
    (se ^^^ CompassPt.SE) |
    (s  ^^^ CompassPt.S)  |
    (sw ^^^ CompassPt.SW) |
    (w  ^^^ CompassPt.W)  |
    (nw ^^^ CompassPt.NW) |
    (c  ^^^ CompassPt.C)  |
    (id ^^ (CompassPt.or apply _))
}

class DotAstDestructors extends DotAst with DotDestructors {
  def decompose_graph(g: Graph) = (g.strict, g.graphType, g.id, g.statements)

  def decompose_nodeStatement(ns: NodeStatement) = (ns.node, ns.attributes)

  def decompose_edgeStatement(es: EdgeStatement) = (es.node, es.nodes, es.attributes)

  def decompose_attributeStatement(as: AttributeStatement) = (as.statementType, as.attributeList)

  def decompose_assignmentStatement(as: AssignmentStatement) = (as.name, as.value)

  def decompose_nodeId(nid: NodeId) = (nid.id, nid.port)

  def decompose_port(p: Port) = (p.id, p.compassPt)

  def decompose_attributeList(al: AttributeList) = (al.attrs1, al.attrs2)

  def decompose_attributeAssignment(as: AttributeAssignment) = (as.name, as.value)

  def decompose_subgraph(sg: Subgraph) = (sg.id, sg.statements)
}

class DotAstRenderer(val out: Appendable) extends DotAstDestructors with DotRenderer {
  
  private final val qt = "\""

  def render_graphType(gt: GraphType) = out append (gt match {
    case GraphType.Graph => "graph"
    case GraphType.Digraph => "digraph"
  })

  def render_id(id: ID) = out append (id match {
    case ID.Identifier(s) => s
    case ID.Numeral(n) => n.toString
    case ID.Quoted(s) => qt + s.toString + qt
    case ID.Html(s) => s
  })

  def render_statement(st: Statement) = st match {
    case ns : NodeStatement => render_nodeStatement(ns)
    case es : EdgeStatement => render_edgeStatement(es)
    case as : AttributeStatement => render_attributeStatement(as)
    case as : AssignmentStatement => render_assignmentStatement(as)
    case sg : Subgraph => render_subgraph(sg)
  }

  def render_node(n: Node) = n match {
    case nid : NodeId => render_nodeId(nid)
    case sg : Subgraph => render_subgraph(sg)
  }

  def render_edgeOp(eo: EdgeOp) = out append (eo match {
    case EdgeOp.-> => "->"
    case EdgeOp.-- => "--"
  })

  def render_statementType(st: StatementType) = out append (st match {
    case StatementType.Graph => "graph"
    case StatementType.Node => "node"
    case StatementType.Edge => "edge"
  })

  def render_compass_pt(cp: CompassPt) = cp match {
    case CompassPt.N => out append "n"
    case CompassPt.NE => out append "ne"
    case CompassPt.E => out append "e"
    case CompassPt.SE => out append "se"
    case CompassPt.S => out append "s"
    case CompassPt.SW => out append "sw"
    case CompassPt.W => out append "w"
    case CompassPt.NW => out append "nw"
    case CompassPt.C => out append "c"
    case CompassPt.or(id) => render_id(id)
  }
}