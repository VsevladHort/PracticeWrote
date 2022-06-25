package com.dak.wrote.backend.contracts.dao

import com.dak.wrote.backend.contracts.database.EntryType
import com.dak.wrote.backend.contracts.entities.Attribute
import com.dak.wrote.backend.contracts.entities.constants.NoteType
import com.dak.wrote.backend.contracts.entities.Book
import com.dak.wrote.backend.contracts.entities.BaseNote
import com.dak.wrote.backend.contracts.entities.UniqueEntity

interface WroteDao {
    /**
     * Creates a Book entry in the database
     *
     * @return true if the insert was successful, false otherwise
     */
    suspend fun insertBook(book: Book): Boolean

    /**
     * Creates a Note entry in the database
     *
     * @return true if the insert was successful, false otherwise
     */
    suspend fun insetNote(parent: UniqueEntity, note: BaseNote): Boolean

    /**
     * Creates a Preset entry in the database
     *
     * @return true if the insert was successful, false otherwise
     */
    suspend fun insetPreset(note: BaseNote): Boolean

    /**
     * Creates an Attribute entry in the database
     *
     * @return true if the insert was successful, false otherwise
     */
    suspend fun insetAttribute(attribute: Attribute): Boolean

    /**
     * Updates an Attribute entry in the database
     *
     * @return true if the insert was successful, false otherwise
     */
    suspend fun updateAttributeEntry(value: Attribute): Boolean

    /**
     * Updates an Attribute object with the data from the database
     *
     * @return true if the insert was successful, false otherwise
     */
    suspend fun updateAttributeObject(value: Attribute): Boolean

    /**
     * Updates a BaseNote entry in the database
     *
     * @return true if the insert was successful, false otherwise
     */
    suspend fun updateNoteEntry(note: BaseNote): Boolean

    /**
     * Updates a BaseNote object with the data from the database
     *
     * @return true if the insert was successful, false otherwise
     */
    suspend fun updateNoteObject(note: BaseNote): Boolean

    /**
     * Updates a Book entry in the database
     *
     * @return true if the insert was successful, false otherwise
     */
    suspend fun updateBookEntry(book: Book): Boolean

    /**
     * Updates a Book object with the data from the database
     *
     * @return true if the insert was successful, false otherwise
     */
    suspend fun updateBookObject(book: Book): Boolean

    /**
     * @param uniqueKey - unique key identifying the note
     *
     * @return NoteType of the note with the given key if such a note entry exists, null otherwise
     */
    suspend fun getNoteType(uniqueKey: String): NoteType?

    /**
     * @param uniqueKey - unique key identifying the note
     *
     * @return save data of the note with the given key if such a note entry exists, null otherwise
     */
    suspend fun getNoteSaveData(uniqueKey: String): ByteArray?

    /**
     * @param uniqueKey - unique key identifying the entry
     *
     * @return EntryType of the entry
     */
    suspend fun getEntryType(uniqueKey: String): EntryType

    /**
     * @param uniqueKey - unique key identifying the attribute
     *
     * @return Attribute object representing an attribute
     * stored with the given key if such an attribute entry exists, null otherwise
     */
    suspend fun getAttribute(uniqueKey: String): Attribute?

    /**
     * @return A list of all attributes created within a book
     */
    suspend fun getAttributes(book: Book): List<Attribute>

    /**
     * @return A list of keys of notes created within the given book
     */
    suspend fun getNoteKeys(book: Book): List<String>

    /**
     * @return A list of all preset keys created within the app
     */
    suspend fun getPresets(): List<String>

    /**
     * @return A list of all books created within the app
     */
    suspend fun getBooks(): List<Book>

    /**
     * @return A list of unique keys of children of the note identified by the given key
     */
    suspend fun getChildrenKeys(uniqueKey: String): List<String>

    /**
     * @return A list of unique keys of children of a given Book
     */
    suspend fun getChildrenKeys(entry: Book): List<String>

    /**
     * @return A list of alternative titles of the note identified by the given key
     */
    suspend fun getAlternateTitles(uniqueKey: String): List<String>

    /**
     * inserts alternative titles for the Note identified with the given key
     *
     * @return true if the insert was successful, false otherwise
     */
    suspend fun insertAlternateTitles(uniqueKey: String, titles: List<String>): Boolean

    /**
     * @return A list of attributes of the note identified by the given key
     */
    suspend fun getAttributes(uniqueKey: String): Set<Attribute>

    /**
     * inserts attributes for the Note identified with the given key
     *
     * @return true if the insert was successful, false otherwise
     */
    suspend fun insertAttributes(uniqueKey: String, titles: List<Attribute>): Boolean

    /**
     * @return parent key for the given note
     */
    suspend fun getParentKey(entry: BaseNote): String

    /**
     * @return name of the entity identified by the given uniqueKey
     */
    suspend fun getName(uniqueKey: String): String

    /**
     * deletes the given uniqueEntity
     *
     * @return true if successful, false otherwise
     */
    suspend fun deleteEntity(entity: Book): Boolean

    /**
     * deletes the given uniqueEntity
     *
     * @return true if successful, false otherwise
     */
    suspend fun deleteEntity(entity: BaseNote): Boolean

    /**
     * deletes the given uniqueEntity
     *
     * @return true if successful, false otherwise
     */
    suspend fun deleteEntity(entity: Attribute): Boolean
}