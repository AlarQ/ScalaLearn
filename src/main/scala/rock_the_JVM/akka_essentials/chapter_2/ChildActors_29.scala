package rock_the_JVM.akka_essentials.chapter_2

import akka.actor.{Actor, ActorRef, ActorSelection, ActorSystem, Props}
import rock_the_JVM.akka_essentials.chapter_2.ChildActors_29.Parent.{CreateChild, TellChild}

object ChildActors_29 extends App {

  object Parent {
    case class CreateChild(name: String)

    case class TellChild(message: String)
  }


  class Parent extends Actor {

    override def receive: Receive = {

      case CreateChild(name) => println(s"${self.path} creating child $name ")
        val childRef = context.actorOf(Props[Child], "child")
        context.become(withChild(childRef))

    }

    def withChild(childRef: ActorRef): Receive = {
      case TellChild(message) => childRef forward message

    }
  }


  class Child extends Actor {
    override def receive: Receive = {
      case nessage => println(s"${self.path} I got message $nessage")
    }
  }

  val system = ActorSystem("childActorsDemo")
  val parent = system.actorOf(Props[Parent], "parent")
  parent ! CreateChild("child")
  parent ! TellChild("Heeeello")

  // Guardian (top-level) actors
  // - /system = system guardian
  // - /user = user-level guardian, every actor created using actorOf is owned by this actor
  // - /- the root guardian, manages /system and /user guardian

  // actor selection
  val childSelection: ActorSelection = system.actorSelection("/user/parent/child")
  childSelection ! TellChild("Hiiiiiiiiiiiii")

  // Danger
  // NEVER PASS MUTABLE ACTOR STATE OR THE `THIS` REFERENCE TO CHILD ACTOR
  // NEVER ! ! !

}
