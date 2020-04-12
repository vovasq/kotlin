class D() {
    val p1: Int
    val p2: String
    val p3: String
    val p4: String


    init {
        p2 = "p2STr"
    }

    private fun initP1() = p4.length

    init {
        p3 = "p3potri"
        <!POSSIBLE_LEAKING_THIS_IN_CONSTRUCTOR!>p1 = initP1()<!>
        p4 = "ooopsss"
    }

}
