/*
Code to article: https://scalac.io/blog/tagless-final-pattern-for-scala-code/
 */
class TaglessFinalDemo {
  // Scala - hosting language

  /*
  Scala implementation of TF:
   Language - defines a subset of operations that the hosted language allows
   Bridges - express Scala values and business logic in the Language
   Interpreters - they run logic expressed as the Language and get the final value (This is not the official lingo, but I will be using this naming here to make explanations simpler.)
   */

  // Language
  trait Language[Wrapper[_]] {
    // Wrapper - allow to change the package on which we operate
    // type of Wrapper - type safety

    def number(v: Int): Wrapper[Int]
    def increment(a: Wrapper[Int]): Wrapper[Int]
    def add(a: Wrapper[Int], b: Wrapper[Int]): Wrapper[Int]

    def text(v: String): Wrapper[String]
    def toUpper(a: Wrapper[String]): Wrapper[String]
    def concat(a: Wrapper[String], b: Wrapper[String]): Wrapper[String]

    // converter
    def toString(v: Wrapper[Int]): Wrapper[String]

  }

  // bridge - fill the gap betweene hosting language (Scala) and our hosted language
  trait ScalaToLanguageBridge[ScalaValue] {
    def apply[Wrapper[_]](implicit L: Language[Wrapper]): Wrapper[ScalaValue]
  }

  def buildNumber(number: Int) = new ScalaToLanguageBridge[Int] {
    override def apply[Wrapper[_]](implicit L: Language[Wrapper]): Wrapper[Int] = L.number(number)
  }

  def buildIncrementNumber(number: Int) = new ScalaToLanguageBridge[Int] {
    override def apply[Wrapper[_]](implicit L: Language[Wrapper]): Wrapper[Int] = L.increment(L.number(number))
  }

  // more complicated expression
  def buildIncrementExpression(expression: ScalaToLanguageBridge[Int]) = new ScalaToLanguageBridge[Int] {
    override def apply[Wrapper[_]](implicit L: Language[Wrapper]): Wrapper[Int] = L.increment(expression.apply)
  }

  // builds an expression like: println(s"$text ${a + (b + 1)}")
  def buildComplexExpression(text: String, a: Int, b: Int) = new ScalaToLanguageBridge[String] {
    override def apply[Wrapper[_]](implicit F: Language[Wrapper]): Wrapper[String] = {
      val addition = F.add(F.number(a), F.increment(F.number(b)))
      F.concat(F.text(text), F.toString(addition))
    }
  }

  val fullExpression = buildComplexExpression("Result is ", 10, 1)

  // interpeter
  type NoWrap[ScalaValue] = ScalaValue

  val interpret = new Language[NoWrap] {
    override def number(v: Int): NoWrap[Int]                      = v
    override def increment(a: NoWrap[Int]): NoWrap[Int]           = a + 1
    override def add(a: NoWrap[Int], b: NoWrap[Int]): NoWrap[Int] = a + b

    override def text(v: String): NoWrap[String]                              = v
    override def toUpper(a: NoWrap[String]): NoWrap[String]                   = a.toUpperCase
    override def concat(a: NoWrap[String], b: NoWrap[String]): NoWrap[String] = a + " " + b

    override def toString(v: NoWrap[Int]): NoWrap[String] = v.toString
  }

  val fullExpression = buildComplexExpression("Result is ", 10, 1)
  println(s"interpreted full: ${fullExpression.apply(interpret)}")
// interpreted full: Result is  12

// another interpeter
  type PrettyPrint[ScalaValue] = String
  val interpretAsPrettyPrint = new Language[PrettyPrint] {
    override def number(v: Int): PrettyPrint[Int]                                = s"($v)"
    override def increment(a: PrettyPrint[Int]): PrettyPrint[Int]                = s"(inc $a)"
    override def add(a: PrettyPrint[Int], b: PrettyPrint[Int]): PrettyPrint[Int] = s"(+ $a $b)"

    override def text(v: String): PrettyPrint[String]                                        = s"[$v]"
    override def toUpper(a: PrettyPrint[String]): PrettyPrint[String]                        = s"(toUpper $a)"
    override def concat(a: PrettyPrint[String], b: PrettyPrint[String]): PrettyPrint[String] = s"(concat $a $b)"

    override def toString(v: PrettyPrint[Int]): PrettyPrint[String] = s"(toString $v)"
  }

  val fullExpression2 = buildComplexExpression("Result is ", 10, 1)
  println(s"interpreted full (as pretty print): ${fullExpression.apply(interpretAsPrettyPrint)}")
  // interpreted full (as pretty print): (concat [Result is ] (toString (+ (10) (inc (1)))))

