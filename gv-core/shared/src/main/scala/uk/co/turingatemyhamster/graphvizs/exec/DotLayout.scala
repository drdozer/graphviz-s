package uk.co.turingatemyhamster.graphvizs.exec


/**
 * Dot layout types.
 *
 * @author Matthew Pocock
 */
case class DotLayout(name: String)
object DotLayout {
  val dot = DotLayout("dot")
  val neato = DotLayout("neato")
  val twopi = DotLayout("twopi")
  val circo = DotLayout("circo")
  val fdp   = DotLayout("fdp")
  val sfdp  = DotLayout("sfdp")
  val patchwork  = DotLayout("patchwork")

  val allLayouts = Seq(
    dot, neato, twopi, circo, fdp, sfdp, patchwork
  )
}
