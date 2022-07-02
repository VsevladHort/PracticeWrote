package com.dak.wrote.frontend.editor

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.dak.wrote.ui.theme.Material3
import kotlinx.serialization.Serializable

class TextDataLayout(text: String) : DataLayout() {
    private val text = mutableStateOf(text)

    @Composable
    override fun DrawEdit() {
        Box(modifier = Modifier.padding(horizontal = 10.dp)) {
            BasicTextField(
                value = text.value,
                onValueChange = { text.value = it },
                textStyle = textStyle(),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    @Composable
    override fun DrawNormal() {
        Box(Modifier.padding(horizontal = 10.dp)) {
            Text(text = text.value, style = textStyle())
        }
    }

    override fun toSerializable(): SerializableDataLayout {
        return STextDL(text.value)
    }

}

@Serializable
class STextDL(val text: String) : SerializableDataLayout() {
    override fun toDisplayable(): DataLayout {
        return TextDataLayout(text)
    }
}

class BoldDL(text: String) : DataLayout() {
    val text = mutableStateOf(text)

    @Composable
    override fun DrawEdit() {
        Box(Modifier.padding(horizontal = 10.dp)) {
            Text(text = text.value, style = textStyle())
        }
    }

    @Composable
    override fun DrawNormal() {
        Box(modifier = Modifier.padding(horizontal = 10.dp)) {
            BasicTextField(
                value = text.value,
                onValueChange = { text.value = it },
                textStyle = textStyle().copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    override fun toSerializable(): SerializableDataLayout {
        return SBoldDL(text.value)
    }
}

@Serializable
class SBoldDL(val text: String) : SerializableDataLayout() {
    override fun toDisplayable(): DataLayout {
        return BoldDL(text)
    }

}

@Composable
private fun textStyle() = Material3.typography.titleSmall