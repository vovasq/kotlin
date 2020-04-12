// PROBLEM: none

class F {
    val p1: Int
    var p2: String

    init {
        p2 = "p2"
        p1 = initP1() // OK
    }

    private fun initP1():Int{
        return initP1AsParam(p2)
    }

    private fun initP1AsParam(s:String): Int {
        return s.length
    }
}
