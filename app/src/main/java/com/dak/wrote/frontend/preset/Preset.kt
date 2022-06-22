package com.dak.wrote.frontend.preset

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.autoSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.dak.wrote.backend.contracts.entities.BaseNote
import com.dak.wrote.frontend.editor.*
import compose.icons.FeatherIcons
import compose.icons.feathericons.Delete
import compose.icons.feathericons.Edit3
import kotlinx.serialization.Serializable

abstract class Preset(
    name: String,
    attributes: List<String>,
    val pageLayout: SerializablePageLayout
) {
    protected val mutError = mutableStateOf(true)
    val name = mutableStateOf(name)
    val attributes = mutableStateOf(attributes)

    val error: State<Boolean>
        get() = mutError

    abstract fun create(): BaseNote

}

class SerializableUserPreset(val name: String, val attributes: List<String>, val pageLayout: SerializablePageLayout) {
    fun toPreset() = UserPreset(name, attributes, pageLayout)
}


class UserPreset(name: String, attributes: List<String>, pageLayout: SerializablePageLayout) :
    Preset(name, attributes, pageLayout) {

    override fun create(): BaseNote {
        TODO("Not yet implemented")
    }

    fun toSerializable() = SerializableUserPreset(name.value, attributes.value, pageLayout)
}

class CharacterPreset() : Preset("Character", listOf("character"), CharacterPresetLayout) {
    override fun create(): BaseNote {
        TODO("Not yet implemented")
    }
}

private val CharacterPresetLayout = SerializablePageLayout(listOf())


//@Composable
//fun PresetViewTop(presets: List<SerializableUserPreset>) {
//    Column {
//        for (i in presets) {
//            PresetViewController(i) {}
//        }
//    }
//}
//
//@Preview
//@Composable
//fun PreviewPresetViewController() {
//    val layout = SerializablePageLayout(
//        listOf(
//            SerializableParagraphLayout(
//                listOf(
//                    SerializableTextDataLayout("Hello")
//                )
//            )
//        )
//    )
//    val preset = SerializableUserPreset("Hello", listOf("Character", "Cat"), layout)
//    PresetViewController(preset) {
//
//    }
//}

//@Composable
//fun PresetViewController(preset: SerializableUserPreset, updatePreset: (SerializableUserPreset) -> Unit) {
//    var inEdit by rememberSaveable() {
//        mutableStateOf(false)
//    }
////    var currentPreset by rememberSaveable(preset) { mutableStateOf(preset.toPresetView()) }
//    Surface() {
//        Column(
//            Modifier
//                .wrapContentHeight()
//                .fillMaxWidth()
//        ) {
//            Row(horizontalArrangement = Arrangement.End) {
//                if (!inEdit) {
//                    IconButton(onClick = {
//                        inEdit = false; currentPreset = preset.toPresetView()
//                    }) {
//                        Icon(imageVector = FeatherIcons.Delete, contentDescription = "Trash edit")
//                    }
//                } else {
//                    IconButton(onClick = { inEdit = true }) {
//                        Icon(
//                            imageVector = FeatherIcons.Edit3,
//                            contentDescription = "Edit preset"
//                        )
//                    }
//                }
//            }
//
//            if (inEdit) {
//                currentPreset.Edit()
//
//                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
//                    Button(
//                        onClick = { if (!currentPreset.error.value) updatePreset(currentPreset.toSerializablePreset()) },
//                        enabled = !currentPreset.error.value
//                    ) {
//                        Text(text = "Submit")
//                    }
//                }
//            } else
//                currentPreset.View()
//        }
//    }
//}
