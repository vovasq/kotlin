// !DUMP_CFG

abstract class BaseA{
    abstract val a1: String
    abstract val a2: Int
    val a3 = a1.length
    open fun kek() = a1.length
}

class DerivedA(override val a2: Int, p1: String) : BaseA(){
    override val a1: String
        get() = a2.toString()
    init {
        a2.toString()
    }
}

interface I {
    val i1: String?

    fun call()

    fun isOk() = false

    fun getLen(p: Int) = p.toString()

}

private val SUPER_QUALIFIER = object : I {
    override val i1: String?
        get() = "da"

    override fun call() {
        val s = "s"
        s.length

    }
}

abstract class BaseK{
    abstract val a1: String
    abstract val a2: Int
    val a3 = kek()
    abstract fun kek(): Int
}

abstract class BaseB : BaseK(){
    abstract val b2:String
}

class LLL(override val a2: Int, p1: String) : BaseB(){
    override val a1: String
        get() = a2.toString()

    override val b2: String

    init {
        a2.toString()
        b2 = "b2"
    }

    override fun kek(): Int{
        return <!POSSIBLE_LEAKING_THIS_IN_CONSTRUCTOR!>b2<!>.length
    }
}

