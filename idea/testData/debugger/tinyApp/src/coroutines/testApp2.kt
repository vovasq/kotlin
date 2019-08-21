package testApp2

import kotlinx.coroutines.*
import kotlin.random.Random

fun main() {
    val coroutine1 = GlobalScope.launch {
        fun1()
    }

    val coroutine2 = GlobalScope.launch {
        delay(2000)
    }
    runBlocking {
        coroutine1.join()
    }
}

suspend fun fun1() {
    val k = "somebody once told me"
    delay(500)
    //Breakpoint!
    val l = "the world is gonna roll me"
}