package uk.co.turingatemyhamster.graphvizs.exec

import java.io.{File}
import sys.process.Process


/**
 * The DOT application, and location of the binary.
 *
 * @author Matthew Pocock
 */

case class DotApp(binary: File, opts: DotOpts) {

  val process = {
    System.out.println(s"Executing ${binary.getAbsolutePath} with options ${opts.generate}")
    Process(binary.getAbsolutePath, opts.generate)
  }

}

/**
 * Dot command-line options.
 *
 * @param layout  -L layout option
 * @param format  -T format option
 *
 * @author Matthew Pocock
 */
case class DotOpts(layout: Option[DotLayout] = None, format: Option[DotFormat] = None, outFile: Option[File] = None, optionalS: Seq[String] = Seq()) {
  def generate = List() ++
    (layout map (l => "-K" :: l.name :: Nil)).getOrElse(Nil) ++
    (format map (f => "-T" :: f.format.mkString(":") :: Nil)).getOrElse(Nil) ++
    (outFile map (f => "-o" :: f.getAbsolutePath :: Nil)).getOrElse(Nil) ++
    optionalS
}


sealed trait StringFormat[F <: DotFormat]

object StringFormat {
  implicit object dotIsStringFormat extends StringFormat[DotFormat.dot.type]

}