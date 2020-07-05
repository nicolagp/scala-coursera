object currying extends App  {
    def product(f: Int => Int)(a: Int, b: Int): Int = {
        if (a>b) 1 else f(a) * product(f)(a+1, b)
    }

    def factorial(a: Int, b: Int): Int = product(x=>x)(a,b)

    def mapReduce(f: Int => Int, combine: (Int, Int) => Int, zero: Int)(a: Int, b: Int): Int = {
        if (a>b) zero else combine(f(a), mapReduce(f, combine, zero)(a+1, b))
    }

    println("Sum of 1...3: " + mapReduce(x=>x, (x, y)=>x+y, 0)(1, 3))
    println("Prod of 1...3: " + mapReduce(x=>x, (x, y)=>x*y, 1)(1, 3))
    println("Sum of squares 1...3: " + mapReduce(x=>x*x, (x, y)=>x+y, 0)(1, 3))
}