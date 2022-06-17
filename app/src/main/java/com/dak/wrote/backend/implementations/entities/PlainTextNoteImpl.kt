package com.dak.wrote.backend.implementations.entities

import com.dak.wrote.backend.abstractions.entities.PlainTextNote
import com.dak.wrote.backend.abstractions.entities.constants.NoteType

class PlainTextNoteImpl(
    private var text: String,
    private var privateTitle: String,
    private val privateType: NoteType,
    private var privateChildren: List<String>, override val uniqueKey: String
) : PlainTextNote {
    override var plainText: String
        get() = text
        set(value) {
            text = value
        }
    override var title: String
        get() = privateTitle
        set(value) {
            privateTitle = value
        }
    override val type: NoteType
        get() = privateType

    override fun getSaveData(): ByteArray {
        return text.toByteArray()
    }

    override var children: List<String>
        get() = privateChildren
        set(value) {
            privateChildren = value
        }

    override fun getIndexingData(): String {
        return plainText
    }
}