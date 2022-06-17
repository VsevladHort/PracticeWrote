package com.dak.wrote.backend.abstractions.dao

import com.dak.wrote.backend.abstractions.entities.constants.NoteType
import com.dak.wrote.backend.abstractions.entities.Book
import com.dak.wrote.backend.abstractions.entities.BaseNote
import com.dak.wrote.backend.abstractions.entities.UniqueEntity

interface WroteDao {
    suspend fun createBook(title: String): Book?
    suspend fun createNote(parent: UniqueEntity, title: String, type: NoteType): BaseNote?
    suspend fun updateNoteEntry(note: BaseNote): Boolean
    suspend fun updateNoteObject(note: BaseNote): Boolean
    suspend fun updateBookEntry(book: Book): Boolean
    suspend fun updateBookObject(book: Book): Boolean
    suspend fun getNote(uniqueKey: String): BaseNote?
}