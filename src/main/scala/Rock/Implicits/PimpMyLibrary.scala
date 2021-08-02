package Rock.Implicits

object PimpMyLibrary extends App {
  // Enrichment allows us to decorate existing classes with additional methods and properties
  implicit class RichInt(val value: Int) extends AnyVal {
    def isEven: Boolean = value % 2 == 0
    def squareRoot: Double = Math.sqrt(value)
  }
  // type enrichment
  12.squareRoot // compile work: new RichInt(12).squareRoot
  // compiler doesn't do multiple implicit searches
  // - if we define next RichInt - it will take first implicit

  // Exercieses
  // Enrich String class
  // - asInt
  // - encrypt: John -> Lqjp

  implicit class RichString(val value: String) extends AnyVal{
    def asInt = Integer.valueOf(value)
    def encrypt(cypherDistance: Int) = value.map(c => (c + cypherDistance).asInstanceOf[Char])
  }

  println("John".encrypt(2))

  // implicit methods - they are discourage
  implicit def fromStringToInt(str: String): Int = Integer.valueOf(str)
  println("6" / 2)

  // Danger zone
  implicit def fromInttoBoolean(int: Int) : Boolean = int == 1
  val aConditionValue = if (3) "Ok" else "something wrong"
  println(aConditionValue) // hard to find a bug in code
}
