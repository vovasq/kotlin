// !DUMP_CFG
open class A(
    val p0: String,
    val p4: String,
    val p5:Int = 0
) {
    val p1 = memberCall1()
    val p2: String
    val p3 = p0.length

    init {
        p2 = p0.length.toString() + "dsadsa" + p3.toString() + p4.toString() + p5.toString()
    }

    private fun memberCall1(): String {
        return memberCall2()
    }

    private fun memberCall2(): String {
        if ( <!POSSIBLE_LEAKING_THIS_IN_CONSTRUCTOR!>p2<!>.length != 0){
            return p2 + "sadsa" + p0
        }  else {
            return "empty"
        }
    }
}


class BB(val b1:String, p0: String, p4: String):A(p0, p4){

}

class B(p0: String) {
    val p1 = p0
    val p2: Int = p0.length
    var p3: String

    constructor(p0: String, p1: String):this(p0){
        p3 = p1
    }

    constructor(p0: String, i: Int):this(p0){
        p3 = p0 + i.toString()
    }

    init {
        p3 = ""
    }
}



