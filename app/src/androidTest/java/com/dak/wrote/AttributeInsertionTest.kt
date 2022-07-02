package com.dak.wrote

import androidx.test.platform.app.InstrumentationRegistry
import com.dak.wrote.backend.contracts.database.EntryType
import com.dak.wrote.backend.contracts.database.UniqueEntityKeyGenerator
import com.dak.wrote.backend.contracts.entities.Attribute
import com.dak.wrote.backend.contracts.entities.Book
import com.dak.wrote.backend.implementations.file_system_impl.dao.WroteDaoFileSystemImpl
import com.dak.wrote.backend.implementations.file_system_impl.database.UniqueKeyGeneratorFileSystemImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.runBlocking
import org.junit.AfterClass
import org.junit.Assert
import org.junit.Test
import java.io.File
import java.util.logging.Level
import java.util.logging.Logger

class AttributeInsertionTest {
    @Test
    fun attributeSetTest() {
        // Context of the app under test.
        logger.log(Level.INFO, "attributeSetTestRuns")
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val baseDir = File(appContext.filesDir, "TEST")
        val generator: UniqueEntityKeyGenerator =
            UniqueKeyGeneratorFileSystemImpl.getInstance(baseDir)
        baseDir.deleteRecursively()
        val book1 = runBlocking { generator.getKey(null, EntryType.BOOK) }
        val book2 = runBlocking { generator.getKey(null, EntryType.BOOK) }
        val book3 = runBlocking { generator.getKey(null, EntryType.BOOK) }
        val book1Object = Book(book1, "BOOK1")
        val book2Object = Book(book2, "BOOK2")
        val book3Object = Book(book3, "BOOK3")
        val attr1_1 =
            runBlocking { Attribute(generator.getKey(book1Object, EntryType.ATTRIBUTE), "1_1") }
        val attr1_2 =
            runBlocking { Attribute(generator.getKey(book1Object, EntryType.ATTRIBUTE), "1_2") }
        val attr1_3 =
            runBlocking { Attribute(generator.getKey(book1Object, EntryType.ATTRIBUTE), "1_3") }
        val attr2_1 =
            runBlocking { Attribute(generator.getKey(book2Object, EntryType.ATTRIBUTE), "2_1") }
        val attr2_2 =
            runBlocking { Attribute(generator.getKey(book2Object, EntryType.ATTRIBUTE), "2_2") }
        val attr2_3 =
            runBlocking { Attribute(generator.getKey(book2Object, EntryType.ATTRIBUTE), "2_3") }
        val dao = WroteDaoFileSystemImpl.getInstance(baseDir)
        runBlocking {
            dao.insertBook(book1Object)
            dao.insertBook(book2Object)
            dao.insertBook(book3Object)
        }
        var listOfAttrs1 = runBlocking { dao.getAttributes(book1Object.uniqueKey) }
        var listOfAttrs2 = runBlocking { dao.getAttributes(book2Object.uniqueKey) }
        var listOfAttrs3 = runBlocking { dao.getAttributes(book3Object.uniqueKey) }
        Assert.assertTrue(listOfAttrs1.isEmpty())
        Assert.assertTrue(listOfAttrs2.isEmpty())
        Assert.assertTrue(listOfAttrs3.isEmpty())
        runBlocking {
            dao.insetAttribute(attr1_1)
            dao.insetAttribute(attr1_2)
            dao.insetAttribute(attr1_3)
            dao.insetAttribute(attr2_1)
            dao.insetAttribute(attr2_2)
        }
        listOfAttrs1 = runBlocking { dao.getAttributes(book1Object.uniqueKey) }
        listOfAttrs2 = runBlocking { dao.getAttributes(book2Object.uniqueKey) }
        listOfAttrs3 = runBlocking { dao.getAttributes(book3Object.uniqueKey) }
        Assert.assertTrue(listOfAttrs1.size == 3)
        Assert.assertTrue(listOfAttrs2.size == 2)
        Assert.assertTrue(listOfAttrs3.isEmpty())
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