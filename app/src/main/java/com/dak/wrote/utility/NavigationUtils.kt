package com.dak.wrote.utility

import androidx.navigation.NavController
import com.dak.wrote.frontend.NavigationScreens

fun String.toNav() = replace('/', '\\')
fun String.fromNav() = replace('\\', '/')

fun navigateToSingleNoteNavigation(
    navController: NavController,
    prefix: String,
    noteKey: String,
    noteTitle: String
) {
    navController.navigate(
        prefix +
                "${NavigationScreens.NoteNavigation.path}/" +
                "${noteKey.toNav()}/" +
                noteTitle.toNav()
    )

}

fun navigateToControllerWithBook(
    navController: NavController,
    bookKey: String,
    bookTitle: String
) {
    navController.navigate(
        "${NavigationScreens.Controller.path}/" +
                "${bookKey.toNav()}/" +  // won't work without replace
                bookTitle.toNav()        // won't work without replace
    )
}
