package recfun

object RecFun extends RecFunInterface {

    def main(args: Array[String]): Unit = {
        println("Pascal's Triangle")
        for (row <- 0 to 10) {
            for (col <- 0 to row)
                print(s"${pascal(col, row)} ")
            println()
        }
    }

    /**
     * Exercise 1
     */
    def pascal(c: Int, r: Int): Int = {
        // definition: pascal(c,r) = pascal(c-1, r-1) + pascal(c, r-1)

        if (c < 0 || r < 0) 0 // BASE 1 check if out of bounds
        else if (c == 0 && r == 0) 1 // BASE 2 first value
        else pascal(c-1, r-1) + pascal(c, r-1)
    }

    /**
     * Exercise 2
     */
    def balance(chars: List[Char]): Boolean = {

        def balance_helper(chars: List[Char], stack: List[Char]): Boolean = {
            if (chars.isEmpty) stack.isEmpty // Base, finished parsing expression, if stack is empty it's balanced
            else if (isOpen(chars.head)) balance_helper(chars.tail, '('::stack)
            else if (isClose(chars.head))
                stack.nonEmpty &&
                matches(stack.head, chars.head) &&
                balance_helper(chars.tail, stack.tail)
            else balance_helper(chars.tail, stack)
        }

        def matches(left: Char, right: Char): Boolean = {
            if (left == '(' && right == ')') true
            else false
        }

        def isOpen(c: Char): Boolean = if (c == '(') true else false
        def isClose(c: Char): Boolean = if (c == ')') true else false

        balance_helper(chars, List())
    }

    /**
     * Exercise 3
     */
    def countChange(money: Int, coins: List[Int]): Int = {
        if (money == 0) 1
        else if (money < 0) 0
        else if (coins.isEmpty) 0
        else countChange(money-coins.head, coins) + countChange(money, coins.tail)
    }
}
