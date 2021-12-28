package algorithms.sorting


  object QuickSortScalaTime {
    def sortFunctional(xs: Array[Int]): Array[Int] = {
      if (xs.length <= 1) xs
      else {
        val pivot = xs(xs.length / 2)
        Array.concat(sortFunctional(
          xs filter (pivot > _)),
          xs filter (pivot == _),
          sortFunctional(xs filter (pivot < _)))
      }
    }

    def sortTraditionl(xs: Array[Int]) {
      def swap(i: Int, j: Int) {
        val t = xs(i);
        xs(i) = xs(j);
        xs(j) = t;
      }

      def sort1(l: Int, r: Int) {
        val pivot = xs((l + r) / 2)
        var i = l;
        var j = r;
        while (i <= j) {
          while (xs(i) < pivot) i += 1
          while (xs(j) > pivot) j -= 1
          if (i <= j) {
            swap(i, j)
            i += 1
            j -= 1
          }
        }
        if (l < j) sort1(l, j)
        if (j < r) sort1(i, r)
      }
      sort1(0, xs.length - 1)
    }
    def main(args: Array[String]): Unit = {

      val arr = Array.fill(100000) { scala.util.Random.nextInt(100000 - 1) }
      var t1 = System.currentTimeMillis
      sortFunctional(arr)
      var t2 = System.currentTimeMillis
      println("Functional style : " + (t2-t1))

      t1 = System.currentTimeMillis
      sortTraditionl(arr)
      t2 = System.currentTimeMillis
      println("Traditional style : " + (t2-t1))
    }


}