package com.dak.wrote.frontend.noteNavigation

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import compose.icons.feathericons.ArrowLeft
import compose.icons.feathericons.Trash2
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.runBlocking

@Composable
fun NoteNavigation(
    navigationViewModel: NoteNavigationViewModel,
    modifier: Modifier = Modifier,
    onEnterButton: (String) -> Unit,
    onDeleteBookButton: () -> Unit,
) {
    WroteTheme {
        Surface(
            modifier = modifier,
            color = Material3.colorScheme.background
        ) {
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
                onBackToBookDisplay = onDeleteBookButton
            )
        }
    }
}

@Composable
fun NavigationDisplay(
    navigationViewModel: NoteNavigationViewModel,
    onEnterButton: (String) -> Unit,
    onBackToBookDisplay: () -> Unit
) {
    val navigationState by navigationViewModel.navigationState.observeAsState()
    val state = navigationState ?: NavigationState()

    NavigationAndTopBar(
        state.currentNote.title,
        state.paragraphs,
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
                    onBackToBookDisplay()
            }
        },
        onBackToBookDisplay = onBackToBookDisplay
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationAndTopBar(
    title: String,
    paragraphs: List<NavigationNote>,
    onNoteClicked: (NavigationNote) -> Unit,
    backButtonEnabled: Boolean,
    onBackButton: () -> Unit,
    onEnterButton: () -> Unit,
    onCreateButton: (NoteCreation) -> Unit,
    onDeleteButton: () -> Unit,
    onBackToBookDisplay: () -> Unit,
) {
    val openDeleteDialog = remember { mutableStateOf(false) }

    if (openDeleteDialog.value)
        DeleteDialog(
            title = title,
            onCloseDialog = { openDeleteDialog.value = false },
            onDeleteButton = onDeleteButton
        )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
//                modifier = Modifier.padding(horizontal = 16.dp),
                title = {
//                    Text(
//                        text = title,
//                        textAlign = TextAlign.Center,
//                        modifier = Modifier
//                            .padding(horizontal = 24.dp),
//                        color = Material3.colorScheme.onBackground,
//                        style = Material3.typography.displaySmall
//                    )
                },
                navigationIcon = {
                    ColoredIconButton(
                        modifier = Modifier.padding(start = 16.dp),
                        onClick = onBackToBookDisplay,
                        imageVector = FeatherIcons.ArrowLeft,
                        description = "Back"
                    )
                },
                actions = {
                    ColoredIconButton(
                        modifier = Modifier.padding(end = 16.dp),
                        onClick = { openDeleteDialog.value = true },
                        imageVector = FeatherIcons.Trash2,
                        description = "Delete",

                        )
                },
//                backgroundColor = Material3.colorScheme.background,
            )
        },
    ) { padding ->
        MainNavigation(
            title = title,
            modifier = Modifier.padding(padding),
            paragraphs = paragraphs,
            onNoteClicked = onNoteClicked,
            backButtonEnabled = backButtonEnabled,
            onBackButton = onBackButton,
            onEnterButton = onEnterButton,
            onCreateButton = onCreateButton,
        )
    }
}

@Composable
fun MainNavigation(
    title: String,
    modifier: Modifier = Modifier,
    paragraphs: List<NavigationNote>,
    onNoteClicked: (NavigationNote) -> Unit,
    backButtonEnabled: Boolean,
    onBackButton: () -> Unit,
    onEnterButton: () -> Unit,
    onCreateButton: (NoteCreation) -> Unit,
) {
    val createDialog = remember { mutableStateOf(false) }
    if (createDialog.value)
        Dialog(onDismissRequest = { createDialog.value = false }, DialogProperties()) {
            NoteAdditionScreen(
                confirmValue = { onCreateButton(it); createDialog.value = false },
                exit = { createDialog.value = false })
        }

    Column(
        modifier = modifier
    ) {
        Divider(color = Material3.colorScheme.primary, thickness = 3.dp)

        val gridModifier = Modifier
            .weight(1f)
            .fillMaxWidth()
        Box(modifier = gridModifier) {
            NoteWithParagraphs(
                title = title,
                modifier = gridModifier,
                paragraphs = paragraphs,
                onNoteClicked = onNoteClicked,
                backButtonEnabled = backButtonEnabled,
                onBackButton = onBackButton,
                onEnterButton = onEnterButton,
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
    title: String,
    modifier: Modifier,
    paragraphs: List<NavigationNote>,
    onNoteClicked: (NavigationNote) -> Unit,
    backButtonEnabled: Boolean,
    onBackButton: () -> Unit,
    onEnterButton: () -> Unit,
) {
//    val openDeleteDialog = remember { mutableStateOf(false) }
//
//    if (openDeleteDialog.value)
//        DeleteDialog(
//            title = title,
//            onCloseDialog = { openDeleteDialog.value = false },
//            onDeleteButton = onDeleteButton
//        )


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
                    onBackButton = onBackButton,
                    buttonsEnabled = backButtonEnabled,
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
                style = Material3.typography.bodyLarge
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
            TextButton(
                onClick = onCloseDialog
            ) {
                Text(
                    text = stringResource(id = R.string.cancel),
                    color = Material3.colorScheme.primary,
                    fontSize = 20.sp,
                    style = Material3.typography.labelMedium
                )
            }
        }
    )
}

@Composable
private fun NavigationButtons(
    buttonsEnabled: Boolean,
    onBackButton: () -> Unit,
    onEnterButton: () -> Unit
) {

/*
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

*/

    Row(
        modifier = Modifier
            .padding(20.dp)
            .fillMaxSize(),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        // Back button
        NavigationButton(
            label = "Back",
            buttonEnabled = buttonsEnabled,
            modifier = Modifier.weight(1f),
            onButtonClicked = onBackButton
        )

        Spacer(modifier = Modifier.weight(0.4f))

        // Enter button
        NavigationButton(
            label = "Enter",
            buttonEnabled = buttonsEnabled,
            modifier = Modifier.weight(1f),
            onButtonClicked = onEnterButton
        )
    }
}

