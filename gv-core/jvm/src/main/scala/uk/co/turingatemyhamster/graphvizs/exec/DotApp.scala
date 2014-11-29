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
case class DotOpts(layout: Option[DotLayout], format: Option[DotFormat], outFile: Option[File] = None) {
  def generate = List() ++
    (layout map (l => "-K" :: l.name :: Nil)).getOrElse(Nil) ++
    (format map (f => "-T" :: f.format.mkString(":") :: Nil)).getOrElse(Nil) ++
    (outFile map (f => "-o" :: f.getAbsolutePath :: Nil)).getOrElse(Nil)
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

}

sealed trait StringFormat[F <: DotFormat]

object StringFormat {
  implicit object dotIsStringFormat extends StringFormat[DotFormat.dot.type]

}