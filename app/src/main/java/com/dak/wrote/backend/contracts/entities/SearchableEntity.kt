package com.dak.wrote.backend.contracts.entities

interface SearchableEntity {
    fun getIndexingData(): String
}