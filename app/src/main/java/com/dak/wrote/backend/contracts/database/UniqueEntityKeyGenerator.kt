package com.dak.wrote.backend.contracts.database

import com.dak.wrote.backend.contracts.entities.UniqueEntity

interface UniqueEntityKeyGenerator {
    /**
     * @param parent parent entry of the entry for which the key is being generated, should be null if parent is absent
     * @param type type of the entry for which the key is being generated
     *
     * @return unique key for the entry
     */
    suspend fun getKey(parent: UniqueEntity?, type: EntryType): String
}