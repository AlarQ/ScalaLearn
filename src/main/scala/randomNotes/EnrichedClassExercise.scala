package randomNotes

import randomNotes.SummatorExercise.Monoid

object EnrichedClassExercise {

  // extending AnyVal reduce redundant allocation
  implicit class RichString(val s: String) extends AnyVal {
    def countChar(c: Char): Int = s.count(_ == c)
  }

  implicit class RichList[A](val s: List[A]) extends AnyVal {
    def sumAll(implicit m: Monoid[A]): A =
      s.foldLeft(m.zero)(m.combine)
  }

  def countCharInString(s: String, char: Char): Int = {
   
    s.countChar(char)
  }

  def sumListElements[A](l: List[A])(implicit m: Monoid[A]): A = {
   
    l.sumAll
  }

 
}
