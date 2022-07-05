package com.dak.wrote.frontend.preset

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dak.wrote.backend.contracts.entities.PresetManager
import com.dak.wrote.backend.contracts.entities.UniqueEntity
import com.dak.wrote.frontend.AligningBasicTextField
import com.dak.wrote.frontend.editor.SerializablePageLayout
import com.dak.wrote.frontend.viewmodel.UpdateHolder
import com.dak.wrote.ui.theme.Material3
import com.dak.wrote.ui.theme.WroteTheme
import compose.icons.FeatherIcons
import compose.icons.feathericons.ChevronDown
import compose.icons.feathericons.ChevronUp
import compose.icons.feathericons.Delete
import compose.icons.feathericons.Edit2
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

/**
 * Second part of preset with page layout
 */
interface FilledPreset {
    val pageLayout: SerializablePageLayout
}

/**
 * First part of a preset with displayed values
 */
interface DisplayPreset {
    val name: String
    val alternateTitles: Set<String>
    val attributes: Set<String>
}

/**
 * Saves and loads presets
 */
@OptIn(ExperimentalSerializationApi::class)
class UserPresetSaver : PresetManager<SerializableDisplayUserPreset, SerializableFilledUserPreset> {
    override fun loadDisplay(byteData: ByteArray): SerializableDisplayUserPreset {
        return Json.decodeFromStream(ByteArrayInputStream(byteData))
    }

    override fun loadFull(byteData: ByteArray): SerializableFilledUserPreset {
        return Json.decodeFromStream(ByteArrayInputStream(byteData))
    }

    override fun saveDisplay(display: SerializableDisplayUserPreset): ByteArray {
        val output = ByteArrayOutputStream()
        Json.encodeToStream(display, output)
        return output.toByteArray()
    }

    override fun saveFull(full: SerializableFilledUserPreset): ByteArray {
        val output = ByteArrayOutputStream()
        Json.encodeToStream(full, output)
        return output.toByteArray()
    }

}

/**
 * Saveable display part of a preset made by user
 */
@Serializable
class SerializableDisplayUserPreset(
    val name: String,
    val alternateTitles: Set<String>,
    val attributes: Set<String>, override val uniqueKey: String,
) : UniqueEntity {
    fun toPreset() = DisplayUserPreset(name, alternateTitles, attributes, uniqueKey)
}

/**
 * Display part of a preset made by a user
 */
class DisplayUserPreset(
    name: String,
    override val alternateTitles: Set<String>,
    override val attributes: Set<String>, override val uniqueKey: String,
) : DisplayPreset, UniqueEntity {
    val nameState = UpdateHolder(name)
    override var name: String
        get() = nameState.next.value
        set(value) {
            nameState.next.value = value
        }

    fun toSerializable() =
        SerializableDisplayUserPreset(name, alternateTitles, attributes, uniqueKey)
}


/**
 * Filled part of a preset made by a user
 */
@Serializable
class SerializableFilledUserPreset(
    override val pageLayout: SerializablePageLayout, override val uniqueKey: String
) : FilledPreset, UniqueEntity

open class BasicPreset(
    override val name: String,
    override val alternateTitles: Set<String>,
    override val attributes: Set<String>,
    override val pageLayout: SerializablePageLayout,

    ) : DisplayPreset, FilledPreset

