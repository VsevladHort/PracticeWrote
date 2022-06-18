package com.dak.wrote.backend.contracts.entities

data class Attribute(override val uniqueKey: String, var name: String) : UniqueEntity {
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

    fun addEntity(entity: UniqueEntity) {
        _associatedEntities += entity.uniqueKey
    }

    fun removeEntity(entity: UniqueEntity) {
        _associatedEntities.remove(entity.uniqueKey)
    }
}