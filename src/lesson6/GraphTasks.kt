@file:Suppress("UNUSED_PARAMETER", "unused")

package lesson6

import lesson6.impl.GraphBuilder

/**
 * Эйлеров цикл.
 * Средняя
 *
 * Дан граф (получатель). Найти по нему любой Эйлеров цикл.
 * Если в графе нет Эйлеровых циклов, вернуть пустой список.
 * Соседние дуги в списке-результате должны быть инцидентны друг другу,
 * а первая дуга в списке инцидентна последней.
 * Длина списка, если он не пуст, должна быть равна количеству дуг в графе.
 * Веса дуг никак не учитываются.
 *
 * Пример:
 *
 *      G -- H
 *      |    |
 * A -- B -- C -- D
 * |    |    |    |
 * E    F -- I    |
 * |              |
 * J ------------ K
 *
 * Вариант ответа: A, E, J, K, D, C, H, G, B, C, I, F, B, A
 *
 * Справка: Эйлеров цикл -- это цикл, проходящий через все рёбра
 * связного графа ровно по одному разу
 */
fun Graph.findEulerLoop(): List<Graph.Edge> {
    TODO()
}

/**
 * Минимальное остовное дерево.
 * Средняя
 *
 * Дан связный граф (получатель). Найти по нему минимальное остовное дерево.
 * Если есть несколько минимальных остовных деревьев с одинаковым числом дуг,
 * вернуть любое из них. Веса дуг не учитывать.
 *
 * Пример:
 *
 *      G -- H
 *      |    |
 * A -- B -- C -- D
 * |    |    |    |
 * E    F -- I    |
 * |              |
 * J ------------ K
 *
 * Ответ:
 *
 *      G    H
 *      |    |
 * A -- B -- C -- D
 * |    |    |
 * E    F    I
 * |
 * J ------------ K
 */
enum class Color { WHITE, GREY, BlACK }

fun findLoop(graph: Graph): Boolean {
    val tabuConnList = mutableMapOf<Graph.Edge, Boolean>()
    val info = mutableMapOf<String, Color>()
    var start = ""
    var loopInd = false

    graph.vertices.forEach {
        if (graph.getConnections(graph.get(it.name)!!).isNotEmpty())
            start = it.name
        for (u in graph.getConnections(graph.get(it.name)!!))
            tabuConnList[u.value] = true
        info[it.name] = Color.WHITE
    } // T(V, E) = O(V + E)

    fun dfs(v: String) {
        info[v] = Color.GREY
        for (u in graph.getConnections(graph.get(v)!!))
            if (tabuConnList[u.value]!!) {
                val currName = if (v == u.value.begin.name)
                    u.value.end.name
                else u.value.begin.name
                if (info[currName] == Color.WHITE) {
                    tabuConnList[u.value] = false
                    dfs(currName)
                } else if (info[currName] == Color.GREY)
                    loopInd = true
            }
        info[v] = Color.BlACK
    } // T(V, E) = O(V + E)

    if (start != "")
        dfs(start)

    return loopInd
}
// T(V, E) = O(V + E) + O(V + E) = O(V + E)

fun Graph.minimumSpanningTree(): Graph {
    val listOfEdges = edges.toList()
    val tabuIndexes = mutableMapOf<Int, Boolean>()
    listOfEdges.forEachIndexed { index, _ -> tabuIndexes[index] = true } // T(V, E) = O(E)
    for (i in listOfEdges.indices) { // T(V, E) = O(E)
        val graph = GraphBuilder().apply {
            vertices.forEach { addVertex(it.name) } // T(V, E) = O(V)
            for (j in 0..i) { // T(V, E) = O(E)
                if (tabuIndexes[j]!!)
                    addConnection(listOfEdges[j].begin, listOfEdges[j].end)
            }
        }.build()
        if (findLoop(graph)) // T(V, E) = O(V + E)
            tabuIndexes[i] = false
    } // T(V, E) = O(E) * (O(V) + O(E) + O(V + E)) = O(E) * O(V + E)
    val graph = GraphBuilder().apply {
        vertices.forEach { addVertex(it.name) } // T(V, E) = O(V)
        listOfEdges.forEachIndexed { index, it -> // T(V, E) = O(E)
            if (tabuIndexes[index]!!)
                addConnection(it.begin, it.end)
        }
    }.build() // T(V, E) = O(V + E)
    return graph
}
// T(V, E) = O(E) + O(E) * O(V + E) + O(V + E) = O(E) * O(V + E)

