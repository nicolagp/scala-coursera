package patmat

/**
 * A huffman code is represented by a binary tree.
 *
 * Every `Leaf` node of the tree represents one character of the alphabet that the tree can encode.
 * The weight of a `Leaf` is the frequency of appearance of the character.
 *
 * The branches of the huffman tree, the `Fork` nodes, represent a set containing all the characters
 * present in the leaves below it. The weight of a `Fork` node is the sum of the weights of these
 * leaves.
 */
abstract class CodeTree
case class Fork(left: CodeTree, right: CodeTree, chars: List[Char], weight: Int) extends CodeTree
case class Leaf(char: Char, weight: Int) extends CodeTree

/**
 * Assignment 4: Huffman coding
 *
 */
trait Huffman extends HuffmanInterface {

  // Part 1: Basics
  def weight(tree: CodeTree): Int = tree match {
    case Fork(_, _, _, w) => w
    case Leaf(_, w) => w
  }

  def chars(tree: CodeTree): List[Char] = tree match {
    case Fork(_, _, chars, _) => chars
    case Leaf(char, _) => List(char)
  }

  def makeCodeTree(left: CodeTree, right: CodeTree) =
    Fork(left, right, chars(left) ::: chars(right), weight(left) + weight(right))

  // Part 2: Generating Huffman trees

  /**
   * In this assignment, we are working with lists of characters. This function allows
   * you to easily create a character list from a given string.
   */
  def string2Chars(str: String): List[Char] = str.toList

  /**
   * This function computes for each unique character in the list `chars` the number of
   * times it occurs. For example, the invocation
   *
   *   times(List('a', 'b', 'a'))
   *
   * should return the following (the order of the resulting list is not important):
   *
   *   List(('a', 2), ('b', 1))
   *
   * The type `List[(Char, Int)]` denotes a list of pairs, where each pair consists of a
   * character and an integer. Pairs can be constructed easily using parentheses:
   *
   *   val pair: (Char, Int) = ('c', 1)
   *
   * In order to access the two elements of a pair, you can use the accessors `_1` and `_2`:
   *
   *   val theChar = pair._1
   *   val theInt  = pair._2
   *
   * Another way to deconstruct a pair is using pattern matching:
   *
   *   pair match {
   *     case (theChar, theInt) =>
   *       println("character is: "+ theChar)
   *       println("integer is  : "+ theInt)
   *   }
   */
  def times(chars: List[Char]): List[(Char, Int)] = {
    // function to count appearances of single character
    def countChar(c: Char, lst: List[Char], count: Int): Int = {
      if (lst.isEmpty) count
      else if (lst.head == c) countChar(c, lst.tail, count+1)
      else countChar(c, lst.tail, count)
    }
    // function to see if char is in result list
    def isIn(c: Char, lst: List[(Char, Int)]): Boolean = {
      if (lst.isEmpty) false
      else if (lst.head._1 == c) true
      else isIn(c, lst.tail)
    }
    // driver function
    def timesHelper(lst: List[Char], acc: List[(Char, Int)]): List[(Char, Int)] = {
      if (lst.isEmpty) acc
      else if (isIn(lst.head, acc)) timesHelper(lst.tail, acc)
      else timesHelper(lst.tail, (lst.head, countChar(lst.head, chars, 0))::acc)
    }

    val result = List()
    timesHelper(chars, result)
  }

  /**
   * Returns a list of `Leaf` nodes for a given frequency table `freqs`.
   *
   * The returned list should be ordered by ascending weights (i.e. the
   * head of the list should have the smallest weight), where the weight
   * of a leaf is the frequency of the character.
   */
  def makeOrderedLeafList(freqs: List[(Char, Int)]): List[Leaf] = {
    def insert(x: (Char, Int), xs: List[Leaf]): List[Leaf] = {
      xs match {
        case List() => List(Leaf(x._1, x._2))
        case y::ys => if (x._2 <= y.weight) Leaf(x._1, x._2)::xs else y::insert(x, ys)
      }
    }

    freqs match {
      case List() => List()
      case x::xs => insert(x, makeOrderedLeafList(xs))
    }
  }

  /**
   * Checks whether the list `trees` contains only one single code tree.
   */
  def singleton(trees: List[CodeTree]): Boolean = trees match {
    case List(_) => true
    case _ => false
  }

