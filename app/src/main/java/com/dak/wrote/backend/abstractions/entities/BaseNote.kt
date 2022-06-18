package com.dak.wrote.backend.abstractions.entities

import com.dak.wrote.backend.abstractions.entities.constants.NoteType

interface BaseNote : SearchableEntity, UniqueEntity {
    var title: String
    val type: NoteType
    fun generateSaveData(): ByteArray
    fun loadSaveData(value: ByteArray)
}