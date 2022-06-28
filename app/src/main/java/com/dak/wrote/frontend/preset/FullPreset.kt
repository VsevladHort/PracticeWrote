package com.dak.wrote.frontend.preset

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.TextButton
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dak.wrote.backend.contracts.entities.PresetManager
import com.dak.wrote.backend.contracts.entities.UniqueEntity
import com.dak.wrote.frontend.editor.*
import com.dak.wrote.frontend.viewmodel.NoteAdditionViewModel
import com.dak.wrote.ui.theme.WroteTheme
import compose.icons.FeatherIcons
import compose.icons.feathericons.ChevronDown
import compose.icons.feathericons.ChevronUp
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

interface FullPreset {
    val pageLayout: SerializablePageLayout
}

interface DisplayPreset {
    val name: String
    val alternateTitles: Set<String>
    val attributes: Set<String>
}

@OptIn(ExperimentalSerializationApi::class)
class UserPresetSaver : PresetManager<SerializableDisplayUserPreset, SerializableFullUserPreset> {
    override fun loadDisplay(byteData: ByteArray): SerializableDisplayUserPreset {
        return Json.decodeFromStream(ByteArrayInputStream(byteData))
    }

    override fun loadFull(byteData: ByteArray): SerializableFullUserPreset {
        return Json.decodeFromStream(ByteArrayInputStream(byteData))
    }

    override fun saveDisplay(display: SerializableDisplayUserPreset): ByteArray {
        val output = ByteArrayOutputStream()
        Json.encodeToStream(display, output)
        return output.toByteArray()
    }

    override fun saveFull(full: SerializableFullUserPreset): ByteArray {
        val output = ByteArrayOutputStream()
        Json.encodeToStream(full, output)
        return output.toByteArray()
    }

}

@Serializable
class SerializableDisplayUserPreset(
    val name: String,
    val alternateTitles: Set<String>,
    val attributes: Set<String>, override val uniqueKey: String,
) : UniqueEntity {
    fun toPreset() = DisplayUserPreset(name, alternateTitles, attributes, uniqueKey)
}

class DisplayUserPreset(
    name: String,
    override val alternateTitles: Set<String>,
    override val attributes: Set<String>, override val uniqueKey: String,
) : DisplayPreset, UniqueEntity {
    val nameState = mutableStateOf(name)
    override var name: String
        get() = nameState.value
        set(value) {
            nameState.value = value
        }

    fun toSerializable() =
        SerializableDisplayUserPreset(name, alternateTitles, attributes, uniqueKey)
}

@Serializable
class SerializableFullUserPreset(
    override val pageLayout: SerializablePageLayout, override val uniqueKey: String
) : FullPreset, UniqueEntity

open class BasicPreset(
    override val name: String,
    override val alternateTitles: Set<String>,
    override val attributes: Set<String>,
    override val pageLayout: SerializablePageLayout,
) : DisplayPreset, FullPreset

class CharacterPreset() :
    BasicPreset("Character", setOf(), setOf("character"), CharacterPresetLayout) {
}

private val CharacterPresetLayout
    get() = SerializablePageLayout(listOf())

@Composable
fun NoteAdditionDisplay() {
    val noteAdditionViewModel = viewModel<NoteAdditionViewModel>()
}

val normalPresets = listOf(CharacterPreset())


@Composable
fun NoteAdditionScreen() {

}

@Composable
fun PresetListView(
    normalPresets: List<BasicPreset>,
    userPresets: List<DisplayUserPreset>,
    selectFull: (BasicPreset) -> Unit,
    selectUser: (DisplayUserPreset) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
        userPresets.forEach {
            UserPresetView(it) { selectUser(it) }
        }
        normalPresets.forEach {
            NormalPresetView(it) { selectFull(it) }
        }
    }
}

@Composable
fun AdditionalPresetView(alternateTitles: Set<String>, attributes: Set<String>) {
    var expanded by remember { mutableStateOf(false) }
    TextButton(onClick = { expanded = !expanded }, modifier = Modifier.padding(4.dp)) {
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
                tonalElevation = 1.5f.dp,
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
fun UserPresetView(userPreset: DisplayUserPreset, onSelect: () -> Unit) {
    Surface(
        tonalElevation = 2.dp,
        shape = RoundedCornerShape(25.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(modifier = Modifier.padding(horizontal = 10.dp, vertical = 15.dp)) {
            Column(
                Modifier.animateContentSize(),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    userPreset.nameState.value,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(start = 10.dp)
                )

                AdditionalPresetView(
                    alternateTitles = userPreset.alternateTitles,
                    attributes = userPreset.attributes
                )
            }
//            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
fun NormalPresetView(preset: BasicPreset, onSelect: () -> Unit) {
    Surface(
        tonalElevation = 2.dp,
        shape = RoundedCornerShape(25.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(modifier = Modifier.padding(horizontal = 10.dp, vertical = 15.dp)) {
            Column(
                Modifier.animateContentSize(),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    preset.name,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = 10.dp)
                )
                AdditionalPresetView(preset.alternateTitles, preset.attributes)
            }
        }
    }
}


@Preview(showSystemUi = true, device = Devices.PIXEL_3)
@Composable
fun PresetViewPreview() {
    UserPresetView(
        DisplayUserPreset(
            "Hehe",
            setOf("Honorable Knight"),
            setOf("я", "character", "king"),
            ""
        ), {}
    )
}

@Preview(showSystemUi = true, device = Devices.PIXEL_3)
@Composable
fun PresetListViewPreview() {
    WroteTheme() {
        PresetListView(
            normalPresets = normalPresets, userPresets = listOf(
                DisplayUserPreset(
                    "Temerian King",
                    setOf("Honorable Knight"),
                    setOf("character", "", "king"),
                    ""
                ),
                DisplayUserPreset(
                    "Hero's team member",
                    setOf(""),
                    setOf("", "character", "hero's team member"),
                    ""
                ),
            ), {}, {}
        )
    }
}