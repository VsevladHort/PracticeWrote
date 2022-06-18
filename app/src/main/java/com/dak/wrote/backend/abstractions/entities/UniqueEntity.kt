package com.dak.wrote.backend.abstractions.entities

sealed interface UniqueEntity {
    val uniqueKey: String
    val associatedAttributes: List<Attribute>
}