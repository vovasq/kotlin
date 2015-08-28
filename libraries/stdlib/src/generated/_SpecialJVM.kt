package kotlin

//
// NOTE THIS FILE IS AUTO-GENERATED by the GenerateStandardLib.kt
// See: https://github.com/JetBrains/kotlin/tree/master/libraries/stdlib
//

import kotlin.platform.*
import java.util.*

import java.util.Collections // TODO: it's temporary while we have java.util.Collections in js

/**
 * Returns a [List] that wraps the original array.
 */
public fun <T> Array<out T>.asList(): List<T> {
    return Arrays.asList(*this)
}

/**
 * Returns a [List] that wraps the original array.
 */
public fun BooleanArray.asList(): List<Boolean> {
    return object : AbstractList<Boolean>(), RandomAccess {
        override fun size(): Int = this@asList.size()
        override fun isEmpty(): Boolean = this@asList.isEmpty()
        override fun contains(o: Any?): Boolean = this@asList.contains(o as Boolean)
        override fun iterator(): MutableIterator<Boolean> = this@asList.iterator() as MutableIterator<Boolean>
        override fun get(index: Int): Boolean = this@asList[index]
        override fun indexOf(o: Any?): Int = this@asList.indexOf(o as Boolean)
        override fun lastIndexOf(o: Any?): Int = this@asList.lastIndexOf(o as Boolean)
    }
}

/**
 * Returns a [List] that wraps the original array.
 */
public fun ByteArray.asList(): List<Byte> {
    return object : AbstractList<Byte>(), RandomAccess {
        override fun size(): Int = this@asList.size()
        override fun isEmpty(): Boolean = this@asList.isEmpty()
        override fun contains(o: Any?): Boolean = this@asList.contains(o as Byte)
        override fun iterator(): MutableIterator<Byte> = this@asList.iterator() as MutableIterator<Byte>
        override fun get(index: Int): Byte = this@asList[index]
        override fun indexOf(o: Any?): Int = this@asList.indexOf(o as Byte)
        override fun lastIndexOf(o: Any?): Int = this@asList.lastIndexOf(o as Byte)
    }
}

/**
 * Returns a [List] that wraps the original array.
 */
public fun CharArray.asList(): List<Char> {
    return object : AbstractList<Char>(), RandomAccess {
        override fun size(): Int = this@asList.size()
        override fun isEmpty(): Boolean = this@asList.isEmpty()
        override fun contains(o: Any?): Boolean = this@asList.contains(o as Char)
        override fun iterator(): MutableIterator<Char> = this@asList.iterator() as MutableIterator<Char>
        override fun get(index: Int): Char = this@asList[index]
        override fun indexOf(o: Any?): Int = this@asList.indexOf(o as Char)
        override fun lastIndexOf(o: Any?): Int = this@asList.lastIndexOf(o as Char)
    }
}

/**
 * Returns a [List] that wraps the original array.
 */
public fun DoubleArray.asList(): List<Double> {
    return object : AbstractList<Double>(), RandomAccess {
        override fun size(): Int = this@asList.size()
        override fun isEmpty(): Boolean = this@asList.isEmpty()
        override fun contains(o: Any?): Boolean = this@asList.contains(o as Double)
        override fun iterator(): MutableIterator<Double> = this@asList.iterator() as MutableIterator<Double>
        override fun get(index: Int): Double = this@asList[index]
        override fun indexOf(o: Any?): Int = this@asList.indexOf(o as Double)
        override fun lastIndexOf(o: Any?): Int = this@asList.lastIndexOf(o as Double)
    }
}

/**
 * Returns a [List] that wraps the original array.
 */
public fun FloatArray.asList(): List<Float> {
    return object : AbstractList<Float>(), RandomAccess {
        override fun size(): Int = this@asList.size()
        override fun isEmpty(): Boolean = this@asList.isEmpty()
        override fun contains(o: Any?): Boolean = this@asList.contains(o as Float)
        override fun iterator(): MutableIterator<Float> = this@asList.iterator() as MutableIterator<Float>
        override fun get(index: Int): Float = this@asList[index]
        override fun indexOf(o: Any?): Int = this@asList.indexOf(o as Float)
        override fun lastIndexOf(o: Any?): Int = this@asList.lastIndexOf(o as Float)
    }
}

