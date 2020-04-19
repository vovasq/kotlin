class E {
    val p1: Int
    var p2: String
    var p3 = initP1()
    var p4 = "sldlasdsa"
    val p5 = String("sadsa")

    init {
        <!POSSIBLE_LEAKING_THIS_IN_CONSTRUCTOR!>p1 = initP1()<!>
        getLength(p2)
        p2 = "p2"
        lel(this)
    }

    private fun initP1(): Int {
        return getLength()
    }

    private fun getLength() = if (p2.length > 0) 1 else 0

    private fun lel(e: E){
        e.getLength()
    }

    private fun getLength(s: String): Int {

        if (s.length > 0)
            return s.length
        else {
            p3 = "asdsa"
            return p3.length
        }
    }

}
