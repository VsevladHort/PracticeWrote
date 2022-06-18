package com.dak.wrote.frontend.editor

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement

class ObjectNote {} // Supposed to be the wrapper for layout


abstract class DataLayout {
    @Composable
    abstract fun DrawEdit()

    @Composable
    abstract fun DrawNormal()

    abstract fun onSubmit(node: ObjectNote)

    abstract fun toSerializable() : SerializableDataLayout
}

@Serializable
abstract class SerializableDataLayout {
    abstract fun toDisplayable() : DataLayout
}


class ParagraphLayout(column : List<SerializableDataLayout>) : DataLayout() {
    private val columns = mutableStateListOf<DataLayout>().apply {
        addAll(column.map { it.toDisplayable() })
    }
    @Composable
    override fun DrawEdit() {
        Column(Modifier.wrapContentSize()) {
            for (i in columns)
                i.DrawEdit()
        }
    }

    @Composable
    override fun DrawNormal() {
        Column(Modifier.wrapContentSize()) {
            for(i in columns)
                i.DrawNormal()
        }
    }

    override fun onSubmit(node: ObjectNote) {
    }

    override fun toSerializable(): SerializableParagraphLayout {
        return SerializableParagraphLayout(columns.map { it.toSerializable() })
    }
}

@Serializable
class SerializableParagraphLayout(val column : List<SerializableDataLayout>) : SerializableDataLayout() {
    override fun toDisplayable(): ParagraphLayout {
        return ParagraphLayout(column)
    }

}

class TextDataLayout(text : String) : DataLayout() {
    private val text = mutableStateOf(text)

    @Composable
    override fun DrawEdit() {
        TextField(value = text.value, onValueChange = { text.value = it })
    }

    @Composable
    override fun DrawNormal() {
        Text(text = text.value)
    }

    override fun onSubmit(node: ObjectNote) {
    }

    override fun toSerializable(): SerializableDataLayout {
        return  SerializableTextDataLayout(text.value)
    }

}

@Serializable
class SerializableTextDataLayout(val text : String) : SerializableDataLayout() {
    override fun toDisplayable(): DataLayout {
        return TextDataLayout(text)
    }
}
