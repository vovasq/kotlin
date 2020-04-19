class H(val p1: String, _p4: String) {
    val p2: String
    val p3: Int
    val p4: String

    init {
        <!POSSIBLE_LEAKING_THIS_IN_CONSTRUCTOR!>p2 = initP2()<!>
    }

    private fun initP2() = p4.length

    init {
        p4 = _p4
    }
}
