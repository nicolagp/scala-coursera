package funsets

object Main extends App {
  import FunSets._
//  println(contains(singletonSet(1), 1))

  val test: FunSet = (x: Int) => (x == 1) || (x == 2) || (x == 3) || (x == 4)
  val s = map(test, (x: Int) => 2*x)
  println(FunSets.toString(s))

}
