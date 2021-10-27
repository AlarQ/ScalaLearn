package akka_streams

import akka.actor.ActorSystem
import akka.stream.scaladsl.Source
import akka.util.ByteString

object Test extends App{

  implicit val system = ActorSystem("intro")

  case class Pio(byteString: ByteString)

  val bs =  Source(List(ByteString("a"),ByteString("b"),ByteString("c")))
  bs.reduce(_ ++ _).map(Pio).runForeach(x => println(x.byteString.utf8String))


  Source(1 to 100).fold(0)(_ + _).map{
    println("mapping....")
    _ + 1
  }.runForeach(println)
}
