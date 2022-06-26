package com.dak.wrote.frontend.editor

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dak.wrote.frontend.noteNavigation.IconButton
import com.dak.wrote.frontend.viewmodel.EditorViewModel
import compose.icons.FeatherIcons
import compose.icons.feathericons.*

private fun goUp(inEdit: MutableState<Boolean>): Boolean {
    return if (inEdit.value) {
        inEdit.value = false
        false
    } else true
}

@Composable
fun Screen(navigateUp: () -> Unit, editorViewModel: EditorViewModel = viewModel()) {
    val noteSTate = editorViewModel.note.collectAsState()
    noteSTate.value.let { note ->
        when (note) {
            null -> Box(modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator()
            }
            else -> {
                BackHandler(note.inEdit.value) {
                    note.inEdit.value = false
                    editorViewModel.updatePage(note)
                }
                Column() {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(
                            modifier = Modifier.wrapContentSize(Alignment.CenterStart),
                            imageVector = FeatherIcons.ArrowLeft,
                            description = "Back",
                            onClick = {
                                if (goUp(note.inEdit)) navigateUp()
                            }
                        )
                        Row(Modifier.wrapContentSize(Alignment.CenterEnd)) {
                            if (!note.inEdit.value) {
                                IconButton(
                                    imageVector = FeatherIcons.Save,
                                    description = "Save",
                                    onClick = { note.inEdit.value = false }
                                )
                                val expandedMenu = remember { mutableStateOf(false) }
                                IconButton(onClick = {
                                    expandedMenu.value = !expandedMenu.value
                                }) {
                                    Icon(
                                        imageVector = FeatherIcons.MoreVertical,
                                        contentDescription = "Open menu"
                                    )
                                }
                                DropdownMenu(
                                    expanded = expandedMenu.value,
                                    onDismissRequest = { expandedMenu.value = false }) {
                                    DropdownMenuItem(onClick = { editorViewModel.savePreset(note) }) {
                                        Text(text = "Save as a preset")
                                    }
                                }
                            } else {
                                IconButton(
                                    imageVector = FeatherIcons.Edit2,
                                    description = "Edit",
                                    onClick = { note.inEdit.value = true }
                                )
                            }
                        }
                    }
                    if (note.inEdit.value)
                        note.page.value.DrawNormal(editorViewModel = editorViewModel)
                    else
                        note.page.value.DrawEdit(editorViewModel = editorViewModel)
                }
            }
        }
    }
}
