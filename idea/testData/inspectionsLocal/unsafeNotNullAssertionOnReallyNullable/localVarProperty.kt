// PROBLEM: none
class A {
    fun unsafeCall() {}
}

fun unsafe() {
    var a: A? = A()
    a<caret>!!.unsafeCall()
}
