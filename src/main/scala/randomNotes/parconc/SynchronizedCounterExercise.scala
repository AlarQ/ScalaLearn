package randomNotes.parconc

import java.lang.Thread

object SynchronizedCounterExercise {

  /**
   * == TODO 1 ==
   *
   * Uzupełnij implementację tego licznika w taki sposób aby był bezpieczny do użycia w wielu wątkach.
   *
   */
  class SynchronizedCounter() {
    private[this] var count = 0

    def addOne(): Unit = synchronized {
      count += 1
    }

    def getCount: Int = synchronized {
      count
    }
  }

  /**
   * == TODO 2 ==
   *
   * Przygotuj metodę main w której stworzysz ok 20 wątków z których każdy będzie 10000 razy inkrementował
   * licznik. Na koniec sprawdź czy finalna wartość jest poprawna.
   */
  def main(args: Array[String]): Unit = {

    val counter = new SynchronizedCounter()

    val threads: List[Thread] = List.fill(20) {
      // tutaj stwórz wątek który 10k razy zawoła metodę counter.addOne()
      new Thread() {
        override def run(): Unit = {
          for (i <- 1 to 10000) {
            counter.addOne()
          }
        }
      }
    }

    threads.foreach(_.start())

    threads.foreach(_.join())

    println(counter.getCount)
    assert(counter.getCount == 20 * 10000)
  }


}