/**
 * Returns a [List] that wraps the original array.
 */
public fun IntArray.asList(): List<Int> {
    return object : AbstractList<Int>(), RandomAccess {
        override fun size(): Int = this@asList.size()
        override fun isEmpty(): Boolean = this@asList.isEmpty()
        override fun contains(o: Any?): Boolean = this@asList.contains(o as Int)
        override fun iterator(): MutableIterator<Int> = this@asList.iterator() as MutableIterator<Int>
        override fun get(index: Int): Int = this@asList[index]
        override fun indexOf(o: Any?): Int = this@asList.indexOf(o as Int)
        override fun lastIndexOf(o: Any?): Int = this@asList.lastIndexOf(o as Int)
    }
}

/**
 * Returns a [List] that wraps the original array.
 */
public fun LongArray.asList(): List<Long> {
    return object : AbstractList<Long>(), RandomAccess {
        override fun size(): Int = this@asList.size()
        override fun isEmpty(): Boolean = this@asList.isEmpty()
        override fun contains(o: Any?): Boolean = this@asList.contains(o as Long)
        override fun iterator(): MutableIterator<Long> = this@asList.iterator() as MutableIterator<Long>
        override fun get(index: Int): Long = this@asList[index]
        override fun indexOf(o: Any?): Int = this@asList.indexOf(o as Long)
        override fun lastIndexOf(o: Any?): Int = this@asList.lastIndexOf(o as Long)
    }
}

/**
 * Returns a [List] that wraps the original array.
 */
public fun ShortArray.asList(): List<Short> {
    return object : AbstractList<Short>(), RandomAccess {
        override fun size(): Int = this@asList.size()
        override fun isEmpty(): Boolean = this@asList.isEmpty()
        override fun contains(o: Any?): Boolean = this@asList.contains(o as Short)
        override fun iterator(): MutableIterator<Short> = this@asList.iterator() as MutableIterator<Short>
        override fun get(index: Int): Short = this@asList[index]
        override fun indexOf(o: Any?): Int = this@asList.indexOf(o as Short)
        override fun lastIndexOf(o: Any?): Int = this@asList.lastIndexOf(o as Short)
    }
}

/**
 * Searches array or range of array for provided element index using binary search algorithm. Array is expected to be sorted according to the specified [comparator].
 */
public fun <T> Array<out T>.binarySearch(element: T, comparator: Comparator<T>, fromIndex: Int = 0, toIndex: Int = size()): Int {
    return Arrays.binarySearch(this, fromIndex, toIndex, element, comparator)
}

/**
 * Searches array or range of array for provided element index using binary search algorithm. Array is expected to be sorted.
 */
public fun <T> Array<out T>.binarySearch(element: T, fromIndex: Int = 0, toIndex: Int = size()): Int {
    return Arrays.binarySearch(this, fromIndex, toIndex, element)
}

/**
 * Searches array or range of array for provided element index using binary search algorithm. Array is expected to be sorted.
 */
public fun ByteArray.binarySearch(element: Byte, fromIndex: Int = 0, toIndex: Int = size()): Int {
    return Arrays.binarySearch(this, fromIndex, toIndex, element)
}

/**
 * Searches array or range of array for provided element index using binary search algorithm. Array is expected to be sorted.
 */
public fun CharArray.binarySearch(element: Char, fromIndex: Int = 0, toIndex: Int = size()): Int {
    return Arrays.binarySearch(this, fromIndex, toIndex, element)
}

/**
 * Searches array or range of array for provided element index using binary search algorithm. Array is expected to be sorted.
 */
public fun DoubleArray.binarySearch(element: Double, fromIndex: Int = 0, toIndex: Int = size()): Int {
    return Arrays.binarySearch(this, fromIndex, toIndex, element)
}

/**
 * Searches array or range of array for provided element index using binary search algorithm. Array is expected to be sorted.
 */
