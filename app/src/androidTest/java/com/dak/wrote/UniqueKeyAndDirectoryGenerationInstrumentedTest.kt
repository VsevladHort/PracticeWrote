package com.dak.wrote

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.dak.wrote.backend.contracts.database.EntryType
import com.dak.wrote.backend.contracts.database.UniqueEntityKeyGenerator
import com.dak.wrote.backend.contracts.entities.Attribute
import com.dak.wrote.backend.contracts.entities.Book
import com.dak.wrote.backend.contracts.entities.UniqueEntity
import com.dak.wrote.backend.implementations.file_system_impl.DIR_ATTRIBUTES
import com.dak.wrote.backend.implementations.file_system_impl.DIR_BOOKS
import com.dak.wrote.backend.implementations.file_system_impl.DIR_PRESETS
import com.dak.wrote.backend.implementations.file_system_impl.dao.WroteDaoFileSystemImpl
import com.dak.wrote.backend.implementations.file_system_impl.database.UniqueKeyGeneratorFileSystemImpl
import kotlinx.coroutines.*
import org.junit.AfterClass

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import java.io.File
import java.util.logging.Level
import java.util.logging.Logger

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class UniqueKeyAndDirectoryGenerationInstrumentedTest {

    class UniqueEntitySpoof(override val uniqueKey: String) : UniqueEntity

    @Test
    fun fileAndKeyGenerationTest() {
        // Context of the app under test.
        logger.log(Level.INFO, "fileGenerationTestRuns")
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val baseDir = File(appContext.filesDir, "TEST")
        val booksDir0 = File(File(baseDir, DIR_BOOKS), "0")
        val booksDirChild = File(booksDir0, "3")
        val booksDirChildChild = File(booksDirChild, "4")
        val attributeDir1 = File(File(booksDir0, DIR_ATTRIBUTES), "1")
        val presetDir2 = File(File(baseDir, DIR_PRESETS), "2")
        val presetChild = File(presetDir2, "5")
        baseDir.deleteRecursively()
        assertTrue(baseDir.mkdir())
        assertFalse(booksDir0.exists())
        assertFalse(attributeDir1.exists())
        assertFalse(presetDir2.exists())
        val generator: UniqueEntityKeyGenerator =
            UniqueKeyGeneratorFileSystemImpl.getInstance(baseDir)
        var book: Book?
        var presets: UniqueEntitySpoof?
        var firstChild: UniqueEntitySpoof?
        var firstChildChild: UniqueEntitySpoof?
        var presetNote: UniqueEntitySpoof?
        var attributes: UniqueEntitySpoof?
        runBlocking {
            logger.log(Level.INFO, "Coroutine Launched")
            book = Book(generator.getKey(null, EntryType.BOOK), "This is a book")
            attributes = UniqueEntitySpoof(generator.getKey(book, EntryType.ATTRIBUTE))
            presets = UniqueEntitySpoof(generator.getKey(null, EntryType.PRESET))
            firstChild = UniqueEntitySpoof(generator.getKey(book, EntryType.NOTE))
            firstChildChild = UniqueEntitySpoof(generator.getKey(firstChild, EntryType.NOTE))
            presetNote = UniqueEntitySpoof(generator.getKey(presets, EntryType.NOTE))
        }
        assertTrue(attributeDir1.exists())
        assertTrue(presetDir2.exists())
        assertTrue(booksDirChild.exists())
        assertTrue(booksDirChildChild.exists())
        assertTrue(presetChild.exists())
        assertEquals(booksDir0.absolutePath, book?.uniqueKey)
        assertEquals(attributeDir1.absolutePath, attributes?.uniqueKey)
        assertEquals(presetDir2.absolutePath, presets?.uniqueKey)
        assertEquals(booksDirChild.absolutePath, firstChild?.uniqueKey)
        assertEquals(booksDirChildChild.absolutePath, firstChildChild?.uniqueKey)
        assertEquals(presetChild.absolutePath, presetNote?.uniqueKey)
        assertEquals("com.dak.wrote", appContext.packageName)
    }

    companion object {
        private var testJob = Job()
        private val logger =
            Logger.getLogger(UniqueKeyAndDirectoryGenerationInstrumentedTest::class.java.canonicalName)
        private val testScope = CoroutineScope(Dispatchers.Main + testJob)

        @AfterClass
        fun after() {
            testJob.cancel()
        }
    }
}