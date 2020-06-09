// !DUMP_CFG
class D {
    val p1 = memberCall1()
    val p2: String
    lateinit var p3: String
    val p3: String
    init {
        p2 = "dsadsa"
    }

    private fun memberCall1(): String {
        return memberCall2()
    }

    private fun memberCall2(): String {
        if ( <!POSSIBLE_LEAKING_THIS_IN_CONSTRUCTOR!>p2<!>.length != 0){
            return p2 + "sadsa"
        }  else {
            return "empty"
        }
    }
}

class Resource{
    val r1 = "dsas"
}

class Owner {
    val valResource: Resource by ResourceDelegate()
    val p1: String = valResource.r1
}

class ResourceDelegate {
    operator fun getValue(thisRef: Owner, property: KProperty<*>): Resource {
        return Resource()
    }
}
