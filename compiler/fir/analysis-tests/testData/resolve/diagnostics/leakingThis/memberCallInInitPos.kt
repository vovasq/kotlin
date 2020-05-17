class C {
    val p1 = "kek"
    val p2: Int
    val p3: String
    val p4: String

    init {
        memberCall1()
        p3 = "ura"
        p2 = p3.length
        p4 = "intiniada"
    }

    fun memberCall1() {
        memberCall2()
        <!POSSIBLE_LEAKING_THIS_IN_CONSTRUCTOR!>p3<!>.length
    }

    fun memberCall2() {
        fun localCallable(){
            <!POSSIBLE_LEAKING_THIS_IN_CONSTRUCTOR!>p4<!>.length
        }

        localCallable()
//       TODO memberCall3()
    }

    fun memberCall3() {
        memberCall2()
    }

}