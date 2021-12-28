package rock_the_JVM.akka_streams.chapter_2

import akka.actor.ActorSystem
import akka.stream.OverflowStrategy
import akka.stream.scaladsl.{Flow, Sink, Source}

object Backpressure_24 extends App {

  implicit val actorSystem = ActorSystem("Backpressure")

  val fastSource = Source(1 to 1000)
  val slowSink = Sink.foreach[Int] { x =>
    // simulate long processing
    Thread.sleep(1000)
    println(s"Sink ${x}")
  }

  //  fastSource.to(slowSink).run() // fusing - not backpressure
  println("_______________________________")
  //  fastSource.async.to(slowSink).run() // backpressure - two actors,
  // protocol has to exist to slow down source

  val simpleFlow = Flow[Int].map { x =>
    println(s"Incoming ${x}")
    x + 1
  }

//  fastSource.async.via(simpleFlow).async
//    .to(slowSink)
//    .run()

  /**
   * Reaction to backpressure:
   * - try to slow down if possible
   * - buffer elements until there's more demand
   * - drop down elements from the buffer if it overflows
   * - tear down/kill the whole stream (failure)
   */

  val bufferedFlow = simpleFlow.buffer(10,OverflowStrategy.dropNew)

  fastSource.async.via(bufferedFlow).async
    .to(slowSink)
    .run()
}