/**
 * Максимальное независимое множество вершин в графе без циклов.
 * Сложная
 *
 * Дан граф без циклов (получатель), например
 *
 *      G -- H -- J
 *      |
 * A -- B -- D
 * |         |
 * C -- F    I
 * |
 * E
 *
 * Найти в нём самое большое независимое множество вершин и вернуть его.
 * Никакая пара вершин в независимом множестве не должна быть связана ребром.
 *
 * Если самых больших множеств несколько, приоритет имеет тот из них,
 * в котором вершины расположены раньше во множестве this.vertices (начиная с первых).
 *
 * В данном случае ответ (A, E, F, D, G, J)
 *
 * Если на входе граф с циклами, бросить IllegalArgumentException
 */
fun Graph.largestIndependentVertexSet(): Set<Graph.Vertex> {
    var maxRes = mutableSetOf<Graph.Vertex>()
    val startK = mutableSetOf<Graph.Vertex>()
    var max = 0

    fun largestIndSet(M: MutableSet<Graph.Vertex>, K: MutableSet<Graph.Vertex>) {
        while (K.isNotEmpty()) {
            val curr = K.first()
            val newK = K.filter {
                getConnections(get(it.name)!!).size == getConnections(get(it.name)!!).filter { it1 ->
                    it == curr || it1.value.begin != curr && it1.value.end != curr
                }.size
            }.toMutableSet()
            if (M.size + newK.size > max) {
                newK.remove(curr)
                val newM = mutableSetOf<Graph.Vertex>()
                newM.add(curr)
                newM.addAll(M)
                largestIndSet(newM, newK)
            }
            println(curr)
            K.remove(curr)
        }
        if (M.size > max) {
            max = M.size
            maxRes = M
        }
    }

    vertices.forEach { startK.add(it) }
    largestIndSet(mutableSetOf(), startK)

    return maxRes
}
// Метод ветвей и границ:
// Получить полиномиальную оценку для числа шагов такого алгоритма сложно.
// Теоретически алгоритм ветвей и границ может в итоге свестись к просмотру всех базисных множеств
// (соответствует простому перебору множества решений).

/**
 * Наидлиннейший простой путь.
 * Сложная
 *
 * Дан граф (получатель). Найти в нём простой путь, включающий максимальное количество рёбер.
 * Простым считается путь, вершины в котором не повторяются.
 * Если таких путей несколько, вернуть любой из них.
 *
 * Пример:
 *
 *      G -- H
 *      |    |
 * A -- B -- C -- D
 * |    |    |    |
 * E    F -- I    |
 * |              |
 * J ------------ K
 *
 * Ответ: A, E, J, K, D, C, H, G, B, F, I
 */
fun Graph.longestSimplePath(): Path {
    TODO()
}

/**
 * Балда
 * Сложная
 *
 * Задача хоть и не использует граф напрямую, но решение базируется на тех же алгоритмах -
 * поэтому задача присутствует в этом разделе
 *
 * В файле с именем inputName задана матрица из букв в следующем формате
 * (отдельные буквы в ряду разделены пробелами):
 *
 * И Т Ы Н
 * К Р А Н
 * А К В А
 *
 * В аргументе words содержится множество слов для поиска, например,
 * ТРАВА, КРАН, АКВА, НАРТЫ, РАК.
 *
 * Попытаться найти каждое из слов в матрице букв, используя правила игры БАЛДА,
 * и вернуть множество найденных слов. В данном случае:
 * ТРАВА, КРАН, АКВА, НАРТЫ
 *
 * И т Ы Н     И т ы Н
 * К р а Н     К р а н
 * А К в а     А К В А
 *
 * Все слова и буквы -- русские или английские, прописные.
 * В файле буквы разделены пробелами, строки -- переносами строк.
 * Остальные символы ни в файле, ни в словах не допускаются.
 */
fun baldaSearcher(inputName: String, words: Set<String>): Set<String> {
    TODO()
}
