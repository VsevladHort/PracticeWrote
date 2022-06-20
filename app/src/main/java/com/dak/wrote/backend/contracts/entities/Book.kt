package com.dak.wrote.backend.contracts.entities

data class Book(override val uniqueKey: String, var title: String) : UniqueEntity