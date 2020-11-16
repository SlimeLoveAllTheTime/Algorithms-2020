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

        private val dataMap = mutableMapOf<Node, String>()

        private var wordsStack = Stack<String>()

        private var lastWord = ""

        private var wordsControl = mutableSetOf<String>()

        private var isBranch = true

        init {
            createDataBase(root)
        }


        // Записываю в стек только слово(по сути проход по веткам), в стек загружается следующее слово в случае, если стек
        // становится пустым, это проверка есть в методе next().
        // На счет прохода каждый раз по пройденным веткам: Если пытаться не проходить по ним повторный раз, то трудоемкость
        // возрастет. При этом повторный проход не грозит сильной потерей в скорости, так как внутренняя логика не будет
        // срабатывать.
        // Добавил только отсечение последних, незначащих нодов "endTag".

        // Трудоемкость = О(branch.length), branch.length - длина ветви.
        // Ресурсоемкость = О(количество нодов + количество слов) -> O(N), где N - количество элементов в мапе.
        private fun createDataBase(current: Node) {
            for ((char, node) in current.children) {
                if (!isBranch) break

                if (dataMap[node.parent] + char in wordsControl) continue

                if (current != root) dataMap[node] = dataMap[node.parent] + char
                else dataMap[node] = char.toString()

                val str = dataMap[node]
                if (str !in wordsControl && char == endTag) {
                    wordsStack.add(dataMap[node.parent]!!)
                    wordsControl.add(str!!)
                    isBranch = false
                    break
                }
                createDataBase(node)
            }
        }

        // Трудоемкость = О(1)
        // Ресурсоемкость = О(1)
        override fun hasNext(): Boolean = wordsStack.isNotEmpty()

        // Трудоемкость = О(1)
        // Ресурсоемкость = О(1)
        override fun next(): String {
            if (!hasNext()) throw IllegalStateException()
            println("состояние стека $wordsStack")
            lastWord = wordsStack.pop()
            if (wordsStack.isEmpty()) {
                isBranch = true
                createDataBase(root)
            }
            return lastWord
        }

        // Трудоемкость = О(lastWord.length)
        // Ресурсоемкость = О(1)
        override fun remove() {
            if (!this@KtTrie.remove(lastWord)) throw IllegalStateException()
        }

    }

}