@Composable
fun PresetListView(
    normalPresets: List<BasicPreset>,
    userPresets: List<DisplayUserPreset>,
    currentSelected: MutableState<DisplayPreset?>,
    updateUserPreset: (DisplayUserPreset) -> Unit,
    delete: (Int) -> Unit,
    selectBasic: (BasicPreset) -> Unit,
    selectUser: (DisplayUserPreset) -> Unit,
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 10.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(15.dp),
    ) {
        userPresets.forEachIndexed { i, preset ->
            UserPresetView(
                preset.nameState.next.value,
                preset.alternateTitles,
                preset.attributes,
                remember { derivedStateOf { currentSelected.value == preset } },
                remember {
                    EditingValues(
                        preset.nameState.next.component2(), { updateUserPreset(preset) },
                        { preset.nameState.next.value = preset.nameState.old },
                        { delete(i) }
                    )
                },
            ) { selectUser(preset) }
        }
        normalPresets.forEach {
            UserPresetView(
                name = it.name,
                alternateTitles = it.alternateTitles,
                attributes = it.attributes,
                isSelected = remember { derivedStateOf { currentSelected.value == it } },
            )
            { selectBasic(it) }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdditionalPresetView(alternateTitles: Set<String>, attributes: Set<String>) {
    var expanded by remember { mutableStateOf(false) }
    if (alternateTitles.size + attributes.size > 0) {
        TextButton(
            onClick = { expanded = !expanded },
            modifier = Modifier.padding(horizontal = 4.dp)
        ) {
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
                if (list.isNotEmpty())
                    OutlinedCard(
                        modifier = Modifier.padding(horizontal = 5.dp),
                        shape = RoundedCornerShape(10.dp)
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
}

/**
 * Values needed to edit a preset
 */
data class EditingValues(
    val updateText: (String) -> Unit,
    val submit: () -> Unit,
    val cancel: () -> Unit,
    val delete: () -> Unit
)

/**
 * View to select a preset
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserPresetView(
    name: String,
    alternateTitles: Set<String>,
    attributes: Set<String>,
    isSelected: State<Boolean>,
    editingValues: EditingValues? = null,
    onSelect: () -> Unit
) {
    val inEdit = rememberSaveable { mutableStateOf(false) }
    Surface(
        tonalElevation = 30.dp,

        shape = RoundedCornerShape(25.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(25.dp)),
        onClick = onSelect,
    ) {
        Box(modifier = Modifier.padding(start = 15.dp, end = 15.dp, top = 15.dp, bottom = 15.dp)) {
            Column(
                Modifier.animateContentSize(),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                if (isSelected.value || (!inEdit.value && editingValues != null))
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp),
                        Arrangement.spacedBy(10.dp, Alignment.End),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (!inEdit.value && editingValues != null) {
                            IconButton(onClick = editingValues.delete) {
                                Icon(
                                    imageVector = FeatherIcons.Delete,
                                    contentDescription = "Delete"
                                )
                            }
                            IconButton(onClick = { inEdit.value = true }) {
                                Icon(imageVector = FeatherIcons.Edit2, contentDescription = "Edit")
                            }
                        }
                        Box(
                            modifier = Modifier.size(26.dp)
                        ) {
                            if (isSelected.value)
                                Box(
                                    modifier = Modifier
                                        .size(26.dp)
                                        .clip(CircleShape)
                                        .background(Material3.colorScheme.tertiary)
                                )
                        }
                    }
                if (!inEdit.value) {
                    Text(
                        name,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    editingValues!!
                    AligningBasicTextField(
                        value = name,
                        onValueChange = editingValues.updateText,
                        textStyle = Material3.typography.titleLarge,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                AdditionalPresetView(
                    alternateTitles = alternateTitles,
                    attributes = attributes
                )
                if (inEdit.value)
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.End)
                    ) {
                        editingValues!!
                        Button(onClick = { editingValues.submit(); inEdit.value = false }) {
                            Text(text = "Submit")
                        }
                        TextButton(onClick = { editingValues.cancel(); inEdit.value = false }) {
                            Text(text = "Cancel")
                        }
                    }
            }
        }
    }
}

val presetImitations = listOf(
    DisplayUserPreset(
        "Temerian King",
        setOf("Honorable Knight"),
        setOf("character", "north", "king"),
        ""
    ),
    DisplayUserPreset(
        "Hero's team member ha ha ha ha ha",
        setOf(),
        setOf("", "character", "hero's team member"),
        ""
    ),

    ) + listOf(
    DisplayUserPreset(
        "Hero's team member",
        setOf(),
        setOf("", "character", "hero's team member"),
        ""
    ),
    DisplayUserPreset(
        "Hero's team member",
        setOf(),
        setOf("", "character", "hero's team member"),
        ""
    ),
    DisplayUserPreset(
        "Hero's team member",
        setOf(),
        setOf("", "character", "hero's team member"),
        ""
    ),
    DisplayUserPreset(
        "Hero's team member",
        setOf(),
        setOf("", "character", "hero's team member"),
        ""
    ),
    DisplayUserPreset(
        "Hero's team member",
        setOf(),
        setOf("", "character", "hero's team member"),
        ""
    ),
)

@Preview(showSystemUi = true, device = Devices.PIXEL_3)
@Composable
fun PresetListViewPreview() {
    WroteTheme() {
        PresetListView(
            normalPresets = predefinedPresets,
//            userPresets = presetImitations,
            userPresets = listOf(),
            remember { mutableStateOf(presetImitations[1]) },
            {},
            {}, {}, {})
    }
}