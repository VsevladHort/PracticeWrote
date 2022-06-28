package com.dak.wrote

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.dak.wrote.backend.contracts.database.EntryType
import com.dak.wrote.backend.contracts.entities.Book
import com.dak.wrote.backend.implementations.file_system_impl.dao.WroteDaoFileSystemImpl
import com.dak.wrote.backend.implementations.file_system_impl.dao.getDAO
import com.dak.wrote.backend.implementations.file_system_impl.database.UniqueKeyGeneratorFileSystemImpl
import com.dak.wrote.backend.implementations.file_system_impl.database.getKeyGen
import com.dak.wrote.frontend.NavigationScreens
import com.dak.wrote.frontend.noteNavigation.NoteNavigation
import com.dak.wrote.frontend.bookNavigation.BookNavigationScreen
import com.dak.wrote.frontend.noteNavigation.NavigationNote
import com.dak.wrote.ui.theme.WroteTheme
import kotlinx.coroutines.*
import java.io.File

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
/*
//        val dir = File(applicationContext.filesDir.absolutePath, "data")
//        dir.mkdir()


        val title = "First Book"
        runBlocking {
            val job = async {

                // First launch
//                val book = Book(
//                    getKeyGen(application)
//                        .getKey(null, EntryType.BOOK), title
//                )
//
//                getDAO(application).insertBook(
//                    book
//                )

                // Second launch
                val book = getDAO(application).getBooks().last()

                book
            }
            val book = job.await()
        }
*/
        setContent {
//          NoteNavigation(NavigationNote(book))
            ApplicationStart()
        }

    }
}

@Composable
fun ApplicationStart() {
    WroteTheme {
        Surface(color = MaterialTheme.colors.background) {
            val controller = rememberNavController()

            NavigationHost(controller)
        }
    }
}

var key = 'a'

@Composable
fun NavigationHost(
    controller: NavHostController,
    application: Application = LocalContext.current.applicationContext as Application
) {
    NavHost(
        navController = controller,
        startDestination = NavigationScreens.BookNavigation.path
    ) {
        composable(NavigationScreens.BookNavigation.path) {
            BookNavigationScreen(
                onBookClicked = { book ->
                    navigateToSingleNoteNavigation(
                        controller,
                        // tse prikol kaneshna
                        book.uniqueKey.replace('/', '\\'), // won't work without replace
                        book.title.replace('/', '\\') // won't work without replace
                    )
                },
                onCreateButton = {
                    /*TODO normal creation*/

                    //Create empty book
                    val book = Book(
                        getKeyGen(application)
                            .getKey(null, EntryType.BOOK), "Book ${key++}"
                    )

                    getDAO(application).insertBook(book)


                }
            )
        }
        composable(
            route = "${NavigationScreens.NoteNavigation.path}/{noteKey}/{noteTitle}",
            arguments = listOf(
                navArgument("noteKey") {
                    type = NavType.StringType
                },
                navArgument("noteTitle") {
                    type = NavType.StringType
                }
            )
        ) { entry ->
            val noteKey = (entry.arguments?.getString("noteKey") ?: "")
                .replace('\\', '/')
            val noteTitle = (entry.arguments?.getString("noteTitle") ?: "")
                .replace('\\', '/')
            NoteNavigation(
                initialNote = NavigationNote(noteKey, noteTitle),
                onEnterButton = { /*TODO*/ },
                onCreateButton = { /*TODO*/ },
                onDeleteBookButton = { controller.popBackStack() }
            )
        }
    }
}

private fun navigateToSingleNoteNavigation(
    navController: NavController,
    noteKey: String,
    noteTitle: String
) {
    navController.navigate("${NavigationScreens.NoteNavigation.path}/$noteKey/$noteTitle")
}