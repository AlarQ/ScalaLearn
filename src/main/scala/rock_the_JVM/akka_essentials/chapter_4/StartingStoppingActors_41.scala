package rock_the_JVM.akka_essentials.chapter_4

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}
import rock_the_JVM.akka_essentials.chapter_4.StartingStoppingActors_41.Parent.{StartChild, Stop, StopChild}

object StartingStoppingActors_41 extends App {
  val system = ActorSystem("startingStoppingDemo")

  object Parent {
    case class StartChild(name: String)

    case class StopChild(name: String)

    case object Stop
  }

  class Parent extends Actor with ActorLogging {
    override def receive: Receive = withChildren(Map())

    def withChildren(children: Map[String, ActorRef]): Receive = {
      case StartChild(name) => log.info(s"Starting child $name")
        context.become(withChildren(children + (name -> context.actorOf(Props[Child], name))))
      case StopChild(name) => log.info(s"Stopping child $name")
        val childOption = children.get(name)
        childOption.foreach(context.stop)
      case Stop => log.info("Stopping my self ")
        context.stop(self)
    }

  }

  class Child extends Actor with ActorLogging {
    override def receive: Receive = {
      case message => log.info(message.toString)
    }
  }

  val parent = system.actorOf(Props[Parent], "parent")
  parent ! StartChild("child1")
  val child = system.actorSelection("/user/parent/child1")
  child ! "Hi, kid"
  parent ! StopChild("child1")

  // when we stop parent actor, first children are stopped
  // another way different from context.stop(actorRef) is to send
  // a message: PoisonPill, Kill (throws actorKillException)
  // context.watch(actorRef) - when actorRef dies watcher actor receives Terminated(ref)
}
