package Rock

object ThreadCommunication extends App{

  // THE PRODUCER-CONSUMER PROBLEM
  class SimpleContainer {
    private var value: Int = 0

    def isEmpty: Boolean = value == 0

    def set(newValue: Int) = value = newValue

    def get = {
      val result = value
      value = 0
      result
    }
  }

  def prodCons(): Unit = {
    val container = new SimpleContainer

    val consumer = new Thread(() => {
      println("Consumer waiting...")
      container.synchronized { // look the object monitor
        container.wait() // release the lock and wait
        // rest of code - when allowed to proceed, lock the monitor and continue
      }

      println("I have consumed " + container.get)
    })

    val producer = new Thread(() => {
      println("Producer computing...")
      Thread.sleep(2000)
      val value = 32
      container.synchronized {
        println("I am producing " + value)
        container.set(value)
        container.notify()
      }
    })
  // wait and notify only in synchronized block
   consumer.start()
   producer.start()
  }

  prodCons()
}