  /**
   * The parameter `trees` of this function is a list of code trees ordered
   * by ascending weights.
   *
   * This function takes the first two elements of the list `trees` and combines
   * them into a single `Fork` node. This node is then added back into the
   * remaining elements of `trees` at a position such that the ordering by weights
   * is preserved.
   *
   * If `trees` is a list of less than two elements, that list should be returned
   * unchanged.
   */
  def combine(trees: List[CodeTree]): List[CodeTree] = {
    def insert1(x: CodeTree, xs: List[CodeTree]): List[CodeTree] = {
      x::xs match {
        case x::List() => List(x)
        case Leaf(_,w1)::Leaf(c2,w2)::ys => if (w1 <= w2) x::xs else Leaf(c2,w2)::insert1(x, ys)
        case Fork(_,_,_,w1)::Leaf(c2,w2)::ys => if (w1 <= w2) x::xs else Leaf(c2,w2)::insert1(x, ys)
        case Leaf(_,w1)::Fork(l,r,c2,w2)::ys => if (w1 <= w2) x::xs else Fork(l,r,c2,w2)::insert1(x, ys)
        case Fork(_,_,_,w1)::Fork(l2,r2,c2,w2)::ys => if (w1 <= w2) x::xs else Fork(l2,r2,c2,w2)::insert1(x, ys)
      }
    }
    def insert2(x: Char, xs: List[Char]): List[Char] = {
      x::xs match {
        case x::List() => List(x)
        case x::y::ys => {
          if (x==y) y::ys
          else if (x<y) x::xs
          else y::insert2(x, ys)
        }
      }
    }
    @scala.annotation.tailrec
    def insertForEach(xs: List[Char], ys: List[Char]): List[Char] = {
      xs match {
        case List() => ys
        case h::t => insertForEach(t, insert2(h, ys))
      }
    }

    trees match {
      case Leaf(c1, w1)::Leaf(c2, w2)::tail => insert1(Fork(Leaf(c1, w1), Leaf(c2, w2), List(c1, c2), w1+w2), tail)
      case Fork(l,r,c1,w1)::Leaf(c2,w2)::tail => insert1(Fork(l,r,insert2(c2, c1), w1+w2), tail)
      case Leaf(c1,w1)::Fork(l,r,c2,w2)::tail => insert1(Fork(l,r,insert2(c1, c2), w1+w2), tail)
      case (f1@Fork(_,_,c1,w1))::(f2@Fork(_,_,c2,w2))::tail => insert1(Fork(f1, f2, insertForEach(c1,c2), w1+w2), tail)
      case _ => trees
    }
  }

  /**
   * This function will be called in the following way:
   *
   *   until(singleton, combine)(trees)
   *
   * where `trees` is of type `List[CodeTree]`, `singleton` and `combine` refer to
   * the two functions defined above.
   *
   * In such an invocation, `until` should call the two functions until the list of
   * code trees contains only one single tree, and then return that singleton list.
   */
  def until(done: List[CodeTree] => Boolean, merge: List[CodeTree] => List[CodeTree])(trees: List[CodeTree]): List[CodeTree] = {
    val comb = merge(trees)
    if (done(comb)) comb else until(done, merge)(comb)
  }

  /**
   * This function creates a code tree which is optimal to encode the text `chars`.
   *
   * The parameter `chars` is an arbitrary text. This function extracts the character
   * frequencies from that text and creates a code tree based on them.
   */
  def createCodeTree(chars: List[Char]): CodeTree = {
    val leaves = makeOrderedLeafList(times(chars))
    until(singleton, combine)(leaves).head
  }


  // Part 3: Decoding

  type Bit = Int

  /**
   * This function decodes the bit sequence `bits` using the code tree `tree` and returns
   * the resulting list of characters.
   */
  def decode(tree: CodeTree, bits: List[Bit]): List[Char] = {
    def decodeHelp(t: CodeTree, b: List[Bit]): (Char, List[Bit]) = {
      t match {
        case Leaf(c, _) => (c, b)
        case Fork(l,r,_,_) => if (b.head == 0) decodeHelp(l, b.tail) else decodeHelp(r, b.tail)
      }
    }
    if (bits.isEmpty) List()
    else {
      val decoded = decodeHelp(tree, bits)
      if (decoded._2.isEmpty) List(decoded._1)
      else decoded._1 :: decode(tree, decoded._2)
    }
  }


  /**
   * A Huffman coding tree for the French language.
   * Generated from the data given at
   *   http://fr.wikipedia.org/wiki/Fr%C3%A9quence_d%27apparition_des_lettres_en_fran%C3%A7ais
   */
  val frenchCode: CodeTree = Fork(Fork(Fork(Leaf('s',121895),Fork(Leaf('d',56269),Fork(Fork(Fork(Leaf('x',5928),Leaf('j',8351),List('x','j'),14279),Leaf('f',16351),List('x','j','f'),30630),Fork(Fork(Fork(Fork(Leaf('z',2093),Fork(Leaf('k',745),Leaf('w',1747),List('k','w'),2492),List('z','k','w'),4585),Leaf('y',4725),List('z','k','w','y'),9310),Leaf('h',11298),List('z','k','w','y','h'),20608),Leaf('q',20889),List('z','k','w','y','h','q'),41497),List('x','j','f','z','k','w','y','h','q'),72127),List('d','x','j','f','z','k','w','y','h','q'),128396),List('s','d','x','j','f','z','k','w','y','h','q'),250291),Fork(Fork(Leaf('o',82762),Leaf('l',83668),List('o','l'),166430),Fork(Fork(Leaf('m',45521),Leaf('p',46335),List('m','p'),91856),Leaf('u',96785),List('m','p','u'),188641),List('o','l','m','p','u'),355071),List('s','d','x','j','f','z','k','w','y','h','q','o','l','m','p','u'),605362),Fork(Fork(Fork(Leaf('r',100500),Fork(Leaf('c',50003),Fork(Leaf('v',24975),Fork(Leaf('g',13288),Leaf('b',13822),List('g','b'),27110),List('v','g','b'),52085),List('c','v','g','b'),102088),List('r','c','v','g','b'),202588),Fork(Leaf('n',108812),Leaf('t',111103),List('n','t'),219915),List('r','c','v','g','b','n','t'),422503),Fork(Leaf('e',225947),Fork(Leaf('i',115465),Leaf('a',117110),List('i','a'),232575),List('e','i','a'),458522),List('r','c','v','g','b','n','t','e','i','a'),881025),List('s','d','x','j','f','z','k','w','y','h','q','o','l','m','p','u','r','c','v','g','b','n','t','e','i','a'),1486387)

