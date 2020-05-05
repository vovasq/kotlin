class C {
    val p1 = "kek"
    val p2: Int
    val p3: String


    init {
        initator()
        p3 = "ura"
        p2 = p3.length
    }

    fun initator() {
        <!POSSIBLE_LEAKING_THIS_IN_CONSTRUCTOR!>p3<!>.length
    }

}