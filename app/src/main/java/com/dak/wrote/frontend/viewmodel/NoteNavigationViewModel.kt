package com.dak.wrote.frontend.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.dak.wrote.backend.contracts.database.EntryType
import com.dak.wrote.backend.contracts.entities.Attribute
import com.dak.wrote.backend.contracts.entities.BaseNote
import com.dak.wrote.backend.contracts.entities.constants.NoteType
import com.dak.wrote.backend.implementations.file_system_impl.dao.WroteDaoFileSystemImpl
import com.dak.wrote.backend.implementations.file_system_impl.dao.getDAO
import com.dak.wrote.backend.implementations.file_system_impl.database.getKeyGen
import com.dak.wrote.frontend.noteNavigation.NavigationNote
import com.dak.wrote.frontend.preset.NoteCreation
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToStream
import java.io.ByteArrayOutputStream
import java.util.*
import kotlin.collections.ArrayDeque

/**
 * Responsible for navigation between notes and holding
 * current note
 */
class NavigationState {
    val currentNote: NavigationNote
    val paragraphs: List<NavigationNote>
    val parents: ArrayDeque<NavigationNote>

    constructor() {
        currentNote = NavigationNote("", "")
        paragraphs = emptyList()
        parents = ArrayDeque()
    }

    constructor(
        currentNote: NavigationNote,
        paragraphs: List<NavigationNote>,
        parents: ArrayDeque<NavigationNote>
    ) {
        this.currentNote = currentNote
        this.paragraphs = paragraphs
        this.parents = parents
    }
}

class NavigationStateFactory {
    companion object {
        suspend fun create(
            newNote: NavigationNote,
            currentNote: NavigationNote?,
            parents: ArrayDeque<NavigationNote>,
            application: Application
        ): NavigationState {
            val rep = getDAO(application)
            currentNote?.let { parents.addLast(it) }

            return NavigationState(
                currentNote = newNote,
                paragraphs = getParagraphs(newNote, rep),
                parents = parents
            )
        }

        private suspend fun getParagraphs(
            note: NavigationNote,
            DAO: WroteDaoFileSystemImpl
        ): List<NavigationNote> {
            val children = DAO.getChildrenKeys(note.uniqueKey)
            val paragraphs = LinkedList<NavigationNote>()
            children.forEach { uniqueKey ->
                paragraphs.add(
                    NavigationNote(
                        uniqueKey = uniqueKey,
                        title = DAO.getName(uniqueKey)
                    )
                )
            }

            return paragraphs
        }
    }
}

