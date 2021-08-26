import scala.concurrent.{Future, Promise}
import scala.concurrent.ExecutionContext.Implicits.global

object   Promises {

  // fuutures are inherently non-deterministic

  // given - multithreaded
  // can call that ourselves
  object MyService {
    def produceValue(arg: Int): String = s"Your number: $arg"

    def submitTask[A](actualArg: A)(f: A => Unit): Boolean = {
      // run f on actualArg at some point
      true
    }
  }

  // introducing - Promises = "controller/wrapper" of Future
  // step 1 - create a promise
  val myPromise = Promise[String]()

  // step 2 - extract its future
  val myFuture = myPromise.future
  // step 3 -consume future
  val processing = myFuture.map(_.toUpperCase)

  // step 4
  def asyncCall(promise: Promise[String]): Unit = {
    promise.success("we got value!")
  }

  // step 5 - call the producer
  asyncCall(myPromise)

  // apply Promises to scenario
  // target
  def giveMeValue(arg: Int): Future[String] = {
    val thePromise = Promise[String]()

    MyService.submitTask(arg) { x: Int =>
      val value = MyService.produceValue(arg)
      thePromise.success(value)
    }

    thePromise.future
  }

}
