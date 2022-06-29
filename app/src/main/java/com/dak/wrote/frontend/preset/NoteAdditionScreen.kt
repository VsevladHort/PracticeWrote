package com.dak.wrote.frontend.preset

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dak.wrote.frontend.AligningOutlinedTextField
import com.dak.wrote.frontend.AligningTextField
import com.dak.wrote.frontend.viewmodel.NoteAdditionViewModel
import com.dak.wrote.ui.theme.Material3
import com.dak.wrote.ui.theme.WroteTheme

data class NoteCreation(
    val name: String, val displayPreset: DisplayPreset,
    val fullPreset: FullPreset
)

@Composable
fun NoteAdditionScreen(
    currentId: String,
    confirmValue: (NoteCreation) -> Unit,
    exit: () -> Unit,
) {
    val noteAdditionViewModel = viewModel<NoteAdditionViewModel>()
    LaunchedEffect(currentId) { noteAdditionViewModel.passId(currentId) }

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
            )
//                Column() {
//
//                PresetListView(
//                    normalPresets = normalPresets,
//                    userPresets = data.userPresets,
//                    selectFull = { noteAdditionViewModel.setFull(data, it) },
//                    selectUser = { },
//                    currentSelected = data.currentSelected
//                )
//            }
        }
    }
    Column() {

//        PresetListView(
//            normalPresets,
//            listOf(
//                DisplayUserPreset(
//                    "Hehe",
//                    setOf("Honorable Knight"),
//                    setOf("я", "character", "king"), ""
//                )
//            ), {}, {}
//        )
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
) {
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
                            Button(onClick = onCreate, enabled = data.canCreate.value) {
                                Text(text = "Create")
                            }
                            TextButton(onClick = exit) {
                                Text(text = "Cancel")
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
                                singleLine = true,
                                textStyle = Material3.typography.titleLarge
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
                    Column(
                        Modifier
                            .verticalScroll(rememberScrollState())
                    ) {
                        PresetListView(
                            normalPresets = normalPresets,
                            userPresets = data.userPresets,
                            currentSelected = data.currentSelected,
                            selectBasic = selectBasic,
                            selectUser = selectUser
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
                NoteAdditionViewModel.Data(presetImitations, null),
                {},
                {},
                {},
                {})
        }
    }
}