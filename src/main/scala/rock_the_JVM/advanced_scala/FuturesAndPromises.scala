package rock_the_JVM.advanced_scala

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future, Promise}
import scala.util.{Failure, Success, Try}

object FuturesAndPromises extends App {

  def someComputations: Int = {
    Thread.sleep(2000)
    42
  }

  val aFuture = Future {
    someComputations // it is computed in another thread
  }

  aFuture.onComplete {
    case Success(value) => println("value " + value)
    case Failure(exception) => println(exception)
  } // SOME thread will compute this - no assumptions for which thread, global context passed by a compiler

  Thread.sleep(3000)

  /*
  To recover from failed Future:
  - recovery
  - fallBackTo

  when need to wait for result:
  - Await.result
   */


  // -------------------------- PROMISES -------------------------------------
  println("----------------- PROMISES -----------------------")
  val promise = Promise[Int] // a controller over a future
  val future = promise.future

  // thread 1 - consumer
  future.onComplete {
    case Success(value) => println("Consumer: I received value = " + value)
  }

  // thread 2 - producer
  val producer = new Thread(() => {
    println("Producer computing...")
    Thread.sleep(1000)
    // fullfilling the promise
    promise.success(42)
    println("Producer done")
  })

  producer.start()
  Thread.sleep(2000)

  // EXERCISE
  // 1 - fulfill a future immediately with a value
  def fulfillImediately[T](value: T): Future[T] = Future(value)

  // 2 - inSequence(futureA, futureB) = run fb if fa is fulfilled
  def inSequence[A, B](futureA: Future[A], futureB: Future[B]): Future[B] = {
    futureA.flatMap(_ => futureB)
  }

  // 3 - first(fA,fB) - returns a future with first value of two futures
  def first[T](fA: Future[T], fB: Future[T]): Future[T] = {
    val promise = Promise[T]
    fA.onComplete {
      case Success(value) => try {
        promise.success(value)
      } catch {
        case _ =>
      }
      case Failure(value) => try {
        promise.failure(value)
      } catch {
        case _ =>
      }

    }

    fB.onComplete {
      case Success(value) => try {
        promise.success(value)
      } catch {
        case _ =>
      }
      case Failure(value) => try {
        promise.failure(value)
      } catch {
        case _ =>
      }
    }

    promise.future
  }

  // 4 - last(fA,fB)
  def last[T](fA: Future[T], fB: Future[T]): Future[T] = {
    // 1 promise which both futures will try to complete
    // 2 promise which will be fullfilled by last future
    val bothPromise = Promise[T]
    val lastPromise = Promise[T]

    def checkAndComplete = (result: Try[T]) =>
      if (!bothPromise.tryComplete(result))
        lastPromise.complete(result)

    fA.onComplete(checkAndComplete)
    fB.onComplete(checkAndComplete)

    lastPromise.future
  }


  // 5 - retRyUntil[T](action: () => Fututre[T], condition: T => Boolean): Future[T] =?
}
