// !DUMP_CFG

class Q {

    private val k = {
        SUPER_QUALIFIER.call()
    }

    private val SUPER_QUALIFIER = object : I {
        override val i1: String?
            get() = "da"

        override fun call() {
            val s = "s"
            s.length
        }
    }

}

interface I {
    val i1: String?

    fun call()

    fun isOk() = false

    fun getLen(p: Int) = p.toString()

}