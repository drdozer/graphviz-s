package uk.co.turingatemyhamster.graphvizs.client

import org.scalajs.dom.{Event, HTMLSelectElement}
import rx.core.Var

import scalatags.ext.Framework._

/**
 *
 *
 * @author Matthew Pocock
 */
case class ReactiveSelect(select: HTMLSelectElement) {
  val value = Var(select.value)

  def updateValue(e: Event) = {
    value() = select.value
  }
  select.modifyWith(Events.change := updateValue _).render
}
