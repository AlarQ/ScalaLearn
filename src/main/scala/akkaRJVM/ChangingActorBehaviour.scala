package akkaRJVM

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akkaRJVM.ChangingActorBehaviour.FussyKid.{HAPPY, KidAccept, KidReject, SAD}
import akkaRJVM.ChangingActorBehaviour.Mom.{Ask, CHOCOLATE, Food, MomStart, VEGETABLE}


object ChangingActorBehaviour extends App {
  class FussyKid extends Actor {
    // internal state of the kid
    // mutable :(
    var state = HAPPY

    override def receive: Receive = {
      case Food(VEGETABLE) => state = SAD
      case Food(CHOCOLATE) => state = HAPPY
      case Ask(_) =>
        // cod can grow up fast, bad way
        if (state == HAPPY) sender() ! KidAccept else sender() ! KidReject
    }
  }

  object FussyKid {

    case object KidAccept

    case object KidReject

    val HAPPY = "happy"
    val SAD = "sad"
  }

  class Mom extends Actor {
    override def receive: Receive = {
      case MomStart(kidRef) => kidRef ! Food(VEGETABLE)
        kidRef ! Ask("Do you want to play?")
      case KidAccept => println("My kid is happy")
      case KidReject => println("My kid is sad")
    }
  }

  object Mom {
    case class MomStart(kidRef: ActorRef)

    case class Food(food: String)

    case class Ask(message: String)

    val VEGETABLE = "veggies"
    val CHOCOLATE = "chocolate"
  }

  val system = ActorSystem("changeActorBehaviour")
  val fussyKid = system.actorOf(Props[FussyKid])
  val mom = system.actorOf(Props[Mom])

  mom ! MomStart(fussyKid)

  class StatelessFussyKid extends Actor {
    override def receive: Receive = happyReceive

    def happyReceive: Receive = {
      // swapping handler
      case Food(VEGETABLE) => context.become(sadReceive,false)
      case Food(CHOCOLATE) =>
      case Ask(_) => sender() ! KidAccept
    }

    def sadReceive: Receive = {
      case Food(VEGETABLE) =>
      case Food(CHOCOLATE) => context.become(happyReceive,false)
      case Ask(_) => sender() ! KidReject
    }
  }
  // Food(veg), Food(choco)
  // we can add discardOld = false parameter to context.become method, then:
  // message handlers will be put on stack and can be reversed using context.unbecome method
  // MessageStack:
  // 1. happyReceive
  // 2. sadReceive
  // 3. happyReceive
  val statelessFussyKid = system.actorOf(Props[StatelessFussyKid])

  mom ! MomStart(statelessFussyKid)
}
