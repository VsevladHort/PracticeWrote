package com.dak.wrote

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.dak.wrote.backend.contracts.database.EntryType
import com.dak.wrote.backend.contracts.entities.Book
import com.dak.wrote.backend.implementations.file_system_impl.dao.WroteDaoFileSystemImpl
import com.dak.wrote.backend.implementations.file_system_impl.database.UniqueKeyGeneratorFileSystemImpl
import com.dak.wrote.frontend.NavigationScreens
import com.dak.wrote.frontend.noteNavigation.NoteNavigation
import com.dak.wrote.frontend.bookNavigation.BookNavigationScreen
import com.dak.wrote.frontend.noteNavigation.NavigationNote
import com.dak.wrote.ui.theme.WroteTheme
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.File

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dir = File(applicationContext.filesDir.absolutePath, "data")
//        dir.mkdir()

//        val book = File(dir.absolutePath, "First Book")
//        book.mkdir()

        val title = "First Book"
        runBlocking {
            val job = async {

                // First lauch
                /*val book = Book(
                    UniqueKeyGeneratorFileSystemImpl.getInstance(dir)
                        .getKey(null, EntryType.BOOK), title
                )

                WroteDaoFileSystemImpl.getInstance(dir).insertBook(
                    book
                )*/

                // Second launch
                val book = WroteDaoFileSystemImpl.getInstance(dir).getBooks().last()

                book
            }
            val book = job.await()
            setContent {
                WroteTheme {
                    // A surface container using the 'background' color from the theme
                    Surface(color = MaterialTheme.colors.background) {
                        NoteNavigation(dir, NavigationNote(book))
                    }
                }
            }
        }

    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    WroteTheme {
        Greeting("Android")
    }
}


@Composable
fun ApplicationStart() {
    val controller = rememberNavController()
    NavHost(navController = controller, startDestination = NavigationScreens.BookNavigation.path) {
        composable(NavigationScreens.BookNavigation.path) {
            BookNavigationScreen()
        }
    }
}