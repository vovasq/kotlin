// !DUMP_CFG

class A {
    val p1 = "kek"
    val p2: Int
    val p3: String

    init {
        p1.length
        <!POSSIBLE_LEAKING_THIS_IN_CONSTRUCTOR!>p3<!>.length
        p2 = p3.length
        p3 = String("asa")
    }

    fun tr() = true
    fun call(): Int{
        var a = 0

        if(tr()){
            a = 1
            return a
        }else{
            a = 2
            return a
        }
        a += 1
        return a
    }
}
