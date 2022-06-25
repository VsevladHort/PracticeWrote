package com.dak.wrote.frontend.editor

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import com.dak.wrote.frontend.viewmodel.EditorViewModel
//import com.dak.wrote.utility.NormalAppBar

class EditorScreen(val editorViewModel: EditorViewModel) {

    fun goUp(inEdit: MutableState<Boolean>): Boolean {
        return if (inEdit.value) {
            inEdit.value = false
            false
        } else true
    }

    @Composable
    fun TitleBar(navigateUp: () -> Unit) {
        val noteState = editorViewModel.note.collectAsState()
        noteState.value.let { note ->
//            when (note) {
//                null -> NormalAppBar(onUp = navigateUp) { }
//                else -> NormalAppBar(onUp = { if (goUp(note.inEdit)) navigateUp() }) {
//
//                }
//            }
        }
    }

    @Composable
    fun Screen() {
        val noteSTate = editorViewModel.note.collectAsState()
        noteSTate.value
    }
}