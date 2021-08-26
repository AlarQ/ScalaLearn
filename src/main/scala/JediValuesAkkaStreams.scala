import akka.{Done, NotUsed}
import akka.actor.ActorSystem
import akka.stream.scaladsl.{Flow, Keep, Sink, Source}

import scala.concurrent.Future

// in akkaStreams - MATERIALIZED VALUES
object JediValuesAkkaStreams {

  /**
   * Akka Streams - implementation of Reactive Streams for JVM
   * Components:
   * -> Source
   * -> Flow
   * -> Sink
   */

  implicit val system: ActorSystem = ActorSystem()
  import system.dispatcher

  val source = Source(1 to 1000)
  val flow = Flow[Int].map(_ * 2)
  val sink = Sink.foreach(println)
  // another JediValues;
  val summingSink = Sink.fold[Int,Int](0)(_ + _)

  val graph = source.via(flow).to(sink)

//  val anotherGraph = source.via(flow).toMat(sink)((leftJediValue,rightJediValue)=> rightJediValue)
  // simpler:
  val anotherGraph = source.via(flow).toMat(sink)(Keep.right)
  // Jedi values


  def main(args: Array[String]): Unit = {
   // val jediValue: NotUsed = graph.run()

    // JediValues can tell us, when the stream is done
//    val anotherJediValue: Future[Done] = anotherGraph.run()
//     anotherJediValue.onComplete(_ => println("Stream is done"))


    val sumFuture: Future[Int] = source.toMat(summingSink)(Keep.right).run()
    sumFuture.foreach(println)

    // once you start stream, no turning back
    // Jedi values may or may not be connected to the actual elements that go thorough the graph
    // Jedi values can have ANY type


  }
}
