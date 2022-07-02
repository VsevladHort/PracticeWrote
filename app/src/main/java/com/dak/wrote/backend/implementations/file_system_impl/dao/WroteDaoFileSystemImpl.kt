package com.dak.wrote.backend.implementations.file_system_impl.dao

import android.content.Context
import com.dak.wrote.backend.contracts.dao.WroteDao
import com.dak.wrote.backend.contracts.database.EntryType
import com.dak.wrote.backend.contracts.database.UniqueEntityKeyGenerator
import com.dak.wrote.backend.contracts.entities.*
import com.dak.wrote.backend.contracts.entities.constants.NoteType
import com.dak.wrote.backend.implementations.file_system_impl.*
import com.dak.wrote.backend.implementations.file_system_impl.dao.exceptions.KeyException
import com.dak.wrote.backend.implementations.file_system_impl.dao.exceptions.KeyForWrongEntityException
import com.dak.wrote.backend.implementations.file_system_impl.dao.exceptions.UnknownKeyException
import java.io.File
import java.lang.IllegalStateException

class WroteDaoFileSystemImpl private constructor(private val baseDir: File) : WroteDao {

    companion object {
        @Volatile
        private var instance: WroteDaoFileSystemImpl? = null

        fun getInstance(baseDir: File): WroteDaoFileSystemImpl {
            synchronized(this) {
                var localInstance = instance
                if (localInstance == null) {
                    localInstance = WroteDaoFileSystemImpl(baseDir)
                    instance = localInstance
                }
                return localInstance
            }
        }
    }

    override suspend fun insertBook(book: Book): Boolean {
        val file = File(book.uniqueKey)
        if (!file.exists())
            return false
        val auxiliaryFile = File(file, DATA_AUXILIARY_FILE_NAME)
        val markerFile = File(file, MARKER_OF_USE)
        markerFile.printWriter().use { pw -> pw.println(EntryType.BOOK.stringRepresentation) }
        auxiliaryFile.printWriter().use { pw -> pw.println(book.title) }
        return true
    }

    override suspend fun insertNote(parent: UniqueEntity, note: BaseNote): Boolean {
        val file = File(note.uniqueKey)
        if (!file.exists())
            return false
        var book = parent.uniqueKey
        while (getEntryType(book) != EntryType.BOOK)
            book = getFileParent(book)
        val fileNotesOfBook = File(book, FILE_NOTES_OF_BOOK)
        fileNotesOfBook.appendText(note.uniqueKey + System.lineSeparator())
        val auxiliaryFile = File(file, DATA_AUXILIARY_FILE_NAME)
        val dataFile = File(file, DATA_MAIN_FILE_NAME)
        val attributes = File(file, DATA_NOTE_ATTRIBUTES)
        val markerFile = File(file, MARKER_OF_USE)
        markerFile.printWriter().use { pw ->
            pw.println(EntryType.NOTE.stringRepresentation)
            pw.println(book)
        }
        auxiliaryFile.printWriter().use { pw ->
            pw.println(note.type.type)
            pw.println(note.title)
            note.alternateTitles.forEach { pw.println(it) }
        }
        attributes.printWriter().use { pw ->
            note.attributes.forEach { pw.println(it.uniqueKey) }
        }
        dataFile.writeBytes(note.generateSaveData())
        return true
    }


    override suspend fun <Display : UniqueEntity, Full : UniqueEntity> insertPreset(
        presetManager: PresetManager<Display, Full>,
        display: Display,
        full: Full
    ): Boolean {
        val file = File(display.uniqueKey)
        if (!file.exists() || display.uniqueKey != full.uniqueKey)
            return false
        val auxiliaryFile = File(file, DATA_AUXILIARY_FILE_NAME)
        val dataFile = File(file, DATA_MAIN_FILE_NAME)
        val markerFile = File(file, MARKER_OF_USE)
        markerFile.printWriter().use { pw -> pw.println(EntryType.PRESET.stringRepresentation) }
        auxiliaryFile.writeBytes(presetManager.saveDisplay(display))
        dataFile.writeBytes(presetManager.saveFull(full))
        return true
    }

    override suspend fun <Display : UniqueEntity> updatePresetDisplay(
        presetManager: PresetManager<Display, *>,
        display: Display,
    ): Boolean {
        val file = File(display.uniqueKey)
        if (!file.exists())
            return false
        val auxiliaryFile = File(file, DATA_AUXILIARY_FILE_NAME)
        val markerFile = File(file, MARKER_OF_USE)
        markerFile.printWriter().use { pw -> pw.println(EntryType.PRESET.stringRepresentation) }
        auxiliaryFile.writeBytes(presetManager.saveDisplay(display))
        return true
    }

