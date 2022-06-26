package com.dak.wrote.frontend.viewmodel

import androidx.lifecycle.*
import com.dak.wrote.backend.contracts.entities.Book
import com.dak.wrote.backend.implementations.file_system_impl.dao.WroteDaoFileSystemImpl
import com.dak.wrote.frontend.noteNavigation.NavigationNote
import com.dak.wrote.frontend.noteNavigation.NoteNavigation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
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
            dir: File
        ): NavigationState {
            val DAO = WroteDaoFileSystemImpl.getInstance(dir)
            currentNote?.let { parents.addLast(it) }

            return NavigationState(
                currentNote = newNote,
                paragraphs = getParagraphs(newNote, DAO),
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
    private val dir: File
) : ViewModel() {
    val DAO = WroteDaoFileSystemImpl.getInstance(dir);

    private val _navigationState = MutableLiveData(
        NavigationState(
            currentNote = initialNote,
            paragraphs = paragraphs,
            parents = parents
        )
    )
    val navigationState: LiveData<NavigationState> = _navigationState


    fun changeNote(newNote: NavigationNote, ignoreCurrent: Boolean = false) {
//        viewModelScope.launch {
        viewModelScope.launch(Dispatchers.IO) {
            val currentNote =
                if (ignoreCurrent || navigationState.value!!.currentNote.title == "")
                    null
                else
                    navigationState.value!!.currentNote

            val newNavigationState =
                NavigationStateFactory.create(
                    newNote = newNote,
                    currentNote = currentNote,
                    parents = navigationState.value!!.parents,
                    dir = dir
                )

            _navigationState.postValue(newNavigationState)
        }
    }


    fun goBack() = changeNote(navigationState.value!!.parents.removeLast(), ignoreCurrent = true)
}

class NoteNavigationViewModelFactory : ViewModelProvider.Factory {
    val initialNote: NavigationNote
    val paragraphs: List<NavigationNote>
    val parents: ArrayDeque<NavigationNote>
    val dir: File


    constructor(
        initialNote: NavigationNote,
        paragraphs: List<NavigationNote>,
        parents: ArrayDeque<NavigationNote>,
        dir: File
    ) {
        this.initialNote = initialNote
        this.paragraphs = paragraphs
        this.parents = parents
        this.dir = dir

    }

    constructor(dir: File) {
        val initialState = NavigationState()
        this.initialNote = initialState.currentNote
        this.paragraphs = initialState.paragraphs
        this.parents = initialState.parents
        this.dir = dir
    }

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return NoteNavigationViewModel(initialNote, paragraphs, parents, dir) as T
    }
}