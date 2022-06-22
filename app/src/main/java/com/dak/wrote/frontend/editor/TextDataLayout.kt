package com.dak.wrote.frontend.editor

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dak.wrote.backend.contracts.entities.BaseNote
import com.dak.wrote.frontend.viewmodel.EditorViewModel
import kotlinx.serialization.Serializable

class TextDataLayout(text: String) : DataLayout() {
    private val text = mutableStateOf(text)

    @Composable
    override fun DrawEdit(editorViewModel: EditorViewModel) {
        Box(modifier = Modifier.padding(horizontal = 10.dp)) {
            BasicTextField(
                value = text.value,
                onValueChange = { text.value = it },
                textStyle = textStyle()
            )
        }
    }

    @Composable
    override fun DrawNormal(editorViewModel: EditorViewModel) {
        Box(Modifier.padding(horizontal = 10.dp)) {
            Text(text = text.value, style = textStyle())
        }
    }

    override fun onSubmit(node: BaseNote) {
    }

    override fun toSerializable(): SerializableDataLayout {
        return SerializableTextDataLayout(text.value)
    }

}

@Serializable
class SerializableTextDataLayout(val text: String) : SerializableDataLayout() {
    override fun toDisplayable(): DataLayout {
        return TextDataLayout(text)
    }
}

@Composable
private fun textStyle() = MaterialTheme.typography.body1.copy(fontSize = 20.sp)