package uk.co.turingatemyhamster.graphvizs.exec

import java.io.{File}
import sys.process.Process


/**
 * The DOT application, and location of the binary.
 *
 * @author Matthew Pocock
 */

case class DotApp(binary: File, opts: DotOpts) {

  val process = Process(binary.getAbsolutePath, opts.generate)

}

/**
 * Dot command-line options.
 *
 * @param layout  -L layout option
 * @param format  -T format option
 *
 * @author Matthew Pocock
 */
case class DotOpts(layout: Option[DotLayout], format: Option[DotFormat]) {
  def generate = List() ++
    (layout map (l => "-K" :: l.name :: Nil)).getOrElse(Nil) ++
    (format map (f => "-T" :: f.format.mkString(":") :: Nil)).getOrElse(Nil)
}

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
}

/**
 * Dot output formats.
 *
 * @param format    the format name, renderer, formatter, e.g. "png", "png:gd"
 */
case class DotFormat(format: List[String])
object DotFormat {
  def apply(f: String): DotFormat = DotFormat(f :: Nil)

  val dot = DotFormat("dot")
  val ps = DotFormat("ps")
  val svg = DotFormat("svg")
  val svgz = DotFormat("svgz")
  val fig = DotFormat("fig")
  val mif = DotFormat("mif")
  val hpgl = DotFormat("hpgl")
  val pcl = DotFormat("pcl")
  val png = DotFormat("png")
  val gif = DotFormat("gif")
  val cmapx = DotFormat("cmapx")
}