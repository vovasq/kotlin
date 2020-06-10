// !DUMP_CFG

abstract class C {
    val c1: String = call()

    abstract fun call(): String

}

abstract class B : C() {

    abstract fun bCall()

    override fun call(): String {
        return "safe"
    }
}

class A : B() {

    val a1: String = "ura"

    override fun bCall() {

    }

    override fun call(): String {
        return "babah sss -> c1, a1!!"+ <!POSSIBLE_LEAKING_THIS_IN_CONSTRUCTOR!>c1<!>.length.toString() + <!POSSIBLE_LEAKING_THIS_IN_CONSTRUCTOR!>a1<!>.length.toString()
    }

}

abstract class BB: C(){
    abstract val bb1: String
}

class AA : BB(){
    override val bb1: String = "BB"

    override fun call(): String {
        return <!POSSIBLE_LEAKING_THIS_IN_CONSTRUCTOR!>bb1<!>.length.toString()
    }

}

