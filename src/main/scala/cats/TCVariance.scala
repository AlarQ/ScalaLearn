package cats

import cats.implicits.catsSyntaxEq

object TCVariance {
  val aComparison: Boolean = Option(2) === Option(3)
  // val invalidComparison = Some(2) === None // compile error, explained later

  /**
   * rule of thumb for variance:
   * "Has a T" relation = covariant // Cage has Cat, Cage has Animal
   * "Acts on T" relation = contravariant // Vet acts on Cat, Vet acts on Animal
   */

  // ----------------------- COVARIANT TC -----------------------
  // Variance affects, how TC instances are being fetched
  class Animal

  class Cat extends Animal

  trait SoundMaker[-T]

  // TC instance
  implicit object AnimalSoundMaker extends SoundMaker[Animal]

  // API
  def makeSound[T](implicit soundMaker: SoundMaker[T]) = println("wow")

  makeSound[Animal] // ok, cause of TC instance defined above
  makeSound[Cat] // also ok, cause of TC instance for Animal is also applicable for cats, due to contravariance

  /**
   * Rule 1: contravariant TCs can use the superclass instances if nothing is available strictly for that type
   */

  implicit object OptionSoundMaker extends SoundMaker[Option[Int]]
  // why it doesn't work????
  //  makeSound(Option[Int])
  //  makeSound(Some[Int])

  // Eq doesn't work for Some(2) === None, because Eq is invariant

  // ----------------------- CONTRAVARIANT TC -----------------------
  trait AnimalShow[+T] {
    def show: String
  }

  implicit object GeneralAnimalShow extends AnimalShow[Animal] {
    override def show: String = "Animals everywhere"
  }

  implicit object CatShow extends AnimalShow[Cat] {
    override def show: String = "Cats everywhere"
  }

  def organizeShow[T](implicit event: AnimalShow[T]): String = event.show

  def main(args: Array[String]): Unit = {
    println(organizeShow[Cat])

    /**
     * rule 2: covariant TCs will always use the more specific TC instance for that type
     * but may confuse compiler if the general TC is also present
     *
     * rule 3: you can't have both benefits
     * cats uses INVARIANT TCs
     */
    // Some(2) === None we can than in that way:
    Option(2) === Option.empty[Int]

  }
}
