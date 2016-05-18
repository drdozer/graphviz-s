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

trait DotAstConstructors extends DotAst with DotConstructors

/**
 * Builder that knows how to make an AST representation.
 *
 * @author Matthew Pocock
 */
trait DotAstBuilder extends DotAstConstructors {

  override def handle_graph(strict: Boolean, graphType: GraphType, id: Option[ID], statements: Seq[Statement]) =
    Graph(strict, graphType, id, statements)

  override def handle_nodeStatement(node: NodeId, attributes: Option[AttributeList]) =
    NodeStatement(node, attributes)

  override def handle_edgeStatement(node: Node, nodes: Seq[(EdgeOp, Node)], attributes: Option[AttributeList]) =
    EdgeStatement(node, nodes, attributes)

  override def handle_attributeStatement(statementType: StatementType, attributes: AttributeList) =
    AttributeStatement(statementType, attributes)

  override def handle_assignmentStatement(name: ID, value: ID) =
    AssignmentStatement(name, value)

  override def handle_nodeId(id: ID, port: Option[Port]) =
    NodeId(id, port)

  override def handle_port(id: Option[ID], compassPt: Option[CompassPt]) =
    Port(id, compassPt)

  override def handle_attributeList(attrs: Seq[AttributeAssignment], next: Option[AttributeList]) =
    AttributeList(attrs, next)

  override def handle_attributeAssignment(name: ID, value: Option[ID]) =
    AttributeAssignment(name, value)

  override def handle_subgraph(id: Option[ID], statements: Seq[Statement]) =
    Subgraph(id, statements)

}

object DotAstParser extends DotAstBuilder with DotParser {

  import fastparse.all._

  lazy val id: P[ID]
  = P(id_identifier | id_numeral | id_quoted_string)


  lazy val id_identifier: P[ID.Identifier]
  = identifier map { x => ID.Identifier(x) }

  lazy val id_numeral: P[ID.Numeral]
  = numeral.! map { x => ID.Numeral(x.toDouble) }

  lazy val id_quoted_string: P[ID.Quoted]
  = dblquoted map { x => ID.Quoted(x) }



  lazy val statement_type: P[StatementType]
  = (GRAPH map (_ => StatementType.Graph)) |
    (NODE map (_ => StatementType.Node)) |
    (EDGE map (_ => StatementType.Edge))


  lazy val graph_type: Parser[GraphType]
  = (GRAPH map (_ => GraphType.Graph)) |
    (DIGRAPH map (_ => GraphType.Digraph))


  lazy val edge_op: Parser[EdgeOp]
  = (directed_edge map (_ => EdgeOp.->)) |
    (undirected_edge map (_ => EdgeOp.--))


  lazy val compass_pt: Parser[T_CompassPt]
  = (n  map (_ => CompassPt.N))  |
    (ne map (_ => CompassPt.NE)) |
    (e  map (_ => CompassPt.E))  |
    (se map (_ => CompassPt.SE)) |
    (s  map (_ => CompassPt.S))  |
    (sw map (_ => CompassPt.SW)) |
    (w  map (_ => CompassPt.W))  |
    (nw map (_ => CompassPt.NW)) |
    (c  map (_ => CompassPt.C))  |
    (id map CompassPt.or.apply)
}

class DotAstDestructors extends DotAst with DotDestructors {
  def decompose_graph(g: Graph) = (g.strict, g.graphType, g.id, g.statements)

  def decompose_nodeStatement(ns: NodeStatement) = (ns.node, ns.attributes)

  def decompose_edgeStatement(es: EdgeStatement) = (es.node, es.nodes, es.attributes)

  def decompose_attributeStatement(as: AttributeStatement) = (as.statementType, as.attributeList)

  def decompose_assignmentStatement(as: AssignmentStatement) = (as.name, as.value)

  def decompose_nodeId(nid: NodeId) = (nid.id, nid.port)

  def decompose_port(p: Port) = (p.id, p.compassPt)

  def decompose_attributeList(al: AttributeList) = (al.attrs, al.next)

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
//    case ID.Html(elem) => elem.toString
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