package lesson5

import ru.spbstu.kotlin.generate.util.nextString
import java.util.*
import kotlin.math.abs
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

abstract class AbstractOpenAddressingSetTest {

    abstract fun <T : Any> create(bits: Int): MutableSet<T>

    protected fun doAddTest() {
        val random = Random()
        for (iteration in 1..100) {
            val controlSet = mutableSetOf<Int>()
            val bitsNumber = random.nextInt(4) + 5
            val openAddressingSet = create<Int>(bitsNumber)
            assertTrue(openAddressingSet.size == 0, "Size of an empty set is not zero.")
            for (i in 1..50) {
                val nextInt = random.nextInt(32)
                val additionResult = openAddressingSet.add(nextInt)
                assertEquals(
                    nextInt !in controlSet, additionResult,
                    "An element was ${if (additionResult) "" else "not"} added when it ${if (additionResult) "was already in the set" else "should have been"}."
                )
                controlSet += nextInt
                assertTrue(nextInt in openAddressingSet, "A supposedly added element is not in the set.")
                assertEquals(controlSet.size, openAddressingSet.size, "The size of the set is not as expected.")
            }
            val smallSet = create<Int>(bitsNumber)
            assertFailsWith<IllegalStateException>("A table overflow is not being prevented.") {
                for (i in 1..4000) {
                    smallSet.add(random.nextInt())
                }
            }
        }
    }

    protected fun doRemoveTest() {
        val random = Random()
        for (iteration in 1..100) {
            val bitsNumber = random.nextInt(4) + 6
            val openAddressingSet = create<Int>(bitsNumber)
            for (i in 1..50) {
                val firstInt = random.nextInt(32)
                val secondInt = firstInt + (1 shl bitsNumber)
                openAddressingSet += secondInt
                openAddressingSet += firstInt
                val expectedSize = openAddressingSet.size - 1
                assertTrue(
                    openAddressingSet.remove(secondInt),
                    "An element wasn't removed contrary to expected."
                )
                assertFalse(
                    secondInt in openAddressingSet,
                    "A supposedly removed element is still in the set."
                )
                assertTrue(
                    firstInt in openAddressingSet,
                    "The removal of the element prevented access to the other elements."
                )
                assertEquals(
                    expectedSize, openAddressingSet.size,
                    "The size of the set is not as expected."
                )
                assertFalse(
                    openAddressingSet.remove(secondInt),
                    "A removed element was supposedly removed twice."
                )
                assertEquals(
                    expectedSize, openAddressingSet.size,
                    "The size of the set is not as expected."
                )
            }
        }
    }

    protected fun someRemoveTest1() {
        val bitsNumber = 9
        val openAddressingSetTest = create<Int>(bitsNumber)
        openAddressingSetTest.add(11)
        val size = openAddressingSetTest.size
        val removedSize = size - 1
        openAddressingSetTest.remove(11)
        assertEquals(size - 1, removedSize)
        assertFalse(openAddressingSetTest.remove(11))
        openAddressingSetTest.clear()
    }

    protected fun someRemoveTest2() {
        val random = Random()
        for (iteration in 1..1000) {
            val bitsNumber = 10
            val openAddressingSet = create<Int>(bitsNumber)
            for (i in 1..50) {
                val firstInt = random.nextInt(32)
                val secondInt = firstInt + (1 shl bitsNumber)
                openAddressingSet += secondInt
                openAddressingSet += firstInt
                val expectedSize = openAddressingSet.size - 1

                assertTrue(openAddressingSet.remove(secondInt))

                assertFalse(secondInt in openAddressingSet)

                assertTrue(firstInt in openAddressingSet)

                assertEquals(expectedSize, openAddressingSet.size)

                assertFalse(openAddressingSet.remove(secondInt))

                assertEquals(expectedSize, openAddressingSet.size)
            }
        }
    }

    protected fun doIteratorTest() {
        val random = Random()
        for (iteration in 1..100) {
            val controlSet = mutableSetOf<String>()
            for (i in 1..15) {
                val string = random.nextString("abcdefgh12345678", 1, 15)
                controlSet.add(string)
            }
            println("Control set: $controlSet")
            val openAddressingSet = create<String>(random.nextInt(6) + 4)
            assertFalse(
                openAddressingSet.iterator().hasNext(),
                "Iterator of an empty set should not have any next elements."
            )
            for (element in controlSet) {
                openAddressingSet += element
            }
            val iterator1 = openAddressingSet.iterator()
            val iterator2 = openAddressingSet.iterator()
            println("Checking if calling hasNext() changes the state of the iterator...")
            while (iterator1.hasNext()) {
                assertEquals(
                    iterator2.next(), iterator1.next(),
                    "Calling OpenAddressingSetIterator.hasNext() changes the state of the iterator."
                )
            }
            val openAddressingSetIter = openAddressingSet.iterator()
            println("Checking if the iterator traverses the entire set...")
            while (openAddressingSetIter.hasNext()) {
                controlSet.remove(openAddressingSetIter.next())
            }
            assertTrue(
                controlSet.isEmpty(),
                "OpenAddressingSetIterator doesn't traverse the entire set."
            )
            assertFailsWith<IllegalStateException>("Something was supposedly returned after the elements ended") {
                openAddressingSetIter.next()
            }
            println("All clear!")
        }
    }

