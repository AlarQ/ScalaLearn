package algorithms.sorting

// O(n^2)
object InsertionSort {
  /* A utility function to print array of size n*/ def printArray(arr: Array[Int]): Unit = {
    val n = arr.length
    for (i <- 0 until n) {
      System.out.print(arr(i) + " ")
    }
    System.out.println()
  }

  // Driver method
  def main(args: Array[String]): Unit = {
    val arr = Array(12, 11, 13, 5, 6)
    val ob = new InsertionSort
    ob.sort(arr)
    printArray(arr)
  }
}

class InsertionSort {
  /*Function to sort array using insertion sort*/
  def sort(arr: Array[Int]): Unit = {
    val n = arr.length
    for (i <- 1 until n) {
      val key = arr(i)
      var j = i - 1
      /* Move elements of arr[0..i-1], that are
                     greater than key, to one position ahead
                     of their current position */ while ( {
        j >= 0 && arr(j) > key
      }) {
        arr(j + 1) = arr(j)
        j = j - 1
      }
      arr(j + 1) = key
      println(arr.mkString("Array(", ", ", ")"))
    }
  }
  /* This code is contributed by Rajat Mishra. */}