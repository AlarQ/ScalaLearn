object SelfType {

  trait Edible

  // hierarchy #1
  trait Person {
    def hasAllergiesTo(thing: Edible): Boolean
  }

  trait Child extends Person

  trait Adult extends Person

  // hierarchy #2
  trait Diet{ self: Person =>
    def canEat(thing: Edible): Boolean = self.hasAllergiesTo(thing)
  }

  trait Carnivore extends Diet with Person
  class VegetarianAthlete extends Diet with Adult{
    override def hasAllergiesTo(thing: Edible): Boolean = false
  }




  // doesn't compile
  // trait Vegetarian extends Diet

  // PROBLEM: Diet must be applicable to Persons only

  //  class VegeterianAthlete extends Vegetarian with Adult // enforce at compile time

  // Oprion #1 - enforce suptype relationship, Diet extends Person,
  // then Diet methods can have access to Person elements
  // but it make any sense

  // Option #2 - add type argument
  //  trait Diet[T <: Person]{
  //    def canEat(thing: Edible) : Boolean
  //  }
  //
  //  trait Carnivore[T<: Person] extends Diet[T]
  //  trait Vegetarian[T <: Person] extends Diet[T]

  // Option #3 = selfType
  // adding selfType to Diet means:
  // whoever extends Diet must also extend (or mix-in) Person
}