public fun FloatArray.binarySearch(element: Float, fromIndex: Int = 0, toIndex: Int = size()): Int {
    return Arrays.binarySearch(this, fromIndex, toIndex, element)
}

/**
 * Searches array or range of array for provided element index using binary search algorithm. Array is expected to be sorted.
 */
public fun IntArray.binarySearch(element: Int, fromIndex: Int = 0, toIndex: Int = size()): Int {
    return Arrays.binarySearch(this, fromIndex, toIndex, element)
}

/**
 * Searches array or range of array for provided element index using binary search algorithm. Array is expected to be sorted.
 */
public fun LongArray.binarySearch(element: Long, fromIndex: Int = 0, toIndex: Int = size()): Int {
    return Arrays.binarySearch(this, fromIndex, toIndex, element)
}

/**
 * Searches array or range of array for provided element index using binary search algorithm. Array is expected to be sorted.
 */
public fun ShortArray.binarySearch(element: Short, fromIndex: Int = 0, toIndex: Int = size()): Int {
    return Arrays.binarySearch(this, fromIndex, toIndex, element)
}

/**
 * Returns new array which is a copy of the original array.
 */
public fun <T, A: Array<out T>> A.copyOf(): A {
    return Arrays.copyOf(this, size()) as A
}

/**
 * Returns new array which is a copy of the original array.
 */
public fun BooleanArray.copyOf(): BooleanArray {
    return Arrays.copyOf(this, size())
}

/**
 * Returns new array which is a copy of the original array.
 */
public fun ByteArray.copyOf(): ByteArray {
    return Arrays.copyOf(this, size())
}

/**
 * Returns new array which is a copy of the original array.
 */
public fun CharArray.copyOf(): CharArray {
    return Arrays.copyOf(this, size())
}

/**
 * Returns new array which is a copy of the original array.
 */
public fun DoubleArray.copyOf(): DoubleArray {
    return Arrays.copyOf(this, size())
}

/**
 * Returns new array which is a copy of the original array.
 */
public fun FloatArray.copyOf(): FloatArray {
    return Arrays.copyOf(this, size())
}

/**
 * Returns new array which is a copy of the original array.
 */
public fun IntArray.copyOf(): IntArray {
    return Arrays.copyOf(this, size())
}

/**
 * Returns new array which is a copy of the original array.
 */
public fun LongArray.copyOf(): LongArray {
    return Arrays.copyOf(this, size())
}

/**
 * Returns new array which is a copy of the original array.
 */
public fun ShortArray.copyOf(): ShortArray {
    return Arrays.copyOf(this, size())
}

/**
 * Returns new array which is a copy of the original array.
 */
public fun <T> Array<out T>.copyOf(newSize: Int): Array<out T?> {
    return Arrays.copyOf(this, newSize)
}

/**
 * Returns new array which is a copy of the original array.
 */
public fun BooleanArray.copyOf(newSize: Int): BooleanArray {
    return Arrays.copyOf(this, newSize)
}

/**
 * Returns new array which is a copy of the original array.
 */
public fun ByteArray.copyOf(newSize: Int): ByteArray {
    return Arrays.copyOf(this, newSize)
}

/**
 * Returns new array which is a copy of the original array.
 */
public fun CharArray.copyOf(newSize: Int): CharArray {
    return Arrays.copyOf(this, newSize)
}

/**
 * Returns new array which is a copy of the original array.
 */
public fun DoubleArray.copyOf(newSize: Int): DoubleArray {
    return Arrays.copyOf(this, newSize)
}

/**
 * Returns new array which is a copy of the original array.
 */
public fun FloatArray.copyOf(newSize: Int): FloatArray {
    return Arrays.copyOf(this, newSize)
}

/**
 * Returns new array which is a copy of the original array.
 */
public fun IntArray.copyOf(newSize: Int): IntArray {
    return Arrays.copyOf(this, newSize)
}

/**
 * Returns new array which is a copy of the original array.
 */
public fun LongArray.copyOf(newSize: Int): LongArray {
    return Arrays.copyOf(this, newSize)
}

