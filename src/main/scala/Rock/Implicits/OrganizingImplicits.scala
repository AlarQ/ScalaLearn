package Rock.Implicits

object OrganizingImplicits extends App {

  // sorted takes implicit parameter
  // this implicit will take the precedence over the implicit value defined in scala.Predef
  implicit def reverseOrdering: Ordering[Int] = Ordering.fromLessThan(_ > _)

  println(List(1, 4, 2, 5, 3).sorted)

  // Implicits (used as implicit parameters):
  // - val / var
  // - objects
  // - accessor methods = defs with no parentheses - reverseOrdering CANT have ()

  // Exercise
  case class Person(name: String, age: Int)

  val persons = List(
    Person("Adam", 12),
    Person("Mike", 45),
    Person("John", 56)
  )

  implicit def ordering: Ordering[Person] = Ordering.fromLessThan((a, b) => a.name.compareTo(b.name) < 0)
  println(persons.sorted)

  // ----------------- IMPLICITS SCOPE -----------------
  // - normal scope = LOCAL SCOPE
  // - imported scope
  // - companion objects of all types involved in the method signature
  // -> e.g for: override def sorted[B >: A](implicit ord: Ordering[B]): C = super.sorted(ord)
  // --> List
  // --> Ordering
  // --> all the types involved: A or any superType

  // ------------------------- BEST PRACTICES --------------------------
  // When defining implicit val:
  // #1
  // - if there is a single possible value for it
  // - and you can edit the code for the type
  // then define the implicit in the companion

  // #2
  // - if there are many possible values for it
  // - but a single is good one
  // - and you can edit the code for the type
  // then define the good implicit in companion

  // Exercise: Add three orderings:
  // - totalPrice = most used (50%)
  // - by unit count = used 25%
  // - by unit price  = used 25%
  case class Purchase(noUnits: Int, unitPrice: Double)

  object Purchase{
    implicit def totalPriceOrdering: Ordering[Purchase] = Ordering.fromLessThan(
      (a,b) => {
        val totalA = a.noUnits * a.unitPrice
        val totalB = b.noUnits * b.unitPrice
        totalA < totalB
      }
    )
  }

  object NoUnitsOrdering{
    implicit def noUnitsOrdering: Ordering[Purchase] = Ordering.fromLessThan(
      (a,b) => a.noUnits < b.noUnits
    )
  }


  object UnitPriceOrdering{
    implicit def unitPriceOrdering: Ordering[Purchase] = Ordering.fromLessThan(
      (a,b) => a.unitPrice < b.unitPrice
    )
  }

}
