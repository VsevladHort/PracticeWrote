package com.dak.wrote.frontend.viewmodel

import android.app.Application
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toLowerCase
import androidx.lifecycle.*
import com.dak.wrote.backend.contracts.database.EntryType
import com.dak.wrote.backend.contracts.entities.Attribute
import com.dak.wrote.backend.contracts.entities.BaseNote
import com.dak.wrote.backend.contracts.entities.Book
import com.dak.wrote.backend.contracts.entities.PresetManager
import com.dak.wrote.backend.contracts.entities.constants.NoteType
import com.dak.wrote.backend.implementations.file_system_impl.dao.getDAO
import com.dak.wrote.backend.implementations.file_system_impl.database.getKeyGen
import com.dak.wrote.frontend.editor.PageLayout
import com.dak.wrote.frontend.editor.SerializablePageLayout
import com.dak.wrote.frontend.editor.mutStateListOf
import com.dak.wrote.frontend.preset.SerializableDisplayUserPreset
import com.dak.wrote.frontend.preset.SerializableFullUserPreset
import com.dak.wrote.frontend.preset.UserPresetSaver
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
        get() = old != next.value

    fun refresh() {
        old = next.value
    }
}


@OptIn(ExperimentalSerializationApi::class)
class EditorViewModel(val currentId: String, val presetUpdate : MutableSharedFlow<Unit>, application: Application) :
    AndroidViewModel(application) {
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
            get() = currentId

    }


    private val rep = getDAO(application)
    private val keyGen = getKeyGen(application)

    var note: MutableStateFlow<ObjectNote?> = MutableStateFlow(null)


    init {
        viewModelScope.launch {
            val id = currentId
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


    fun savePreset(note: ObjectNote) {
        viewModelScope.launch {
            note.processing.value = true
            val key = keyGen.getKey(null, EntryType.PRESET)
            rep.insertPreset(
                UserPresetSaver(),
                SerializableDisplayUserPreset(
                    note.name.value,
                    note.alternateTitles,
                    note.attributes.map { it.name }.toSet(),
                    key
                ),
                SerializableFullUserPreset(note.sPage, key)
            )
            note.processing.value = false
            presetUpdate.emit(Unit)
        }
    }

    fun updatePage(
        note: ObjectNote,
    ) {
        note.processing.value = true
        viewModelScope.launch {
            val parsedAttributes = note.dAttributes.filter {
                it.old != null || !it.next.value.isNullOrBlank()
            }
            for (it in parsedAttributes) {
                it.next.value = it.next.value?.toLowerCase(Locale.current)
            }
            val addedAttributes =
                parsedAttributes.filter { it.updated && !it.next.value.isNullOrBlank() }
                    .map { it.next.value!! }.toSet()
            val removedAttributes =
                parsedAttributes.filter { it.updated && it.old != null }.map { it.old!! }.toSet()
            val unchangedAttributes =
                parsedAttributes.filter { !it.updated && !it.next.value.isNullOrBlank() }
                    .map { it.next.value!! }.toSet()

            println(addedAttributes)

            val updatedAttributes = note.attributes.toMutableList()
            removedAttributes.forEach { attributeName ->
                val attribute = note.attributes.find { it.name == attributeName }!!
                rep.updateAttributeObject(attribute)
                attribute.removeEntity(note.currentId)
                rep.updateAttributeEntry(attribute)
                updatedAttributes.remove(attribute)
            }
            val bookId = rep.getBookOfNote(note.currentId)
            val book: Book = rep.getBooks().find { bookId == it.uniqueKey }!!
            addedAttributes.forEach { added ->
                val attribute = rep.getOrCreateAttribute(book, keyGen, added)
                rep.updateAttributeObject(attribute)
                attribute.addEntity(note.currentId)
                println("inserting $attribute")
                rep.insertAttribute(attribute)
                updatedAttributes.add(attribute)
            }


//            rep.insertAttributes(bookId, allAttributes)
            val alternateNames = note.dAlternateNames.filter { !it.next.value.isNullOrBlank() }
                .map { it.next.value!! }
            rep.insertAlternateTitles(note.currentId, alternateNames)


            note.attributes = updatedAttributes.toSet()
            note.alternateTitles = alternateNames.toSet()
            note.dAttributes.clear()
            note.dAttributes.addAll(note.attributes.toSortedSet { f, s -> f.name.compareTo(s.name) }
                .map { UpdateHolder(it.name) })
            note.dAlternateNames.clear()
            note.dAlternateNames.addAll(note.alternateTitles.map { UpdateHolder(it) })

            note.sPage = note.page.value.toSerializable()
            note.page.value = note.sPage.toDisplayable()

            rep.updateNoteEntry(note)


            note.processing.value = false
        }
    }
}

class EditorViewModelFactory(
    private val selectedNote: String,
    private val presetUpdate: MutableSharedFlow<Unit>,
    private val application: Application
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return EditorViewModel(selectedNote, presetUpdate, application) as T
    }
}
