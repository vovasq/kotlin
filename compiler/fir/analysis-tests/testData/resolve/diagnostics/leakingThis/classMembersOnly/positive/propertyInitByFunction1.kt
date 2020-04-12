class A1 {
    private val p1 = <!POSSIBLE_LEAKING_THIS_IN_CONSTRUCTOR!>initP1()<!>
    private val p2: String

    init {
        p2 = "p2"
    }

    private fun initP1(): Int {
        return p2.length // LEAKING THIS HERE
    }

}
