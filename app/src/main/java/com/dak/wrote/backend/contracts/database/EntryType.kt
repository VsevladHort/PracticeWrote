package com.dak.wrote.backend.contracts.database

enum class EntryType(val stringRepresentation: String) {
    NOTE("NOTE"),
    BOOK("BOOK"),
    ATTRIBUTE("ATTRIBUTE"),
    PRESET("PRESET")
}