class NoteNavigationViewModel(
    initialNote: NavigationNote,
    paragraphs: List<NavigationNote>,
    parents: ArrayDeque<NavigationNote>,
    application: Application,
    private val update: MutableSharedFlow<Unit>
) : AndroidViewModel(application) {
    val rep = getDAO(application)
    val keyGen = getKeyGen(application)

    private val _noteState = MutableStateFlow(initialNote)
    val noteState: StateFlow<NavigationNote> = _noteState

    private val _navigationState = MutableLiveData(
        NavigationState(
            currentNote = initialNote,
            paragraphs = paragraphs,
            parents = parents
        )
    )
    val navigationState: LiveData<NavigationState> = _navigationState

    fun selectNote(note: NavigationNote) {
        viewModelScope.launch {
            _noteState.emit(note)
            changeNote(note).join()
        }
    }

    fun changeNote(
        note: NavigationNote,
        ignoreCurrent: Boolean = false,
        initialUpdate: Boolean = false
    ): Job {
        return viewModelScope.launch() {
            val currentNote =
                if (ignoreCurrent || navigationState.value!!.currentNote.title == "")
                    null
                else
                    navigationState.value!!.currentNote
            val key = note.uniqueKey
            val name = if (rep.getEntryType(key) == EntryType.BOOK)
                rep.getBook(key).title
            else rep.getName(key)


            val parents = navigationState.value!!.parents
            parents.clear()
            if (rep.getEntryType(note.uniqueKey) != EntryType.BOOK) {
                val book = rep.getBookOfNote(note.uniqueKey)
                var currentNoteKey = note.uniqueKey
                while (true) {
                    currentNoteKey = rep.getParentKey(currentNoteKey)
                    if (currentNoteKey == book)
                        break
                    parents.addFirst(NavigationNote(currentNoteKey, rep.getName(currentNoteKey)))
                }

                parents.addFirst(NavigationNote(book, rep.getBook(book).title))
            }


            val newNavigationState =
                NavigationStateFactory.create(
                    newNote = note.copy(title = name),
                    currentNote = currentNote,
                    parents = parents,
                    application = getApplication<Application>()
                )

            _navigationState.postValue(newNavigationState)
        }
    }


    fun goBackAsync() =
        viewModelScope.launch {
            val last = navigationState.value!!.parents.removeLast()
            _noteState.emit(last)
            changeNote(last, ignoreCurrent = true)
        }


    @OptIn(ExperimentalSerializationApi::class)
    fun createNote(creation: NoteCreation) {
        viewModelScope.launch(Dispatchers.IO) {

            val key = keyGen.getKey(navigationState.value!!.currentNote, EntryType.NOTE)

            val attributes = creation.displayPreset.attributes.map {
                val currentNoteKey = navigationState.value!!.currentNote.uniqueKey
                rep.run {
                    getOrCreateAttribute(
                        getBook(
                            if (getEntryType(currentNoteKey) == EntryType.BOOK)
                                currentNoteKey
                            else
                                getBookOfNote(
                                    currentNoteKey
                                )
                        ),
                        keyGen, it
                    )
                }
            }.toSet()
            val dummyNote: BaseNote = object : BaseNote {
                override var title: String = creation.name
                override var alternateTitles: Set<String> = creation.displayPreset.alternateTitles
                override var attributes: Set<Attribute> = attributes
                override val type: NoteType = NoteType.PLAIN_TEXT
                override fun generateSaveData(): ByteArray {
                    val byteArr = ByteArrayOutputStream()
                    Json.encodeToStream(creation.fullPreset.pageLayout, byteArr)
                    return byteArr.toByteArray()
                }

                override fun loadSaveData(value: ByteArray) = throw Error("Unimplemented")
                override fun getIndexingData() = ""
                override val uniqueKey: String = key
            }
            attributes.forEach {
                rep.updateAttributeObject(it)
                it.addEntity(key)
                rep.updateAttributeEntry(it)
            }
            rep.insertNote(
                navigationState.value!!.currentNote,
                dummyNote
            )

//              update for new note to appear
//            changeNote(ignoreCurrent = true)
            update.emit(Unit)
        }
    }

    fun deleteAsync(): Deferred<Boolean> {
        return viewModelScope.async {
            val state = navigationState.value!!
            if (rep.getEntryType(state.currentNote.uniqueKey) == EntryType.BOOK) {
                rep.deleteEntityBook(
                    entity = state.currentNote.uniqueKey
                )
                true
            } else {

                rep.deleteEntityNote(
                    entity = state.currentNote.uniqueKey
                )
                val next = navigationState.value!!.parents.removeLast()
                _noteState.emit(next)
                update.emit(Unit)
                false
            }
        }
    }

    fun update() {
        viewModelScope.launch {
            changeNote(_noteState.value, true, true)
        }
    }

    fun startupUpdate(noteKey: String, noteTitle: String) {
        viewModelScope.launch {
            if(rep.getEntryType(noteKey) != EntryType.BOOK) {
               _noteState.emit(NavigationNote(noteKey, noteTitle))
                update()
            }
        }
    }


    init {
        update()
        viewModelScope.launch {
            update.collect {
                val cur = _noteState.value
                changeNote(cur, true, true).join()
            }

        }
    }
}

class NoteNavigationViewModelFactory : ViewModelProvider.Factory {
    val initialNote: NavigationNote
    val paragraphs: List<NavigationNote>
    val parents: ArrayDeque<NavigationNote>
    val application: Application
    val update: MutableSharedFlow<Unit>


    constructor(
        initialNote: NavigationNote,
        paragraphs: List<NavigationNote>,
        parents: ArrayDeque<NavigationNote>,
        application: Application,
        update: MutableSharedFlow<Unit>
    ) {
        this.initialNote = initialNote
        this.paragraphs = paragraphs
        this.parents = parents
        this.application = application

        this.update = update
    }

    constructor(application: Application, note: NavigationNote, update: MutableSharedFlow<Unit>) {
        val initialState = NavigationState()
        this.initialNote = note
        this.paragraphs = initialState.paragraphs
        this.parents = initialState.parents
        this.application = application
        this.update = update
    }

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return NoteNavigationViewModel(initialNote, paragraphs, parents, application, update) as T
    }
}