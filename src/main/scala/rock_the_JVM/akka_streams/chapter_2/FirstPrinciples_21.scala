package rock_the_JVM.akka_streams.chapter_2

import akka.actor.ActorSystem
import akka.stream.scaladsl.{Flow, Sink, Source}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object FirstPrinciples_21 extends App {
  /**
   * REACTIVE STREAMS
   * publisher - emits elements (asynchronously)
   * subscriber - receives elements
   * processor - transforms elements along way
   * async
   * backpressure
   *
   * Reactive Streams is an SPI (service provider interface), not an API
   * Course goal: how to use akka streams API
   *
   * AKKA STREAMS
   * - Source = publisher
   * - Sink = subscriber
   * - Flow = processor
   */

  implicit val system = ActorSystem("intro")

  val source = Source(1 to 10)
  val sink = Sink.foreach[Int](println)
  val flow = Flow[Int].map(x => x + 100)

  val sourceWithFlow = source.via(flow)
  val flowWithSink = flow.to(sink)
  val graph = source.to(sink)
  sourceWithFlow.to(sink).run

  // null are not allowed in null
  // Source can emit immutable and serializable objects

  // Source types
  val emptySource = Source.empty[Int]
  val infiniteSource = Source(LazyList(1))
  val futureSource = Source.future(Future(42))

  // Sink types
  val theMostBoringSink = Sink.ignore
  val foreachSink = Sink.foreach(println)
  val headSink = Sink.head[Int] // retrieves head and then close the stream
  val foldSink = Sink.fold[Int, Int](0)(_ + _)

  // flows type
  val mapFlow = Flow[Int].map(_ * 2)
  val takeFlow = Flow[Int].take(5)
  // dont have flatMap

  val doubleFlowGraph = source.via(mapFlow).via(takeFlow).to(sink)

  // syntactic sugars
  val mapSource = Source(1 to 10).map(_ + 2) // without via
  mapSource.runForeach(println) // without to sink

  // OPERATORS = components

}
