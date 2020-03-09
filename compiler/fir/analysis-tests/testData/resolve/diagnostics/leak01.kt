class A {

//    init {
//    TODO:
//        foo(this)
//    }

    constructor() {
        <!POSSIBLE_LEAKING_THIS_IN_CONSTRUCTOR!>foo(this)<!>
    }

    fun foo(a: A) {

    }
}

