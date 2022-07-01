package com.dak.wrote.frontend.viewmodel

import android.app.Application
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dak.wrote.backend.implementations.file_system_impl.dao.getDAO
import com.dak.wrote.frontend.editor.mutStateListOf
import com.dak.wrote.frontend.preset.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

class NoteAdditionViewModel(application: Application) : AndroidViewModel(application) {
    data class Data(
        val userPresets: SnapshotStateList<DisplayUserPreset>,
        var loadingJob: Job? = null,
        val name: MutableState<String> = mutableStateOf(""),
        val loading: MutableState<Boolean> = mutableStateOf(false),
        val currentSelected: MutableState<DisplayPreset?> = mutableStateOf(null),
        val loadedSelected: MutableState<FullPreset?> = mutableStateOf(null),
        val canCreate: MutableState<Boolean> = mutableStateOf(false)
    )

    val rep = getDAO(application.applicationContext)
    val data: MutableStateFlow<Data?> = MutableStateFlow(null)

    fun load(data: Data, userPreset: DisplayUserPreset) {
        data.loadingJob?.cancel()
        data.loading.value = true
        data.canCreate.value = false
        data.currentSelected.value = userPreset
        data.loadingJob = viewModelScope.launch {
            data.loadedSelected.value = rep.getPresetFull(UserPresetSaver(), userPreset.uniqueKey)
            data.loading.value = false
            data.canCreate.value = true
        }
    }

    fun updateName(displayUserPreset: DisplayUserPreset) {
        viewModelScope.launch {
            rep.updatePresetDisplay(UserPresetSaver(), displayUserPreset.toSerializable())
        }
    }

    fun delete(data: Data, displayUserPreset: DisplayUserPreset) {
        data.userPresets.remove(displayUserPreset)
        viewModelScope.launch {
            rep.deletePreset(displayUserPreset.uniqueKey)
        }
    }

    fun setFull(data: Data, preset: BasicPreset) {
        data.loadingJob?.cancel()
        data.loading.value = false
        data.loadedSelected.value = preset
        data.currentSelected.value = preset
        data.canCreate.value = true
    }

    fun update() {
        viewModelScope.launch {
            data.value = Data(
                mutStateListOf(rep.getPresets().map {
                    rep.getPresetDisplay(UserPresetSaver(), it).toPreset()
                }),
            )
        }

    }

    fun remove(data: Data, ind: Int) {
       val displayValue = data.userPresets.removeAt(ind)
        viewModelScope.launch {
            rep.deletePreset(displayValue.uniqueKey)
        }
    }

    init {
        update()
    }
}