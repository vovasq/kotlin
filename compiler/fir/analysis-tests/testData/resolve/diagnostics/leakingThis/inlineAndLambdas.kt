// !DUMP_CFG

class A {
    val p1 = "asa"
    val p2: String
    val p3 = notInline { 3 }

    init {
        inLineCatch { p1.length }
        val local1 = notInline { 1 }
        p2 = "dsadsa"
        notInline { p1.length }
    }

    private inline fun inLineCatch(f: () -> Int){
        <!POSSIBLE_LEAKING_THIS_IN_CONSTRUCTOR!>p2<!>.length
        f()
    }
    // todo
    private fun notInline(f: () -> Int){
        memberCall1()
        f()
        memberCall1()
    }

    private fun memberCall1(){

    }

}
