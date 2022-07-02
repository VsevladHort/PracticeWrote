package com.dak.wrote.backend.contracts.dao

import com.dak.wrote.backend.contracts.database.EntryType
import com.dak.wrote.backend.contracts.database.UniqueEntityKeyGenerator
import com.dak.wrote.backend.contracts.entities.*
import com.dak.wrote.backend.contracts.entities.constants.NoteType

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
    suspend fun insertNote(parent: UniqueEntity, note: BaseNote): Boolean

    /**
     * Creates a Preset entry in the database
     *
     * @return true if the insert was successful, false otherwise
     */
    suspend fun <Display : UniqueEntity, Full : UniqueEntity> insertPreset(
        presetManager: PresetManager<Display, Full>, display: Display,
        full: Full
    ): Boolean

    suspend fun <Display : UniqueEntity> updatePresetDisplay(
        presetManager: PresetManager<Display, *>,
        display: Display,
    ): Boolean

    suspend fun <Full : UniqueEntity> updatePresetFull(
        presetManager: PresetManager<*, Full>,
        full: Full,
    ): Boolean

    /**
     * Deletes the Preset identified by the given key
     *
     * @return true if the delete was successful, false otherwise
     */
    suspend fun deletePreset(uniqueKey: String): Boolean

    /**
     * Returns display of the preset identified be the given uniqueKey,
     * constructed by the provided PresetManager
     *
     * @return display representation of the preset
     */
    suspend fun <Display : UniqueEntity> getPresetDisplay(
        presetManager: PresetManager<Display, *>, uniqueKey: String
    ): Display

    /**
     * Returns full of the preset identified be the given uniqueKey,
     * constructed by the provided PresetManager
     *
     * @return full representation of the preset
     */
    suspend fun <Full : UniqueEntity> getPresetFull(
        presetManager: PresetManager<*, Full>, uniqueKey: String
    ): Full

    /**
     * Creates an Attribute entry in the database
     *
     * @return true if the insert was successful, false otherwise
     */
    suspend fun insertAttribute(attribute: Attribute): Boolean

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
     * @param uniqueKey - unique key identifying the note
     *
     * @return unique key identifying the book the note belongs
     */
    suspend fun getBookOfNote(uniqueKey: String): String

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
    @Deprecated("Use the method that takes unique key instead")
    suspend fun getAttributes(book: Book): List<Attribute>

    /**
     * @return A set of keys of notes created within the given book
     */
    suspend fun getNoteKeys(book: Book): Set<String>

    /**
     * @return A set of keys of notes created within the given book that do not have attributes
     */
    suspend fun getNoteKeysWithoutAttributes(book: Book): Set<String>

    /**
     * @return A list of all preset keys created within the app
     */
    suspend fun getPresets(): List<String>

    /**
     * @return A list of all books created within the app
     */
    suspend fun getBooks(): List<Book>

    suspend fun getBook(uniqueKey: String): Book

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
    suspend fun insertAttributes(uniqueKey: String, titles: Set<Attribute>): Boolean

    /**
     * @return parent key for the given note
     */
    suspend fun getParentKey(uniqueKey: String): String

    /**
     * @return name of the entity identified by the given uniqueKey
     */
    suspend fun getName(uniqueKey: String): String

    /**
     * deletes the given book
     *
     * @return true if successful, false otherwise
     */
    suspend fun deleteEntityBook(entity: String): Boolean

    /**
     * deletes the given note
     *
     * @return true if successful, false otherwise
     */
    suspend fun deleteEntityNote(entity: String): Boolean

    /**
     * deletes the given attribute
     *
     * @return true if successful, false otherwise
     */
    suspend fun deleteEntityAttribute(entity: Attribute): Boolean

    suspend fun getOrCreateAttribute(
        book: Book,
        keyGenerator: UniqueEntityKeyGenerator,
        name: String
    ): Attribute
}