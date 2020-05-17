class A{
    val p1 : String
    val p2 : Int
    val p3 : String

    init {
        p1 = "P1"
        memberCall1()
        p2 = p1.length
        p3 = "danklds"
        memberCall2()
    }

    fun memberCall1 (){
        B().foreighnCall1(this)
    }

    fun memberCall2(){
        B().foreighnCall2(this.p3)
    }

}

class B{
    fun foreighnCall1(a: A){
        a.p1.length
        a.<!POSSIBLE_LEAKING_THIS_IN_CONSTRUCTOR!>p3<!>.length
    }

    fun foreighnCall2(s: String){
        s.length
    }
}