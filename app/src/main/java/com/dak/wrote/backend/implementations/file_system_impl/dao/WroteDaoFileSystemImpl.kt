package com.dak.wrote.backend.implementations.file_system_impl.dao

import com.dak.wrote.backend.contracts.dao.WroteDao
import com.dak.wrote.backend.contracts.entities.Attribute
import com.dak.wrote.backend.contracts.entities.BaseNote
import com.dak.wrote.backend.contracts.entities.Book
import com.dak.wrote.backend.contracts.entities.UniqueEntity
import com.dak.wrote.backend.contracts.entities.constants.NoteType
import com.dak.wrote.backend.implementations.file_system_impl.*
import com.dak.wrote.backend.implementations.file_system_impl.dao.exceptions.UnknownKeyException
import java.io.File

class WroteDaoFileSystemImpl(private val baseDir: File) : WroteDao {

    override suspend fun insertBook(book: Book): Boolean {
        val file = File(book.uniqueKey)
        if (!file.exists())
            return false
        val auxiliaryFile = File(file, DATA_AUXILIARY_FILE_NAME)
        auxiliaryFile.printWriter().use { println(book.title) }
        return true
    }

    override suspend fun insetNote(parent: UniqueEntity, note: BaseNote): Boolean {
        val file = File(note.uniqueKey)
        if (!file.exists())
            return false
        val auxiliaryFile = File(file, DATA_AUXILIARY_FILE_NAME)
        val dataFile = File(file, DATA_MAIN_FILE_NAME)
        val attributes = File(file, DATA_NOTE_ATTRIBUTES)
        auxiliaryFile.printWriter().use { pw ->
            println(note.type.type)
            println(note.title)
            note.alternateTitles.forEach { pw.println(it) }
        }
        attributes.printWriter().use {
            note.attributes.forEach { println(it.uniqueKey) }
        }
        dataFile.writeBytes(note.generateSaveData())
        return true
    }

    override suspend fun insetAttribute(attribute: Attribute): Boolean {
        val file = File(attribute.uniqueKey)
        if (!file.exists())
            return false
        val auxiliaryFile = File(file, DATA_AUXILIARY_FILE_NAME)
        auxiliaryFile.printWriter().use {
            println(attribute.name)
            attribute.associatedEntities.forEach { println(it) }
        }
        return true
    }

    override suspend fun updateAttributeEntry(value: Attribute): Boolean {
        return insetAttribute(value)
    }

    override suspend fun updateAttributeObject(value: Attribute): Boolean {
        val file = File(value.uniqueKey)
        if (!file.exists())
            return false
        val auxiliaryFile = File(file, DATA_AUXILIARY_FILE_NAME)
        val mutableSet = mutableSetOf<String>()
        auxiliaryFile.readLines().withIndex().forEach {
            if (it.index == 0)
                value.name = it.value
            else
                mutableSet.add(it.value)
        }
        value.associatedEntities = mutableSet
        return true
    }

    override suspend fun updateNoteEntry(note: BaseNote): Boolean {
        val file = File(note.uniqueKey)
        if (!file.exists())
            return false
        val auxiliaryFile = File(file, DATA_AUXILIARY_FILE_NAME)
        val dataFile = File(file, DATA_MAIN_FILE_NAME)
        val attributes = File(file, DATA_NOTE_ATTRIBUTES)
        auxiliaryFile.printWriter().use { pw ->
            println(note.type.type)
            println(note.title)
            note.alternateTitles.forEach { pw.println(it) }
        }
        attributes.printWriter().use {
            note.attributes.forEach { println(it.uniqueKey) }
        }
        dataFile.writeBytes(note.generateSaveData())
        return true
    }

