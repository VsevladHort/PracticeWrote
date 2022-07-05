package com.dak.wrote.frontend.editor

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.dak.wrote.frontend.AligningBasicTextField
import com.dak.wrote.ui.theme.Material3
import kotlinx.serialization.Serializable

/**
 * Layout for basic text
 */
class TextDataLayout(text: String) : DataLayout() {
    private val text = mutableStateOf(text)

    @Composable
    override fun DrawEdit() {
        Column(modifier = Modifier.padding(horizontal = 10.dp)) {
            AligningBasicTextField(
                value = text.value,
                onValueChange = { text.value = it },
                textStyle = textStyle(),
                modifier = Modifier.fillMaxWidth(),
                cursorBrush = SolidColor(Material3.colorScheme.primary)
            )
            Divider(thickness = 1.dp, modifier = Modifier.fillMaxWidth())
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

/**
 * Layout for bold text
 */
class BoldDL(text: String) : DataLayout() {
    val text = mutableStateOf(text)

    @Composable
    override fun DrawEdit() {
        Column(modifier = Modifier.padding(horizontal = 10.dp)) {
            AligningBasicTextField(
                value = text.value,
                onValueChange = { text.value = it },
                textStyle = boldTextStyle(),
                modifier = Modifier.fillMaxWidth(),

                cursorBrush = SolidColor(Material3.colorScheme.primary)

            )
            Divider(thickness = 1.dp, modifier = Modifier.fillMaxWidth())
        }
    }

    @Composable
    override fun DrawNormal() {
        Box(Modifier.padding(horizontal = 10.dp)) {
            Text(text = text.value, style = boldTextStyle().copy())
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
private fun textStyle() = Material3.typography.titleLarge.copy(color = Material3.colorScheme.onBackground)

@Composable
private fun boldTextStyle() = textStyle().copy(fontWeight = FontWeight.Bold)