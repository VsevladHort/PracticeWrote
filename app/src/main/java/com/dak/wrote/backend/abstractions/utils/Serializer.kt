package com.dak.wrote.backend.abstractions.utils

import com.dak.wrote.backend.abstractions.entities.BaseNote

interface Serializer {
    fun serialize(note: BaseNote): Boolean
}