package com.dak.wrote.backend.abstractions.entities

interface Attribute : UniqueEntity {
    fun getAssociatedEntities(): List<String>
    fun addEntity(entity: UniqueEntity)
    fun removeEntity(entity: UniqueEntity)
    var name: String
}