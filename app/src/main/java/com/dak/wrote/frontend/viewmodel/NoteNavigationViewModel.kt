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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToStream
import java.io.ByteArrayOutputStream
import java.util.*
import kotlin.collections.ArrayDeque

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
    application: Application
) : AndroidViewModel(application) {
    val rep = getDAO(application)
    val keyGen = getKeyGen(application)

    private val _navigationState = MutableLiveData(
        NavigationState(
            currentNote = initialNote,
            paragraphs = paragraphs,
            parents = parents
        )
    )
    val navigationState: LiveData<NavigationState> = _navigationState


    fun changeNote(newNote: NavigationNote, ignoreCurrent: Boolean = false) {
        viewModelScope.launch(Dispatchers.IO) {
            val currentNote =
//                if (ignoreCurrent || navigationState.value!!.currentNote.title == "")
                if (ignoreCurrent)
                    null
                else
                    navigationState.value!!.currentNote

            val newNavigationState =
                NavigationStateFactory.create(
                    newNote = newNote,
                    currentNote = currentNote,
                    parents = navigationState.value!!.parents,
                    application = getApplication<Application>()
                )

            _navigationState.postValue(newNavigationState)
        }
    }


    fun goBack() = changeNote(navigationState.value!!.parents.removeLast(), ignoreCurrent = true)

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
                it.addEntity(key)
                rep.updateAttributeEntry(it)
            }
            rep.insetNote(
                navigationState.value!!.currentNote,
                dummyNote
            )

//              update for new note to appear
            changeNote(navigationState.value!!.currentNote, ignoreCurrent = true)
        }
    }
}

class NoteNavigationViewModelFactory : ViewModelProvider.Factory {
    val initialNote: NavigationNote
    val paragraphs: List<NavigationNote>
    val parents: ArrayDeque<NavigationNote>
    val application: Application


    constructor(
        initialNote: NavigationNote,
        paragraphs: List<NavigationNote>,
        parents: ArrayDeque<NavigationNote>,
        application: Application
    ) {
        this.initialNote = initialNote
        this.paragraphs = paragraphs
        this.parents = parents
        this.application = application

    }

    constructor(application: Application) {
        val initialState = NavigationState()
        this.initialNote = initialState.currentNote
        this.paragraphs = initialState.paragraphs
        this.parents = initialState.parents
        this.application = application
    }

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return NoteNavigationViewModel(initialNote, paragraphs, parents, application) as T
    }
}