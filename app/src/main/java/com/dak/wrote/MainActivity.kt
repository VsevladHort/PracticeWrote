package com.dak.wrote

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.dak.wrote.backend.contracts.database.EntryType
import com.dak.wrote.backend.contracts.entities.Book
import com.dak.wrote.backend.implementations.file_system_impl.dao.getDAO
import com.dak.wrote.backend.implementations.file_system_impl.database.getKeyGen
import com.dak.wrote.frontend.NavigationScreens
import com.dak.wrote.frontend.noteNavigation.NoteNavigation
import com.dak.wrote.frontend.bookNavigation.BookDisplay
import com.dak.wrote.frontend.controller.ControllerDisplay
import com.dak.wrote.frontend.noteNavigation.NavigationNote
import com.dak.wrote.ui.theme.WroteTheme

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
            BookDisplay(
                onBookClicked = { book ->
                    navigateToControllerWithBook(
                        controller,
                        book.uniqueKey,
                        book.title
                    )
                }
            )
        }
        composable(
            route = "${NavigationScreens.Controller.path}/{bookKey}/{bookTitle}",
            arguments = listOf(
                navArgument("bookKey") {
                    type = NavType.StringType
                },
                navArgument("bookTitle") {
                    type = NavType.StringType
                }
            )
        ) { entry ->
            val noteKey = (entry.arguments?.getString("bookKey") ?: "")
                .replace('\\', '/')
            val noteTitle = (entry.arguments?.getString("bookTitle") ?: "")
                .replace('\\', '/')
            ControllerDisplay(
                book = Book(noteKey, noteTitle),
            )
        }
    }
}

private fun navigateToControllerWithBook(
    navController: NavController,
    bookKey: String,
    bookTitle: String
) {
    navController.navigate(
        "${NavigationScreens.Controller.path}/" +
                "${bookKey.replace('/', '\\')}/" +  // won't work without replace
                bookTitle.replace('/', '\\')        // won't work without replace
    )
}