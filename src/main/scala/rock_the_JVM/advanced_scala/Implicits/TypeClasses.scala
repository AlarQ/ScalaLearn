package rock_the_JVM.advanced_scala.Implicits

// Type class - a trait that takes a type and describes operations which can be done for that type
object TypeClasses extends App {

  trait HTMLWritable {
    def toHtml: String
  }

  case class User(name: String, age: Int, email: String) extends HTMLWritable {
    override def toHtml: String = s"<div>$name, $age, <a href = $email/></div>"
  }

  User("Adam", 23, "adam@ex.com")
  // Solution above has to disadvantages:
  // 1 - works for the types WE write
  // 2 - ONE implementation out of quite a number

  // Better solutions
  //  ---------------- #2 - pattern matching ------------------------------------
  object HTMLSerializerPM {
    def serializeToHtml(value: Any) = value match {
      case User(n, a, e) =>
      case _ =>
    }
  }

  // disadvantages:
  // 1 - lost type safety
  // 2 - need to modify code every time
  // 3 - still ONE implementation

  // ----- better solution - type class ------------
  // type class
  trait HTMLSerializer[T] {
    def serialize(value: T): String
  }

  // type class instance
  object UserSerializer extends HTMLSerializer[User] {
    override def serialize(user: User): String = s"<div>${user.name}, ${user.age}, <a href = ${user.email}/></div>"
  }

  println(UserSerializer.serialize(User("John", 12, "john@ex.com")))

  // advantages:
  // 1 - we can define serializers for other types

  import java.util.Date

  object DateSerializer extends HTMLSerializer[Date] {
    override def serialize(date: Date): String = s"<div>${date.toString}</div>"
  }

  // 2 - we can define multiple serializer for given type
  object PartialUserSerializer extends HTMLSerializer[User] {
    override def serialize(user: User): String = s"<div>${user.name}</div>"
  }

  // Type class template
  trait TypeClassTemplate[T] {
    def action(value: T): String
  }

  object TypeClassTemplate {
    def apply[T](implicit instance: TypeClassTemplate[T]) = instance
  }

  // Exercise
  trait Equal[T] {
    def apply(v1: T, v2: T): Boolean
  }

  implicit object NameEquality extends Equal[User] {
    override def apply(v1: User, v2: User): Boolean = v1.name == v2.name
  }

  object EmailEquality extends Equal[User] {
    override def apply(v1: User, v2: User): Boolean = v1.email == v2.email
  }

  // --------------------- IMPLICIT TYPE CLASS INSTANCES ------------------------------
  object HTMLSerializer {
    def serialize[T](value: T)(implicit serializer: HTMLSerializer[T]): String = serializer.serialize(value)

    def apply[T](implicit serializer: HTMLSerializer[T]) = serializer
  }

  implicit object IntSerializer extends HTMLSerializer[Int] {
    override def serialize(value: Int): String = s"<div>$value</div>"
  }

  println(HTMLSerializer.serialize(42))

  implicit object PartialUserSerializer1 extends HTMLSerializer[User] {
    override def serialize(user: User): String = s"<div>${user.name}</div>"
  }

  println(HTMLSerializer.serialize(User("Mark", 23, "c")))
  // access to the entire type class interface
  println(HTMLSerializer[User].serialize(User("Mark", 23, "c")))

  // Exercise: implement type class pattern for Equal
  object Equal {
    def apply[T](a: T, b: T)(implicit equalizer: Equal[T]): Boolean = equalizer.apply(a, b)
  }
  // if we have implicit Equal defined for different types we can call for them Equal(a,b)

  // AD-HOC polymorphism
  // ------------------------ TYPE CLASSES WITH ENRICHMENT --------------------------------------
  implicit class HTMLEnrichment[T](value: T) {
    def toHTML(implicit serializer: HTMLSerializer[T]): String = serializer.serialize(value)
  }

  val mark = User("Mark", 23, "mark@ex.co")
  val tom = User("Tom", 45, "tom@ex.con")
  // - extends to new types
  // - choose implementation
  // - super expressive
  println(mark.toHTML)
  println(12.toHTML)

  // Exercise: improve Equal TC with implicit conversion class
  implicit class EqualEnrichment[T](value: T) {
    def ===(anotherValue: T)(implicit equalizer: Equal[T]) = equalizer.apply(value, anotherValue)

    def !==(anotherValue: T)(implicit equalizer: Equal[T]) = !equalizer.apply(value, anotherValue)
  }

  println(tom === mark) // new EqualEnrichment[User](tom).===(mark)(nameEquality)

  // TYPE SAFE
  //  println(tom === 2)

  // ---------------- CONTEXT BOUNDS ----------------
  def boilerPlate[T](content: T)(implicit serializer: HTMLSerializer[T]): String =
    s"html: ${content.toHTML(serializer)}"

  // dont have to pass serializer to this bound
  def htmlSugar[T: HTMLSerializer](content: T): String =
    s"html: ${content.toHTML}"

  // implicitly
  case class Permissions(mask: String)
  implicit val defaultPermissions: Permissions = Permissions("0744")

  // in some other part of the code we want to see what sits in defaultPermissions
  val standardPerms = implicitly[Permissions]
  println(s"Standard perms: $standardPerms")
}
