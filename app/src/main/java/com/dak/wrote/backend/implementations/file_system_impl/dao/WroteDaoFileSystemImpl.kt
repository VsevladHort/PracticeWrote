package com.dak.wrote.backend.implementations.file_system_impl.dao

import com.dak.wrote.backend.contracts.dao.WroteDao
import com.dak.wrote.backend.contracts.entities.Attribute
import com.dak.wrote.backend.contracts.entities.BaseNote
import com.dak.wrote.backend.contracts.entities.Book
import com.dak.wrote.backend.contracts.entities.UniqueEntity
import com.dak.wrote.backend.contracts.entities.constants.NoteType
import com.dak.wrote.backend.implementations.file_system_impl.DATA_AUXILIARY_FILE_NAME
import com.dak.wrote.backend.implementations.file_system_impl.DIR_BOOKS
import java.io.File

class WroteDaoFileSystemImpl(private val baseDir: File) : WroteDao {

    override suspend fun insertBook(book: Book): Boolean {
        val file = File(book.uniqueKey)
        val auxiliaryFile = File(file, DATA_AUXILIARY_FILE_NAME)
        auxiliaryFile.printWriter().use { println(book.title) }
        return true
    }

    override suspend fun insetNote(parent: UniqueEntity, note: BaseNote): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun insetAttribute(attribute: Attribute): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun updateAttributeEntry(value: Attribute): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun updateAttributeObject(value: Attribute): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun updateNoteEntry(note: BaseNote): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun updateNoteObject(note: BaseNote): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun updateBookEntry(book: Book): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun updateBookObject(book: Book): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun getNoteType(uniqueKey: String): NoteType? {
        TODO("Not yet implemented")
    }

    override suspend fun getNoteSaveData(uniqueKey: String): ByteArray? {
        TODO("Not yet implemented")
    }

    override suspend fun getAttribute(uniqueKey: String): Attribute? {
        TODO("Not yet implemented")
    }

    override suspend fun getAttributes(book: Book): List<Attribute> {
        TODO("Not yet implemented")
    }

    override suspend fun getAttributes(uniqueKey: String): List<Attribute> {
        TODO("Not yet implemented")
    }

    override suspend fun getBooks(): List<Book> {
        TODO("Not yet implemented")
    }

    override suspend fun getChildrenKeys(uniqueKey: String): List<String> {
        TODO("Not yet implemented")
    }

    override suspend fun getChildrenKeys(entry: Book): List<String> {
        TODO("Not yet implemented")
    }

    override suspend fun getAlternateTitles(uniqueKey: String): List<String> {
        TODO("Not yet implemented")
    }

    override suspend fun insertAlternateTitles(uniqueKey: String, titles: List<String>): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun insertAttributes(uniqueKey: String, titles: List<Attribute>): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun getParentKey(entry: BaseNote): String {
        TODO("Not yet implemented")
    }

    override suspend fun deleteEntity(entity: Book): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun deleteEntity(entity: BaseNote): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun deleteEntity(entity: Attribute): Boolean {
        TODO("Not yet implemented")
    }
}