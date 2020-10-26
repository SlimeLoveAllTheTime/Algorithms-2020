package lesson4

import java.util.*
import kotlin.math.abs
import ru.spbstu.kotlin.generate.util.nextString
import kotlin.test.*

abstract class AbstractTrieTest {

    abstract fun create(): MutableSet<String>

    private fun <R> implementationTest(function: () -> R) {
        try {
            function()
        } catch (e: Error) {
            if (e is NotImplementedError) {
                throw e
            }
        } catch (e: Exception) {
            // let it slide for now
        }
    }

    protected fun doGeneralTest() {
        val random = Random()
        for (iteration in 1..100) {
            val trie = create()
            assertEquals(0, trie.size)
            assertFalse("some" in trie)
            var wordCounter = 0
            val wordList = mutableSetOf<String>()
            val removeIndex = random.nextInt(15) + 1
            var toRemove = ""
            for (i in 1..15) {
                val string = random.nextString("abcdefgh", 1, 15)
                wordList += string
                if (i == removeIndex) {
                    toRemove = string
                }
                if (trie.add(string)) {
                    wordCounter++
                }
                assertTrue(
                    string in trie,
                    "An element wasn't added to trie when it should've been."
                )
                if (string.length != 1) {
                    val substring = string.substring(0, random.nextInt(string.length - 1))
                    if (substring !in wordList) {
                        assertTrue(
                            substring !in trie,
                            "An element is considered to be in trie when it should not be there."
                        )
                    }
                }
            }
            assertEquals(wordCounter, trie.size)
            trie.remove(toRemove)
            assertEquals(wordCounter - 1, trie.size)
            assertFalse(
                toRemove in trie,
                "A supposedly removed element is still considered to be in trie."
            )
            trie.clear()
            assertEquals(0, trie.size)
            assertFalse("some" in trie)
        }
    }

    protected fun doIteratorTest() {
        implementationTest { create().iterator().hasNext() }
        implementationTest { create().iterator().next() }
        val random = Random()
        for (iteration in 1..100) {
            val controlSet = mutableSetOf<String>()
            for (i in 1..15) {
                val string = random.nextString("abcdefgh", 1, 15)
                controlSet.add(string)
            }
            println("Control set: $controlSet")
            val trieSet = create()
            assertFalse(
                trieSet.iterator().hasNext(),
                "Iterator of an empty set should not have any next elements."
            )
            for (element in controlSet) {
                trieSet += element
            }
            val iterator1 = trieSet.iterator()
            val iterator2 = trieSet.iterator()
            println("Checking if calling hasNext() changes the state of the iterator...")
            while (iterator1.hasNext()) {
                assertEquals(
                    iterator2.next(), iterator1.next(),
                    "Calling TrieIterator.hasNext() changes the state of the iterator."
                )
            }
            val trieIter = trieSet.iterator()
            println("Checking if the iterator traverses the entire set...")
            while (trieIter.hasNext()) {
                controlSet.remove(trieIter.next())
            }
            assertTrue(
                controlSet.isEmpty(),
                "TrieIterator doesn't traverse the entire set."
            )
            assertFailsWith<IllegalStateException>("Something was supposedly returned after the elements ended") {
                trieIter.next()
            }
            println("All clear!")

            for (i in 1..2) {
                val string =
                    random.nextString("12345345678901203102301203012301024023402350340603456045067056041023ad", 1, 70)
                controlSet.add(string)
            }
            println(controlSet)
            val iteratorA = trieSet.iterator()
            val iteratorB = KtTrie().iterator()
            println("Checking if calling hasNext() changes the state of the iterator...")
            while (iteratorB.hasNext()) {
                assertEquals(
                    iteratorA.next(), iteratorB.next(),
                    "Calling TrieIterator.hasNext() changes the state of the iterator."
                )
            }
            controlSet.clear()
            for (i in 1..5) {
                val string =
                    random.nextString("12", 1, 2)
                controlSet.add(string)
            }
            val iteratorC = trieSet.iterator()
            val iteratorD = KtTrie().iterator()
            println("Checking if calling hasNext() changes the state of the iterator...")
            while (iteratorD.hasNext()) {
                assertEquals(
                    iteratorC.next(), iteratorD.next(),
                    "Calling TrieIterator.hasNext() changes the state of the iterator."
                )
            }
            while (iteratorD.hasNext()) controlSet.remove(iteratorD.next())
        }
    }

