package com.dak.wrote.backend.implementations.entities

import com.dak.wrote.backend.abstractions.entities.Book

data class BookImpl(
    override var title: String,
    override var children: List<String>,
    override val uniqueKey: String
) : Book