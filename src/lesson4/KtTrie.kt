package lesson4

import java.util.*
import kotlin.NoSuchElementException

/**
 * Префиксное дерево для строк
 */
class KtTrie : AbstractMutableSet<String>(), MutableSet<String> {

    private class Node {
        val children: SortedMap<Char, Node> = sortedMapOf()
    }

    private val root = Node()

    override var size: Int = 0
        private set

    override fun clear() {
        root.children.clear()
        size = 0
    }

    private fun String.withZero() = this + 0.toChar()

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
        if (current.children.remove(0.toChar()) != null) {
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
    override fun iterator(): MutableIterator<String> = KtTrieIterator()

    inner class KtTrieIterator internal constructor() : MutableIterator<String> {

        private lateinit var currNode: Map.Entry<Char, Node>
        private var currStr = StringBuilder()
        private var iterCounter = 0
        private var oneTimeRemoveFlag = false
        private var deque = ArrayDeque<Map.Entry<Char, Node>>()
        private var stringDeque = ArrayDeque<Pair<String, Map.Entry<Char, Node>>>()

        init {
            root.children.forEach { deque.addLast(it) }
        }

        private fun traverse() {
            currStr = StringBuilder(if (stringDeque.isNotEmpty()) stringDeque.peekFirst().first else "")
            do {
                currNode = deque.pollFirst()
                currStr.append(currNode.key)

                if (stringDeque.isNotEmpty() && stringDeque.first.second.value.children.values.last() == currNode.value)
                    stringDeque.pollFirst()

                if ((currNode.value.children.firstKey().code == 0 && currNode.value.children.size != 1)
                    || currNode.value.children.size > 1
                ) stringDeque.addFirst(Pair(currStr.toString(), currNode))

                val tempDeque = ArrayDeque<Map.Entry<Char, Node>>()                              //---|   за все итер.
                currNode.value.children.forEach { if (it.key.code != 0) tempDeque.addFirst(it) } //   |-> = 2*O(N) =
                tempDeque.forEach { if (it.key.code != 0) deque.addFirst(it) }                   //---|   = O(N)
            } while (currNode.value.children.firstKey().code != 0)
            iterCounter++
        }
        // Для while: T(M), где M - кол-во итераций до нижней ноды, являющейся последней буквой слова.
        // При этом если мы добрались до ноды, у которой один потомок с кодом 0, то берем ноду из deque, что
        // обеспечивает наилучшее быстродействие.
        // По итогу получается, что T(N) = O(N).

        override fun hasNext(): Boolean = iterCounter < size
        // T(N) = O(1)

        override fun next(): String {
            if (iterCounter < size) {
                traverse()
                oneTimeRemoveFlag = false
                return currStr.toString()
            } else throw NoSuchElementException()
        }
        // T(N) = O(N)

        override fun remove() {
            if (oneTimeRemoveFlag || iterCounter == 0)
                throw IllegalStateException()
            var prev = root.children
            var current = root
            var tempRemoveCounter = 0
            var removeCounter = 0
            for (char in currStr) { // T(N) = O(N)
                current = current.children[char]!!
                tempRemoveCounter++
                if (current.children.size > 1) {
                    removeCounter += tempRemoveCounter
                    tempRemoveCounter = 0
                    prev = current.children
                }
            }
            if (tempRemoveCounter == 0) prev.remove(prev.firstKey()) // T(N) = O(N)
            else prev.remove(currStr[removeCounter]) // T(N) = O(N)
            oneTimeRemoveFlag = true
            iterCounter--
            size--
        }
        // T(N) = O(N)
    }

}