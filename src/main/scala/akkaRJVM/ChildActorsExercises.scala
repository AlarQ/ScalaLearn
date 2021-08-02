package akkaRJVM

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akkaRJVM.ChildActorsExercises.WordCounterMaster.{Initialize, WordCountReply, WordCountTask}

import scala.collection.immutable.Queue

object ChildActorsExercises extends App {
  // distributed word counting
  object WordCounterMaster {
    case class Initialize(nChildren: Int)

    case class WordCountTask(id: Int, text: String)

    case class WordCountReply(id: Int, text: String, count: Int)
  }

  class WordCounterMaster extends Actor {
    override def receive: Receive = {
      case Initialize(n) =>
        println("[master] Initializing children...")

        def loop(n: Int): Queue[ActorRef] = {
          val rest = if (n > 1) loop(n - 1) else Queue.empty
          Queue(context.actorOf(Props[WordCounterWorker])).enqueueAll(rest)
        }

        val children = loop(n)
        context.become(assignTasks(children, 0, Map()))
    }

    def assignTasks(children: Queue[ActorRef], currentTaskId: Int, requestMap: Map[Int, ActorRef]): Receive = {
      case text: String =>
        val originalSender = sender()
        val (first, newQueue) = children.dequeue
        first forward WordCountTask(currentTaskId, text)
        val newTaskId = currentTaskId + 1
        val newRequestMap = requestMap + (newTaskId -> originalSender)
        context.become(assignTasks(newQueue.enqueue(first), currentTaskId + 1, newRequestMap))
      case reply: WordCountReply =>
        requestMap(reply.id) ! reply
        context.become(assignTasks(children, currentTaskId, requestMap - reply.id))
    }
  }

  class WordCounterWorker extends Actor {
    override def receive: Receive = {
      case WordCountTask(id, text) =>
        println(s"[Worker] executing task...")
        val count = text.split(" ").length
        sender() ! WordCountReply(id, text, count)
    }
  }

  class TestActor extends Actor {
    override def receive: Receive = {
      case "go" =>
        val master: ActorRef = context.actorOf(Props[WordCounterMaster])
        master ! Initialize(3)
        master ! "Akka is awesome"
        master ! "Akka is awesome, right?"
        master ! "Akka is awesome, right? I am certain"
        master ! "Akka is awesome, right? Of course!"
        master ! "Akka is awesome, right? Of course!!!!!!!!!!!"
      case WordCountReply(id, text, count) =>
        println(s"[Test] Got reply for: $text count: $count")
    }
  }

  val system: ActorSystem = ActorSystem("childActorsExercises")
  val testActor = system.actorOf(Props[TestActor])
  testActor ! "go"

}