    override suspend fun updateNoteObject(note: BaseNote): Boolean {
        val file = File(note.uniqueKey)
        if (!file.exists())
            return false
        val auxiliaryFile = File(file, DATA_AUXILIARY_FILE_NAME)
        val dataFile = File(file, DATA_MAIN_FILE_NAME)
        val attributes = File(file, DATA_NOTE_ATTRIBUTES)
        val altTitleSet = mutableSetOf<String>()
        auxiliaryFile.readLines().withIndex().forEach { pair ->
            when (pair.index) {
                0 -> {
                }
                1 -> {
                    note.title = pair.value
                }
                else -> {
                    altTitleSet.add(pair.value)
                }
            }
        }
        note.alternateTitles = altTitleSet
        val attrSet = mutableSetOf<Attribute>()
        attributes.readLines().forEach {
            getAttribute(it).let { it1 -> attrSet.add(it1) }
        }
        note.attributes = attrSet
        note.loadSaveData(dataFile.readBytes())
        return true
    }

    override suspend fun updateBookEntry(book: Book): Boolean {
        return insertBook(book)
    }

    override suspend fun updateBookObject(book: Book): Boolean {
        val file = File(book.uniqueKey)
        if (!file.exists())
            return false
        if (!file.exists())
            return false
        val auxiliaryFile = File(file, DATA_AUXILIARY_FILE_NAME)
        book.title = auxiliaryFile.readLines()[0]
        return true
    }

    override suspend fun getNoteType(uniqueKey: String): NoteType? {
        val file = File(uniqueKey)
        if (!file.exists())
            throw UnknownKeyException("Provided key leads nowhere")
        val auxiliaryFile = File(file, DATA_AUXILIARY_FILE_NAME)
        File(file, DATA_MAIN_FILE_NAME)
        File(file, DATA_NOTE_ATTRIBUTES)
        // should be expanded to correspond to NoteType properly
        when (auxiliaryFile.readLines()[0]) {
            NoteType.PLAIN_TEXT.type -> return NoteType.PLAIN_TEXT
        }
        return null
    }

    override suspend fun getNoteSaveData(uniqueKey: String): ByteArray? {
        val file = File(uniqueKey)
        if (!file.exists())
            throw UnknownKeyException("Provided key leads nowhere")
        val dataFile = File(file, DATA_MAIN_FILE_NAME)
        return if (dataFile.exists())
            dataFile.readBytes()
        else
            null
    }

    override suspend fun getAttribute(uniqueKey: String): Attribute {
        val file = File(uniqueKey)
        if (!file.exists())
            throw UnknownKeyException("Provided key leads nowhere")
        val auxiliaryFile = File(file, DATA_AUXILIARY_FILE_NAME)
        return Attribute(uniqueKey, auxiliaryFile.readLines()[0])
    }

    override suspend fun getAttributes(book: Book): List<Attribute> {
        val fileBooks = File(baseDir, DIR_BOOKS)
        val fileAttributes = File(fileBooks, DIR_ATTRIBUTES)
        val list = mutableListOf<Attribute>()
        fileAttributes.listFiles()?.let { stream ->
            stream.forEach { list.add(getAttribute(it.absolutePath)) }
        }
        return list
    }

    override suspend fun getAttributes(uniqueKey: String): Set<Attribute> {
        val file = File(uniqueKey)
        if (!file.exists())
            throw UnknownKeyException("Provided key leads nowhere")
        val attributes = File(file, DATA_NOTE_ATTRIBUTES)
        val attrSet = mutableSetOf<Attribute>()
        attributes.readLines().forEach {
            getAttribute(it).let { it1 -> attrSet.add(it1) }
        }
        return attrSet
    }

    override suspend fun getBooks(): List<Book> {
        val file = File(baseDir, DIR_BOOKS)
        val list = mutableListOf<Book>()
        file.listFiles()?.let { arrayOfFiles ->
            arrayOfFiles.asSequence().filter { it.isDirectory }.forEach {
                val book = Book(it.absolutePath, File(it, DATA_AUXILIARY_FILE_NAME).readLines()[0])
                list.add(book)
            }
        }
        return list
    }