    protected fun someIteratorTest() {
        val bitsNumber1 = 12
        val openAddressingSet1 = create<Int>(bitsNumber1)
        val iterator1 = openAddressingSet1.iterator()
        assertFalse(iterator1.hasNext())
        openAddressingSet1.add(45)
        assertFalse(iterator1.hasNext())
        assertFailsWith<IllegalStateException> { iterator1.next() }
        openAddressingSet1.clear()

        val list = mutableListOf(1, 85, 123, 5678, 567, 23, 4)
        val bitsNumber2 = 12
        val openAddressingSet2 = create<Int>(bitsNumber2)
        openAddressingSet2.addAll(list)
        val iterator2 = openAddressingSet2.iterator()
        assertTrue(iterator2.hasNext())
        assertEquals(1, iterator2.next())
        assertTrue(iterator2.hasNext())
        assertEquals(4, iterator2.next())
        assertEquals(23, iterator2.next())
        for (i in 0..2) {
            iterator2.next()
        }
        assertEquals(5678, iterator2.next())
        assertFalse(iterator2.hasNext())
        assertFailsWith<IllegalStateException> { iterator2.next() }
        list.clear()
        openAddressingSet1.clear()
        openAddressingSet2.clear()
    }

    protected fun doIteratorRemoveTest() {
        val random = Random()
        for (iteration in 1..100) {
            val controlSet = mutableSetOf<String>()
            val removeIndex = random.nextInt(15) + 1
            var toRemove = ""
            for (i in 1..15) {
                val string = random.nextString("abcdefgh12345678", 1, 15)
                controlSet.add(string)
                if (i == removeIndex) {
                    toRemove = string
                }
            }
            println("Initial set: $controlSet")
            val openAddressingSet = create<String>(random.nextInt(6) + 4)
            for (element in controlSet) {
                openAddressingSet += element
            }
            controlSet.remove(toRemove)
            println("Control set: $controlSet")
            println("Removing element \"$toRemove\" from open addressing set through the iterator...")
            val iterator = openAddressingSet.iterator()
            assertFailsWith<IllegalStateException>("Something was supposedly deleted before the iteration started") {
                iterator.remove()
            }
            var counter = openAddressingSet.size
            while (iterator.hasNext()) {
                val element = iterator.next()
                counter--
                if (element == toRemove) {
                    iterator.remove()
                }
            }
            assertEquals(
                0, counter,
                "OpenAddressingSetIterator.remove() changed iterator position: ${abs(counter)} elements were ${if (counter > 0) "skipped" else "revisited"}."
            )
            assertEquals(
                controlSet.size, openAddressingSet.size,
                "The size of the set is incorrect: was ${openAddressingSet.size}, should've been ${controlSet.size}."
            )
            for (element in controlSet) {
                assertTrue(
                    openAddressingSet.contains(element),
                    "Open addressing set doesn't have the element $element from the control set."
                )
            }
            for (element in openAddressingSet) {
                assertTrue(
                    controlSet.contains(element),
                    "Open addressing set has the element $element that is not in control set."
                )
            }
            println("All clear!")
        }
    }

    protected fun someIteratorRemoveTest() {
        val bitsNumber = 12
        val openAddressingSet1 = create<Int>(bitsNumber)
        val list = mutableListOf(1, 123, 63456, 34534, 434, 623)
        openAddressingSet1.addAll(list)
        val iterator1 = openAddressingSet1.iterator()
        iterator1.next()
        iterator1.remove()
        assertFalse(openAddressingSet1.contains(1))
        assertTrue(openAddressingSet1.contains(123))
        iterator1.next()
        iterator1.remove()
        assertFalse(openAddressingSet1.contains(123))
        for (i in 0..2) {
            list.add(iterator1.next())
            iterator1.remove()
        }
        assertFalse(openAddressingSet1.containsAll(list))
        val size = openAddressingSet1.size
        assertEquals(63456, iterator1.next())
        iterator1.remove()
        assertTrue(!(openAddressingSet1.contains(63456)))
        assertEquals(0, size - 1)
        assertFailsWith<IllegalStateException> { iterator1.next() }
        openAddressingSet1.clear()
        list.clear()
    }
}