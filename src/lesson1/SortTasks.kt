@file:Suppress("UNUSED_PARAMETER")

package lesson1

import java.io.File
import kotlin.math.abs

/**
 * Сортировка времён
 *
 * Простая
 * (Модифицированная задача с сайта acmp.ru)
 *
 * Во входном файле с именем inputName содержатся моменты времени в формате ЧЧ:ММ:СС AM/PM,
 * каждый на отдельной строке. См. статью википедии "12-часовой формат времени".
 *
 * Пример:
 *
 * 01:15:19 PM
 * 07:26:57 AM
 * 10:00:03 AM
 * 07:56:14 PM
 * 01:15:19 PM
 * 12:40:31 AM
 *
 * Отсортировать моменты времени по возрастанию и вывести их в выходной файл с именем outputName,
 * сохраняя формат ЧЧ:ММ:СС AM/PM. Одинаковые моменты времени выводить друг за другом. Пример:
 *
 * 12:40:31 AM
 * 07:26:57 AM
 * 10:00:03 AM
 * 01:15:19 PM
 * 01:15:19 PM
 * 07:56:14 PM
 *
 * В случае обнаружения неверного формата файла бросить любое исключение.
 */
fun sortTimes(inputName: String, outputName: String) {
    val list = mutableListOf<String>() // T(N) = 1, R(N) = O(N)
    File(inputName).forEachLine {
        if (!it.matches(Regex("""(\d{2}:\d{2}:\d{2}.(AM|PM))"""))) // T(N) = O(N)
            throw IllegalArgumentException() // T(N) = 0..1
        list.add(it) // T(N) = O(N)
    }
    val arr = list.toTypedArray() // T(N) = 1, R(N) = O(N)
    mergeSortTimes(arr, 0, list.size) // T(N) = O(N * logN)
    File(outputName).bufferedWriter().use { out ->
        arr.forEach {
            out.write("$it\n") // T(N) = O(N)
        }
    }
}
// T(N) = O(N * logN)
// R(N) = O(N) = O(N)

fun compDate(str1: String, str2: String): Comparable<Int> {
    val l1 = str1.split(" ")
    val l2 = str2.split(" ")
    return if (l1[1] == "PM" && l2[1] == "AM" || (l1[1] == l2[1] && time(l1[0]) >= time(l2[0]))) 1
    else -1
}

fun time(str: String) = str.split(":")
    .mapIndexed { index, it ->
        when (index) {
            0 -> if (it.toInt() == 12) 0 else it.toInt() * 3600
            1 -> it.toInt() * 60
            else -> it.toInt()
        }
    }.sum()

private fun mergeSortTimes(elements: Array<String>, begin: Int, end: Int) {
    if (end - begin <= 1) return
    val middle = (begin + end) / 2
    mergeSortTimes(elements, begin, middle)
    mergeSortTimes(elements, middle, end)
    mergeTimes(elements, begin, middle, end)
}

private fun mergeTimes(elements: Array<String>, begin: Int, middle: Int, end: Int) {
    val left = elements.copyOfRange(begin, middle)
    val right = elements.copyOfRange(middle, end)
    var li = 0
    var ri = 0
    for (i in begin until end)
        elements[i] =
            if ((li < left.size && ri == right.size) || (li < left.size && compDate(left[li], right[ri]) == -1))
                left[li++]
            else
                right[ri++]
}

/**
 * Сортировка адресов
 *
 * Средняя
 *
 * Во входном файле с именем inputName содержатся фамилии и имена жителей города с указанием улицы и номера дома,
 * где они прописаны. Пример:
 *
 * Петров Иван - Железнодорожная 3
 * Сидоров Петр - Садовая 5
 * Иванов Алексей - Железнодорожная 7
 * Сидорова Мария - Садовая 5
 * Иванов Михаил - Железнодорожная 7
 *
 * Людей в городе может быть до миллиона.
 *
 * Вывести записи в выходной файл outputName,
 * упорядоченными по названию улицы (по алфавиту) и номеру дома (по возрастанию).
 * Людей, живущих в одном доме, выводить через запятую по алфавиту (вначале по фамилии, потом по имени). Пример:
 *
 * Железнодорожная 3 - Петров Иван
 * Железнодорожная 7 - Иванов Алексей, Иванов Михаил
 * Садовая 5 - Сидоров Петр, Сидорова Мария
 *
 * В случае обнаружения неверного формата файла бросить любое исключение.
 */
