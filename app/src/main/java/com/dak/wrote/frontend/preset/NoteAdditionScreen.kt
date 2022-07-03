package com.dak.wrote.frontend.preset

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dak.wrote.frontend.AligningOutlinedTextField
import com.dak.wrote.frontend.editor.mutStateListOf
import com.dak.wrote.frontend.viewmodel.NoteAdditionViewModel
import com.dak.wrote.frontend.viewmodel.NoteAdditionViewModelFactory
import com.dak.wrote.ui.theme.Material3
import com.dak.wrote.ui.theme.WroteTheme
import kotlinx.coroutines.flow.SharedFlow

data class NoteCreation(
    val name: String, val displayPreset: DisplayPreset,
    val fullPreset: FullPreset
)

@Composable
fun NoteAdditionScreen(
    confirmValue: (NoteCreation) -> Unit,
    exit: () -> Unit,
    updatePreset: SharedFlow<Unit>,
) {
    val noteAdditionViewModel = viewModel<NoteAdditionViewModel>(factory = NoteAdditionViewModelFactory(
        LocalContext.current.applicationContext as Application, updatePreset))

    noteAdditionViewModel.data.collectAsState().value.let { data ->
        when (data) {
            null -> CircularProgressIndicator()
            else -> NoteAdditionScreenImpl(
                data = data,
                selectUser = { noteAdditionViewModel.load(data, it) },
                selectBasic = { noteAdditionViewModel.setFull(data, it) },
                {
                    if (data.canCreate.value) confirmValue(
                        NoteCreation(
                            data.name.value,
                            data.currentSelected.value!!,
                            data.loadedSelected.value!!
                        )
                    )
                },
                exit,
                noteAdditionViewModel::updateName,
                { noteAdditionViewModel.remove(data, it) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteAdditionScreenImpl(
    data: NoteAdditionViewModel.Data,
    selectUser: (DisplayUserPreset) -> Unit,
    selectBasic: (BasicPreset) -> Unit,
    onCreate: () -> Unit,
    exit: () -> Unit,
    updateUserPreset: (DisplayUserPreset) -> Unit,
    delete: (Int) -> Unit
) {
    val focusManager = LocalFocusManager.current


    Box(Modifier.imePadding()) {
        Box(modifier = Modifier.padding(start = 0.dp, end = 0.dp, top = 50.dp, bottom = 60.dp)) {
            ElevatedCard(shape = RoundedCornerShape(10.dp)) {
                Scaffold(
                    Modifier
                        .fillMaxSize(),
                    bottomBar = {
                        Surface(tonalElevation = 40.dp) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp, 5.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.End)
                            ) {
                                TextButton(onClick = exit) {
                                    Text(text = "Cancel")
                                }
                                Button(onClick = onCreate, enabled = data.canCreate.value) {
                                    Text(text = "Create")
                                }
                            }
                        }
                    },
                    topBar = {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp, 20.dp)
                        ) {
                            Column {
                                AligningOutlinedTextField(
                                    value = data.name.value,
                                    onValueChange = data.name.component2(),
                                    modifier = Modifier.fillMaxWidth(),
                                    label = {
                                        Text(
                                            text = "Name",
                                            style = Material3.typography.titleLarge
                                        )
                                    },
                                    keyboardOptions = KeyboardOptions.Default.copy(
                                        imeAction = ImeAction.Done
                                    ),
                                    keyboardActions = KeyboardActions(
                                        onDone = { focusManager.clearFocus() }
                                    ),
                                    singleLine = true,
                                    textStyle = Material3.typography.titleLarge,
                                )
                            }
                        }
                    }
                ) {
                    Box(
                        Modifier
                            .padding(it)
                            .fillMaxSize()
                    ) {
                        PresetListView(
                            normalPresets = predefinedPresets,
                            userPresets = data.userPresets,
                            currentSelected = data.currentSelected,
                            updateUserPreset,
                            delete,
                            selectBasic = selectBasic,
                            selectUser = selectUser,
                        )
                    }
                }
            }
        }
    }
}

@Preview(device = Devices.PIXEL_3, showSystemUi = true)
@Composable
fun NoteAdditionScreenPreview() {
    WroteTheme(useDarkTheme = false) {
        Dialog(onDismissRequest = { /*TODO*/ }) {
            NoteAdditionScreenImpl(
                NoteAdditionViewModel.Data(mutStateListOf(presetImitations), null),
                {},
                {},
                {},
                {},
                {},
                {})
        }
    }
}