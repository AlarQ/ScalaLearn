package rock_the_JVM.cats.chapter_3

import cats.data.{Writer, WriterT}

import java.io
import scala.annotation.tailrec

object Writers_32 {

  /**
   * Writers - a data type, which let you keep track of useful information, while your data is manipulated
   */

  // Writer is a wrapper around some valuable value, in our case Int
  // While we manipulate this data, we might want to keep track of some sort of additional info, like logs, sequence of modification
  val aWriter: Writer[List[String], Int] = Writer(List("Started something"), 45)

  // some transformations
  val anIncreasedWriter = aWriter.map(_ + 1) // value increases, logs stay the same
  val aLogsWriter = aWriter.mapWritten(_ :+ "next steps") // value stay the same, log change
  val aWriterWithBoth = aWriter.bimap(_ :+ "next steps", _ + 1) // both value and logs change
  val aWriterWithBoth2 = aWriter.mapBoth{(logs, value) =>
    (logs :+ "next steps", value + 1)
  }

  /**
   * Writer usage steps:
   * - define them at the start
   * - manipulate them with pure FP
   * - dump either the value or the logs
   */
  val desireValue = aWriter.value
  val logs = aWriter.written
  val (l,v) = aWriter.run

  val writerA = Writer(Vector("log A1, log A2"),10)
  val writerB = Writer(Vector("log B1"),34)

  // We can compose multiple writers - flatMap
  // writer returned will have logs combined due to natural Semigroup of strings,
  val compositeWriter = for{
    va <- writerA
    vb <- writerB
  } yield va + vb

  // reset the logs
  // reset works only with presence of monoid implicit
  val anEmptyWriter = aWriter.reset // clear the logs, keep the value

  // Exercise 1 - rewrite a function which "prints" things with writers
  def countAndSay(n: Int):Unit = {
    if(n<= 0) println("starting!")
    else {
      countAndSay(n-1)
      println(n)
    }
  }

  def countAndLog(n:Int): Writer[Vector[String],Int] = {
      if(n <= 0) Writer(Vector("starting!"),0)
      else countAndLog(n-1).flatMap(_ => Writer(Vector(s"$n"),n))
  }

  def main(args: Array[String]): Unit = {
    println(compositeWriter.run)

    countAndSay(5)
    println(countAndLog(5).run)
  }
}
