// !DUMP_CFG

open class A(
    open val a1: String,
    val a2: Int,
    a3: String
) {
    val a4 = a3.length
    val a5 = baseCall()
    open val a6: Int

    init {
        a6 = <!POSSIBLE_LEAKING_THIS_IN_CONSTRUCTOR!>a5<!>.length
        baseCall()
    }

    open fun baseCall(): String {
        return <!POSSIBLE_LEAKING_THIS_IN_CONSTRUCTOR!>a6<!>.toString()
    }

    fun checkA(): Boolean {
        return true
    }
}

class B(
    override val a1: String,
    val b1: String,
    p2: Int,
    p3: String
) : A(a1, p2, p3) {

    val b2: String
    val b3: String

    init {
        b3 = b1
        wrongCall()
        b2 = a1
    }

//    override fun baseCall(): String {
//        return "a6.toString()"
//    }

    private fun wrongCall() {
        <!POSSIBLE_LEAKING_THIS_IN_CONSTRUCTOR!>b2<!>.length
    }

}


open class C(
    open val c1: String,
    c2: Int
) {

    val c3: String

    init {
        c3 = baseCall() + c2
    }

    open fun baseCall(): String {
        return "a6.toString()"
    }

    private fun wrongCall():Int {
        return c1.length
    }
}


class D(override val c1: String, c2:Int):C(c1,c2){
    override fun baseCall(): String {
        return <!POSSIBLE_LEAKING_THIS_IN_CONSTRUCTOR!>c3<!>.length.toString() + "s" + c1
    }
}

