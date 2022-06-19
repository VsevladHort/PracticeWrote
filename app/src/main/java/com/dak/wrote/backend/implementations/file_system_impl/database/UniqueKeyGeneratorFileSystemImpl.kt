package com.dak.wrote.backend.implementations.file_system_impl.database

import com.dak.wrote.backend.contracts.database.EntryType
import com.dak.wrote.backend.contracts.database.UniqueEntityKeyGenerator
import com.dak.wrote.backend.contracts.entities.Book
import com.dak.wrote.backend.contracts.entities.UniqueEntity
import com.dak.wrote.backend.implementations.file_system_impl.*

import java.io.File

private var currentUniqueKey = UNIQUE_ID_INITIAL_VALUE

class UniqueKeyGeneratorFileSystemImpl private constructor(private val baseDir: File) :
    UniqueEntityKeyGenerator {

    private val currentFile: File = File(baseDir, UNIQUE_KEY_CACHE_STORAGE_FILE)

    init {
        if (currentFile.exists()) {
            val firstLine = currentFile.readLines()[0]
            if (firstLine.isNotBlank() && firstLine.isNotBlank())
                currentUniqueKey = firstLine.toInt()
        }
    }

    companion object {
        @Volatile
        private var instance: UniqueEntityKeyGenerator? = null

        fun getInstance(baseDir: File): UniqueEntityKeyGenerator {
            synchronized(this) {
                var localInstance = instance
                if (localInstance == null) {
                    localInstance = UniqueKeyGeneratorFileSystemImpl(baseDir)
                    instance = localInstance
                }
                return localInstance
            }
        }
    }

    override suspend fun getKey(parent: UniqueEntity?, type: EntryType): String {
        return run {
            when (type) {
                EntryType.NOTE -> {
                    if (parent == null)
                        throw IllegalArgumentException("Note has to have a parent")
                    val path = File(parent.uniqueKey, currentUniqueKey.toString())
                    if (!path.exists())
                        path.mkdirs()
                    currentUniqueKey++
                    currentFile.writeText(currentUniqueKey.toString())
                    path.absolutePath
                }
                EntryType.BOOK -> {
                    val path = File(File(baseDir, DIR_BOOKS), currentUniqueKey.toString())
                    if (!path.exists())
                        path.mkdirs()
                    currentUniqueKey++
                    currentFile.writeText(currentUniqueKey.toString())
                    path.absolutePath
                }
                EntryType.ATTRIBUTE -> {
                    if (parent == null || parent !is Book)
                        throw IllegalArgumentException("Attribute has to have a book parent")
                    val path =
                        File(File(parent.uniqueKey, DIR_ATTRIBUTES), currentUniqueKey.toString())
                    if (!path.exists())
                        path.mkdirs()
                    currentUniqueKey++
                    currentFile.writeText(currentUniqueKey.toString())
                    path.absolutePath
                }
                EntryType.PRESET -> {
                    val path = File(File(baseDir, DIR_PRESETS), currentUniqueKey.toString())
                    if (!path.exists())
                        path.mkdirs()
                    currentUniqueKey++
                    currentFile.writeText(currentUniqueKey.toString())
                    path.absolutePath
                }
            }
        }
    }
}