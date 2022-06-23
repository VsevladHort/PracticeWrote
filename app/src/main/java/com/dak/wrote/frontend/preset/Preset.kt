package com.dak.wrote.frontend.preset

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.autoSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dak.wrote.backend.contracts.entities.BaseNote
import com.dak.wrote.frontend.editor.*
import com.dak.wrote.frontend.viewmodel.NoteAdditionViewModel
import compose.icons.FeatherIcons
import compose.icons.feathericons.Delete
import compose.icons.feathericons.Edit3
import kotlinx.serialization.Serializable

abstract class Preset(
    name: String,
    alternateTitles: List<String>,
    attributes: List<String>,
    val pageLayout: SerializablePageLayout
) {
    protected val mutError = mutableStateOf(true)
    val name = mutableStateOf(name)
    val attributes = mutableStateListOf(*attributes.toTypedArray())
    val alternateTitles = mutableStateListOf(*alternateTitles.toTypedArray())

    val error: State<Boolean>
        get() = mutError

    abstract fun create(): BaseNote

}

class SerializableUserPreset(
    val name: String,
    val alternateTitles: List<String>,
    val attributes: List<String>,
    val pageLayout: SerializablePageLayout
) {
    fun toPreset() = UserPreset(name, alternateTitles, attributes, pageLayout)
}


class UserPreset(
    name: String,
    alternateTitles: List<String>,
    attributes: List<String>,
    pageLayout: SerializablePageLayout
) :
    Preset(name, alternateTitles, attributes, pageLayout) {

    override fun create(): BaseNote {
        TODO("Not yet implemented")
    }

    fun toSerializable() = SerializableUserPreset(name.value, alternateTitles, attributes, pageLayout)
}

class CharacterPreset() : Preset("Character", listOf(), listOf("character"), CharacterPresetLayout) {
    override fun create(): BaseNote {
        TODO("Not yet implemented")
    }
}

private val CharacterPresetLayout = SerializablePageLayout(listOf())

@Composable
fun NoteAdditionDisplay() {
    val noteAdditionViewModel = viewModel<NoteAdditionViewModel>()
}

val normalPresets = listOf<Preset>(CharacterPreset())

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
