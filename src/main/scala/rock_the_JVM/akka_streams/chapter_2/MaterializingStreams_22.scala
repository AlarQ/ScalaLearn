package rock_the_JVM.akka_streams.chapter_2

import akka.{Done, NotUsed}
import akka.actor.ActorSystem
import akka.stream.scaladsl.{Flow, Keep, Sink, Source}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

object MaterializingStreams_22 extends App {

  /**
   * - graph - blueprint for a stream
   * - running a graph allocates the right resources
   *  - actors, thread pools
   *  - sockets, connections
   *
   * running a graph - materializing a graph
   */

  /**
   * Materialized value
   * -> Materializing graph
   */

  implicit val system = ActorSystem("materializing")

  // #1 NotUsed
  val simpleGraph = Source(1 to 100).to(Sink.foreach(println))
  val materializedValue: NotUsed = simpleGraph.run()

  // #2 materialized value
  val source = Source(1 to 10)
  val sink = Sink.reduce[Int](_ + _)
  val someFuture: Future[Int] = source.runWith(sink)
  someFuture.onComplete{
    case Success(value) => println("value " + value)
    case Failure(exception) => println(exception)
  }

  // #3 choosing mat value
  val simpleSource = Source(1 to 10)
  val simpleFlow = Flow[Int].map(_ + 1)
  val simpleSink = Sink.foreach[Int](println)
  val graph: Future[Done] = simpleSource.viaMat(simpleFlow)(Keep.right).toMat(simpleSink)(Keep.right).run

  // #4 sugars
  Source(1 to 10).runWith(Sink.reduce[Int](_ + _)) // == source.to(Sink.reduce)(Keep.right)
  // shorter
  Source(1 to 10).runReduce[Int](_ + _)

  Flow[Int].map(_ * 2).runWith(simpleSource,simpleSink)

  // #4 Exercises

  // I - return last element of a source
  val lastV1: Future[Int] = Source(1 to 10).runWith(Sink.last[Int])
  val lastV2: Future[Int] = Source(1 to 10).toMat(Sink.last[Int])(Keep.right).run

  // II - compute total word count from list of sentence
  val sentenceSource =  Source(List(
    "I am happy",
    "Scala is fun",
    "Hello there!"
  ))
  val wordCountSink = Sink.fold[Int,String](0)((wordCount,newSentence)=> wordCount + newSentence.split(" ").length)
  val g1: Future[Int] = sentenceSource.toMat(wordCountSink)(Keep.right).run
  val g21: Future[Int] = sentenceSource.runWith(wordCountSink)
  val g3: Future[Int] = sentenceSource.runFold(0)((wordCount, newSentence)=> wordCount + newSentence.split(" ").length)
}
