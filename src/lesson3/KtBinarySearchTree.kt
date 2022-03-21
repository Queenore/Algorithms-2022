package lesson3

import java.util.*
import kotlin.math.max

// attention: Comparable is supported but Comparator is not
class KtBinarySearchTree<T : Comparable<T>> : AbstractMutableSet<T>(), CheckableSortedSet<T> {

    private class Node<T>(
        var value: T
    ) {
        var left: Node<T>? = null
        var right: Node<T>? = null
    }

    private var root: Node<T>? = null

    override var size = 0
        private set

    private fun find(value: T): Node<T>? =
        root?.let { find(it, value) }

    private fun find(start: Node<T>, value: T): Node<T> {
        val comparison = value.compareTo(start.value)
        return when {
            comparison == 0 -> start
            comparison < 0 -> start.left?.let { find(it, value) } ?: start
            else -> start.right?.let { find(it, value) } ?: start
        }
    }

    private fun findParent(value: T): Node<T>? {
        if (root == null) return null
        var node = root
        while ((node!!.left == null || node.left!!.value != value) && (node.right == null || node.right!!.value != value))
            if (node.right != null && node.value < value)
                node = node.right
            else if (node.left != null)
                node = node.left
            else {
                node = null
                break
            }
        return node
    }
    // худший случай: T(N) = O(height)

    override operator fun contains(element: T): Boolean {
        val closest = find(element)
        return closest != null && element.compareTo(closest.value) == 0
    }

    /**
     * Добавление элемента в дерево
     *
     * Если элемента нет в множестве, функция добавляет его в дерево и возвращает true.
     * В ином случае функция оставляет множество нетронутым и возвращает false.
     *
     * Спецификация: [java.util.Set.add] (Ctrl+Click по add)
     *
     * Пример
     */
    override fun add(element: T): Boolean {
        val closest = find(element)
        val comparison = if (closest == null) -1 else element.compareTo(closest.value)
        if (comparison == 0) {
            return false
        }
        val newNode = Node(element)
        when {
            closest == null -> root = newNode
            comparison < 0 -> {
                assert(closest.left == null)
                closest.left = newNode
            }
            else -> {
                assert(closest.right == null)
                closest.right = newNode
            }
        }
        size++
        return true
    }

    /**
     * Удаление элемента из дерева
     *
     * Если элемент есть в множестве, функция удаляет его из дерева и возвращает true.
     * В ином случае функция оставляет множество нетронутым и возвращает false.
     * Высота дерева не должна увеличиться в результате удаления.
     *
     * Спецификация: [java.util.Set.remove] (Ctrl+Click по remove)
     * (в Котлине тип параметера изменён с Object на тип хранимых в дереве данных)
     *
     * Средняя
     */
    override fun remove(element: T): Boolean {
        TODO()
    }

    override fun comparator(): Comparator<in T>? =
        null

    override fun iterator(): MutableIterator<T> =
        BinarySearchTreeIterator()

    inner class BinarySearchTreeIterator internal constructor() : MutableIterator<T> {

        private var currNode = root
        private val list = mutableListOf<Node<T>?>() // R(N) = O(N)
        private var flag = false

        // реализован прямой обход дерева с сохранением нод в list, которые были пройдены
        private fun traverse(): Node<T>? {
            if (root == null) return null
            var node = root

            if (currNode == root && list.isEmpty())
                while (node!!.left != null) // худший случай: T(N) = O(height)
                    node = node.left
            else node = currNode

            if (node!!.right != null) {
                if (list.contains(node)) // T(N) = O(N)
                    node = node.right
                while (node!!.left != null) // худший случай: T(N) = O(height)
                    node = node.left
            } else {
                while (list.contains(node)) // T(N) = O(N) + O(height) = O(N)
                    node = findParent(node!!.value) // худший случай: T(N) = O(height)
            }
            return node
        }
        // худший случай: T(N) = O(N * height)

        /**
         * Проверка наличия следующего элемента
         *
         * Функция возвращает true, если итерация по множеству ещё не окончена (то есть, если вызов next() вернёт
         * следующий элемент множества, а не бросит исключение); иначе возвращает false.
         *
         * Спецификация: [java.util.Iterator.hasNext] (Ctrl+Click по hasNext)
         *
         * Средняя
         */
        override fun hasNext(): Boolean = traverse() != null
        // худший случай: T(N) = O(N * height)

        /**
         * Получение следующего элемента
         *
         * Функция возвращает следующий элемент множества.
         * Так как BinarySearchTree реализует интерфейс SortedSet, последовательные
         * вызовы next() должны возвращать элементы в порядке возрастания.
         *
         * Бросает NoSuchElementException, если все элементы уже были возвращены.
         *
         * Спецификация: [java.util.Iterator.next] (Ctrl+Click по next)
         *
         * Средняя
         */
        override fun next(): T {
            currNode = traverse() // T(N) = O(N * height)
            list.add(currNode)
            flag = false
            if (currNode != null)
                return currNode!!.value
            else throw NoSuchElementException()
        }
        // худший случай: T(N) = O(N * height)

        /**
         * Удаление предыдущего элемента
         *
         * Функция удаляет из множества элемент, возвращённый крайним вызовом функции next().
         *
         * Бросает IllegalStateException, если функция была вызвана до первого вызова next() или же была вызвана
         * более одного раза после любого вызова next().
         *
         * Спецификация: [java.util.Iterator.remove] (Ctrl+Click по remove)
         *
         * Сложная
         */
        private fun hasNoDescendant(node: Node<T>?) = (node == null || (node.left == null && node.right == null))

        private fun findRightMaxNode(node: Node<T>?): Node<T>? {
            var nd: Node<T>? = node ?: return null
            while (nd!!.right != null)
                nd = nd.right!!
            return nd
        }
        // худший случай: T(N) = O(height)

        override fun remove() {
            if (list.isEmpty() || flag)
                throw IllegalStateException()

            val node = currNode
            val parent = findParent(node!!.value) // T(N) = O(height)
            val leftChild = node.left
            val rightChild = node.right
            val value = node.value

            if (root!!.value == value && hasNoDescendant(root)) {
                root = null
            } else if (root!!.value == value && rightChild == null && leftChild != null) { // value is root and has 1 left descendant
                root = leftChild
            } else if (root!!.value == value && leftChild == null && rightChild != null) { // value is root and has 1 right descendant
                root = rightChild
            } else if (leftChild == null && rightChild == null) { // value has no descendant
                if (parent!!.value > value) parent.left = null else parent.right = null
            } else if (rightChild == null) { // value has 1 left descendant
                if (parent!!.value > value) parent.left = leftChild else parent.right = leftChild
            } else if (leftChild == null) { // value has 1 right descendant
                if (parent!!.value > value) parent.left = rightChild else parent.right = rightChild
            } else { // value has 2 descendant
                val rightMaxNode = findRightMaxNode(leftChild) // T(N) = O(height)
                val rightMaxNodeParent = findParent(rightMaxNode!!.value) // T(N) = O(height)

                if ((leftChild.left != null && leftChild.right == null) || hasNoDescendant(leftChild)) {
                    node.left = leftChild.left
                    node.value = leftChild.value
                } else {
                    node.value = rightMaxNode.value
                    if (rightMaxNodeParent != null)
                        rightMaxNodeParent.right = rightMaxNode.left
                }
            }

            size--
            flag = true
            list.removeLast()
            currNode = if (list.isNotEmpty()) list.last() else root
        }
        // худший случай: T(N) = O(height)
    }

