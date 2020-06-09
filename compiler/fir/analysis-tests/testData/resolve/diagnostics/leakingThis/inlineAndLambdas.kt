// !DUMP_CFG

inline fun run(block: () -> Unit) {
    block()
}

class A {
    val p1: String
    val p2: String
    val p3 = {
        val k = { "adsnkjsa" + p2 }
        1 + p1.length
    }
    val p4 = fun(i: Int): String { return i.toString() }
    val p5: (Int) -> String
    val p6: (Int) -> String
    val p7: (Unit) -> String
    val p8: String
        get() = "dasd" + p1
    val p9: String
        get() = "dasd" + p1

    init {
        val local1 = { 22 + p1.length }
        val local2 = fun(i: Int) { return "dasjkdnsa" + i.toString() }
        p2 = "dsasdsa"
        p6 = { i: Int -> <!POSSIBLE_LEAKING_THIS_IN_CONSTRUCTOR!>p1<!>+"dadsa" }
        p5 = local2
        p7 = { "ewqeqweqw" }
        <!POSSIBLE_LEAKING_THIS_IN_CONSTRUCTOR!>p8<!>.length
        p1 = "asa"
        p9.length
    }
}

class B(val p1:String) {
    val p2: String
        get() = p1 + "sda" + p3 + <!POSSIBLE_LEAKING_THIS_IN_CONSTRUCTOR!>p4<!>.length.toString()

    val p3 = "p3"
    val p4: String

    init {
        <!POSSIBLE_LEAKING_THIS_IN_CONSTRUCTOR!>p2<!>.length
        p4 = "asd"
    }
}