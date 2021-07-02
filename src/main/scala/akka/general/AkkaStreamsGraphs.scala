package akka.general

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.ClosedShape
import akka.stream.scaladsl.{Broadcast, Flow, GraphDSL, RunnableGraph, Sink, Source, Zip}

object AkkaStreamsGraphs extends App {
//  implicit val system = ActorSystem()
//
//  // a source - emits elements
//  val source = Source(1 to 1000)
//
//  // a flow - receives elements, transforms them and emits their result
//  val flow = Flow[Int].map(_ * 3)
//
//  // a sink - receiver of elements
//  val sink = Sink.foreach[Int](println)
//
//  val graph = source.via(flow).to(sink)
//
////  graph.run()
//
//  // TASK:
//  // source of ints -> 2 independend "hard" computations -> concat in tuple -> print the tuple
//  // Step #1 - the frame
//  val specialGraph = GraphDSL.create(){
//    implicit builder: GraphDSL.Builder[NotUsed] =>
//      import GraphDSL.Implicits._
//
//      // Step #2 - create the building blocks
//      val input = builder.add(Source(1 to 1000))
//      val incrementer = builder.add(Flow[Int].map(_ + 1))
//      val multiplier = builder.add(Flow[Int].map(_ * 10))
//      val output = builder.add(Sink.foreach[(Int,Int)](println))
//
//      val broadcast = builder.add(Broadcast[2](2))
//      val zip = builder.add(Zip[Int,Int])
//
//      // Step #3 - glue components together
//      // input FEEDS INTO broadcast
//      input ~> broadcast
//
//      broadcast.out(0) ~> incrementer ~> zip.in0
//      broadcast.out(1) ~> multiplier ~> zip.in1
//
//      zip.out ~> output
//
//      // Step #4 - closing
//      // marker to validate graph
//      ClosedShape
//  }
//
//  RunnableGraph.fromGraph(specialGraph).run()

}
