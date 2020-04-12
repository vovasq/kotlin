package com.vovas.examples.test.positive


class A2 {
    private val p1 = initP1()
    private val p2: String = "p2"

    private fun initP1(): Int {
        return p2.length // LEAKING THIS HERE
    }
}
