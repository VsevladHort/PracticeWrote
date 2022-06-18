package com.dak.wrote.backend.abstractions.dao

import com.dak.wrote.backend.abstractions.entities.Attribute
import com.dak.wrote.backend.abstractions.entities.constants.NoteType
import com.dak.wrote.backend.abstractions.entities.Book
import com.dak.wrote.backend.abstractions.entities.BaseNote
import com.dak.wrote.backend.abstractions.entities.UniqueEntity

interface WroteDao {
    suspend fun insertBook(title: String): Boolean
    suspend fun insetNote(parent: UniqueEntity, title: String, type: NoteType): Boolean
    suspend fun insetAttribute(attribute: Attribute): Boolean
    suspend fun updateAttributeEntry(note: BaseNote): Boolean
    suspend fun updateAttributeObject(book: Book): Boolean
    suspend fun updateNoteEntry(note: BaseNote): Boolean
    suspend fun updateNoteObject(note: BaseNote): Boolean
    suspend fun updateBookEntry(book: Book): Boolean
    suspend fun updateBookObject(book: Book): Boolean
    suspend fun getNote(uniqueKey: String): BaseNote?
    suspend fun getAttribute(uniqueKey: String): Attribute?
    suspend fun getBooks(): List<Book>
    suspend fun getChildrenKeys(entry: BaseNote): List<String>
    suspend fun getChildrenKeys(entry: Book): List<String>
    suspend fun getAlternateTitles(entry: BaseNote): List<String>
    suspend fun getParentKey(entry: BaseNote): String
    suspend fun deleteEntity(entity: UniqueEntity): Boolean
}