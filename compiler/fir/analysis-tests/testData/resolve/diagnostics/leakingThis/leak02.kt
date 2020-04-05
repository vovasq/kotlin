class Foo {

    val field: String

    <!POSSIBLE_LEAKING_THIS_IN_CONSTRUCTOR!>init{
        test(this::class)
        foo()
    }<!>

    fun test(c: KClass<out Foo>) {

    }

    fun foo(){
        test(this::class)
    }



}

