package com.dak.wrote.frontend.preset

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog

@Composable
fun NoteAdditionScreen(
    confirmValue: (FullPreset) -> Unit,
    exit: () -> Unit,

) {
    Column() {

        PresetListView(
            normalPresets,
            listOf(
                DisplayUserPreset(
                    "Hehe",
                    setOf("Honorable Knight"),
                    setOf("я", "character", "king")
                )
            )
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