package com.dak.wrote.backend.implementations.dao

import com.dak.wrote.backend.abstractions.dao.WroteDao
import com.dak.wrote.backend.abstractions.entities.BaseNote
import com.dak.wrote.backend.abstractions.entities.Book
import com.dak.wrote.backend.abstractions.entities.PlainTextNote
import com.dak.wrote.backend.abstractions.entities.UniqueEntity
import com.dak.wrote.backend.abstractions.entities.constants.NoteType
import com.dak.wrote.backend.implementations.dao.exceptions.UnknownImplementationException
import com.dak.wrote.backend.implementations.entities.BookImpl
import com.dak.wrote.backend.implementations.entities.PlainTextNoteImpl
import com.dak.wrote.backend.implementations.file_system_impl_constants.DIR_BOOKS
import com.dak.wrote.backend.implementations.file_system_impl_constants.SERVICE_INFO_STORAGE_FILE
import com.dak.wrote.backend.implementations.file_system_impl_constants.UNIQUE_ID_INITIAL_VALUE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

private var currentFreeUniqueId = UNIQUE_ID_INITIAL_VALUE

class WroteDaoFileSystemImpl(private val baseDir: File) : WroteDao {

    /*
    * Context.baseDir/BOOKS/0/1/4
    * Context.baseDir/BOOKS/0/2/5
    * Context.baseDir/BOOKS/0/3/7
    *
    *
    *
    * */
    init {
        val file = File(baseDir, SERVICE_INFO_STORAGE_FILE)
        if (file.exists()) {
            currentFreeUniqueId = file.readLines()[0].toInt()
        } else
            file.printWriter().use { it.println(UNIQUE_ID_INITIAL_VALUE.toString()) }
    }

    override suspend fun createBook(title: String): Book {
        return withContext(Dispatchers.IO) {
            val dir = File(baseDir, DIR_BOOKS)
            if (!dir.exists())
                dir.mkdir()
            val bookDir = File(dir, currentFreeUniqueId.toString())
            if (!bookDir.exists())
                bookDir.mkdir()
            File(bookDir, SERVICE_INFO_STORAGE_FILE).printWriter()
                .use { println(title) }
            val children = getDirectoryChildrenOfFile(bookDir)
            if (children != null)
                BookImpl(title, children, bookDir.absolutePath)
            else
                BookImpl(title, listOf(), bookDir.absolutePath)
        }
    }

    override suspend fun createNote(
        parent: UniqueEntity,
        title: String,
        type: NoteType
    ): BaseNote {
        return withContext(Dispatchers.IO) {
            when (type) {
                NoteType.PLAIN_TEXT -> {
                    val file = File(parent.uniqueKey, currentFreeUniqueId.toString())
                    if (!file.exists())
                        file.mkdir()
                    File(file, SERVICE_INFO_STORAGE_FILE).printWriter()
                        .use { println(type.type); println(title) }
                    val children = getDirectoryChildrenOfFile(file)
                    if (children != null)
                        PlainTextNoteImpl("", title, type, children, file.absolutePath)
                    else
                        PlainTextNoteImpl("", title, type, listOf(), file.absolutePath)
                }
            }
        }
    }

    override suspend fun updateNoteEntry(note: BaseNote): Boolean {
        return withContext(Dispatchers.IO) {
            when (note.type) {
                NoteType.PLAIN_TEXT -> {
                    File(
                        note.uniqueKey,
                        NoteType.PLAIN_TEXT.dataFileName
                    ).writeBytes(note.getSaveData())
                    true
                }
            }
        }
    }

    override suspend fun updateNoteObject(note: BaseNote): Boolean {
        return withContext(Dispatchers.IO) {
            val file = File(note.uniqueKey)
            if (!file.exists())
                return@withContext false
            when (note.type) {
                NoteType.PLAIN_TEXT -> {
                    readPlainTextToNote(note, file)
                }
            }
            note.title = File(file, SERVICE_INFO_STORAGE_FILE).readLines()[1]
            val children = getDirectoryChildrenOfFile(file)
            if (children != null)
                note.children = children
            else
                note.children = listOf()
            true
        }
    }

    private fun readPlainTextToNote(note: BaseNote, file: File) {
        if (note is PlainTextNote) {
            note.plainText = File(file, NoteType.PLAIN_TEXT.dataFileName).readText()
        } else
            throw
            UnknownImplementationException(
                "Expected ${note.type.type}" +
                        ", got unexpected  type"
            )
    }

    override suspend fun updateBookEntry(book: Book): Boolean {
        return withContext(Dispatchers.IO) {
            val dir = File(book.uniqueKey)
            if (!dir.exists())
                dir.mkdir()
            val bookDir = File(dir, currentFreeUniqueId.toString())
            if (!bookDir.exists())
                bookDir.mkdir()
            File(bookDir, SERVICE_INFO_STORAGE_FILE).printWriter()
                .use { println(book.title) }
            val children = getDirectoryChildrenOfFile(bookDir)
            if (children != null)
                true
            else
                false
        }
    }

    override suspend fun updateBookObject(book: Book): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun getNote(uniqueKey: String): BaseNote? {
        TODO("Not yet implemented")
    }

    private fun getDirectoryChildrenOfFile(file: File): List<String>? {
        return file.listFiles()?.asSequence()?.filter { it.isDirectory }
            ?.map { it.absolutePath }?.toList()
    }
}