  /**
   * What does the secret message say? Can you decode it?
   * For the decoding use the 'frenchCode' Huffman tree defined above.
   */
  val secret: List[Bit] = List(0,0,1,1,1,0,1,0,1,1,1,0,0,1,1,0,1,0,0,1,1,0,1,0,1,1,0,0,1,1,1,1,1,0,1,0,1,1,0,0,0,0,1,0,1,1,1,0,0,1,0,0,1,0,0,0,1,0,0,0,1,0,1)

  /**
   * Write a function that returns the decoded secret
   */
  def decodedSecret: List[Char] = decode(frenchCode, secret)


  // Part 4a: Encoding using Huffman tree

  /**
   * This function encodes `text` using the code tree `tree`
   * into a sequence of bits.
   */
  def encode(tree: CodeTree)(text: List[Char]): List[Bit] = {
    @scala.annotation.tailrec
    def encodeChar(t: CodeTree, c: Char, acc: List[Bit]): List[Bit] = {
      t match {
        case Leaf(_, _) => acc
        case Fork(l,r,_,_) => if (isIn(c, l)) encodeChar(l, c, 0::acc) else encodeChar(r, c, 1::acc)
      }
    }
    def isIn(c: Char, t: CodeTree): Boolean = {
      @scala.annotation.tailrec
      def inList(xs: List[Char]): Boolean ={
        xs match {
          case List() => false
          case x::xs => if (x == c) true else inList(xs)
        }
      }
      t match {
        case Leaf(x, _) => x == c
        case Fork(_,_,xs,_) => inList(xs)
      }
    }
    if (text.isEmpty) List()
    else encodeChar(tree, text.head, List()):::encode(tree)(text.tail)
  }

  // Part 4b: Encoding using code table

  type CodeTable = List[(Char, List[Bit])]

  /**
   * This function returns the bit sequence that represents the character `char` in
   * the code table `table`.
   */
  def codeBits(table: CodeTable)(char: Char): List[Bit] = {
    if (table.head._1 == char) table.head._2
    else codeBits(table.tail)(char)
  }

  /**
   * Given a code tree, create a code table which contains, for every character in the
   * code tree, the sequence of bits representing that character.
   *
   * Hint: think of a recursive solution: every sub-tree of the code tree `tree` is itself
   * a valid code tree that can be represented as a code table. Using the code tables of the
   * sub-trees, think of how to build the code table for the entire tree.
   */
  def convert(tree: CodeTree): CodeTable = {
    def preprendForEach(ct: CodeTable, bit: Bit): CodeTable = {
      ct match {
        case List() => List()
        case h::t => (h._1, bit::h._2)::preprendForEach(t, bit)
      }
    }

    tree match {
      case Leaf(c,_) => List((c, List()))
      case Fork(l,r,_,_) => preprendForEach(convert(l),0) ::: preprendForEach(convert(r), 1)
    }
  }

  /**
   * This function takes two code tables and merges them into one. Depending on how you
   * use it in the `convert` method above, this merge method might also do some transformations
   * on the two parameter code tables.
   */
  def mergeCodeTables(a: CodeTable, b: CodeTable): CodeTable = a:::b

  /**
   * This function encodes `text` according to the code tree `tree`.
   *
   * To speed up the encoding process, it first converts the code tree to a code table
   * and then uses it to perform the actual encoding.
   */
  def quickEncode(tree: CodeTree)(text: List[Char]): List[Bit] = {
    val treeTable = convert(tree)
    def encodeText(t: List[Char]): List[Bit] = {
      def encodeChar(c: Char, table: CodeTable): List[Bit] = if (table.head._1 == c) table.head._2 else encodeChar(c, table.tail)
      if (t.isEmpty) List() else encodeChar(t.head, treeTable):::encodeText(t.tail)
    }
    encodeText(text)
  }
}

object Huffman extends Huffman
