// PROBLEM: none

class C1N {
    val p1: Int
    var p2: String

    init {
        p2 = "p2"
        p1 = initP1()
    }

    private fun initP1(): Int {
        return p2.length // OK
    }

}