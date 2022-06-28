package com.dak.wrote.backend.contracts.entities

interface PresetManager<Display, Full> {
    fun loadDisplay(byteData: ByteArray): Display
    fun loadFull(byteData: ByteArray): Full
    fun saveDisplay(display: Display): ByteArray
    fun saveFull(full: Full): ByteArray
}
