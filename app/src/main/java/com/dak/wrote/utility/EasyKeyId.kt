package com.dak.wrote.utility

/**
 * Class used to get ids for items
 */
class EasyKeyId {
    private var starting = 0

    val take : Int
        get() {
            return starting++
        }

    fun <T> map(l : List<T>): List<Pair<Int, T>> {
        return l.map { take to it }
    }
}