package com.dak.wrote.backend.abstractions.entities

import com.dak.wrote.backend.abstractions.entities.constants.NoteType

interface BaseNote : TreeEntity, SearchableEntity, UniqueEntity {
    var title: String
    val type: NoteType
    fun getSaveData(): ByteArray
}