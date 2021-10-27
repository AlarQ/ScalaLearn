package rock_the_JVM.akka_essentials.chapter_2

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import rock_the_JVM.akka_essentials.chapter_2.ChangingActorBehaviourExercises_28.CounterActor.{Decrement, Increment, Print}

object ChangingActorBehaviourExercises_28 extends App {
  object CounterActor {

    object Increment

    object Decrement

    object Print
  }

  class CounterActor extends Actor {

    override def receive: Receive = countReceive(0)

    def countReceive(value: Int): Receive = {
      case Increment => context.become(countReceive(value + 1))
      case Decrement => context.become(countReceive(value - 1))
      case Print => println(value)
    }


  }

  val system = ActorSystem("actorChangingBehaviour")
  val counterActor = system.actorOf(Props[CounterActor], "counterActor")
  counterActor ! Increment
  counterActor ! Increment
  counterActor ! Increment
  counterActor ! Decrement

  counterActor ! Print

  println("------------------------------------------------------------------")

  // simplified voting system
  class Citizen extends Actor {
    override def receive: Receive = {
      case Vote(candidate) => context.become(votedReceive(Some(candidate)))
      case VoteStatusRequest => sender() ! VoteStatusReply(None)
    }

    def votedReceive(candidate: Option[String]): Receive = {
      case VoteStatusRequest => sender() ! VoteStatusReply(candidate)
    }
  }

  case class Vote(candidate: String)

  case object VoteStatusRequest

  case class VoteStatusReply(candidate: Option[String])

  case class AggregateVotes(citizens: Set[ActorRef])

  class VoteAggregator extends Actor {

    override def receive: Receive = awaitingCommand

    def awaitingCommand: Receive = {
      case AggregateVotes(citizens) => citizens.foreach {
        _ ! VoteStatusRequest
      }
        context.become(awaitingStatuses(citizens, Map()))
    }

    def awaitingStatuses(stillWaiting: Set[ActorRef], currentStats: Map[String, Int]): Receive = {
      case VoteStatusReply(None) =>
        sender() ! VoteStatusRequest
      case VoteStatusReply(Some(candidate)) =>
        val newStillWaiting = stillWaiting - sender()
        val currentStatsOfCandidate = currentStats.getOrElse(candidate, 0)
        val newStats = currentStats + (candidate -> (currentStatsOfCandidate + 1))
        if (newStillWaiting.isEmpty) {
          println(s"Results: $newStats")
        } else {
          context.become(awaitingStatuses(newStillWaiting, newStats))
        }
    }
  }

  val alice = system.actorOf(Props[Citizen])
  val bob = system.actorOf(Props[Citizen])
  val tom = system.actorOf(Props[Citizen])

  alice ! Vote("Mark")
  bob ! Vote("Adam")
  tom ! Vote("Adam")

  val voteAggregator = system.actorOf(Props[VoteAggregator])
  voteAggregator ! AggregateVotes(Set(alice, bob, tom))
}
