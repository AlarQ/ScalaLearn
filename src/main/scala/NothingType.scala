object NothingType {

  // for every type..
  def giveMeInt() : Int = throw new NoSuchElementException
  def giveMeString(): String = throw new NoSuchElementException

  // throw exceptions returns NOTHING
  // Nothing != Unit != Null != anything at all
  // Nothing is yhe type of "nothingness"

  def functionAboutNothing(a: Nothing): Int = 45
  // crash because exception is evaluated
  functionAboutNothing(throw new NullPointerException)

  def functionReturningNothing(): Nothing = throw new RuntimeException

  // Nothing is usefull in COVARIANT GENERICS
  abstract class MyList[+T]
  class NonEmptyList[+T](head: T, tail: MyList[T])
  object EmptyList extends MyList[Nothing]

  val listOfStrings: MyList[String] = EmptyList
  val listOfIntegers: MyList[Int] = EmptyList
}