fun sortAddresses(inputName: String, outputName: String) {
    TODO()
}

/**
 * Сортировка температур
 *
 * Средняя
 * (Модифицированная задача с сайта acmp.ru)
 *
 * Во входном файле заданы температуры различных участков абстрактной планеты с точностью до десятых градуса.
 * Температуры могут изменяться в диапазоне от -273.0 до +500.0.
 * Например:
 *
 * 24.7
 * -12.6
 * 121.3
 * -98.4
 * 99.5
 * -12.6
 * 11.0
 *
 * Количество строк в файле может достигать ста миллионов.
 * Вывести строки в выходной файл, отсортировав их по возрастанию температуры.
 * Повторяющиеся строки сохранить. Например:
 *
 * -98.4
 * -12.6
 * -12.6
 * 11.0
 * 24.7
 * 99.5
 * 121.3
 */
fun sortTemperatures(inputName: String, outputName: String) {
    val list = mutableListOf<Float>() // R(N) = O(N), T(N) = 1
    File(inputName).forEachLine { list.add(it.toFloat()) } // T(N) = O(N)
    val arr = list.toFloatArray() // R(N) = O(N), T(N) = 1
    countingSort(arr) // T(N) = O(N)
    File(outputName).bufferedWriter().use { out ->
        arr.forEach {
            out.write("$it\n") // T(N) = O(N)
        }
    }
}
// T(N) = O(N)
// R(N) = O(N)

fun countingSort(elements: FloatArray) {
    var b = 0
    val negArr = IntArray(2731) { 0 }
    val posArr = IntArray(5011) { 0 }
    elements.forEach {
        if (it >= 0)
            posArr[(abs(it) * 10).toInt()]++
        else negArr[(abs(it) * 10).toInt()]++
    }
    for (j in negArr.size - 1 downTo 0)
        for (i in 0 until negArr[j])
            elements[b++] = "-${j / 10}.${j % 10}".toFloat()
    for (j in posArr.indices)
        for (i in 0 until posArr[j])
            elements[b++] = "${j / 10}.${j % 10}".toFloat()
}


/**
 * Сортировка последовательности
 *
 * Средняя
 * (Задача взята с сайта acmp.ru)
 *
 * В файле задана последовательность из n целых положительных чисел, каждое в своей строке, например:
 *
 * 1
 * 2
 * 3
 * 2
 * 3
 * 1
 * 2
 *
 * Необходимо найти число, которое встречается в этой последовательности наибольшее количество раз,
 * а если таких чисел несколько, то найти минимальное из них,
 * и после этого переместить все такие числа в конец заданной последовательности.
 * Порядок расположения остальных чисел должен остаться без изменения.
 *
 * 1
 * 3
 * 3
 * 1
 * 2
 * 2
 * 2
 */
fun sortSequence(inputName: String, outputName: String) {
    val map = mutableMapOf<Int, Int>() // T(N) = 1, R(N) = O(N)
    var pair = Pair(Int.MAX_VALUE, 0) // T(N) = 1
    val list = mutableListOf<Int>() // T(N) = 1, R(N) = O(N)
    File(inputName).forEachLine { // T(N) = O(N)
        val elem = it.toInt()
        if (!map.containsKey(elem)) map[elem] = 1
        else map[elem] = map[elem]!!.plus(1)
        val currMapValue = map[elem]!!
        if (currMapValue > pair.second || (currMapValue == pair.second && elem < pair.first))
            pair = Pair(elem, currMapValue)
        list.add(elem)
    }
    File(outputName).bufferedWriter().use { out ->
        list.forEach { // T(N) = O(N)
            if (it != pair.first)
                out.write("$it\n") // T(N) = 0..O(N-1)
        }
        for (i in 1..pair.second)
            out.write("${pair.first}\n") // T(N) = 1..O(N)
    }
}
// T(N) = O(N)
// R(N) = O(N)

/**
 * Соединить два отсортированных массива в один
 *
 * Простая
 *
 * Задан отсортированный массив first и второй массив second,
 * первые first.size ячеек которого содержат null, а остальные ячейки также отсортированы.
 * Соединить оба массива в массиве second так, чтобы он оказался отсортирован. Пример:
 *
 * first = [4 9 15 20 28]
 * second = [null null null null null 1 3 9 13 18 23]
 *
 * Результат: second = [1 3 4 9 9 13 15 20 23 28]
 */
fun <T : Comparable<T>> mergeArrays(first: Array<T>, second: Array<T?>) {
    TODO()
}

