package com.dak.wrote.frontend.bookNavigation

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.dak.wrote.frontend.AligningOutlinedTextField
import com.dak.wrote.ui.theme.Material3
import com.dak.wrote.ui.theme.WroteTheme

/**
 * Creates a dialog to add a book
 */
@Composable
fun BookAdditionDialog(exit: () -> Unit, submit: (String) -> Unit) {
    val name = rememberSaveable { mutableStateOf("") }
    AlertDialog(onDismissRequest = exit,
        confirmButton = {
            Button(onClick = { submit(name.value) }) {
                Text(text = "Create")
            }
        },
        title = {
            Text(text = "Create a book")
        },
        text = {
            AligningOutlinedTextField(
                value = name.value,
                onValueChange = name.component2(),
                modifier = Modifier.fillMaxWidth(),
                textStyle = Material3.typography.titleLarge,
                singleLine = true
            )
        }, dismissButton = {
            TextButton(onClick = exit) {
                Text(text = "Cancel")
            }
        })
}

@Preview
@Composable
private fun DialogPreview() {
    WroteTheme() {
        BookAdditionDialog(exit = {}, submit = {})
    }
}