package lesson4

import java.lang.IllegalStateException
import java.util.*


/**
 * Префиксное дерево для строк
 */
class KtTrie : AbstractMutableSet<String>(), MutableSet<String> {

    private class Node {

        val children: MutableMap<Char, Node> = linkedMapOf()

        var parent: Node? = null

    }

    private var root = Node()

    override var size: Int = 0
        private set

    private val endTag = 0.toChar()

    override fun clear() {
        root.children.clear()
        size = 0
    }

    private fun String.withZero() = this + endTag

    private fun findNode(element: String): Node? {
        var current = root
        for (char in element) {
            current = current.children[char] ?: return null
        }
        return current
    }

    override fun contains(element: String): Boolean =
        findNode(element.withZero()) != null

    override fun add(element: String): Boolean {
        var current = root
        var modified = false
        for (char in element.withZero()) {
            val child = current.children[char]
            if (child != null) {
                current = child
            } else {
                modified = true
                val newChild = Node()
                current.children[char] = newChild
                newChild.parent = current
                current = newChild
            }
        }
        if (modified) {
            size++
        }
        return modified
    }

    override fun remove(element: String): Boolean {
        val current = findNode(element) ?: return false
        if (current.children.remove(endTag) != null) {
            size--
            return true
        }
        return false
    }

    /**
     * Итератор для префиксного дерева
     *
     * Спецификация: [java.util.Iterator] (Ctrl+Click по Iterator)
     *
     * Сложная
     */
    override fun iterator(): MutableIterator<String> = TrieIterator()


    inner class TrieIterator : MutableIterator<String> {

        private var current: Node? = root

        private val dataMap = mutableMapOf<Node, String>()

        private var wordsStack = Stack<String>()

        private var lastWord: String = ""

        init {
            createDataBase(current!!)
        }


        // Трудоемкость = О(F * Q),где F - наибольшее слово (size), Q - кол-во вариантов слов
        // Ресурсоемкость: О(N) + O(M), где N = l * (l - 1) / 2, l - конечная длина слова, при этом в мапе будет сумма
        // таких слов, т.е l1 * (l1 - 1) / 2 + ... + lk * (lk - 1) / 2 (k - кол-во слов в trie),
        // M - кол-во конечных слов в стаке, N всегда больше M
        // Ресурсоемкость = О(N) + O(M) = О(N)
        private fun createDataBase(curNode: Node) {

            for ((char, node) in curNode.children) {

                if (curNode != root) dataMap[node] = dataMap[node.parent] + char
                else dataMap[node] = char.toString()

                val str = dataMap[node]
                current = findNode(str!!)

                if (current != null && str !in wordsStack && char == endTag) wordsStack.add(dataMap[node.parent]!!)

                createDataBase(current!!)
            }
        }

        // Трудоемкость = О(1)
        // Ресурсоемкость = О(1)
        override fun hasNext(): Boolean = wordsStack.isNotEmpty()

        // Трудоемкость = О(1)
        // Ресурсоемкость = О(1)
        override fun next(): String {
            if (!hasNext()) throw IllegalStateException()
            lastWord = wordsStack.pop()
            return lastWord
        }

        // Трудоемкость = О(lastWord.length)
        // Ресурсоемкость = О(1)
        override fun remove() {
            if (!this@KtTrie.remove(lastWord)) throw IllegalStateException()
        }

    }

}