package com.dak.wrote.frontend.noteNavigation

import com.dak.wrote.backend.contracts.entities.Book
import com.dak.wrote.backend.contracts.entities.UniqueEntity

data class NavigationNote(override val uniqueKey: String, val title: String) : UniqueEntity {
    constructor(book: Book) : this(book.uniqueKey, book.title)
}