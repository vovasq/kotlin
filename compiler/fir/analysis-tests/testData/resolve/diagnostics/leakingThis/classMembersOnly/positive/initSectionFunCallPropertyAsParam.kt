class F {
    val p1: Int
    var p2: String

    init {
        <!POSSIBLE_LEAKING_THIS_IN_CONSTRUCTOR!>p1 = initP1()<!>
        p2 = "p2"
    }

    private fun initP1(): Int {
        return initP1AsParam(p2)
    }

    private fun initP1AsParam(s: String): Int {
        return s.length
    }
}
