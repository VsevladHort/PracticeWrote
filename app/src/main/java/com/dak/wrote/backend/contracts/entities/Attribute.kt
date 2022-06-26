package com.dak.wrote.backend.contracts.entities

data class Attribute(override val uniqueKey: String, var name: String) : UniqueEntity, Comparable<Attribute> {
    private var _associatedEntities = mutableSetOf<String>()
    var associatedEntities: Set<String>
        get() {
            return _associatedEntities
        }
        set(value) {
            _associatedEntities = run {
                val result = mutableSetOf<String>()
                value.forEach { result += it }
                result
            }
        }

    fun addEntity(uniqueKey: String) {
        _associatedEntities += uniqueKey
    }

    fun removeEntity(uniqueKey: String) {
        _associatedEntities.remove(uniqueKey)
    }

    override fun compareTo(other: Attribute): Int {
        return uniqueKey.compareTo(other.uniqueKey)
    }
}