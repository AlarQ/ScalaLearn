package randomNotes

import scala.collection.immutable.Queue

object Conversions {

  case class UrlPattern(q: Queue[UrlElement]) {
    def /(elem: UrlElement): UrlPattern = copy(q.appended(elem))

    override def toString: String = {
      q.mkString(" / ")
    }
  }

  object Root extends UrlPattern(Queue.empty)

  sealed trait UrlElement

  case class Constant(s: String) extends UrlElement

  case object * extends UrlElement

  object UrlElement {
    val root: List[UrlElement] = List.empty[UrlElement]
  }

  def main(args: Array[String]): Unit = {
    
    val pattern = Root / Constant("orders") / * / Constant("items") / *
    println(pattern)

    // String -> UrlPattern
    implicit def toRoot(s: String): UrlPattern = Root / Constant(s)

    // String -> Constant
    implicit def toPattern(s: String): UrlElement = Constant(s)

    val pattern2 = "orders" / * / "items" / *
    println(pattern2)
  }
}
