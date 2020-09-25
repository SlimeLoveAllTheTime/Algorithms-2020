@file:Suppress("UNUSED_PARAMETER")

package lesson2

import kotlin.math.sqrt

/**
 * Получение наибольшей прибыли (она же -- поиск максимального подмассива)
 * Простая
 *
 * Во входном файле с именем inputName перечислены цены на акции компании в различные (возрастающие) моменты времени
 * (каждая цена идёт с новой строки). Цена -- это целое положительное число. Пример:
 *
 * 201
 * 196
 * 190
 * 198
 * 187
 * 194
 * 193
 * 185
 *
 * Выбрать два момента времени, первый из них для покупки акций, а второй для продажи, с тем, чтобы разница
 * между ценой продажи и ценой покупки была максимально большой. Второй момент должен быть раньше первого.
 * Вернуть пару из двух моментов.
 * Каждый момент обозначается целым числом -- номер строки во входном файле, нумерация с единицы.
 * Например, для приведённого выше файла результат должен быть Pair(3, 4)
 *
 * В случае обнаружения неверного формата файла бросить любое исключение.
 */
fun optimizeBuyAndSell(inputName: String): Pair<Int, Int> {
    TODO()
}

/**
 * Задача Иосифа Флафия.
 * Простая
 *
 * Образовав круг, стоят menNumber человек, пронумерованных от 1 до menNumber.
 *
 * 1 2 3
 * 8   4
 * 7 6 5
 *
 * Мы считаем от 1 до choiceInterval (например, до 5), начиная с 1-го человека по кругу.
 * Человек, на котором остановился счёт, выбывает.
 *
 * 1 2 3
 * 8   4
 * 7 6 х
 *
 * Далее счёт продолжается со следующего человека, также от 1 до choiceInterval.
 * Выбывшие при счёте пропускаются, и человек, на котором остановился счёт, выбывает.
 *
 * 1 х 3
 * 8   4
 * 7 6 Х
 *
 * Процедура повторяется, пока не останется один человек. Требуется вернуть его номер (в данном случае 3).
 *
 * 1 Х 3
 * х   4
 * 7 6 Х
 *
 * 1 Х 3
 * Х   4
 * х 6 Х
 *
 * х Х 3
 * Х   4
 * Х 6 Х
 *
 * Х Х 3
 * Х   х
 * Х 6 Х
 *
 * Х Х 3
 * Х   Х
 * Х х Х
 *
 * Общий комментарий: решение из Википедии для этой задачи принимается,
 * но приветствуется попытка решить её самостоятельно.
 */
fun josephTask(menNumber: Int, choiceInterval: Int): Int {
    TODO()
}

/**
 * Наибольшая общая подстрока.
 * Средняя
 *
 * Дано две строки, например ОБСЕРВАТОРИЯ и КОНСЕРВАТОРЫ.
 * Найти их самую длинную общую подстроку -- в примере это СЕРВАТОР.
 * Если общих подстрок нет, вернуть пустую строку.
 * При сравнении подстрок, регистр символов *имеет* значение.
 * Если имеется несколько самых длинных общих подстрок одной длины,
 * вернуть ту из них, которая встречается раньше в строке first.
 */
fun longestCommonSubstring(first: String, second: String): String {
    if (first == "" || second == "") return ""
    val strTable = Array(first.length + 1) { Array(second.length + 1) { 0 } }
    var subLength = 0
    var end = 0
    for (i in first.indices) {
        for (j in second.indices) {
            if (first[i] == second[j]) {
                strTable[i + 1][j + 1] = strTable[i][j] + 1
                if (strTable[i + 1][j + 1] > subLength) {
                    subLength = strTable[i + 1][j + 1]
                    end = i + 1
                }
            }
        }
    }
    return first.substring(end - subLength, end)
}


/**
 * Число простых чисел в интервале
 * Простая
 *
 * Рассчитать количество простых чисел в интервале от 1 до limit (включительно).
 * Если limit <= 1, вернуть результат 0.
 *
 * Справка: простым считается число, которое делится нацело только на 1 и на себя.
 * Единица простым числом не считается.
 */
fun calcPrimesNumber(limit: Int): Int {

    if (limit <= 1) return 0

    //создание решето
    val isTrueList = BooleanArray(limit + 1) { false }
    isTrueList[2] = true
    if (limit > 2) isTrueList[3] = true

    val sqrtLimit = sqrt(limit.toDouble()).toInt()
    var result = 0
    atkinTeor(limit, sqrtLimit, isTrueList)
    filterSqrTrueNumber(limit, sqrtLimit, isTrueList).forEach { if (it) result++ }
    return result
}

//фильтрация в соответсвии с теоремой Аткина
fun atkinTeor(limit: Int, sqrtLimit: Int, isTrueList: BooleanArray) {
    var sqrX = 0
    var sqrY: Int
    var n: Int
    for (i in 1..sqrtLimit) {
        sqrX += 2 * i - 1
        sqrY = 0
        for (j in 1..sqrtLimit) {
            sqrY += 2 * j - 1
            n = 4 * sqrX + sqrY
            if (n <= limit && (n % 12 == 1 || n % 12 == 5)) isTrueList[n] = !isTrueList[n]
            n -= sqrX
            if (n <= limit && n % 12 == 7) isTrueList[n] = !isTrueList[n]
            n -= 2 * sqrY
            if (i > j && n <= limit && n % 12 == 11) isTrueList[n] = !isTrueList[n]
        }
    }
}


//Фильтрация (путем деления чисел на квадраты простых)
fun filterSqrTrueNumber(limit: Int, sqrtLimit: Int, isTrueList: BooleanArray): BooleanArray {
    var n: Int
    var j: Int
    for (i in 5..sqrtLimit) {
        if (isTrueList[i]) {
            n = i * i
            j = n
            while (j <= limit) {
                isTrueList[j] = false
                j += n
            }
        }
    }
    return isTrueList
}
