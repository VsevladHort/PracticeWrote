package com.dak.wrote.frontend.bookNavigation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dak.wrote.R
import com.dak.wrote.backend.contracts.entities.Book
import com.dak.wrote.frontend.noteNavigation.ColumnButton
import com.dak.wrote.frontend.noteNavigation.CreateButton
import com.dak.wrote.frontend.viewmodel.BookNavigationViewModel
import com.dak.wrote.ui.theme.Material3

/**
 * Creates a display to view user books
 */
@Composable
fun BookDisplay(
    onBookClicked: (Book) -> Unit,
    booksViewModel: BookNavigationViewModel = viewModel()
) {
    val books by booksViewModel.bookState.observeAsState()

    val addingBook = remember { mutableStateOf(false) }

    if (addingBook.value)
        BookAdditionDialog(exit = { addingBook.value = false }, submit = {
            val book = it.ifBlank { "Plain Book" }
            booksViewModel.createBook(book)
            addingBook.value = false
        })
    BookNavigation(
        title = stringResource(id = R.string.welcome),
        books = books ?: emptyList(),
        onBookClicked = onBookClicked,
        onCreateButton = { addingBook.value = true }
    )
}

/**
 * Shows book column and a button to create a book
 */
@Composable
fun BookNavigation(
    title: String,
    books: List<Book>,
    onBookClicked: (Book) -> Unit,
    onCreateButton: () -> Unit
) {
    Column {
        Divider(color = Material3.colorScheme.primary, thickness = 3.dp)


        val modifier = Modifier
            .weight(1f)
            .fillMaxWidth()
        Box(modifier = modifier) {
            BooksColumn(
                modifier = modifier,
                title = title,
                books = books,
                onBookClicked = onBookClicked
            )
            CreateButton(
                modifier = Modifier.align(Alignment.BottomCenter),
                onCreateButton = onCreateButton
            )
        }
    }
}

/**
 * Displays user books
 */
@Composable
fun BooksColumn(
    modifier: Modifier,
    title: String,
    books: List<Book>,
    onBookClicked: (Book) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(
            horizontal = 18.dp,
            vertical = 6.dp
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
    ) {
        item {
            Text(
                text = title,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                color = Material3.colorScheme.onBackground,
                style = Material3.typography.headlineMedium
            )
        }
        item {
            Divider(color = Material3.colorScheme.primary, thickness = 2.dp)
        }
        items(books) { book ->
            ColumnButton(
                book = book,
                onBookClicked = onBookClicked,
                modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            Spacer(modifier = Modifier.height(60.dp))
        }

    }
}
