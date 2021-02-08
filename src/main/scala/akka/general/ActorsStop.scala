package akka.general

import akka.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}
import akka.actor.typed.{ActorSystem, Behavior, PostStop, Signal}
/**
 * Actor lifecycle
 * -> actor is stopped => all of its children are recursively stopped
 * -> to stop an actor return: Behaviors.stopped()
 *    -- stopping child: context.stop(childRef)
 *
 */



object StartStopActor1 {
  def apply(): Behavior[String] =
    Behaviors.setup(context => new StartStopActor1(context))
}

class StartStopActor1(context: ActorContext[String]) extends AbstractBehavior[String](context) {
  println("first started")
  context.spawn(StartStopActor2(), "second")

  override def onMessage(msg: String): Behavior[String] =
    msg match {
      case "stop" => Behaviors.stopped
    }

  override def onSignal: PartialFunction[Signal, Behavior[String]] = {
    case PostStop =>
      println("first stopped")
      this
  }

}

object StartStopActor2 {
  def apply(): Behavior[String] =
    Behaviors.setup(new StartStopActor2(_))
}

class StartStopActor2(context: ActorContext[String]) extends AbstractBehavior[String](context) {
  println("second started")

  override def onMessage(msg: String): Behavior[String] = {
    // no messages handled by this actor
    Behaviors.unhandled
  }

  override def onSignal: PartialFunction[Signal, Behavior[String]] = {
    case PostStop =>
      println("second stopped")
      this
  }

}

/**
 * When we stopped actor first, it stopped its child actor, second, before stopping itself. This ordering is strict,
 * all PostStop signals of the children are processed before the PostStop signal of the parent is processed.
 */

object ActorsStopExperiment extends App {

  val first = ActorSystem(StartStopActor1(), "first")
  first ! "stop"
}

