package com.dak.wrote.frontend.noteNavigation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dak.wrote.backend.contracts.entities.Book
import com.dak.wrote.frontend.viewmodel.NoteNavigationViewModel
import com.dak.wrote.frontend.viewmodel.NoteNavigationViewModelFactory
import com.dak.wrote.ui.theme.SoftBlueTransparent
import com.dak.wrote.ui.theme.WroteTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import com.dak.wrote.frontend.viewmodel.NavigationState
import compose.icons.FeatherIcons
import compose.icons.feathericons.ArrowLeft
import compose.icons.feathericons.Trash2

val map = mapOf(
    "Characters" to List(30) { Book("Character №$it", "Character №$it") },
    "Places" to List(30) { Book("Place №$it", "Place №$it") },
    "All" to listOf(
        Book("Characters", "Characters"),
        Book("Places", "Places")
    )
)

fun parent(child: String): String? {
    map.forEach { entry ->
        if (entry.value.map { note -> note.uniqueKey }.contains(child))
            return entry.key
    }

    return null
}

@Preview(showSystemUi = true)
@Composable
fun PreviewNavigation() {
    WroteTheme {
        Surface(color = Color.White) {
            val uniqueKey = "All"
            val factory: NoteNavigationViewModelFactory = NoteNavigationViewModelFactory(
                initialNote = uniqueKey,
                paragraphs = map[uniqueKey] ?: emptyList(),
                hasParent = parent(uniqueKey) != null
            )
            val thisViewModel: NoteNavigationViewModel =
                viewModel(key = null, factory = factory)


            NavigationDisplay(uniqueKey, factory, thisViewModel)

        }
    }
}

@Composable
fun NavigationDisplay(
    uniqueKey: String,
    factory: NoteNavigationViewModelFactory,
    thisViewModel: NoteNavigationViewModel

) {
    //TODO nice logic that works with dao and stores everything in viewModel
//    var note by remember { mutableStateOf(uniqueKey) }
    //val children by remember { mutableStateOf() }

/*
    val factory = NoteNavigationViewModelFactory(
        initialNote = uniqueKey,
        paragraphs = map[uniqueKey] ?: emptyList(),
        hasParent = true
    )

    val thisViewModel: NoteNavigationViewModel = viewModel(key = null, factory = factory)
*/

    val navigationState by thisViewModel.navigationState.observeAsState()
    val state = navigationState ?: NavigationState("", emptyList(), false)
    MainNavigation(state.currentNote, state.paragraphs,
        onNoteTapped = { newNoteForNavigation -> thisViewModel.changeNote(newNoteForNavigation) },
        backButtonEnabled = state.hasParent,
        onBackButton = { parent(state.currentNote)?.let { thisViewModel.changeNote(it) } },
        onEnterButton = { /*TODO*/ },
        onCreateButton = { /*TODO*/ },
        onDeleteButton = { /*TODO*/ }
    )
}

@Composable
fun MainNavigation(
    title: String,
    paragraphs: List<Book>,
    onNoteTapped: (String) -> Unit,
    backButtonEnabled: Boolean,
    onBackButton: () -> Unit,
    onEnterButton: () -> Unit,
    onCreateButton: () -> Unit,
    onDeleteButton: () -> Unit
) {
    Column {
        Divider(color = SoftBlueTransparent, thickness = 3.dp)

        val modifier = Modifier
            .weight(1f)
            .fillMaxWidth()
        Box(modifier = modifier) {
            NoteWithParagraphs(
                modifier = modifier,
                title = title,
                paragraphs = paragraphs,
                onNoteTapped = onNoteTapped,
                backButtonEnabled = backButtonEnabled,
                onBackButton = onBackButton,
                onEnterButton = onEnterButton,
                onDeleteButton = onDeleteButton
            )
            CreateButton(
                modifier = Modifier.align(Alignment.BottomCenter),
                onCreateButton = onCreateButton
            )
        }
    }
}

@Composable
fun NoteWithParagraphs(
    modifier: Modifier,
    title: String,
    paragraphs: List<Book>,
    onNoteTapped: (String) -> Unit,
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
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            )
            {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
/*                    IconButton(
                        modifier = modifier.wrapContentSize(Alignment.CenterStart),
                        imageVector = FeatherIcons.ArrowLeft,
                        description = "Back",
                        buttonEnabled = backButtonEnabled,
                        onClick = onBackButton
                    )
                    IconButton(
                        modifier = modifier.wrapContentSize(Alignment.CenterEnd),
                        imageVector = FeatherIcons.Trash2,
                        description = "Delete",
                        onClick = { openDeleteDialog.value = true }
                    )*/
                }
                Text(
                    text = title,
                    textAlign = TextAlign.Center,
                    color = Color.Black,
                    style = MaterialTheme.typography.h3
                )
            }
        }

        // Navigation buttons
        item(
            span = { GridItemSpan(maxLineSpan) },
        ) {
            Column {
                Divider(color = SoftBlueTransparent, thickness = 2.dp)
                NavigationButtons(
                    onDeleteButton = { openDeleteDialog.value = true },
                    onBackButton = onBackButton,
                    backButtonEnabled = backButtonEnabled,
                    onEnterButton = onEnterButton,
                )
                Divider(
                    color = SoftBlueTransparent, thickness = 2.dp,
                    modifier = Modifier.padding(bottom = 20.dp)
                )
            }
        }

        items(paragraphs) { book ->
            GridButton(
                title = book.title,
                onNoteTapped = onNoteTapped
            )
        }
        item {
            Spacer(modifier = Modifier.height(50.dp))
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
                text = "Delete note?",
                style = MaterialTheme.typography.h5
            )
        },
        text = {
            Text(
                text = "Are you sure you want to delete note \"$title\"?",
                style = MaterialTheme.typography.subtitle1
            )
        },
        confirmButton = {
            DialogButton(
                text = "Delete",
                onClick = {
                    onDeleteButton()
                    onCloseDialog()
                }
            )
        },
        dismissButton = {
            DialogButton(
                text = "Cancel",
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
            ),
        horizontalArrangement = Arrangement.spacedBy(40.dp)
    ) {
        // Back button
        IconButton(
            imageVector = FeatherIcons.ArrowLeft,
            description = "Back",
            buttonEnabled = backButtonEnabled,
            onClick = onBackButton
        )


        // Enter button
        NavigationButton(
            label = "Enter",
            modifier = Modifier
                .weight(0.9f),
//                .padding(
//                    vertical = 5.dp,
//                    horizontal = 50.dp
//                ),
            onButtonClicked = onEnterButton
        )

        //Delete button
        IconButton(
            modifier = Modifier,
            imageVector = FeatherIcons.Trash2,
            description = "Delete",
            onClick = onDeleteButton
        )
    }
}

