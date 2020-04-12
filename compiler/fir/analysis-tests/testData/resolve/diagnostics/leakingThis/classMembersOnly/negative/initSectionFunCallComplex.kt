// PROBLEM: none

class E {
    val p1: Int
    var p2: String

    init {
        p2 = "p2"
        p1 = initP1() // OK
    }

    private fun initP1(): Int {
        return getLength()
    }

    private fun getLength() = p2.length
}
