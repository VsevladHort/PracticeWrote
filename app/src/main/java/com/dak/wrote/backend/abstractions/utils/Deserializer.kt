package com.dak.wrote.backend.abstractions.utils

interface Deserializer {
    fun deserialize(title: String): Boolean
}