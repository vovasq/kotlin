class A {

    val name = "lol"
    var check: Int

    <!POSSIBLE_LEAKING_THIS_IN_CONSTRUCTOR!>constructor() {
        check = foo(name)
    }<!>

    fun foo(s: String) {
        return s.length
    }

    fun checkName(): Int {
        if (name != null) return 1
        else return 2
    }
}

