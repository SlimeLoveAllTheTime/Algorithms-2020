package lesson5

import java.lang.IllegalStateException

/**
 * Множество(таблица) с открытой адресацией на 2^bits элементов без возможности роста.
 */
@Suppress("UNCHECKED_CAST")
class KtOpenAddressingSet<T : Any>(private val bits: Int) : AbstractMutableSet<T>() {
    init {
        require(bits in 2..31)
    }

    private val capacity = 1 shl bits

    private val storage = Array<Any?>(capacity) { null }

    override var size: Int = 0

    //Мне немного подсказали подумать на счет такого подхода
    object Removed

    /**
     * Индекс в таблице, начиная с которого следует искать данный элемент
     */
    private fun T.startingIndex(): Int {
        return hashCode() and (0x7FFFFFFF shr (31 - bits))
    }

    /**
     * Проверка, входит ли данный элемент в таблицу
     */
    override fun contains(element: T): Boolean {
        var index = element.startingIndex()
        var current = storage[index]
        while (current != null) {
            if (current == element && current != Removed) return true
            index = (index + 1) % capacity
            current = storage[index]
        }
        return false
    }

    /**
     * Добавление элемента в таблицу.
     *
     * Не делает ничего и возвращает false, если такой же элемент уже есть в таблице.
     * В противном случае вставляет элемент в таблицу и возвращает true.
     *
     * Бросает исключение (IllegalStateException) в случае переполнения таблицы.
     * Обычно Set не предполагает ограничения на размер и подобных контрактов,
     * но в данном случае это было введено для упрощения кода.
     */
    override fun add(element: T): Boolean {
        val startingIndex = element.startingIndex()
        var index = startingIndex
        var current = storage[index]
        while (current != null && current != Removed) {
            if (current == element) {
                return false
            }
            index = (index + 1) % capacity
            check(index != startingIndex) { "Table is full" }
            current = storage[index]
        }
        storage[index] = element
        size++
        return true
    }

    /**
     * Удаление элемента из таблицы
     *
     * Если элемент есть в таблица, функция удаляет его из дерева и возвращает true.
     * В ином случае функция оставляет множество нетронутым и возвращает false.
     * Высота дерева не должна увеличиться в результате удаления.
     *
     * Спецификация: [java.util.Set.remove] (Ctrl+Click по remove)
     *
     * Средняя
     */

    // Трудоемкость (в худшем случае) = О(N), где N - количество элементов. В общем случае трудоемкость = О(S),
    // где S - количество проходов по циклу.
    // Ресурсоемкость = О(1)
    override fun remove(element: T): Boolean {
        if (!contains(element)) return false
        val startInd = element.startingIndex()
        var ind = startInd
        var current = storage[ind]
        while (current != null && current != Removed) {
            if (current == element) {
                storage[ind] = Removed
                size--
                return true
            }
            ind = (ind + 1) % capacity
            current = storage[ind]
            if (ind == startInd) break
        }
        return false
    }

    /**
     * Создание итератора для обхода таблицы
     *
     * Не забываем, что итератор должен поддерживать функции next(), hasNext(),
     * и опционально функцию remove()
     *
     * Спецификация: [java.util.Iterator] (Ctrl+Click по Iterator)
     *
     * Средняя (сложная, если поддержан и remove тоже)
     */
    override fun iterator(): MutableIterator<T> = OpenAddressingSetIterator()

    inner class OpenAddressingSetIterator : MutableIterator<T> {

        private var current: T? = null

        private var curInd = 0

        private var currentSize = size

        // Трудоемкость = О(S), где S - количество проходов по циклу
        // Ресурсоемкость = О(1)
        private fun findCurrent(): T? {
            while (storage[curInd] == null || storage[curInd] == Removed) curInd = (curInd + 1) % capacity
            current = storage[curInd] as T
            currentSize--
            return current
        }

        // Трудоемкость = О(1)
        // Ресурсоемкость = О(1)
        override fun hasNext(): Boolean = currentSize > 0

        // Трудоемкость = О(S), где S - количество проходов по циклу
        // Ресурсоемкость = О(1)
        override fun next(): T {
            if (!hasNext()) throw IllegalStateException()
            curInd = (curInd + 1) % capacity
            return findCurrent()!!
        }

        // Трудоемкость = О(1), доступ элемента array по индексу
        // Ресурсоемкость = О(1)
        override fun remove() {
            if (current == null) throw IllegalStateException()
            storage[curInd] = Removed
            size--
        }

    }

}