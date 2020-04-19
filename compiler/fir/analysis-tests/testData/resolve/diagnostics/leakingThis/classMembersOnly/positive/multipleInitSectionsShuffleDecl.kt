class G() {

    val p1: Int
    val p2: String

    init {
        <!POSSIBLE_LEAKING_THIS_IN_CONSTRUCTOR!>p1 = initP1()<!>
        p2 = "p2STr"
    }

    val p3: String
    private fun initP1() = p3.length

    init {
        p3 = "ooopsss"
    }
}