    override suspend fun <Full : UniqueEntity> updatePresetFull(
        presetManager: PresetManager<*, Full>,
        full: Full,
    ): Boolean {
        val file = File(full.uniqueKey)
        if (!file.exists())
            return false
        val markerFile = File(file, MARKER_OF_USE)
        val dataFile = File(file, DATA_MAIN_FILE_NAME)
        dataFile.writeBytes(presetManager.saveFull(full))
        markerFile.printWriter().use { pw -> pw.println(EntryType.PRESET.stringRepresentation) }
        return true
    }


    override suspend fun deletePreset(uniqueKey: String): Boolean {
        val file = File(uniqueKey)
        checkEntryValidity(file)
        return file.deleteRecursively()
    }

    override suspend fun <Display : UniqueEntity> getPresetDisplay(
        presetManager: PresetManager<Display, *>,
        uniqueKey: String
    ): Display {
        val file = File(uniqueKey)
        checkEntryValidity(file)
        return presetManager.loadDisplay(File(file, DATA_AUXILIARY_FILE_NAME).readBytes())
    }

    override suspend fun <Full : UniqueEntity> getPresetFull(
        presetManager: PresetManager<*, Full>,
        uniqueKey: String
    ): Full {
        val file = File(uniqueKey)
        checkEntryValidity(file)
        return presetManager.loadFull(File(file, DATA_MAIN_FILE_NAME).readBytes())
    }

    override suspend fun insertAttribute(attribute: Attribute): Boolean {
        val file = File(attribute.uniqueKey)
        if (!file.exists())
            return false
        val auxiliaryFile = File(file, DATA_AUXILIARY_FILE_NAME)
        val markerFile = File(file, MARKER_OF_USE)
        markerFile.printWriter().use { it.println(EntryType.ATTRIBUTE.stringRepresentation) }
        auxiliaryFile.printWriter().use { pw ->
            pw.println(attribute.name)
            attribute.associatedEntities.forEach { pw.println(it) }
        }
        return true
    }

    override suspend fun updateAttributeEntry(value: Attribute): Boolean {
        return insertAttribute(value)
    }

    override suspend fun updateAttributeObject(value: Attribute): Boolean {
        val file = File(value.uniqueKey)
        if (!file.exists() || !checkIfInserted(file))
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
        if (!file.exists() || !checkIfInserted(file))
            return false
        val auxiliaryFile = File(file, DATA_AUXILIARY_FILE_NAME)
        val dataFile = File(file, DATA_MAIN_FILE_NAME)
        val attributes = File(file, DATA_NOTE_ATTRIBUTES)
        auxiliaryFile.printWriter().use { pw ->
            pw.println(note.type.type)
            pw.println(note.title)
            note.alternateTitles.forEach { pw.println(it) }
        }
        attributes.printWriter().use { pw ->
            note.attributes.forEach { pw.println(it.uniqueKey) }
        }
        dataFile.writeBytes(note.generateSaveData())
        return true
    }

    override suspend fun updateNoteObject(note: BaseNote): Boolean {
        val file = File(note.uniqueKey)
        if (!file.exists() || !checkIfInserted(file))
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
        if (!file.exists() || !checkIfInserted(file))
            return false
        val auxiliaryFile = File(file, DATA_AUXILIARY_FILE_NAME)
        book.title = auxiliaryFile.readLines()[0]
        return true
    }

    override suspend fun getNoteType(uniqueKey: String): NoteType? {
        val file = File(uniqueKey)
        checkEntryValidity(file)
        val auxiliaryFile = File(file, DATA_AUXILIARY_FILE_NAME)
        File(file, DATA_MAIN_FILE_NAME)
        File(file, DATA_NOTE_ATTRIBUTES)
        // should be expanded to correspond to NoteType properly
        when (auxiliaryFile.readLines()[0]) {
            NoteType.PLAIN_TEXT.type -> return NoteType.PLAIN_TEXT
        }
        return null
    }

    override suspend fun getEntryType(uniqueKey: String): EntryType {
        val file = File(uniqueKey)
        checkEntryValidity(file)
        val markerFile = File(file, MARKER_OF_USE)
        return when (markerFile.readLines()[0]) {
            EntryType.PRESET.stringRepresentation -> EntryType.PRESET
            EntryType.BOOK.stringRepresentation -> EntryType.BOOK
            EntryType.NOTE.stringRepresentation -> EntryType.NOTE
            EntryType.ATTRIBUTE.stringRepresentation -> EntryType.ATTRIBUTE
            else -> throw KeyException("Unknown entry type exception")
        }
    }

    override suspend fun getNoteSaveData(uniqueKey: String): ByteArray? {
        val file = File(uniqueKey)
        checkEntryValidity(file)
        val dataFile = File(file, DATA_MAIN_FILE_NAME)
        return if (dataFile.exists())
            dataFile.readBytes()
        else
            null
    }

