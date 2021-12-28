package rock_the_JVM.akka_streams.chapter_2

import akka.actor.ActorSystem
import akka.stream.scaladsl.{Flow, Sink, Source}

object OperatorFusion_23 extends App {

  implicit val system = ActorSystem("OperatorFusion")

  val simpleSource = Source(1 to 1000)

  val simpleFlow = Flow[Int].map(_ + 1)
  val simpleFlow2 = Flow[Int].map(_ * 10)

  val simpleSink = Sink.foreach[Int](println)

  // runs on the SAME ACTOR
  // simpleSource.via(simpleFlow).via(simpleFlow2).to(simpleSink).run()
  // operator / component FUSION

  // complex flows
  val complexFlow = Flow[Int].map{ x =>
    // simulating long computation
    Thread.sleep(1000)
    x+1
  }

  val complexFlow2 = Flow[Int].map{ x =>
    // simulating long computation
    Thread.sleep(1000)
    x*1
  }

  // runs using one actor - looong time!
  // simpleSource.via(complexFlow).via(complexFlow2).to(simpleSink).run()

  // async boundary
//  simpleSource.via(complexFlow).async // runs on one actor
//    .via(simpleFlow2).async // runs on another actor
//    .to(simpleSink) // runs on third actor
//    .run()

  // ordering guaranties

  // every element is fully processed in the stream
  // before new element is put into the stream
  Source(1 to 3)
    .map(el => {println(s"Flow A: $el"); el})
    .map(el => {println(s"Flow B: $el"); el})
    .map(el => {println(s"Flow C: $el"); el})
    .runWith(Sink.ignore)

  // with async boundary with one order guarantee
  // relative orders for element
  Source(1 to 3)
    .map(el => {println(s"Flow A: $el"); el}).async
    .map(el => {println(s"Flow B: $el"); el}).async
    .map(el => {println(s"Flow C: $el"); el}).async
    .runWith(Sink.ignore)
}
