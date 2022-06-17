package com.dak.wrote.backend.abstractions.entities

interface SearchableEntity {
    fun getIndexingData(): String
}