/**
 * Returns new array which is a copy of the original array.
 */
public fun ShortArray.copyOf(newSize: Int): ShortArray {
    return Arrays.copyOf(this, newSize)
}

/**
 * Returns new array which is a copy of the original array.
 */
platformName("mutableCopyOf")
public fun <T> Array<T>.copyOf(newSize: Int): Array<T?> {
    return Arrays.copyOf(this, newSize)
}

/**
 * Returns new array which is a copy of range of original array.
 */
public fun <T, A: Array<out T>> A.copyOfRange(fromIndex: Int, toIndex: Int): A {
    return Arrays.copyOfRange(this, fromIndex, toIndex) as A
}

/**
 * Returns new array which is a copy of range of original array.
 */
public fun BooleanArray.copyOfRange(fromIndex: Int, toIndex: Int): BooleanArray {
    return Arrays.copyOfRange(this, fromIndex, toIndex)
}

/**
 * Returns new array which is a copy of range of original array.
 */
public fun ByteArray.copyOfRange(fromIndex: Int, toIndex: Int): ByteArray {
    return Arrays.copyOfRange(this, fromIndex, toIndex)
}

/**
 * Returns new array which is a copy of range of original array.
 */
public fun CharArray.copyOfRange(fromIndex: Int, toIndex: Int): CharArray {
    return Arrays.copyOfRange(this, fromIndex, toIndex)
}

/**
 * Returns new array which is a copy of range of original array.
 */
public fun DoubleArray.copyOfRange(fromIndex: Int, toIndex: Int): DoubleArray {
    return Arrays.copyOfRange(this, fromIndex, toIndex)
}

/**
 * Returns new array which is a copy of range of original array.
 */
public fun FloatArray.copyOfRange(fromIndex: Int, toIndex: Int): FloatArray {
    return Arrays.copyOfRange(this, fromIndex, toIndex)
}

/**
 * Returns new array which is a copy of range of original array.
 */
public fun IntArray.copyOfRange(fromIndex: Int, toIndex: Int): IntArray {
    return Arrays.copyOfRange(this, fromIndex, toIndex)
}

/**
 * Returns new array which is a copy of range of original array.
 */
public fun LongArray.copyOfRange(fromIndex: Int, toIndex: Int): LongArray {
    return Arrays.copyOfRange(this, fromIndex, toIndex)
}

/**
 * Returns new array which is a copy of range of original array.
 */
public fun ShortArray.copyOfRange(fromIndex: Int, toIndex: Int): ShortArray {
    return Arrays.copyOfRange(this, fromIndex, toIndex)
}

/**
 * Fills original array with the provided value.
 */
public fun BooleanArray.fill(element: Boolean, fromIndex: Int = 0, toIndex: Int = size()): Unit {
    Arrays.fill(this, fromIndex, toIndex, element)
}

/**
 * Fills original array with the provided value.
 */
public fun ByteArray.fill(element: Byte, fromIndex: Int = 0, toIndex: Int = size()): Unit {
    Arrays.fill(this, fromIndex, toIndex, element)
}

/**
 * Fills original array with the provided value.
 */
public fun CharArray.fill(element: Char, fromIndex: Int = 0, toIndex: Int = size()): Unit {
    Arrays.fill(this, fromIndex, toIndex, element)
}

/**
 * Fills original array with the provided value.
 */
public fun DoubleArray.fill(element: Double, fromIndex: Int = 0, toIndex: Int = size()): Unit {
    Arrays.fill(this, fromIndex, toIndex, element)
}

/**
 * Fills original array with the provided value.
 */
public fun FloatArray.fill(element: Float, fromIndex: Int = 0, toIndex: Int = size()): Unit {
    Arrays.fill(this, fromIndex, toIndex, element)
}

/**
 * Fills original array with the provided value.
 */
public fun IntArray.fill(element: Int, fromIndex: Int = 0, toIndex: Int = size()): Unit {
    Arrays.fill(this, fromIndex, toIndex, element)
}

/**
 * Fills original array with the provided value.
 */
