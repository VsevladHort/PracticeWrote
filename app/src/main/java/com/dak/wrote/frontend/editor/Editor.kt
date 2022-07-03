package com.dak.wrote.frontend.editor

import android.app.Application
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dak.wrote.backend.contracts.entities.Attribute
import com.dak.wrote.frontend.noteNavigation.ColoredIconButton
import com.dak.wrote.frontend.viewmodel.EditorViewModel
import com.dak.wrote.frontend.viewmodel.EditorViewModelFactory
import com.dak.wrote.ui.theme.WroteTheme
import compose.icons.FeatherIcons
import compose.icons.feathericons.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

private fun goUp(inEdit: MutableState<Boolean>): Boolean {
    return if (inEdit.value) {
        inEdit.value = false
        false
    } else true
}

@Composable
fun EditorScreen(
    navigateUp: () -> Unit,
    presetUpdate : MutableSharedFlow<Unit>,
    selectedNote: String,
    editorViewModel: EditorViewModel = viewModel(
        factory = EditorViewModelFactory(
            selectedNote,
            presetUpdate,
            LocalContext.current.applicationContext as Application,
        )
    )
) {
    val noteSTate = editorViewModel.note.collectAsState()
    noteSTate.value.let { note ->
        when (note) {
            null -> Box(modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator()
            }
            else -> {
                EditorScreenImpl(
                    note = note,
                    updatePage = { editorViewModel.updatePage(note) },
                    navigateUp = navigateUp
                ) {
                    editorViewModel.savePreset(note)
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreenImpl(
    note: EditorViewModel.ObjectNote,
    updatePage: () -> Unit,
    navigateUp: () -> Unit,
    savePreset: () -> Unit
) {
    BackHandler(note.inEdit.value) {
        note.inEdit.value = false
        updatePage()
    }
    Scaffold(topBar = {
        SmallTopAppBar(modifier = Modifier.padding(horizontal = 16.dp), navigationIcon = {
            ColoredIconButton(
                modifier = Modifier.wrapContentSize(Alignment.CenterStart),
                imageVector = FeatherIcons.ArrowLeft,
                description = "Back",
                onClick = {
                    if (goUp(note.inEdit)) navigateUp()
                }
            )
        }, actions = {
            Row(Modifier.wrapContentSize(Alignment.CenterEnd)) {
                if (note.inEdit.value) {
                    ColoredIconButton(
                        imageVector = FeatherIcons.Save,
                        description = "Save",
                        onClick = { note.inEdit.value = false; updatePage() }
                    )
                } else {
                    ColoredIconButton(
                        imageVector = FeatherIcons.Edit2,
                        description = "Edit",
                        onClick = { note.inEdit.value = true }
                    )
                    val expandedMenu = remember { mutableStateOf(false) }
                    IconButton(
                        onClick = {
                        expandedMenu.value = !expandedMenu.value
                    },
                        Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = FeatherIcons.MoreVertical,
                            contentDescription = "Open menu"
                        )
                    }
                    DropdownMenu(
                        expanded = expandedMenu.value,
                        onDismissRequest = { expandedMenu.value = false }) {
                        DropdownMenuItem(
                            onClick = { savePreset(); expandedMenu.value = false },
                            text = {
                                Text(text = "Save as a preset")
                            })
                    }
                }
            }
        }, title = {})
//        {
//            Row(
//                horizontalArrangement = Arrangement.SpaceBetween,
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(start = 20.dp, end = 20.dp, top = 15.dp, bottom = 15.dp)
//            ) {
//            }
//        }
    }) {
        Box(modifier = Modifier.padding(it)) {
            if (note.inEdit.value)
                PageEdit(note.name, note.dAlternateNames, note.dAttributes, note.page.value)
            else
                PageView(
                    note.name.value,
                    note.dAlternateNames.map { it.next.value!! },
                    note.dAttributes.map { it.next.value!! },
                    note.page.value
                )
        }
    }
}

@Preview(showSystemUi = true, device = Devices.PIXEL_3)
@Composable
fun EditorScreenPreview() {
    WroteTheme() {
        EditorScreenImpl(
            note = remember {
                EditorViewModel.ObjectNote(
                    "",
                    mutableStateOf(""),
                    alternateTitles = setOf("King of the florals", "Horn of the tribe"),
                    attributes = setOf("character", "king", "floral").map { Attribute("", it) }
                        .toSet(),
                    sPage = SerializablePageLayout(
                        listOf(
                            SerializableParagraphLayout(
                                "History",
                                testDataLayout.map { it.toSerializable() }),
                            SerializableParagraphLayout(
                                "History",
                                testDataLayout.map { it.toSerializable() })
                        )
                    )
                )
            },
            updatePage = { },
            navigateUp = { }) {

        }
    }
}