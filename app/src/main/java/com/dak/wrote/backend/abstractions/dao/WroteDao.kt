package com.dak.wrote.backend.abstractions.dao

import com.dak.wrote.backend.abstractions.entities.constants.NoteType
import com.dak.wrote.backend.abstractions.entities.Book
import com.dak.wrote.backend.abstractions.entities.BaseNote
import com.dak.wrote.backend.abstractions.entities.UniqueEntity

interface WroteDao {
    suspend fun insertBook(title: String): Book?
    suspend fun insetNote(parent: UniqueEntity, title: String, type: NoteType): BaseNote?
    suspend fun updateNoteEntry(note: BaseNote): Boolean
    suspend fun updateNoteObject(note: BaseNote): Boolean
    suspend fun updateBookEntry(book: Book): Boolean
    suspend fun updateBookObject(book: Book): Boolean
    suspend fun getNote(uniqueKey: String): BaseNote?
    suspend fun getBooks(uniqueKey: String): List<Book>?
    suspend fun deleteEntity(entity: UniqueEntity): Boolean
}