package com.dak.wrote.backend.contracts.entities

import com.dak.wrote.backend.contracts.entities.constants.NoteType

interface BaseNote : SearchableEntity, UniqueEntity {
    var title: String
    val type: NoteType
    fun generateSaveData(): ByteArray
    fun loadSaveData(value: ByteArray)
}