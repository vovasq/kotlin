// !DUMP_CFG

abstract class BaseA{
    abstract val a1: String
    abstract val a2: Int
    open fun kek() = a1.length
}

class DerivedA(override val a2: Int, p1: String) : BaseA(){
    override val a1: String
        get() = a2.toString()
    init {
        a2.toString()
    }
}

