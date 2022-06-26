package com.dak.wrote.frontend.preset

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dak.wrote.backend.contracts.entities.UniqueEntity
import com.dak.wrote.frontend.editor.*
import com.dak.wrote.frontend.viewmodel.NoteAdditionViewModel
import compose.icons.FeatherIcons
import compose.icons.feathericons.ChevronDown
import compose.icons.feathericons.ChevronUp
import compose.icons.feathericons.ChevronsDown
import kotlinx.serialization.Serializable

abstract class FullPreset(
    name: String,
    val alternateTitles: Set<String>,
    val attributes: Set<String>,
    val pageLayout: SerializablePageLayout
) {
    val name = mutableStateOf(name)
}

@Serializable
class SerializableDisplayUserPreset(
    val name: String,
    val alternateTitles: Set<String>,
    val attributes: Set<String>,
) {
    fun toPreset() = DisplayUserPreset(name, alternateTitles, attributes)
}

class DisplayUserPreset(
    name: String,
    val alternateTitles: Set<String>,
    val attributes: Set<String>,
) {
    val name = mutableStateOf(name)

    fun toSerializable() = DisplayUserPreset(name.value, alternateTitles, attributes)
}

@Serializable
class SerializableFullUserPreset(
    val name: String,
    val alternateTitles: Set<String>,
    val attributes: Set<String>,
    val pageLayout: SerializablePageLayout
) {
}


class CharacterPreset() :
    FullPreset("Character", setOf(), setOf("character"), CharacterPresetLayout) {
}

private val CharacterPresetLayout
    get() = SerializablePageLayout(listOf())

@Composable
fun NoteAdditionDisplay() {
    val noteAdditionViewModel = viewModel<NoteAdditionViewModel>()
}

val normalPresets = listOf<FullPreset>(CharacterPreset())


@Composable
fun NoteAdditionScreen() {

}

@Composable
fun PresetListView(normalPresets: List<FullPreset>, userPresets: List<DisplayUserPreset>) {
    Column() {
        userPresets.forEach {
            UserPresetView(userPreset = it)
        }
        normalPresets.forEach {
            NormalPresetView(userPreset = it)
        }
    }
}

@Composable
fun AdditionalPresetView(alternateTitles: Set<String>, attributes: Set<String>) {
    var expanded by remember { mutableStateOf(false) }
    TextButton(onClick = { expanded = !expanded }, shape = CircleShape) {
        Text(text = "Additional")
        Spacer(modifier = Modifier.width(10.dp))
        Icon(
            imageVector = if (expanded) FeatherIcons.ChevronUp else FeatherIcons.ChevronDown,
            contentDescription = null
        )
    }
    if (expanded) {
        @Composable
        fun titles(text: String, list: List<String>) {
            Surface(
                elevation = 1.5f.dp,
                modifier = Modifier.padding(horizontal = 5.dp),
                shape = RoundedCornerShape(20.dp)
            ) {
                Box(Modifier.padding(10.dp)) {
                    Column() {
                        Text(text = text, fontWeight = FontWeight.Bold)
                        list.forEach {
                            Text(it)
                        }
                    }
                }
            }
        }
        titles(text = "Alternate Titles", list = alternateTitles.toList())
        titles(text = "Attributes", list = attributes.toList())
    }
}

@Composable
fun NormalPresetView(userPreset: FullPreset) {
    Surface(
        elevation = 2.dp,
        shape = RoundedCornerShape(25.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(modifier = Modifier.padding(horizontal = 10.dp, vertical = 15.dp)) {
            Column(
                Modifier.animateContentSize(),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    userPreset.name.value,
                    style = MaterialTheme.typography.h5,
                    modifier = Modifier.padding(start = 10.dp)
                )
            }
            AdditionalPresetView(
                alternateTitles = userPreset.alternateTitles,
                attributes = userPreset.attributes
            )
        }
    }
}

@Composable
fun UserPresetView(userPreset: DisplayUserPreset) {
    Surface(
        elevation = 2.dp,
        shape = RoundedCornerShape(25.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(modifier = Modifier.padding(horizontal = 10.dp, vertical = 15.dp)) {
            Column(
                Modifier.animateContentSize(),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    userPreset.name.value,
                    style = MaterialTheme.typography.h5,
                    modifier = Modifier.padding(start = 10.dp)
                )
                AdditionalPresetView(userPreset.alternateTitles, userPreset.attributes)
            }
        }
    }
}


@Preview(showSystemUi = true, device = Devices.PIXEL_3)
@Composable
fun UserPresetViewPreview() {
    UserPresetView(
        userPreset = DisplayUserPreset(
            "Hehe",
            setOf("Honorable Knight"),
            setOf("я", "character", "king")
        )
    )
}

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