public fun LongArray.fill(element: Long, fromIndex: Int = 0, toIndex: Int = size()): Unit {
    Arrays.fill(this, fromIndex, toIndex, element)
}

/**
 * Fills original array with the provided value.
 */
public fun ShortArray.fill(element: Short, fromIndex: Int = 0, toIndex: Int = size()): Unit {
    Arrays.fill(this, fromIndex, toIndex, element)
}

/**
 * Fills original array with the provided value.
 */
public fun <T> Array<T>.fill(element: T, fromIndex: Int = 0, toIndex: Int = size()): Unit {
    Arrays.fill(this, fromIndex, toIndex, element)
}

/**
 * Returns a list containing all elements that are instances of specified type parameter R.
 */
public inline fun <reified R> Array<*>.filterIsInstance(): List<R> {
    return filterIsInstanceTo(ArrayList<R>())
}

/**
 * Returns a list containing all elements that are instances of specified type parameter R.
 */
public inline fun <reified R> Iterable<*>.filterIsInstance(): List<R> {
    return filterIsInstanceTo(ArrayList<R>())
}

/**
 * Returns a sequence containing all elements that are instances of specified type parameter R.
 */
public inline fun <reified R> Sequence<*>.filterIsInstance(): Sequence<R> {
    return filter { it is R } as Sequence<R>
}

/**
 * Returns a list containing all elements that are instances of specified class.
 */
public fun <R> Array<*>.filterIsInstance(klass: Class<R>): List<R> {
    return filterIsInstanceTo(ArrayList<R>(), klass)
}

/**
 * Returns a list containing all elements that are instances of specified class.
 */
public fun <R> Iterable<*>.filterIsInstance(klass: Class<R>): List<R> {
    return filterIsInstanceTo(ArrayList<R>(), klass)
}

/**
 * Returns a sequence containing all elements that are instances of specified class.
 */
public fun <R> Sequence<*>.filterIsInstance(klass: Class<R>): Sequence<R> {
    return filter { klass.isInstance(it) } as Sequence<R>
}

/**
 * Appends all elements that are instances of specified type parameter R to the given [destination].
 */
public inline fun <reified R, C : MutableCollection<in R>> Array<*>.filterIsInstanceTo(destination: C): C {
    for (element in this) if (element is R) destination.add(element)
    return destination
}

/**
 * Appends all elements that are instances of specified type parameter R to the given [destination].
 */
public inline fun <reified R, C : MutableCollection<in R>> Iterable<*>.filterIsInstanceTo(destination: C): C {
    for (element in this) if (element is R) destination.add(element)
    return destination
}

/**
 * Appends all elements that are instances of specified type parameter R to the given [destination].
 */
public inline fun <reified R, C : MutableCollection<in R>> Sequence<*>.filterIsInstanceTo(destination: C): C {
    for (element in this) if (element is R) destination.add(element)
    return destination
}

/**
 * Appends all elements that are instances of specified class to the given [destination].
 */
public fun <C : MutableCollection<in R>, R> Array<*>.filterIsInstanceTo(destination: C, klass: Class<R>): C {
    for (element in this) if (klass.isInstance(element)) destination.add(element as R)
    return destination
}

/**
 * Appends all elements that are instances of specified class to the given [destination].
 */
public fun <C : MutableCollection<in R>, R> Iterable<*>.filterIsInstanceTo(destination: C, klass: Class<R>): C {
    for (element in this) if (klass.isInstance(element)) destination.add(element as R)
    return destination
}

/**
 * Appends all elements that are instances of specified class to the given [destination].
 */
public fun <C : MutableCollection<in R>, R> Sequence<*>.filterIsInstanceTo(destination: C, klass: Class<R>): C {
    for (element in this) if (klass.isInstance(element)) destination.add(element as R)
    return destination
}

/**
 * Returns an array containing all elements of the original array and then all elements of the given [array].
 */
public fun BooleanArray.plus(array: BooleanArray): BooleanArray {
    val thisSize = size()
    val arraySize = array.size()
    val result = Arrays.copyOf(this, thisSize + arraySize)
    System.arraycopy(array, 0, result, thisSize, arraySize)
    return result
}

