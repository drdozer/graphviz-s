package uk.co.turingatemyhamster.graphvizs.dsl

/**
 * The Graph datatype.
 *
 * @author Matthew Pocock
 */

case class Graph(strict: Boolean, graphType: GraphType, id: Option[ID], statements: Seq[Statement])

sealed trait GraphType
object GraphType {
  case object Graph extends GraphType
  case object Digraph extends GraphType
}


sealed trait Statement

case class NodeStatement(node: NodeId, attributes: Option[AttributeList]) extends Statement

case object NodeStatement {
  def apply(node: NodeId): NodeStatement = NodeStatement(node, None)
  def apply(node: NodeId, attrs: AttributeList): NodeStatement = NodeStatement(node, Some(attrs))
}

case class EdgeStatement(node: Node, nodes: Seq[(EdgeOp, Node)], attributes: Option[AttributeList]) extends Statement

sealed trait EdgeOp
object EdgeOp {
  case object -> extends EdgeOp
  case object -- extends EdgeOp
}


sealed trait Node

case class NodeId(id: ID,  port: Option[Port]) extends Node

case class Port(id: Option[ID], compassPt: Option[CompassPt]) // can't both be None

sealed trait CompassPt
object CompassPt {
  case object N extends CompassPt
  case object NE extends CompassPt
  case object E extends CompassPt
  case object SE extends CompassPt
  case object S extends CompassPt
  case object SW extends CompassPt
  case object W extends CompassPt
  case object NW extends CompassPt
  case object C extends CompassPt
  case class or(id: ID) extends CompassPt
}

case class AttributeStatement(statementType: StatementType, attributeList: AttributeList) extends Statement

case class AttributeList(attrs1: Option[Seq[AttributeAssignment]], attrs2: Option[Seq[AttributeAssignment]])

object AttributeList {
  def apply(): AttributeList = AttributeList(None, None)
  def apply(attrs1: Option[Seq[AttributeAssignment]]): AttributeList = AttributeList(attrs1, None)
  def apply(as: AttributeAssignment*): AttributeList = AttributeList(Some(as), None)
}

sealed trait StatementType
object StatementType {
  case object Graph extends StatementType
  case object Node extends StatementType
  case object Edge extends StatementType
}

case class AttributeAssignment(name: ID, value: Option[ID]) // value=None ~> value=Some(true)

object AttributeAssignment {
  def apply(name: ID): AttributeAssignment = AttributeAssignment(name, None)
  def apply(name: ID, value: ID): AttributeAssignment = AttributeAssignment(name, Some(value))
}

case class AssignmentStatement(name: ID,  value: ID) extends Statement

case class Subgraph(id: Option[ID], statements: Seq[Statement]) extends Statement with Node

object Subgraph {
//  def apply(id: Option[ID], statements: Seq[Statement])
  def apply(name: ID): Subgraph = Subgraph(Some(name), Seq())
  def apply(aa: AssignmentStatement*): Subgraph = Subgraph(None, aa)
}


sealed trait ID
object ID {
  case class Identifier(value: String) extends ID // Any string of alphabetic ([a-zA-Z'200-'377]) characters, underscores ('_') or digits ([0-9]), not beginning with a digit
  case class Numeral(value: Double) extends ID // a numeral [-]?(.[0-9]+ | [0-9]+(.[0-9]*)? );
  case class Quoted(value: String) extends ID // any double-quoted string ("...") possibly containing escaped quotes ('")
  case class Html(value: String) extends ID // an HTML string (<...>)
}