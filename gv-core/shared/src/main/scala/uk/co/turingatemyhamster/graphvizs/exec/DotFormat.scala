package uk.co.turingatemyhamster.graphvizs.exec

/**
 * Dot output formats.
 *
 * @param format    the format name, renderer, formatter, e.g. "png", "png:gd"
 */
case class DotFormat(format: List[String]) {
  def this(f: String) = this(f :: Nil)
}

object DotFormat {
  object dot    extends DotFormat("dot")
  object ps     extends DotFormat("ps")
  object svg    extends DotFormat("svg")
  object svgz   extends DotFormat("svgz")
  object fig    extends DotFormat("fig")
  object mif    extends DotFormat("mif")
  object hpgl   extends DotFormat("hpgl")
  object pcl    extends DotFormat("pcl")
  object png    extends DotFormat("png")
  object gif    extends DotFormat("gif")
  object cmapx  extends DotFormat("cmapx")

  lazy val allFormats = Seq(
    dot, ps, svg, svgz, fig, mif, hpgl, pcl, png, gif, cmapx
  )
}