    override suspend fun getBookOfNote(uniqueKey: String): String {
        val file = File(uniqueKey)
        checkEntryValidity(file)
        val markerFile = File(file, MARKER_OF_USE)
        val lines = markerFile.readLines()
        if (lines.size < 2)
            throw IllegalStateException("Malformed note entry")
        return markerFile.readLines()[1]
    }

    override suspend fun getBook(uniqueKey: String): Book {
        val file = File(uniqueKey)
        checkEntryValidity(file)
        if (getEntryType(uniqueKey) != EntryType.BOOK)
            throw KeyForWrongEntityException("Expected a book key, got something else")
        val auxiliaryFile = File(file, DATA_AUXILIARY_FILE_NAME)
        return Book(uniqueKey, auxiliaryFile.readLines()[0])
    }

    override suspend fun getAttribute(uniqueKey: String): Attribute {
        val file = File(uniqueKey)
        checkEntryValidity(file)
        val auxiliaryFile = File(file, DATA_AUXILIARY_FILE_NAME)
        return Attribute(uniqueKey, auxiliaryFile.readLines()[0])
    }

    override suspend fun getAttributes(book: Book): List<Attribute> {
        val fileBooks = File(baseDir, DIR_BOOKS)
        val fileAttributes = File(fileBooks, DIR_ATTRIBUTES)
        val list = mutableListOf<Attribute>()
        fileAttributes.listFiles()?.let { stream ->
            stream.asSequence().filter { checkIfInserted(it) }
                .forEach { list.add(getAttribute(it.absolutePath)) }
        }
        return list
    }

    override suspend fun getAttributes(uniqueKey: String): Set<Attribute> {
        val file = File(uniqueKey)
        checkEntryValidity(file)
        return when (getEntryType(uniqueKey)) {
            EntryType.NOTE -> {
                val attributes = File(file, DATA_NOTE_ATTRIBUTES)
                if (attributes.exists()) {
                    val attrSet = mutableSetOf<Attribute>()
                    attributes.readLines().forEach {
                        getAttribute(it).let { it1 -> attrSet.add(it1) }
                    }
                    attrSet
                } else
                    emptySet()
            }
            EntryType.BOOK -> {
                val fileAttributes = File(file, DIR_ATTRIBUTES)
                val attrSet = mutableSetOf<Attribute>()
                fileAttributes.listFiles()?.let { stream ->
                    stream.asSequence().filter { checkIfInserted(it) }
                        .forEach { attrSet.add(getAttribute(it.absolutePath)) }
                }
                attrSet
            }
            else -> throw KeyForWrongEntityException("Only books or notes can contain attributes")
        }
    }

    override suspend fun getNoteKeys(book: Book): Set<String> {
        val file = File(book.uniqueKey)
        checkEntryValidity(file)
        val result = mutableSetOf<String>()
        File(file, FILE_NOTES_OF_BOOK).readLines().forEach {
            result.add(it)
        }
        return result
    }

    override suspend fun getNoteKeysWithoutAttributes(book: Book): Set<String> {
        val file = File(book.uniqueKey)
        checkEntryValidity(file)
        val result = mutableSetOf<String>()
        File(file, FILE_NOTES_OF_BOOK).readLines()
            .filter {
                checkIfInserted(File(it)) && File(it, DATA_NOTE_ATTRIBUTES).readLines().isEmpty()
            }.forEach {
                result.add(it)
            }
        return result
    }

    override suspend fun getPresets(): List<String> {
        val file = File(baseDir, DIR_PRESETS)
        val list = file.listFiles() ?: return listOf()
        return list.asSequence().filter { it.isDirectory && checkIfInserted(it) }
            .map { it.absolutePath }.toList()
    }

    override suspend fun getBooks(): List<Book> {
        val file = File(baseDir, DIR_BOOKS)
        val list = mutableListOf<Book>()
        file.listFiles()?.let { arrayOfFiles ->
            arrayOfFiles.asSequence().filter { it.isDirectory && checkIfInserted(it) }.forEach {
                val book = Book(it.absolutePath, File(it, DATA_AUXILIARY_FILE_NAME).readLines()[0])
                list.add(book)
            }
        }
        return list
    }

    override suspend fun getChildrenKeys(uniqueKey: String): List<String> {
        val file = File(uniqueKey)
        checkEntryValidity(file)
        val list = mutableListOf<String>()
        file.listFiles()?.let { arrayOfFiles ->
            arrayOfFiles.asSequence().filter { it.isDirectory && checkIfInserted(it) }.forEach {
                list.add(it.absolutePath)
            }
        }
        return list
    }