    protected fun someIteratorTest() {
        val trie1 = KtTrie()
        val list1 = mutableListOf("q", "qwer", "qwert", "qwerty")
        trie1.addAll(list1)
        trie1.remove("q")
        assertEquals("qwerty", trie1.iterator().next())
        assertEquals("qwer", trie1.last())
        list1.clear()
        trie1.clear()

        val trie2 = KtTrie()
        trie2.add("a")
        trie2.remove("a")
        assertFalse(trie2.iterator().hasNext())
        trie2.clear()

        val trie3 = KtTrie()
        trie3.add("0")
        assertEquals("0", trie3.iterator().next())
        trie3.remove("0")
        assertTrue(trie3.isEmpty())
        trie3.clear()
    }

    protected fun doIteratorRemoveTest() {
        implementationTest { create().iterator().remove() }
        val random = Random()
        for (iteration in 1..100) {
            val controlSet = mutableSetOf<String>()
            val removeIndex = random.nextInt(15) + 1
            var toRemove = ""
            for (i in 1..15) {
                val string = random.nextString("abcdefgh", 1, 15)
                controlSet.add(string)
                if (i == removeIndex) {
                    toRemove = string
                }
            }
            println("Initial set: $controlSet")
            val trieSet = create()
            for (element in controlSet) {
                trieSet += element
            }
            controlSet.remove(toRemove)
            println("Control set: $controlSet")
            println("Removing element \"$toRemove\" from trie set through the iterator...")
            val iterator = trieSet.iterator()
            assertFailsWith<IllegalStateException>("Something was supposedly deleted before the iteration started") {
                iterator.remove()
            }
            var counter = trieSet.size
            while (iterator.hasNext()) {
                val element = iterator.next()
                counter--
                if (element == toRemove) {
                    iterator.remove()
                    assertFailsWith<IllegalStateException>("Trie.remove() was successfully called twice in a row.") {
                        iterator.remove()
                    }
                }
            }
            assertEquals(
                0, counter,
                "TrieIterator.remove() changed iterator position: ${abs(counter)} elements were ${if (counter > 0) "skipped" else "revisited"}."
            )

            for (element in controlSet) {
                assertTrue(
                    trieSet.contains(element),
                    "Trie set doesn't have the element $element from the control set."
                )
            }
            for (element in trieSet) {
                assertTrue(
                    controlSet.contains(element),
                    "Trie set has the element $element that is not in control set."
                )
            }
            println("All clear!")


        }
    }

    protected fun someIteratorRemoveTest1() {
        implementationTest { create().iterator().remove() }
        val random = Random()
        for (iteration1 in 1..2) {
            val controlSet1 = mutableSetOf<String>()
            val removeIndex1 = random.nextInt(1) + 1
            var toRemove1 = ""
            for (i in 1..2) {
                val string = random.nextString(
                    "12345345678901203102301203012301024023402350340603456045067056041023ad",
                    1,
                    70
                )
                controlSet1.add(string)
                if (i == removeIndex1) {
                    toRemove1 = string
                }
            }
            val trieSet1 = create()
            for (element in controlSet1) {
                trieSet1 += element
            }
            controlSet1.remove(toRemove1)
            val iterator1 = KtTrie().iterator()
            assertFailsWith<IllegalStateException> {
                iterator1.remove()
            }
            var counter1 = trieSet1.size
            while (iterator1.hasNext()) {
                val element = iterator1.next()
                counter1--
                if (element == toRemove1) {
                    iterator1.remove()
                    assertFailsWith<IllegalStateException>("Trie.remove() was successfully called twice in a row.") {
                        iterator1.remove()
                    }
                    assertEquals(0, counter1)

                    for (element1 in controlSet1) {
                        assertTrue(trieSet1.contains(element))
                    }
                    for (element1 in trieSet1) {
                        assertTrue(controlSet1.contains(element))
                    }
                }
            }
        }
    }

    protected fun someIteratorRemoveTest2() {
        val trie = KtTrie()
        val list = mutableListOf("a", "ab", "ac")
        trie.addAll(list)
        trie.remove("ab")
        assertEquals("ac", trie.iterator().next())
    }

}