//package rock_the_JVM.akka_streams.chapter_3
//
//import akka.NotUsed
//import akka.actor.ActorSystem
//import akka.stream.ClosedShape
//import akka.stream.scaladsl.{Broadcast, Flow, GraphDSL, RunnableGraph, Sink, Source, Zip}
//
//object GraphBasics {
//
//  implicit val system = ActorSystem("graphBasics")
//
//  // TASK:
//  // source of ints -> 2 independend "hard" computations -> concat in tuple -> print the tuple
//  // Step #1 - the frame
//  val graph = RunnableGraph.fromGraph(GraphDSL.create(){
//    implicit builder: GraphDSL.Builder[NotUsed] => // builder - mutable
//
//      // ----- MUTATE BUILDER SHAPE
//      import GraphDSL.Implicits._
//
//      // Step #2 - create the building blocks
//      val input = builder.add(Source(1 to 1000))
//      val incrementer = builder.add(Flow[Int].map(_ + 1))
//      val multiplier = builder.add(Flow[Int].map(_ * 10))
//      val output = builder.add(Sink.foreach[(Int,Int)](println))
//
//      val broadcast = builder.add(Broadcast[2](2)) // fan-out operator
//      val zip = builder.add(Zip[Int,Int]) // fan-in operator
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
//      // FREEZE BUILDER
//      ClosedShape
//
//      // shape
//  } // graph
//  ) // runnableGraph
//
//  graph.run()
//}
