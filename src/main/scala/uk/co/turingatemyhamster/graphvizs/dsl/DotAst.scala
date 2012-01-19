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

}

/**
 * Builder that knows how to make an AST representation.
 *
 * @author Matthew Pocock
 */
trait DotAstBuilder extends DotAst {

  type Dot[T] = T

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