    override suspend fun getChildrenKeys(entry: Book): List<String> {
        val file = File(entry.uniqueKey)
        checkEntryValidity(file)
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
        checkEntryValidity(file)
        val auxiliaryFile = File(file, DATA_AUXILIARY_FILE_NAME)
        File(file, DATA_MAIN_FILE_NAME)
        File(file, DATA_NOTE_ATTRIBUTES)
        val list = mutableListOf<String>()
        auxiliaryFile.readLines().withIndex()
            .forEach { if (it.index != 0 && it.index != 1) list.add(it.value) }
        return list
    }

    override suspend fun insertAlternateTitles(uniqueKey: String, titles: List<String>): Boolean {
        val file = File(uniqueKey)
        checkEntryValidity(file)
        val auxiliaryFile = File(file, DATA_AUXILIARY_FILE_NAME)
        val listLines = auxiliaryFile.readLines()
        auxiliaryFile.printWriter().use { pw ->
            pw.println(listLines[0])
            pw.println(listLines[1])
            titles.forEach { pw.println(it) }
        }
        return true
    }

    override suspend fun insertAttributes(uniqueKey: String, titles: Set<Attribute>): Boolean {
        val file = File(uniqueKey)
        checkEntryValidity(file)
        val attributes = File(file, DATA_NOTE_ATTRIBUTES)
        attributes.printWriter().use { pw ->
            titles.forEach { pw.println(it) }
        }
        return true
    }

    override suspend fun getParentKey(uniqueKey: String): String {
        checkEntryValidity(File(uniqueKey))
        if (getEntryType(uniqueKey) != EntryType.NOTE)
            throw KeyForWrongEntityException("Expected key for Note, got something else")
        return getFileParent(uniqueKey)
    }

    private fun getFileParent(fileName: String): String {
        val file = File(fileName)
        return File(file.parent!!).absolutePath
    }

    override suspend fun getName(uniqueKey: String): String {
        val file = File(uniqueKey)
        checkEntryValidity(file)
        val auxiliaryFile = File(file, DATA_AUXILIARY_FILE_NAME)
        return auxiliaryFile.readLines()[1]
    }

    override suspend fun deleteEntityBook(entity: String): Boolean {
        if (getEntryType(entity) != EntryType.BOOK)
            return false
        return File(entity).deleteRecursively()
    }

    override suspend fun deleteEntityNote(entity: String): Boolean {
        if (getEntryType(entity) != EntryType.NOTE)
            return false
        val listOfKeysDeleted = mutableSetOf<String>()
        var book = getFileParent(entity)
        val result = recursiveDelete(entity, listOfKeysDeleted)
        while (getEntryType(book) != EntryType.BOOK)
            book = getFileParent(book)
        val fileListOfNotes = File(book, FILE_NOTES_OF_BOOK)
        val newSetOfNotes =
            fileListOfNotes.readLines().asSequence().filter { !listOfKeysDeleted.contains(it) }
                .toList()
        fileListOfNotes.printWriter().use { pw -> newSetOfNotes.forEach { pw.println(it) } }
        return result
    }

    private suspend fun recursiveDelete(
        uniqueKey: String,
        listOfKeysDeleted: MutableSet<String>
    ): Boolean {
        val file = File(uniqueKey)
        if (checkIfInserted(file))
            listOfKeysDeleted.add(uniqueKey)
        deleteFromAttributes(uniqueKey)
        file.listFiles()?.let { stream ->
            stream.forEach {
                if (it.isDirectory)
                    recursiveDelete(it.absolutePath, listOfKeysDeleted)
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

    override suspend fun deleteEntityAttribute(entity: Attribute): Boolean {
        entity.associatedEntities.forEach {
            val fixedAttrs = mutableSetOf<Attribute>()
            fixedAttrs.addAll(getAttributes(it))
            fixedAttrs.remove(entity)
            insertAttributes(it, fixedAttrs)
        }
        return File(entity.uniqueKey).deleteRecursively()
    }

    override suspend fun getOrCreateAttribute(
        book: Book,
        keyGenerator: UniqueEntityKeyGenerator,
        name: String
    ): Attribute {
        return getAttributes(book.uniqueKey).find { it.name.compareTo(name, true) == 0 }
            ?: kotlin.run {
                Attribute(
                    keyGenerator.getKey(
                        book,
                        EntryType.ATTRIBUTE
                    ), name
                )
            }
    }

    private fun checkEntryValidity(file: File) {
        if (!file.exists())
            throw UnknownKeyException("Provided key leads nowhere")
        if (!checkIfInserted(file))
            throw KeyException("Entry has not been properly inserted")
    }

    private fun checkIfInserted(file: File): Boolean {
        return File(file, MARKER_OF_USE).exists()
    }
}


fun getDAO(context: Context) = WroteDaoFileSystemImpl.getInstance(context.filesDir)