  // TF benefits

  // # Extensibility
  trait LanguageWithMul[Wrapper[_]] extends Language[Wrapper] {
    def multiply(a: Wrapper[Int], b: Wrapper[Int]): Wrapper[Int]
  }

  trait ScalaToLanguageWithMulBridge[ScalaValue] {
    def apply[Wrapper[_]](implicit L: LanguageWithMul[Wrapper]): Wrapper[ScalaValue]
  }

  def multiply(a: Int, b: Int) = new ScalaToLanguageWithMulBridge[Int] {
    override def apply[Wrapper[_]](implicit L: LanguageWithMul[Wrapper]): Wrapper[Int] =
      L.multiply(L.number(a), L.number(b))
  }

  val interpretWithMul = new LanguageWithMul[NoWrap] {
    override def multiply(a: NoWrap[Int], b: NoWrap[Int]): NoWrap[Int] = a * b

    override def number(v: Int): NoWrap[Int]                      = v
    override def increment(a: NoWrap[Int]): NoWrap[Int]           = a + 1
    override def add(a: NoWrap[Int], b: NoWrap[Int]): NoWrap[Int] = a + b

    override def text(v: String): NoWrap[String]                              = v
    override def toUpper(a: NoWrap[String]): NoWrap[String]                   = a.toUpperCase
    override def concat(a: NoWrap[String], b: NoWrap[String]): NoWrap[String] = a + " " + b

    override def toString(v: NoWrap[Int]): NoWrap[String] = v.toString
  }

  // # Composability
  // builds a 10 + (((0 + 1)+1)+1) expression
  def buildIncrementExpression() = new ScalaToLanguageBridge[Int] {
    override def apply[Wrapper[_]](implicit L: Language[Wrapper]): Wrapper[Int] =
      L.add(L.number(10), L.increment(L.increment(L.increment(L.number(0)))))
  }
  // but it's not optimal as e.g. 10 + (0+3) - for DB read/write it's expensive

  /*
  if we would write a specific interpreter that takes an expression,
  turns it into plain Scala, but that result would happen to be a bridge,
  then we could interpret his output later on.
   */

  type Nested[ScalaValue] = ScalaToLanguageBridge[ScalaValue]
  val simplify = new Language[Nested] {
    var nesting = 0

    override def number(v: Int): Nested[Int] = new ScalaToLanguageBridge[Int] {
      override def apply[Wrapper[_]](implicit L: Language[Wrapper]): Wrapper[Int] =
        if (nesting > 0) {
          val temp = nesting
          nesting = 0
          L.add(L.number(temp), L.number(v))
        } else {
          L.number(v)
        }
    }
    override def increment(a: ScalaToLanguageBridge[Int]): Nested[Int] = new ScalaToLanguageBridge[Int] {
      override def apply[Wrapper[_]](implicit L: Language[Wrapper]): Wrapper[Int] = {
        nesting = nesting + 1
        a.apply(L)
      }
    }
    override def add(a: ScalaToLanguageBridge[Int], b: ScalaToLanguageBridge[Int]): Nested[Int] =
      new ScalaToLanguageBridge[Int] {
        override def apply[Wrapper[_]](implicit L: Language[Wrapper]): Wrapper[Int] =
          if (nesting > 0) {
            val temp = nesting
            nesting = 0
            L.add(L.number(temp), L.add(a.apply(L), b.apply(L)))
          } else {
            L.add(a.apply(L), b.apply(L))
          }
      }

    override def text(v: String): Nested[String]                              = ???
    override def toUpper(a: Nested[String]): Nested[String]                   = ???
    override def concat(a: Nested[String], b: Nested[String]): Nested[String] = ???

    override def toString(v: Nested[Int]): Nested[String] = ???
  }

  val example1 = simpleVersion.apply(simplify)
  println(s"Example 1: ${example1.apply(interpret)} = ${example1.apply(interpretAsPrettyPrint)}")

  val example2 = new ScalaToLanguageBridge[Int] {
    override def apply[Wrapper[_]](implicit L: Language[Wrapper]): Wrapper[Int] =
      // ((0 + 1) + [0 + 1 + 1]) + 1
      L.increment(L.add(L.increment(L.number(0)), L.increment(L.increment(L.number(0)))))
  }
  println(s"Example 2: ${example2.apply(interpret)} = ${example2.apply(interpretAsPrettyPrint)}")
  println(
    s"Example 2: ${example2.apply(simplify).apply(interpret)} = ${example2.apply(simplify).apply(interpretAsPrettyPrint)}"
  )
}
