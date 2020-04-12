// PROBLEM: none

class C2N {
    val p1: Int
    var p2 = "p2"

    init {
        p1 = initP1()
    }

    private fun initP1(): Int {
        return p2.length // OK
    }
}