/**
 * Returns an array containing all elements of the original array and then all elements of the given [array].
 */
public fun ByteArray.plus(array: ByteArray): ByteArray {
    val thisSize = size()
    val arraySize = array.size()
    val result = Arrays.copyOf(this, thisSize + arraySize)
    System.arraycopy(array, 0, result, thisSize, arraySize)
    return result
}

/**
 * Returns an array containing all elements of the original array and then all elements of the given [array].
 */
public fun CharArray.plus(array: CharArray): CharArray {
    val thisSize = size()
    val arraySize = array.size()
    val result = Arrays.copyOf(this, thisSize + arraySize)
    System.arraycopy(array, 0, result, thisSize, arraySize)
    return result
}

/**
 * Returns an array containing all elements of the original array and then all elements of the given [array].
 */
public fun DoubleArray.plus(array: DoubleArray): DoubleArray {
    val thisSize = size()
    val arraySize = array.size()
    val result = Arrays.copyOf(this, thisSize + arraySize)
    System.arraycopy(array, 0, result, thisSize, arraySize)
    return result
}

/**
 * Returns an array containing all elements of the original array and then all elements of the given [array].
 */
public fun FloatArray.plus(array: FloatArray): FloatArray {
    val thisSize = size()
    val arraySize = array.size()
    val result = Arrays.copyOf(this, thisSize + arraySize)
    System.arraycopy(array, 0, result, thisSize, arraySize)
    return result
}

/**
 * Returns an array containing all elements of the original array and then all elements of the given [array].
 */
public fun IntArray.plus(array: IntArray): IntArray {
    val thisSize = size()
    val arraySize = array.size()
    val result = Arrays.copyOf(this, thisSize + arraySize)
    System.arraycopy(array, 0, result, thisSize, arraySize)
    return result
}

/**
 * Returns an array containing all elements of the original array and then all elements of the given [array].
 */
public fun LongArray.plus(array: LongArray): LongArray {
    val thisSize = size()
    val arraySize = array.size()
    val result = Arrays.copyOf(this, thisSize + arraySize)
    System.arraycopy(array, 0, result, thisSize, arraySize)
    return result
}

/**
 * Returns an array containing all elements of the original array and then all elements of the given [array].
 */
public fun ShortArray.plus(array: ShortArray): ShortArray {
    val thisSize = size()
    val arraySize = array.size()
    val result = Arrays.copyOf(this, thisSize + arraySize)
    System.arraycopy(array, 0, result, thisSize, arraySize)
    return result
}

/**
 * Returns an array containing all elements of the original array and then all elements of the given [array].
 */
public fun <T> Array<T>.plus(array: Array<out T>): Array<T> {
    val thisSize = size()
    val arraySize = array.size()
    val result = Arrays.copyOf(this, thisSize + arraySize)
    System.arraycopy(array, 0, result, thisSize, arraySize)
    return result
}

/**
 * Returns an array containing all elements of the original array and then all elements of the given [collection].
 */
public fun BooleanArray.plus(collection: Collection<Boolean>): BooleanArray {
    var index = size()
    val result = Arrays.copyOf(this, index + collection.size())
    for (element in collection) result[index++] = element
    return result
}

/**
 * Returns an array containing all elements of the original array and then all elements of the given [collection].
 */
public fun ByteArray.plus(collection: Collection<Byte>): ByteArray {
    var index = size()
    val result = Arrays.copyOf(this, index + collection.size())
    for (element in collection) result[index++] = element
    return result
}

/**
 * Returns an array containing all elements of the original array and then all elements of the given [collection].
 */
public fun CharArray.plus(collection: Collection<Char>): CharArray {
    var index = size()
    val result = Arrays.copyOf(this, index + collection.size())
    for (element in collection) result[index++] = element
    return result
}

/**
 * Returns an array containing all elements of the original array and then all elements of the given [collection].
 */
