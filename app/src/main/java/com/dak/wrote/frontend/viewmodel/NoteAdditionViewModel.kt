package com.dak.wrote.frontend.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.dak.wrote.backend.implementations.file_system_impl.dao.WroteDaoFileSystemImpl
import com.dak.wrote.backend.implementations.file_system_impl.dao.getDAO
import com.dak.wrote.frontend.preset.Preset
import com.dak.wrote.frontend.preset.UserPreset
import com.dak.wrote.frontend.preset.normalPresets

class NoteAdditionViewModel(application: Application) : AndroidViewModel(application) {

    val rep = getDAO(application.applicationContext)
    val presets: List<Preset>
//    val userPresets: List<UserPreset>

    init {
        presets = normalPresets
//        userPresets = rep
    }
}