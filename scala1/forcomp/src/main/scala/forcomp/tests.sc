import forcomp.Anagrams.Occurrences

def subtract(x: Occurrences, y: Occurrences): Occurrences = {
  val yMap = y.toMap withDefaultValue 0
  for ((c, n) <- x if n > yMap(c)) yield (c, n - yMap(c))
}
//    if (k > n) xMap + (c -> (k-n))
//    else xMap - c

val xx = List(('a', 2), ('b', 3), ('c', 4))
val yy = List(('a', 1), ('b', 3))

subtract(xx, yy)