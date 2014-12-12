package uk.co.turingatemyhamster.graphvizs.dsl

import scala.util.parsing.combinator.RegexParsers

/**
 * Arrow types.
 *
 * @param names  list of arrow names, maximum of 4
 * @author Matthew Pocock
 */
case class ArrowType(names: List[ArrowName]) {
  def name = names.map(_.name).mkString
}

/**
 * Arrow names, composed of modifier options and a shape.
 *
 * @param modifiers   modifiers to the shape
 * @param shape       the basic shape
 * @author Matthew Pocock
 */
case class ArrowName(modifiers: Modifiers, shape: Shape) {
  def name = modifiers.name + shape.name
}

/**
 * Arrow name utilities.
 *
 * @author Matthew Pocock
 */
object ArrowName {
  /**
   * Create a name for `shape` with no modifiers.
   *
   * @param shape   the base shape
   * @return        an `ArrowName` of that shape
   */
  def apply(shape: Shape): ArrowName = ArrowName(Modifiers(), shape)

  /**
   * Implicit conversion from a `Shape` to an `ArrowName`.
   *
   * @param shape   the base shape
   * @return        an `ArrowName` of that shape
   */
  implicit def shapeToArrowName(shape: Shape): ArrowName = apply(shape)
}

/**
 * Arrow shape modifiers.
 *
 * If both modifiers are `None` then the shape is used as-is.
 *
 * @param o   optionally, the shape should be hollow
 * @param lr  optionally, the shape should be clipped to use only the left or right half
 * @author Matthew Pocock
 */
case class Modifiers(o: Option[O.type], lr: Option[LR]) {
  def name = o.map(_.name).getOrElse("") + lr.map(_.name).getOrElse("")
}

/**
 * Modifiers utilities.
 *
 * @author Matthew Pocock
 */
object Modifiers {
  /**
   * Make a new `Modifiers` that leaves the shape unchanged.
   *
   * @return  the null modifier
   */
  def apply(): Modifiers = Modifiers(None, None)

  /**
   * Make a new `Modifiers` that applies the two modifications.
   *
   * @param o   the shape should be hollow
   * @param lr  the shape should be clipped to the left or right
   * @return
   */
  def apply(o: O.type, lr: LR): Modifiers = Modifiers(Some(o), Some(lr))

  /**
   * Implicit conversion from the `O` object to the equivalent `Modifiers` instance.
   *
   * @param o   the `O` object
   * @return    a `Modifiers` that flags the shape as hollow
   */
  implicit def promoteO(o: O.type): Modifiers = Modifiers(Some(O), None)

  /**
   * Implicit conversion from the left/right flag to the equivalent `Modifiers` instance.
   *
   * @param lr  `L` or `R`
   * @return    a `Modifiers` that applies this to the shape
   */
  implicit def promoteLR(lr: LR): Modifiers = Modifiers(None, Some(lr))
}

/**
 * The open modifier flag.
 *
 * @author Matthew Pocock
 */
case object O { def name = "o" }

/**
 * The left/right flags.
 *
 * @author Matthew Pocock
 */
sealed trait LR {
  def name: String
}

/**
 * The left flag, indicating that only the left half of an arrow should be displayed and the right half clipped.
 *
 * @author Matthew Pocock
 */
case object L extends LR { def name = "l" }

/**
 * The right flag, indicating that only the right half of an arrow should be displayed and the left half clipped.
 *
 * @author Matthew Pocock
 */
case object R extends LR { def name = "r" }


/**
 * An arrowhead shape.
 *
 * @author Matthew Pocock
 */
sealed trait Shape {
  def name: String
}

/**
 * Shape constants.
 *
 * @author Matthew Pocock
 */
object Shape {
  case object Box     extends Shape { def name = "box" }
  case object Crow    extends Shape { def name = "crow" }
  case object Diamond extends Shape { def name = "diamond" }
  case object Dot     extends Shape { def name = "dot" }
  case object Inv     extends Shape { def name = "inv" }
  case object None    extends Shape { def name = "none" }
  case object Normal  extends Shape { def name = "normal" }
  case object Tee     extends Shape { def name = "tee" }
  case object Vee     extends Shape { def name = "vee" }
}


/**
 * Parser from DOT arrow type strings to the arrow data model.
 *
 * @author Matthew Pocock
 */
object ArrowTypeParsers extends RegexParsers {
  
  val shape: Parser[Shape]
  = ("box"      ^^^ Shape.Box) |
    ("crow"     ^^^ Shape.Crow) |
    ("diamond"  ^^^ Shape.Diamond) |
    ("dot"      ^^^ Shape.Dot) |
    ("inv"      ^^^ Shape.Inv) |
    ("none"     ^^^ Shape.None) |
    ("normal"   ^^^ Shape.Normal) |
    ("tee"      ^^^ Shape.Tee) |
    ("vee"      ^^^ Shape.Vee)
  
  val lr: Parser[LR] = ("l" ^^^ L) | ("r" ^^^ R)

  val o: Parser[O.type] = "o" ^^^ O

  val modifiers: Parser[Modifiers] = o.? ~ lr.? ^^ { case o~lr => Modifiers(o, lr) }

  val arrowName: Parser[ArrowName] = modifiers ~ shape ^^ { case m~s => ArrowName(m, s) }

  val arrowType: Parser[ArrowType] = arrowName.* ^^ ArrowType
}