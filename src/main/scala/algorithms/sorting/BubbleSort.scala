package algorithms.sorting

// O(n^2)
object BubbleSort { // Driver method to test above
  def main(args: Array[String]): Unit = {
    val ob = new BubbleSort
    val arr = Array(64, 34, 25, 12, 22, 11, 90)
    ob.bubbleSort(arr)
    System.out.println("Sorted array")
    ob.printArray(arr)
  }
}

class BubbleSort {
  def bubbleSort(arr: Array[Int]): Unit = {
    val n = arr.length
    for (i <- 0 until n - 1) {
      for (j <- 0 until n - i - 1) {
        if (arr(j) > arr(j + 1)) { // swap arr[j+1] and arr[j]
          val temp = arr(j)
          arr(j) = arr(j + 1)
          arr(j + 1) = temp
        }
      }
    }
  }

  /* Prints the array */ def printArray(arr: Array[Int]): Unit = {
    val n = arr.length
    for (i <- 0 until n) {
      System.out.print(arr(i) + " ")
    }
    System.out.println()
  }
}