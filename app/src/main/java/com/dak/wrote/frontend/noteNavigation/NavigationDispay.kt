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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dak.wrote.frontend.viewmodel.NoteNavigationViewModel
import com.dak.wrote.frontend.viewmodel.NoteNavigationViewModelFactory
import com.dak.wrote.ui.theme.SoftBlueTransparent
import com.dak.wrote.ui.theme.WroteTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import com.dak.wrote.backend.contracts.database.EntryType
import com.dak.wrote.backend.contracts.database.UniqueEntityKeyGenerator
import com.dak.wrote.backend.contracts.entities.*
import com.dak.wrote.backend.contracts.entities.constants.NoteType
import com.dak.wrote.backend.implementations.file_system_impl.database.UniqueKeyGeneratorFileSystemImpl
import com.dak.wrote.frontend.viewmodel.NavigationState
import com.dak.wrote.ui.theme.customColors
import compose.icons.FeatherIcons
import compose.icons.feathericons.ArrowLeft
import compose.icons.feathericons.Trash2
import kotlinx.coroutines.*
import java.io.File

//@Preview(showSystemUi = true)
//@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun NoteNavigation(dir: File, initialNote: NavigationNote) {
    WroteTheme {
        Surface(color = MaterialTheme.customColors.background) {
            val factory = NoteNavigationViewModelFactory(dir = dir)

            val navigationViewModel: NoteNavigationViewModel =
                viewModel(key = null, factory = factory)

            LaunchedEffect(rememberCoroutineScope()) {
                navigationViewModel.changeNote(initialNote)
            }

            NavigationDisplay(dir, initialNote, navigationViewModel)
        }
    }
}

var key = 0

@Composable
fun NavigationDisplay(
    dir: File,
    initialNote: NavigationNote,
//    factory: NoteNavigationViewModelFactory,
    navigationViewModel: NoteNavigationViewModel

) {
    //TODO nice logic that works with dao and stores everything in viewModel
    val coroutine = rememberCoroutineScope()

    val navigationState by navigationViewModel.navigationState.observeAsState()
    val state = // Does it really can be null?
        navigationState ?: NavigationState(
            NavigationNote("", ""),
            emptyList(),
            ArrayDeque()
        )

    MainNavigation(state.currentNote.title, state.paragraphs,
        onNoteTapped = { newNoteForNavigation -> navigationViewModel.changeNote(newNoteForNavigation) },
        backButtonEnabled = !state.parents.isEmpty(),
        onBackButton = { navigationViewModel.goBack() },
        onEnterButton = { /*TODO*/ },
        onCreateButton = {
            coroutine.launch {

                // Create empty note for testing
                val generator: UniqueEntityKeyGenerator =
                    UniqueKeyGeneratorFileSystemImpl.getInstance(dir)

                val child = generator.getKey(state.currentNote, EntryType.NOTE)

                val dummyNote: BaseNote = object : BaseNote {
                    override var title: String = "Note №${key++}"
                    override var alternateTitles: Set<String> = emptySet()
                    override var attributes: Set<Attribute> = emptySet()
                    override val type: NoteType = NoteType.PLAIN_TEXT
                    override fun generateSaveData(): ByteArray = byteArrayOf()
                    override fun loadSaveData(value: ByteArray) {}
                    override fun getIndexingData(): String = ""
                    override val uniqueKey: String = child
                }

                navigationViewModel.DAO.insetNote(
                    object : UniqueEntity {
                        override val uniqueKey: String = state.currentNote.uniqueKey
                    },
                    dummyNote
                )

                navigationViewModel.changeNote(state.currentNote, ignoreCurrent = true)
            }
        },
        onDeleteButton = {
            if (state.parents.isEmpty()) {
                //TODO delete entire Book
            } else {
                coroutine.launch {
                    val dummyNote: BaseNote = object : BaseNote {
                        override var title: String = ""
                        override var alternateTitles: Set<String> = emptySet()
                        override var attributes: Set<Attribute> = emptySet()
                        override val type: NoteType = NoteType.PLAIN_TEXT
                        override fun generateSaveData(): ByteArray = byteArrayOf()
                        override fun loadSaveData(value: ByteArray) {}
                        override fun getIndexingData(): String = ""
                        override val uniqueKey: String = state.currentNote.uniqueKey
                    }

                    navigationViewModel.DAO.deleteEntity(
                        entity = dummyNote
                    )


                    navigationViewModel.goBack()
                }
            }
        }
    )
}

@Composable
fun MainNavigation(
    title: String,
    paragraphs: List<NavigationNote>,
    onNoteTapped: (NavigationNote) -> Unit,
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
    paragraphs: List<NavigationNote>,
    onNoteTapped: (NavigationNote) -> Unit,
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
                    color = MaterialTheme.customColors.onBackground,
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

        items(paragraphs) { note ->
            GridButton(
                note = note,
                onNoteTapped = onNoteTapped
            )
        }
        item {
            Spacer(modifier = Modifier.height(100.dp))
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
            )
            .fillMaxWidth(),
//        horizontalArrangement = Arrangement.spacedBy(30.dp)
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Back button
        ColoredIconButton(
            imageVector = FeatherIcons.ArrowLeft,
            modifier = Modifier,
            description = "Back",
            buttonEnabled = backButtonEnabled,
            onClick = onBackButton
        )


        // Enter button
        NavigationButton(
            label = "Enter",
            modifier = Modifier,
//                .weight(0.9f),
//                .padding(
//                    vertical = 5.dp,
//                    horizontal = 50.dp
//                ),
            onButtonClicked = onEnterButton
        )

        //Delete button
        ColoredIconButton(
            imageVector = FeatherIcons.Trash2,
//            modifier = Modifier.weight(0.35f),
            description = "Delete",
            onClick = onDeleteButton
        )
    }
}

