// !JVM_DEFAULT_MODE: all
// TARGET_BACKEND: JVM
// FILE: Simple.java

public interface Simple extends KInterface {
    default String test() {
        return test2();
    }
}

// FILE: Foo.java
public class Foo implements Simple {

}

// FILE: main.kt
// JVM_TARGET: 1.8
// WITH_RUNTIME

interface KInterface  {
    fun test2(): String {
        return "OK"
    }
}


fun box(): String {
    val result = Foo().test()
    if (result != "OK") return "fail 1: ${result}"

    return Foo().test2()
}
