// !WITH_NEW_INFERENCE

fun <T : Any> nullable(): T? = null

val value = nullable<Int>() <!NI;TYPE_MISMATCH!>?:<!> <!OI;TYPE_INFERENCE_EXPECTED_TYPE_MISMATCH!>nullable()<!>