package com.dak.wrote.backend.abstractions.entities

interface UniqueEntityKeyGenerator {
    fun getKey(parent: UniqueEntity): String
}