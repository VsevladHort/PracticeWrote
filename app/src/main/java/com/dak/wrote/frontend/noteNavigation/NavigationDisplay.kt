package com.dak.wrote.frontend.noteNavigation

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.AlertDialog
import androidx.compose.material.Divider
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dak.wrote.R
import com.dak.wrote.frontend.preset.NoteAdditionScreen
import com.dak.wrote.frontend.preset.NoteCreation
import com.dak.wrote.frontend.viewmodel.NavigationState
import com.dak.wrote.frontend.viewmodel.NoteNavigationViewModel
import com.dak.wrote.frontend.viewmodel.NoteNavigationViewModelFactory
import com.dak.wrote.ui.theme.Material3
import com.dak.wrote.ui.theme.WroteTheme
import compose.icons.FeatherIcons
import compose.icons.feathericons.CornerLeftUp
import compose.icons.feathericons.Trash2
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.runBlocking

@Composable
fun NoteNavigation(
    modifier: Modifier = Modifier,
    initialNote: NavigationNote,
    onEnterButton: (String) -> Unit,
    onDeleteBookButton: () -> Unit,
    update: MutableSharedFlow<Unit>,
    application: Application = LocalContext.current.applicationContext as Application
) {
    WroteTheme {
        Surface(
            modifier = modifier,
            color = Material3.colorScheme.background
        ) {
            val factory = NoteNavigationViewModelFactory(
                application = application,
                initialNote,
                update
            )

            val navigationViewModel: NoteNavigationViewModel =
                viewModel(key = null, factory = factory)

//            val firstInit = rememberSaveable {
//                mutableStateOf(false)
//            }
//            if (!firstInit.value) {
//                navigationViewModel.changeNote(initialNote, true)
//                firstInit.value = true
//            }

            NavigationDisplay(
                navigationViewModel = navigationViewModel,
                onEnterButton = onEnterButton,
                onDeleteBookButton = onDeleteBookButton
            )
        }
    }
}

@Composable
fun NavigationDisplay(
    navigationViewModel: NoteNavigationViewModel,
    onEnterButton: (String) -> Unit,
    onDeleteBookButton: () -> Unit
) {
    val navigationState by navigationViewModel.navigationState.observeAsState()
    val state = navigationState ?: NavigationState()

    MainNavigation(
        state.currentNote.title, state.paragraphs,
        onNoteClicked = { newNoteForNavigation ->
            navigationViewModel.selectNote(
                newNoteForNavigation
            )
        },
        backButtonEnabled = !state.parents.isEmpty(),
        onBackButton = { navigationViewModel.goBackAsync() },
        onEnterButton = { onEnterButton(state.currentNote.uniqueKey) },
        onCreateButton = navigationViewModel::createNote,
        onDeleteButton = {
            runBlocking {
                val res = navigationViewModel.deleteAsync().await()
                if (res)
                    onDeleteBookButton()
            }
        }
    )
}

@Composable
fun MainNavigation(
    title: String,
    paragraphs: List<NavigationNote>,
    onNoteClicked: (NavigationNote) -> Unit,
    backButtonEnabled: Boolean,
    onBackButton: () -> Unit,
    onEnterButton: () -> Unit,
    onCreateButton: (NoteCreation) -> Unit,
    onDeleteButton: () -> Unit
) {
    val createDialog = remember { mutableStateOf(false) }
    if (createDialog.value)
        Dialog(onDismissRequest = { createDialog.value = false }, DialogProperties()) {
            NoteAdditionScreen(
                confirmValue = { onCreateButton(it); createDialog.value = false },
                exit = { createDialog.value = false })
        }

    Column {
        Divider(color = Material3.colorScheme.primary, thickness = 3.dp)

        val modifier = Modifier
            .weight(1f)
            .fillMaxWidth()
        Box(modifier = modifier) {
            NoteWithParagraphs(
                modifier = modifier,
                title = title,
                paragraphs = paragraphs,
                onNoteClicked = onNoteClicked,
                backButtonEnabled = backButtonEnabled,
                onBackButton = onBackButton,
                onEnterButton = onEnterButton,
                onDeleteButton = onDeleteButton
            )
            CreateButton(
                modifier = Modifier.align(Alignment.BottomCenter),
                onCreateButton = { createDialog.value = true }
            )
        }
    }
}

