package uk.co.turingatemyhamster.graphvizs.dsl

/**
 * An AST that binds the DOT constructors to concrete datatypes.
 *
 * @author Matthew Pocock
 */

trait DotAst extends DotConstructors {

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
trait DotAstBuilder extends DotAst {

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
    (id ^^ (CompassPt.or apply _))
}