    override suspend fun getChildrenKeys(uniqueKey: String): List<String> {
        val file = File(uniqueKey)
        val list = mutableListOf<String>()
        file.listFiles()?.let { arrayOfFiles ->
            arrayOfFiles.asSequence().filter { it.isDirectory }.forEach {
                list.add(it.absolutePath)
            }
        }
        return list
    }

    override suspend fun getChildrenKeys(entry: Book): List<String> {
        val file = File(entry.uniqueKey)
        val list = mutableListOf<String>()
        file.listFiles()?.let { arrayOfFiles ->
            arrayOfFiles.asSequence().filter { it.isDirectory }.forEach {
                list.add(it.absolutePath)
            }
        }
        return list
    }

    override suspend fun getAlternateTitles(uniqueKey: String): List<String> {
        val file = File(uniqueKey)
        if (!file.exists())
            throw UnknownKeyException("Provided key leads nowhere")
        val auxiliaryFile = File(file, DATA_AUXILIARY_FILE_NAME)
        File(file, DATA_MAIN_FILE_NAME)
        File(file, DATA_NOTE_ATTRIBUTES)
        val list = mutableListOf<String>()
        auxiliaryFile.readLines().withIndex()
            .forEach { if (it.index != 1 && it.index != 2) list.add(it.value) }
        return list
    }

    override suspend fun insertAlternateTitles(uniqueKey: String, titles: List<String>): Boolean {
        val file = File(uniqueKey)
        if (!file.exists())
            throw UnknownKeyException("Provided key leads nowhere")
        val auxiliaryFile = File(file, DATA_AUXILIARY_FILE_NAME)
        val listLines = auxiliaryFile.readLines()
        auxiliaryFile.printWriter().use { _ ->
            println(listLines[0])
            println(listLines[1])
            titles.forEach { println(it) }
        }
        return true
    }

    override suspend fun insertAttributes(uniqueKey: String, titles: List<Attribute>): Boolean {
        val file = File(uniqueKey)
        if (!file.exists())
            throw UnknownKeyException("Provided key leads nowhere")
        val attributes = File(file, DATA_NOTE_ATTRIBUTES)
        attributes.printWriter().use { _ ->
            titles.forEach { println(it) }
        }
        return true
    }

    override suspend fun getParentKey(entry: BaseNote): String {
        val file = File(entry.uniqueKey)
        if (!file.exists())
            throw UnknownKeyException("Provided key leads nowhere")
        return File(file.parent!!).absolutePath
    }

    override suspend fun getName(uniqueKey: String): String {
        val file = File(uniqueKey)
        if (!file.exists())
            throw UnknownKeyException("Provided key leads nowhere")
        val auxiliaryFile = File(file, DATA_AUXILIARY_FILE_NAME)
        return auxiliaryFile.readLines()[1]
    }

    override suspend fun deleteEntity(entity: Book): Boolean {
        // just nukes the book, dangerous!
        return File(entity.uniqueKey).deleteRecursively()
    }

    override suspend fun deleteEntity(entity: BaseNote): Boolean {
        return recursiveDelete(entity.uniqueKey)
    }

    private suspend fun recursiveDelete(uniqueKey: String): Boolean {
        val file = File(uniqueKey)
        deleteFromAttributes(uniqueKey)
        file.listFiles()?.let { stream ->
            stream.forEach {
                if (it.isDirectory)
                    recursiveDelete(it.absolutePath)
                else
                    it.delete()
            }
        }
        return file.delete()
    }

    private suspend fun deleteFromAttributes(uniqueKey: String) {
        val attrs = getAttributes(uniqueKey)
        attrs.forEach {
            it.removeEntity(uniqueKey)
            updateAttributeEntry(it)
        }
    }

    override suspend fun deleteEntity(entity: Attribute): Boolean {
        entity.associatedEntities.forEach {
            val fixedAttrs = mutableListOf<Attribute>()
            fixedAttrs.addAll(getAttributes(it))
            fixedAttrs.remove(entity)
            insertAttributes(it, fixedAttrs)
        }
        return File(entity.uniqueKey).deleteRecursively()
    }
}