public fun DoubleArray.plus(collection: Collection<Double>): DoubleArray {
    var index = size()
    val result = Arrays.copyOf(this, index + collection.size())
    for (element in collection) result[index++] = element
    return result
}

/**
 * Returns an array containing all elements of the original array and then all elements of the given [collection].
 */
public fun FloatArray.plus(collection: Collection<Float>): FloatArray {
    var index = size()
    val result = Arrays.copyOf(this, index + collection.size())
    for (element in collection) result[index++] = element
    return result
}

/**
 * Returns an array containing all elements of the original array and then all elements of the given [collection].
 */
public fun IntArray.plus(collection: Collection<Int>): IntArray {
    var index = size()
    val result = Arrays.copyOf(this, index + collection.size())
    for (element in collection) result[index++] = element
    return result
}

/**
 * Returns an array containing all elements of the original array and then all elements of the given [collection].
 */
public fun LongArray.plus(collection: Collection<Long>): LongArray {
    var index = size()
    val result = Arrays.copyOf(this, index + collection.size())
    for (element in collection) result[index++] = element
    return result
}

/**
 * Returns an array containing all elements of the original array and then all elements of the given [collection].
 */
public fun ShortArray.plus(collection: Collection<Short>): ShortArray {
    var index = size()
    val result = Arrays.copyOf(this, index + collection.size())
    for (element in collection) result[index++] = element
    return result
}

/**
 * Returns an array containing all elements of the original array and then all elements of the given [collection].
 */
public fun <T> Array<T>.plus(collection: Collection<T>): Array<T> {
    var index = size()
    val result = Arrays.copyOf(this, index + collection.size())
    for (element in collection) result[index++] = element
    return result
}

/**
 * Returns an array containing all elements of the original array and then the given [element].
 */
public fun BooleanArray.plus(element: Boolean): BooleanArray {
    val index = size()
    val result = Arrays.copyOf(this, index + 1)
    result[index] = element
    return result
}

/**
 * Returns an array containing all elements of the original array and then the given [element].
 */
public fun ByteArray.plus(element: Byte): ByteArray {
    val index = size()
    val result = Arrays.copyOf(this, index + 1)
    result[index] = element
    return result
}

/**
 * Returns an array containing all elements of the original array and then the given [element].
 */
public fun CharArray.plus(element: Char): CharArray {
    val index = size()
    val result = Arrays.copyOf(this, index + 1)
    result[index] = element
    return result
}

/**
 * Returns an array containing all elements of the original array and then the given [element].
 */
public fun DoubleArray.plus(element: Double): DoubleArray {
    val index = size()
    val result = Arrays.copyOf(this, index + 1)
    result[index] = element
    return result
}

/**
 * Returns an array containing all elements of the original array and then the given [element].
 */
public fun FloatArray.plus(element: Float): FloatArray {
    val index = size()
    val result = Arrays.copyOf(this, index + 1)
    result[index] = element
    return result
}

/**
 * Returns an array containing all elements of the original array and then the given [element].
 */
public fun IntArray.plus(element: Int): IntArray {
    val index = size()
    val result = Arrays.copyOf(this, index + 1)
    result[index] = element
    return result
}

/**
 * Returns an array containing all elements of the original array and then the given [element].
 */
public fun LongArray.plus(element: Long): LongArray {
    val index = size()
    val result = Arrays.copyOf(this, index + 1)
    result[index] = element
    return result
}

/**
 * Returns an array containing all elements of the original array and then the given [element].
 */
public fun ShortArray.plus(element: Short): ShortArray {
    val index = size()
    val result = Arrays.copyOf(this, index + 1)
    result[index] = element
    return result
}

/**
 * Returns an array containing all elements of the original array and then the given [element].
 */
public fun <T> Array<T>.plus(element: T): Array<T> {
    val index = size()
    val result = Arrays.copyOf(this, index + 1)
    result[index] = element
    return result
}

/**
 * Sorts array or range in array inplace.
 */
public fun <T> Array<out T>.sort(fromIndex: Int = 0, toIndex: Int = size()): Unit {
    Arrays.sort(this, fromIndex, toIndex)
}

