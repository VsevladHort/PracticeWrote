package com.dak.wrote

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.dak.wrote.backend.contracts.entities.Book
import com.dak.wrote.frontend.NavigationScreens
import com.dak.wrote.frontend.bookNavigation.BookDisplay
import com.dak.wrote.frontend.controller.ControllerDisplay
import com.dak.wrote.ui.theme.WroteTheme
import com.dak.wrote.utility.fromNav
import com.dak.wrote.utility.navigateToControllerWithBook

/**
 * Main activity of the application
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ApplicationStart()
        }

    }
}

/**
 * Starting point of the display
 */
@Composable
fun ApplicationStart() {
    WroteTheme {
        Surface(color = MaterialTheme.colors.background) {
            val controller = rememberNavController()

            NavigationHost(controller)
        }
    }
}

/**
 * Navigation host to
 * move between book selection and note navigation
 */
@Composable
fun NavigationHost(
    controller: NavHostController
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
                .fromNav()
            val noteTitle = (entry.arguments?.getString("bookTitle") ?: "")
                .fromNav()
            ControllerDisplay(
                book = Book(noteKey, noteTitle),
                goUp = {
                    controller.navigate(NavigationScreens.BookNavigation.path) {
                        popUpTo(0)
                    }
                }
            )
        }
    }
}