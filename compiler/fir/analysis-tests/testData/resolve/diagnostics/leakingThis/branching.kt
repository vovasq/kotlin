// !DUMP_CFG

class A{
    val p1 = true
    val p2 = false
    val p3: String
    val p4: String
    val p5: Any
    val p6: String
    val p7: String
    init {
        if(p1 && p2 || p2){
            p3 = "if"
        }else{
            p3 = "else"
        }
        p3.length

        if(p1 && p2 || p2){
            p4 = "daas"
        }
        <!POSSIBLE_LEAKING_THIS_IN_CONSTRUCTOR!>p4<!>.length
        p5 = Short
        p6 = "p6 str"
        call()
        while (p1)
        {
          p7 = ""
        }
        <!POSSIBLE_LEAKING_THIS_IN_CONSTRUCTOR!>p7<!>.length
    }


    fun call(){
        if(p1 && p2 || p2){
            // ups
        }else{
            p3 = "else"
        }
        p6.length
    }
}
