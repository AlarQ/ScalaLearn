import akka.actor.typed.ActorSystem

import scala.concurrent.{Future, Promise}
import scala.concurrent.ExecutionContext.Implicits.global
import akka.actor.typed.scaladsl.Behaviors

object Threads {

  // synchronous
  def blockingFunction(x: Int): Int = {
    Thread.sleep(10000)
    x + 42
  }

  // blocking call
  blockingFunction(5)

  // will wait 10 seconds before evaluating
  val y = 12

  // asynchronous blocking call
  def asyncBlockingFunction(x: Int): Future[Int] = Future {
    Thread.sleep(10000)
    x + 42
  }

  asyncBlockingFunction(12)

  // evaluates immediately
  val anotherY = 23

  // asynchronous and NON - blocking
  def createSimpleActor = Behaviors.receiveMessage[String] { someMessage =>
    println(s"I received a message: $someMessage")
    Behaviors.same
  }

  val rootActor = ActorSystem(createSimpleActor,"TestSystem")
  // enqueing a message, asynchronous and NON-blocking
  // NON-blocking - calling thread can go on and no other thread is blocked either, WHY ???
  rootActor ! "Message #1"

  val promiseResolver = ActorSystem(
    Behaviors.receiveMessage[(String,Promise[Int])]{
      case (message, promise) =>
        // do some computations
        promise.success(message.length)
        Behaviors.same
    },
    "PromiseResolver"
  )

  def doAsyncNonBlockimgComputation(s: String) : Future[Int] = {
    val aPromise = Promise[Int]()
    promiseResolver ! (s,aPromise)
    aPromise.future
  }

  val asyncNonBlockingResult = doAsyncNonBlockimgComputation("SOme message") // async, NON-blocking
  asyncNonBlockingResult.onComplete(println)

  def main(args: Array[String]): Unit = {

  }
}
