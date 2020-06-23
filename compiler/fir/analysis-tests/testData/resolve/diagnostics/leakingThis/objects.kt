// !DUMP_CFG

open class Base{

    protected val SUPER_QUALIFIER = object : I {
        override val i1: String?
            get() = "da"

        override fun call(): String {
            val s = "s"
            s.length
            return s
        }
    }
}

class Q : Base(){

    private val k = k()

    fun k() : String{
        return SUPER_QUALIFIER.call()
    }

}

interface I {
    val i1: String?

    fun call():String

    fun isOk() = false

    fun getLen(p: Int) = p.toString()

}