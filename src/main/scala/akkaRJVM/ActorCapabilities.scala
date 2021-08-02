package akkaRJVM

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akkaRJVM.ActorCapabilities.CounterActor.{Decrement, Increment, Print}
;object ActorCapabilities extends App {
  class SimpleActor extends Actor {
    override def receive: Receive = {
      case "Hi" => context.sender() ! "hello there!!!!!"
      case message: String => println(s"[$self] I have received a message: $message")
      // messages can be of any type
      case number: Int => println(s"[simple actor] I received a number: $number")
      case specialMessage: SpecialMessage => println(s"[simple actor] I received a special message with contents: ${specialMessage.contents}")
      case SendMessageToYourself(content) =>
        self ! content
      case SayHiTo(ref) => ref ! "Hi"
      case WirelessPhoneMessage(content, ref) => ref forward content + "s"
    }
  }

  val system = ActorSystem("actorCapabilitiesDemo")
  val actor = system.actorOf(Props[SimpleActor])
  actor ! "Hello world"
  actor ! 12

  // 1- messages can have any type
  // messages must be IMMUTABLE
  // messages must be SERIALIZABLE
  // in practise use case classes, case objects
  case class SpecialMessage(contents: String)

  actor ! SpecialMessage("bla bla")

  // 2 - actors have information about their context and about themselves
  // context.self === this in OOP
  case class SendMessageToYourself(content: String)

  actor ! SendMessageToYourself("I am an actor")
  // 3 - actors can reply to messages
  val alice = system.actorOf(Props[SimpleActor], "alice")
  val bob = system.actorOf(Props[SimpleActor], "bob")

  case class SayHiTo(ref: ActorRef)

  alice ! SayHiTo(bob)

  // 4  - if there is no sender, like here, te reply goes to dead letters, sure?
  alice ! "Hi"

  // 5 - forwardi ng messages
  case class WirelessPhoneMessage(content: String, ref: ActorRef)

  alice ! WirelessPhoneMessage("Hi", bob)

  // ------------------------------------------------- EXERCISE -------------------------------------------------
  println("------------------------------------------------- EXERCISES -------------------------------------------------")
  Thread.sleep(1000)

  // EX. 1

  object CounterActor {

    object Increment

    object Decrement

    object Print
  }

  class CounterActor extends Actor {
    var counter = 0

    override def receive: Receive = {
      case Increment => counter += 1
      case Decrement => counter -= 1
      case Print => println(s"counter value: $counter")
    }
  }

  val counterActor = system.actorOf(Props[CounterActor], "counterActor")
  counterActor ! Increment
  counterActor ! Increment
  counterActor ! Increment
  counterActor ! Decrement
  counterActor ! Print

  // EX. 2
  class BankAccount extends Actor {
    var founds = 0

    override def receive: Receive = {
      case Deposit(value) =>
        founds += value
        sender() ! "Success"
      case Withdraw(value) =>
        if (founds <= 0)
          sender() ! "Failure"
        else {
          founds += value
          sender() ! "Success"
        }
      case Statement => sender() ! founds.toString
    }
  }

  class Client extends Actor{
    override def receive: Receive = {
      case Deposit(value) => bankAccount ! Deposit(value)
      case Withdraw(value) => bankAccount ! Withdraw(value)
      case Statement => bankAccount ! Statement
      case "Failure" => println("Operation failed")
      case "Success" => println("Operation succeed")
      case value : String => println(s"Founds: $value")
    }
  }

  case class Deposit(value: Int)
  case class Withdraw(value: Int)
  object Statement

  Thread.sleep(1000)
  val bankAccount = system.actorOf(Props[BankAccount],"bankAccount")
  val client = system.actorOf(Props[Client],"client")

  client ! Withdraw(200)
  client ! Deposit(200)
  client ! Deposit(200)
  client ! Statement


}
