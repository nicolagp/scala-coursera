//import patmat.Huffman
import patmat._

val abc: CodeTree = Fork(Leaf('A', 4),
  Fork(Leaf('B', 3),
    Fork(Leaf('C', 2), Leaf('D', 1), List('C', 'D'), 3),List('B','C', 'D'), 6),List('A','B','C','D'),10)

Huffman.decode(abc, List(0,1,0,1,1,0))