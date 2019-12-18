// PROBLEM: none

fun unsafe(string: String?) {
    val s: String? = null
    var i = 0
    i = 1
    var j = 0
    j = if (i % 2 == 0) s<caret>!!.length + string!!.length else 0
    var k = 0
    k = when (i) {
        0 -> s!!.length + string!!.length
        1 ->  2
        else ->  8
    }
}
