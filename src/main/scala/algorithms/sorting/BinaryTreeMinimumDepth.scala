package algorithms.sorting

class Node(var data: Int) {
  var left: Node = null
  var right: Node = null
}

object BinaryTree {
  /* Driver program to test above functions */ def main(args: Array[String]): Unit = {
    val tree = new BinaryTree
    tree.root = new Node(1)
    tree.root.left = new Node(2)
    tree.root.right = new Node(3)
    tree.root.left.left = new Node(4)
    tree.root.left.right = new Node(5)
    tree.root.left.right.left = new Node(5)
    System.out.println("The minimum depth of " + "binary tree is : " + tree.minimumDepth)
  }
}

class BinaryTree { //Root of the Binary Tree
  var root: Node = null

  def minimumDepth: Int = minimumDepth(root)

  /* Function to calculate the minimum depth of the tree */
  def minimumDepth(root: Node): Int = {
    // Corner case. Should never be hit unless the code is
    // called on root = NULL
    if (root == null) return 0
    // Base case : Leaf Node. This accounts for height = 1.
    if (root.left == null && root.right == null) return 1
    // If left subtree is NULL, recur for right subtree
    if (root.left == null) return minimumDepth(root.right) + 1
    // If right subtree is NULL, recur for left subtree
    if (root.right == null) return minimumDepth(root.left) + 1
    Math.min(minimumDepth(root.left), minimumDepth(root.right)) + 1
  }
}