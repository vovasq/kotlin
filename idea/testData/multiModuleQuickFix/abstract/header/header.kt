// "Create actual class for module testModule_JS (JS)" "true"
// ERROR: Expected class 'Abstract' has no actual declaration in module testModule_JS for JS

expect abstract class <caret>Abstract {
    fun foo(param: String): Int

    abstract fun String.bar(y: Double): Boolean

    val isGood: Boolean

    abstract var status: Int
}