class B {
    private val p1 = <!POSSIBLE_LEAKING_THIS_IN_CONSTRUCTOR!>initP1()<!>
    private val p2: String = "p2"
    private val p1Length: Int
        get() = p2.length


    private fun initP1(): Int {
        return p1Length
    }

}
