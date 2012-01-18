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
  case object Undefined extends CompassPt
}

case class AttributeStatement(statementType: StatementType, attributes1: Option[AttributeList], attributes2: Option[AttributeList]) extends Statement

sealed trait StatementType
object StatementType {
  case object Graph extends StatementType
  case object Node extends StatementType
  case object Edge extends StatementType
}

case class AttributeAssignment(name: ID, value: Option[ID]) // value=None ~> value=Some(true)

case class AssignmentStatement(name: ID,  value: ID) extends Statement
case class Subgraph(id: Option[ID], statements: Seq[Statement]) extends Statement with Node


sealed trait ID
object ID {
  case class Identifier(value: String) // Any string of alphabetic ([a-zA-Z'200-'377]) characters, underscores ('_') or digits ([0-9]), not beginning with a digit
  case class Numeral(value: Int) // a numeral [-]?(.[0-9]+ | [0-9]+(.[0-9]*)? );
  case class Quoted(value: String) // any double-quoted string ("...") possibly containing escaped quotes ('")
  case class Html(value: String) // an HTML string (<...>)
}