package foo


open class A

trait B

class C: A(), B

fun box(): String {

    val a = A()
    val b = object : B {}
    val c = C()

    if (a is B) return "a is B"
    if (b !is B) return "b !is B"
    if (c !is B) return "c !is B"
    return "OK"
}