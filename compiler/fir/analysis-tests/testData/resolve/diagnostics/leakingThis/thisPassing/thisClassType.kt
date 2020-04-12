// PROBLEM: none
import kotlin.reflect.KClass

open class Foo {
    init {
        test(this::class)
    }
}

fun test(c: KClass<out Foo>) {

}

class Bar : Foo()

fun main(args: Array<String>) {
    Foo()
    Bar()
}