    /**
     * Подмножество всех элементов в диапазоне [fromElement, toElement)
     *
     * Функция возвращает множество, содержащее в себе все элементы дерева, которые
     * больше или равны fromElement и строго меньше toElement.
     * При равенстве fromElement и toElement возвращается пустое множество.
     * Изменения в дереве должны отображаться в полученном подмножестве, и наоборот.
     *
     * При попытке добавить в подмножество элемент за пределами указанного диапазона
     * должен быть брошен IllegalArgumentException.
     *
     * Спецификация: [java.util.SortedSet.subSet] (Ctrl+Click по subSet)
     * (настоятельно рекомендуется прочитать и понять спецификацию перед выполнением задачи)
     *
     * Очень сложная (в том случае, если спецификация реализуется в полном объёме)
     */
    override fun subSet(fromElement: T, toElement: T): SortedSet<T> {
        TODO()
    }

    /**
     * Подмножество всех элементов строго меньше заданного
     *
     * Функция возвращает множество, содержащее в себе все элементы дерева строго меньше toElement.
     * Изменения в дереве должны отображаться в полученном подмножестве, и наоборот.
     *
     * При попытке добавить в подмножество элемент за пределами указанного диапазона
     * должен быть брошен IllegalArgumentException.
     *
     * Спецификация: [java.util.SortedSet.headSet] (Ctrl+Click по headSet)
     * (настоятельно рекомендуется прочитать и понять спецификацию перед выполнением задачи)
     *
     * Сложная
     */
    override fun headSet(toElement: T): SortedSet<T> {
        TODO()
    }

    /**
     * Подмножество всех элементов нестрого больше заданного
     *
     * Функция возвращает множество, содержащее в себе все элементы дерева нестрого больше toElement.
     * Изменения в дереве должны отображаться в полученном подмножестве, и наоборот.
     *
     * При попытке добавить в подмножество элемент за пределами указанного диапазона
     * должен быть брошен IllegalArgumentException.
     *
     * Спецификация: [java.util.SortedSet.tailSet] (Ctrl+Click по tailSet)
     * (настоятельно рекомендуется прочитать и понять спецификацию перед выполнением задачи)
     *
     * Сложная
     */
    override fun tailSet(fromElement: T): SortedSet<T> {
        TODO()
    }

    override fun first(): T {
        var current: Node<T> = root ?: throw NoSuchElementException()
        while (current.left != null) {
            current = current.left!!
        }
        return current.value
    }

    override fun last(): T {
        var current: Node<T> = root ?: throw NoSuchElementException()
        while (current.right != null) {
            current = current.right!!
        }
        return current.value
    }

    override fun height(): Int =
        height(root)

    private fun height(node: Node<T>?): Int {
        if (node == null) return 0
        return 1 + max(height(node.left), height(node.right))
    }

    override fun checkInvariant(): Boolean =
        root?.let { checkInvariant(it) } ?: true

    private fun checkInvariant(node: Node<T>): Boolean {
        val left = node.left
        if (left != null && (left.value >= node.value || !checkInvariant(left))) return false
        val right = node.right
        return right == null || right.value > node.value && checkInvariant(right)
    }
}