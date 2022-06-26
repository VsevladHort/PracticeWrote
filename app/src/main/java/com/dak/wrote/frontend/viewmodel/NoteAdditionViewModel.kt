package com.dak.wrote.frontend.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dak.wrote.backend.implementations.file_system_impl.dao.getDAO
import com.dak.wrote.frontend.preset.DisplayUserPreset
import com.dak.wrote.frontend.preset.FullPreset
import com.dak.wrote.frontend.preset.normalPresets
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

class NoteAdditionViewModel(application: Application) : AndroidViewModel(application) {
    data class Data(
        val userPresets: List<Pair<String, DisplayUserPreset>>,
    )

    private val _currentId: MutableStateFlow<String?> = MutableStateFlow(null)
    val currentId = _currentId.distinctUntilChanged { old, new -> old == new }

    fun passId(id: String) {
        _currentId.value = id
    }

    val rep = getDAO(application.applicationContext)
    val data: MutableStateFlow<Data?> = MutableStateFlow(null)

    init {
        viewModelScope.launch {
//            data.value = Data(rep.getPresets().map { })
            currentId.filterNotNull().collect {
//                rep.getPresets()
            }
        }
    }
}