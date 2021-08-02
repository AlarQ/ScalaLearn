package akkaRJVM

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
;

object ActorIntro extends App {

  // recommended one per application instance
  val actorSystem = ActorSystem("firstActorSystem")
  println(actorSystem.name)

  // word count actor
  class WordCountActor extends Actor {
    // internal data
    var totalWords = 0

    // behaviour
    override def receive: Receive = {
      case message: String => totalWords += message.split(" ").length
        println("[wordCounter] message received: " + message)
      case msg => println("Cannot understand message: " + msg)
    }
  }

  // instantiate actor
  val wordCounter: ActorRef = actorSystem.actorOf(Props[WordCountActor], "wordCounter")
  val anotherWordCounter: ActorRef = actorSystem.actorOf(Props[WordCountActor], "anotherWordCounter")

  // asynchronous - no order guaranteed
  wordCounter ! "I am learning akka!" // tell method
  anotherWordCounter ! "Different message"

  class Person(name: String) extends Actor {
    override def receive: Receive = {
      case "hi" => println(s"Hi, my name is $name")
      case _ =>
    }
  }

  // how send parameters to constructor, discouraged
  val person = actorSystem.actorOf(Props(new Person("Bob")))
  person ! "hi"

  // best practise, declare companion object
  object Person {
    def props(name: String) = Props(new Person(name))
  }

  val person1 = actorSystem.actorOf(Person.props("Bob"))
}
