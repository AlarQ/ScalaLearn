package rock_the_JVM.cats

import cats.data.OptionT

object MonadTransformers_28 {

  // # - Option transformer
  // to implement this you have to unwrap every single object inside values list
  // and wrap it back
  def sumAllOptions(values: List[Option[Int]]): Int = ???

  // Monad transformers help us to work with nested monads
  // without need of unwrapping it many times

  // list of options of int
  val listOfNumberOptions: OptionT[List, Int] = OptionT(List(Option(1), Option(2)))
  val listOfCharOptions: OptionT[List, Char] = OptionT(List(Option('a'), Option('d')))
  val listOfTuples: OptionT[List, (Int, Char)] = for {
    char <- listOfCharOptions
    number <- listOfNumberOptions
  } yield (number, char)

  println(listOfTuples.value)

  // TODO exercises


  def main(args: Array[String]): Unit = {
    println(listOfTuples.value)
  }
}
