package uk.co.turingatemyhamster.graphvizs.dsl

trait Dot {

  // data-structure types
  type T_Graph

  type T_Statement
  type T_NodeStatement <: T_Statement
  type T_EdgeStatement <: T_Statement
  type T_AttributeStatement <: T_Statement
  type T_AssignmentStatement <: T_Statement

  type T_AttributeList
  type T_AttributeAssignment

  type T_NodeId <: T_Node
  type T_Port
  type T_Subgraph <: T_Statement with T_Node

  // terminals, literals, types without handle_ methods
  type T_EdgeOp
  type T_Node
  type T_CompassPt
  type T_StatementType
  type T_GraphType
  type T_ID

}
