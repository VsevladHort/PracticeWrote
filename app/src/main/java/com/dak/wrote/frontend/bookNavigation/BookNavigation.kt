package com.dak.wrote.frontend.bookNavigation

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dak.wrote.backend.contracts.entities.Book
import com.dak.wrote.frontend.noteNavigation.ColumnButton
import com.dak.wrote.frontend.noteNavigation.CreateButton
import com.dak.wrote.frontend.noteNavigation.GridButton
import com.dak.wrote.frontend.viewmodel.BookNavigationViewModel
import com.dak.wrote.ui.theme.customColors

@Composable
fun BookNavigationScreen(
    application: Application = LocalContext.current.applicationContext as Application,
    booksViewModel: BookNavigationViewModel = viewModel()
) {
    val title = "Welcome to \"Wrote\""
    LaunchedEffect(rememberCoroutineScope()) {
        booksViewModel.updateBooks(application)
    }

    val books by booksViewModel.bookState.observeAsState()

    BookNavigation(
        title = title,
        books = books ?: emptyList(),
        onBookClicked = {},
        onCreateButton = {}
    )
}

@Composable
fun BookNavigation(
    title: String,
    books: List<Book>,
    onBookClicked: (Book) -> Unit,
    onCreateButton: () -> Unit
) {
    Column {
        Divider(color = MaterialTheme.customColors.primary, thickness = 3.dp)


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
                color = MaterialTheme.customColors.onBackground,
                style = MaterialTheme.typography.h4
            )
        }
        item {
            Divider(color = MaterialTheme.customColors.primary, thickness = 2.dp)
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
