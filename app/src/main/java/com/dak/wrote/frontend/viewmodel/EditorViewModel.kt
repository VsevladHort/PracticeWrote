package com.dak.wrote.frontend.viewmodel

import android.app.Application
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.*
import com.dak.wrote.backend.contracts.database.EntryType
import com.dak.wrote.backend.contracts.entities.Attribute
import com.dak.wrote.backend.contracts.entities.BaseNote
import com.dak.wrote.backend.contracts.entities.Book
import com.dak.wrote.backend.contracts.entities.constants.NoteType
import com.dak.wrote.backend.implementations.file_system_impl.dao.getDAO
import com.dak.wrote.backend.implementations.file_system_impl.database.getKeyGen
import com.dak.wrote.frontend.editor.PageLayout
import com.dak.wrote.frontend.editor.SerializablePageLayout
import com.dak.wrote.frontend.editor.mutStateListOf
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import java.io.ByteArrayInputStream

class ChangedValue<T> {
    var changed: Boolean = true
        private set
    private var _field: T? = null

    @Suppress("UNCHECKED_CAST")
    var field: T
        get() = _field as T
        set(value) {
            changed = true
            _field = value
        }
}


class UpdateHolder<T>(old: T) {
    var old: T = old
        private set

    constructor(old: T, next: T) : this(old) {
        this.next.value = next
    }

    var next = mutableStateOf(old)
    val updated
        get() = old == next.value

    fun refresh() {
        old = next.value
    }
}


@OptIn(ExperimentalSerializationApi::class)
class EditorViewModel(application: Application) : AndroidViewModel(application) {
    data class ObjectNote(
        val currentId: String,
        val name: MutableState<String>,
        override var alternateTitles: Set<String>,
        override var attributes: Set<Attribute>,
        var sPage: SerializablePageLayout,
        val dAlternateNames: SnapshotStateList<UpdateHolder<String?>> = mutStateListOf(
            alternateTitles.toList()
        ) { UpdateHolder(it) },
        val dAttributes: SnapshotStateList<UpdateHolder<String?>> = mutStateListOf(
            attributes.toList()
        )
        { UpdateHolder(it.name) },
        var page: MutableState<PageLayout> = mutableStateOf(sPage.toDisplayable()),
        var inEdit: MutableState<Boolean> = mutableStateOf(false),
        var processing: MutableState<Boolean> = mutableStateOf(false)
    ) : BaseNote {
        override var title: String
            get() = name.value
            set(value) {
                name.value = value
            }
        override val type: NoteType
            get() = NoteType.PLAIN_TEXT

        override fun generateSaveData(): ByteArray {
            return Json.encodeToString(sPage).encodeToByteArray()
        }

        override fun loadSaveData(value: ByteArray) {
            page.value = Json.decodeFromStream(ByteArrayInputStream(value))
        }

        override fun getIndexingData(): String {
            return ""
        }

        override val uniqueKey: String
            get() = TODO("Not yet implemented")

    }

    private val _currentId: MutableStateFlow<String?> = MutableStateFlow(null)
    val currentId = _currentId.distinctUntilChanged { old, new -> old == new }

    private val rep = getDAO(application)
    private val keyGen = getKeyGen(application)

    var note: MutableStateFlow<ObjectNote?> = MutableStateFlow(null)


    init {
        viewModelScope.launch {

            currentId.filterNotNull().collect { id ->
                val page =
                    Json.decodeFromStream<SerializablePageLayout>(
                        ByteArrayInputStream(
                            rep.getNoteSaveData(
                                id
                            )
                        )
                    )
                val attributes = rep.getAttributes(id)
                val alternateNames = rep.getAlternateTitles(id).toSet()
                note.value =
                    ObjectNote(
                        id,
                        mutableStateOf(rep.getName(id)),
                        alternateNames,
                        attributes,
                        page,
                    )
            }
        }
    }

//    fun onSave(note : ObjectNote) {
//       note.sPage = note.page.value.toSerializable()
//       note.page.value = note.sPage.toDisplayable()
//    }

    fun savePreset(note: ObjectNote) {
        viewModelScope.launch {
            note.processing.value = true
            val key = keyGen.getKey(null, EntryType.PRESET)
            rep.insetPreset(note)
            note.processing.value = false
        }
    }

    fun updatePage(
        note: ObjectNote,
    ) {
        note.processing.value = true
        viewModelScope.launch {
            val parsedAttributes = note.dAttributes.filter {
                it.old != null && !it.next.value.isNullOrBlank()
            }
            val addedAttributes =
                parsedAttributes.filter { it.updated && !it.next.value.isNullOrBlank() }
                    .map { it.next.value!! }.toSet()
            val removedAttributes =
                parsedAttributes.filter { it.updated && it.old != null }.map { it.old!! }.toSet()
            val unchangedAttributes =
                parsedAttributes.filter { !it.updated && !it.next.value.isNullOrBlank() }
                    .map { it.next.value!! }.toSet()

            val updatedAttributes = note.attributes.toMutableList()
            removedAttributes.forEach { attributeName ->
                val attribute = note.attributes.find { it.name == attributeName }!!
                attribute.removeEntity(note.currentId)
                rep.updateAttributeEntry(attribute)
                updatedAttributes.remove(attribute)
            }
            val bookId = rep.getBookOfNote(note.currentId)
            val book: Book = rep.getBooks().find { bookId == it.uniqueKey }!!
            val allAttributes = rep.getAttributes(book)
            addedAttributes.forEach { added ->
                val found = allAttributes.find { it.name == added }
                if (found != null) {
                    found.addEntity(note.currentId)
                    rep.insetAttribute(found)
                } else {
                    val key = keyGen.getKey(null, EntryType.ATTRIBUTE)
                    val attribute = Attribute(key, added)
                    rep.insetAttribute(attribute)
                    updatedAttributes.add(attribute)
                }
            }


            val alternateNames = note.dAlternateNames.filter { !it.next.value.isNullOrBlank() }
                .map { it.next.value!! }
            rep.insertAlternateTitles(note.currentId, alternateNames)


            note.attributes = updatedAttributes.toSet()
            note.alternateTitles = alternateNames.toSet()
            note.dAttributes.clear()
            note.dAttributes.addAll(note.attributes.map { UpdateHolder(it.name) })
            note.dAlternateNames.clear()
            note.dAlternateNames.addAll(note.alternateTitles.map { UpdateHolder(it) })

            note.sPage = note.page.value.toSerializable()
            note.page.value = note.sPage.toDisplayable()

            rep.updateNoteEntry(note)

            note.processing.value = false
        }
    }


}