/**
 * Sorts array or range in array inplace.
 */
public fun ByteArray.sort(fromIndex: Int = 0, toIndex: Int = size()): Unit {
    Arrays.sort(this, fromIndex, toIndex)
}

/**
 * Sorts array or range in array inplace.
 */
public fun CharArray.sort(fromIndex: Int = 0, toIndex: Int = size()): Unit {
    Arrays.sort(this, fromIndex, toIndex)
}

/**
 * Sorts array or range in array inplace.
 */
public fun DoubleArray.sort(fromIndex: Int = 0, toIndex: Int = size()): Unit {
    Arrays.sort(this, fromIndex, toIndex)
}

/**
 * Sorts array or range in array inplace.
 */
public fun FloatArray.sort(fromIndex: Int = 0, toIndex: Int = size()): Unit {
    Arrays.sort(this, fromIndex, toIndex)
}

/**
 * Sorts array or range in array inplace.
 */
public fun IntArray.sort(fromIndex: Int = 0, toIndex: Int = size()): Unit {
    Arrays.sort(this, fromIndex, toIndex)
}

/**
 * Sorts array or range in array inplace.
 */
public fun LongArray.sort(fromIndex: Int = 0, toIndex: Int = size()): Unit {
    Arrays.sort(this, fromIndex, toIndex)
}

/**
 * Sorts array or range in array inplace.
 */
public fun ShortArray.sort(fromIndex: Int = 0, toIndex: Int = size()): Unit {
    Arrays.sort(this, fromIndex, toIndex)
}

/**
 * Sorts array or range in array inplace.
 */
public fun <T> Array<out T>.sortWith(comparator: Comparator<in T>, fromIndex: Int = 0, toIndex: Int = size()): Unit {
    Arrays.sort(this, fromIndex, toIndex, comparator)
}

/**
 * Returns a *typed* object array containing all of the elements of this primitive array.
 */
public fun BooleanArray.toTypedArray(): Array<Boolean> {
    val result = arrayOfNulls<Boolean>(size())
    for (index in indices)
        result[index] = this[index]
    return result as Array<Boolean>
}

/**
 * Returns a *typed* object array containing all of the elements of this primitive array.
 */
public fun ByteArray.toTypedArray(): Array<Byte> {
    val result = arrayOfNulls<Byte>(size())
    for (index in indices)
        result[index] = this[index]
    return result as Array<Byte>
}

/**
 * Returns a *typed* object array containing all of the elements of this primitive array.
 */
public fun CharArray.toTypedArray(): Array<Char> {
    val result = arrayOfNulls<Char>(size())
    for (index in indices)
        result[index] = this[index]
    return result as Array<Char>
}

/**
 * Returns a *typed* object array containing all of the elements of this primitive array.
 */
public fun DoubleArray.toTypedArray(): Array<Double> {
    val result = arrayOfNulls<Double>(size())
    for (index in indices)
        result[index] = this[index]
    return result as Array<Double>
}

/**
 * Returns a *typed* object array containing all of the elements of this primitive array.
 */
public fun FloatArray.toTypedArray(): Array<Float> {
    val result = arrayOfNulls<Float>(size())
    for (index in indices)
        result[index] = this[index]
    return result as Array<Float>
}

/**
 * Returns a *typed* object array containing all of the elements of this primitive array.
 */
public fun IntArray.toTypedArray(): Array<Int> {
    val result = arrayOfNulls<Int>(size())
    for (index in indices)
        result[index] = this[index]
    return result as Array<Int>
}

/**
 * Returns a *typed* object array containing all of the elements of this primitive array.
 */
public fun LongArray.toTypedArray(): Array<Long> {
    val result = arrayOfNulls<Long>(size())
    for (index in indices)
        result[index] = this[index]
    return result as Array<Long>
}

/**
 * Returns a *typed* object array containing all of the elements of this primitive array.
 */
public fun ShortArray.toTypedArray(): Array<Short> {
    val result = arrayOfNulls<Short>(size())
    for (index in indices)
        result[index] = this[index]
    return result as Array<Short>
}

