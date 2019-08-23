package testApp1

import kotlinx.coroutines.*
import kotlin.random.Random
import kotlinx.coroutines.channels.Channel

val channel = Channel<Int>()
fun main() {
    val coroutine1 = GlobalScope.launch {
        fun1()
    }
    runBlocking {
        channel.send(5)
        coroutine1.join()
    }
}

suspend fun fun1() {
    val k = 8 // avoid tail call optimization
    fun2()

}

suspend fun fun2() {
    val k = 8
    var l = "somebody once told the world is gonna roll me"
    fun3()
}

suspend fun fun3() {
    val k = 8
    fun4()

}

suspend fun fun4() {
    val k = 8
    fun5()

}

suspend fun fun5() {
    val k = 8
    fun6()

}

suspend fun fun6() {
    val k = 8
    fun7()

}

suspend fun fun7() {
    val k = 8
    fun8()

}

suspend fun fun8() {
    val k = 8
    fun9()

}

suspend fun fun9() {
    val k = 8
    channel.receive()
    fun10()
}

fun fun10() {
    //Breakpoint!
    Unit
}