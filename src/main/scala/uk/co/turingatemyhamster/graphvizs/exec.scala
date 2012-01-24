package uk.co.turingatemyhamster.graphvizs

import io.Source
import java.io._
import sys.process.{ProcessIO, Process}

/**
 * Dot application execution.
 * 
 * @author Matthew Pocock
 */

package object exec extends Exec {

  /** The platform-, system-dependendent location of the dot binary. */

  var dotBinary: File = new File("dot")

}
