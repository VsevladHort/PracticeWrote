package com.dak.wrote.frontend.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.*
import com.dak.wrote.backend.contracts.entities.Book
import com.dak.wrote.frontend.noteNavigation.map

data class NavigationState(
    val currentNote: String,
    val paragraphs: List<Book>,
    val hasParent: Boolean
)

class NoteNavigationViewModel(
    initialNote: String,
    paragraphs: List<Book>,
    hasParent: Boolean
) : ViewModel() {
    private val _navigationState = MutableLiveData(
        NavigationState(
            currentNote = initialNote,
            paragraphs = paragraphs,
            hasParent = hasParent
        )
    )

    val navigationState: LiveData<NavigationState> = _navigationState


    fun changeNote(newNote: String) {
        val paragraphs: List<Book> = map[newNote] ?: emptyList()
        val hasParent: Boolean = true

        _navigationState.value = NavigationState(
            currentNote = newNote,
            paragraphs = paragraphs,
            hasParent = hasParent
        )
    }
}

class NoteNavigationViewModelFactory(
    val initialNote: String,
    val paragraphs: List<Book>,
    val hasParent: Boolean
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return NoteNavigationViewModel(initialNote, paragraphs, hasParent) as T
    }
//        modelClass.getConstructor(NoteNavigationViewModel::class.java)
//            .newInstance(initialNote, paragraphs, hasParent)

}