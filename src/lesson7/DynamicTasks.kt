@file:Suppress("UNUSED_PARAMETER")

package lesson7

import java.lang.Integer.MIN_VALUE
import java.util.*


/**
 * Наибольшая общая подпоследовательность.
 * Средняя
 *
 * Дано две строки, например "nematode knowledge" и "empty bottle".
 * Найти их самую длинную общую подпоследовательность -- в примере это "emt ole".
 * Подпоследовательность отличается от подстроки тем, что её символы не обязаны идти подряд
 * (но по-прежнему должны быть расположены в исходной строке в том же порядке).
 * Если общей подпоследовательности нет, вернуть пустую строку.
 * Если есть несколько самых длинных общих подпоследовательностей, вернуть любую из них.
 * При сравнении подстрок, регистр символов *имеет* значение.
 */

// Трудоемкость = О(fLength * sLength)
// Ресурсоемкость = О(fLength * sLength)
fun longestCommonSubSequence(first: String, second: String): String {
    var fLength = first.length
    var sLength = second.length
    val matrix = Array(fLength + 1) { IntArray(sLength + 1) }
    for (i in 0 until fLength) {
        for (j in 0 until sLength) {
            if (first[i] == second[j]) matrix[i + 1][j + 1] = matrix[i][j] + 1
            else matrix[i + 1][j + 1] = matrix[i + 1][j].coerceAtLeast(matrix[i][j + 1])
        }
    }
    val result = StringBuffer()
    while (fLength > 0 && sLength > 0) {
        if (matrix[fLength][sLength] == matrix[fLength - 1][sLength]) fLength--
        else if (matrix[fLength][sLength] == matrix[fLength][sLength - 1]) sLength--
        else {
            if (first[fLength - 1] == second[sLength - 1]) {
                result.append(first[fLength - 1])
                fLength--
                sLength--
            }
        }
    }
    return result.reverse().toString()
}


/**
 * fun findAllSubSeq(
 * matrix: Array<Array<Int>>, current: Int, list: MutableList<String>,
 *    n: Int, m: Int, first: String, position: List<Triple<Int, Int, Int>>
 *        ) {
 *    val cur = position[current - 1]
 *    var firstInd = cur.third + 1
 *    val secondInd = cur.second + 1
 *    var curValue = current
 *    val result = StringBuilder()
 *    var lineControl = false
 *    var lastElem = false
 *    result.append(first[firstInd - 1])
 *    for (i in secondInd until m) {
 *    for (j in firstInd until n) {
 *        if (matrix[i][j] > curValue && matrix[i][j - 1] < matrix[i][j] && !lineControl) {
 *            curValue = matrix[i][j]
 *            result.append(first[j])
 *            lineControl = true
 *            if (lineControl && j == n - 1) lastElem = true
 *              }
 *            }
 *        if (firstInd < n - 1) firstInd++
 *        if (lastElem) break
 *        lineControl = false
 *        }
 *    list.add(result.toString())
 *    result.clear()
 * }
 *
 * fun longestCommonSubSequence(a: String, b: String): String {
 *    val n = first.length
 *    val m = second.length
 *    val result = mutableListOf<String>()
 *    val matrix = Array(m) { Array(n) { 0 } }
 *    var count = 0
 *    val position = mutableListOf<Triple<Int, Int, Int>>()
 *    for (i in 0 until m) {
 *    for (j in 0 until n) {
 *    if (first[j] == second[i]) {
 *        count++
 *        position.add(Triple(count, i, j))
 *       }
 *       matrix[i][j] = count
 *     }
 *   }
 *   var control = 1
 *   while (control <= count) {
 *          findAllSubSeq(matrix, control, result, n, m, first, position)
 *          control++
 *        }
 *   println(result)
 *   }
 */

/**
 * Наибольшая возрастающая подпоследовательность
 * Сложная
 *
 * Дан список целых чисел, например, [2 8 5 9 12 6].
 * Найти в нём самую длинную возрастающую подпоследовательность.
 * Элементы подпоследовательности не обязаны идти подряд,
 * но должны быть расположены в исходном списке в том же порядке.
 * Если самых длинных возрастающих подпоследовательностей несколько (как в примере),
 * то вернуть ту, в которой числа расположены раньше (приоритет имеют первые числа).
 * В примере ответами являются 2, 8, 9, 12 или 2, 5, 9, 12 -- выбираем первую из них.
 */

// Трудоемкость = О(N^2)
// Ресурсоемкость = О(N)
fun longestIncreasingSubSequence(list: List<Int>): List<Int> {
    if (list.size < 2) return list
    var maxLength = 0
    val data = mutableListOf<Pair<Int, Int>>()
    for (i in list.indices) {
        data.add(Pair(MIN_VALUE, 0))
        for (j in i - 1 downTo 0) {
            if (list[i] > list[j] && data[i].second <= data[j].second + 1) data[i] = Pair(j, data[j].second + 1)
        }
        maxLength = maxLength.coerceAtLeast(data[i].second)
    }
    val result = mutableListOf<Int>()
    for (i in list.indices) {
        if (data[i].second == maxLength) {
            var current = i
            while (current != MIN_VALUE) {
                result.add(list[current])
                current = data[current].first
            }
            break
        }
    }
    return result.reversed()
}

/**
 * Самый короткий маршрут на прямоугольном поле.
 * Средняя
 *
 * В файле с именем inputName задано прямоугольное поле:
 *
 * 0 2 3 2 4 1
 * 1 5 3 4 6 2
 * 2 6 2 5 1 3
 * 1 4 3 2 6 2
 * 4 2 3 1 5 0
 *
 * Можно совершать шаги длиной в одну клетку вправо, вниз или по диагонали вправо-вниз.
 * В каждой клетке записано некоторое натуральное число или нуль.
 * Необходимо попасть из верхней левой клетки в правую нижнюю.
 * Вес маршрута вычисляется как сумма чисел со всех посещенных клеток.
 * Необходимо найти маршрут с минимальным весом и вернуть этот минимальный вес.
 *
 * Здесь ответ 2 + 3 + 4 + 1 + 2 = 12
 */
fun shortestPathOnField(inputName: String): Int {
    TODO()
}

// Задачу "Максимальное независимое множество вершин в графе без циклов"
// смотрите в уроке 5