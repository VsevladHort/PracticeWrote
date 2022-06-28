package com.dak.wrote.frontend.preset

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dak.wrote.frontend.viewmodel.NoteAdditionViewModel

@Composable
fun NoteAdditionScreen(
    confirmValue: (FullPreset) -> Unit,
    exit: () -> Unit,

    ) {
    val noteAdditionViewModel = viewModel<NoteAdditionViewModel>()

    noteAdditionViewModel.data.collectAsState().value.let { data ->
        when(data) {
            null -> CircularProgressIndicator()
            else -> Column() {
               PresetListView(
                   normalPresets = normalPresets,
                   userPresets = data.userPresets,
                   selectFull = { noteAdditionViewModel.setFull(data, it)},
                   selectUser = { }
               )
            }
        }
    }
    Column() {

        PresetListView(
            normalPresets,
            listOf(
                DisplayUserPreset(
                    "Hehe",
                    setOf("Honorable Knight"),
                    setOf("я", "character", "king"), ""
                )
            ), {}, {}
        )
    }
}

@Preview(device = Devices.PIXEL_3, widthDp = 400, heightDp = 900)
@Composable
fun NoteAdditionScreenPreview() {
    Dialog(onDismissRequest = { /*TODO*/ }) {
        NoteAdditionScreen(confirmValue = { }, {})
    }
}