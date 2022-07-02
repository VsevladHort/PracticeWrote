package com.dak.wrote.frontend.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ControllerViewModel : ViewModel() {
    val update: MutableSharedFlow<Unit> = MutableSharedFlow()

    fun callUpdate() {
        viewModelScope.launch {
            update.emit(Unit)
        }
    }

}