@Composable
fun NoteWithParagraphs(
    modifier: Modifier,
    title: String,
    paragraphs: List<NavigationNote>,
    onNoteClicked: (NavigationNote) -> Unit,
    backButtonEnabled: Boolean,
    onBackButton: () -> Unit,
    onEnterButton: () -> Unit,
    onDeleteButton: () -> Unit
) {
    val openDeleteDialog = remember { mutableStateOf(false) }

    if (openDeleteDialog.value)
        DeleteDialog(
            title = title,
            onCloseDialog = { openDeleteDialog.value = false },
            onDeleteButton = onDeleteButton
        )


    LazyVerticalGrid(
        columns = GridCells.Adaptive(120.dp),
        contentPadding = PaddingValues(
            horizontal = 18.dp,
            vertical = 6.dp
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
    ) {
        // Note title
        item(
            span = { GridItemSpan(maxLineSpan) }
        ) {
//            if (backButtonEnabled) // to ignore book title
            Text(
                text = title,
                textAlign = TextAlign.Center,
                color = Material3.colorScheme.onBackground,
                style = Material3.typography.displayMedium
            )
        }

        // Navigation buttons
        item(
            span = { GridItemSpan(maxLineSpan) },
        ) {
            Column {
                Divider(color = Material3.colorScheme.primary, thickness = 2.dp)
                NavigationButtons(
                    onDeleteButton = { openDeleteDialog.value = true },
                    onBackButton = onBackButton,
                    backButtonEnabled = backButtonEnabled,
                    onEnterButton = onEnterButton,
                )
                Divider(
                    color = Material3.colorScheme.primary, thickness = 2.dp,
                    modifier = Modifier.padding(bottom = 20.dp)
                )
            }
        }

        items(paragraphs) { note ->
            GridButton(
                note = note,
                onNoteClicked = onNoteClicked
            )
        }
        item(
            span = { GridItemSpan(maxLineSpan) },
        ) {
            Spacer(modifier = Modifier.height(60.dp))
        }

    }
}

@Composable
fun DeleteDialog(
    title: String,
    onCloseDialog: () -> Unit,
    onDeleteButton: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { onCloseDialog() },
        title = {
            Text(
                text = stringResource(id = R.string.delete_dialog_title),
                style = Material3.typography.titleLarge
            )
        },
        text = {
            Text(
                text = stringResource(id = R.string.delete_dialog_body, title),
                style = Material3.typography.bodyMedium
            )
        },
        confirmButton = {
            DialogButton(
                text = stringResource(id = R.string.delete),
                onClick = {
                    onDeleteButton()
                    onCloseDialog()
                }
            )
        },
        dismissButton = {
            DialogButton(
                text = stringResource(id = R.string.cancel),
                onClick = onCloseDialog
            )
        }
    )
}

@Composable
private fun NavigationButtons(
    onDeleteButton: () -> Unit,
    backButtonEnabled: Boolean,
    onBackButton: () -> Unit,
    onEnterButton: () -> Unit
) {

    Row(
        modifier = Modifier
            .padding(
                vertical = 20.dp,
                horizontal = 16.dp
            )
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Back button
        ColoredIconButton(
            imageVector = FeatherIcons.CornerLeftUp,
            modifier = Modifier,
            description = "Back",
            buttonEnabled = backButtonEnabled,
            onClick = onBackButton
        )


        // Enter button
        NavigationButton(
            label = "Enter",
            modifier = Modifier,
            buttonEnabled = backButtonEnabled,
            onButtonClicked = onEnterButton
        )

        //Delete button
        ColoredIconButton(
            imageVector = FeatherIcons.Trash2,
            description = "Delete",
            onClick = onDeleteButton
        )
    }
}

