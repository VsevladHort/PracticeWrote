package com.dak.wrote.frontend.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dak.wrote.frontend.noteNavigation.NavigationNote
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ControllerViewModel() : ViewModel() {
    val updateNotes: MutableSharedFlow<Unit> = MutableSharedFlow()
    val updatePresets : MutableSharedFlow<Unit> = MutableSharedFlow()
    val checkNavigation = mutableStateOf(true)
    val currentNote: MutableState<NavigationNote?> = mutableStateOf(null)

    fun callUpdate() {
        viewModelScope.launch {
            updateNotes.emit(Unit)
        }
    }

}