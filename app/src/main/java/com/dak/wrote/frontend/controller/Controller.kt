package com.dak.wrote.frontend.controller

import android.app.Application
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.dak.wrote.R
import com.dak.wrote.backend.contracts.entities.Book
import com.dak.wrote.frontend.NavigationScreens
import com.dak.wrote.frontend.editor.EditorScreen
import com.dak.wrote.frontend.noteNavigation.NavigationNote
import com.dak.wrote.frontend.noteNavigation.NoteNavigation
import com.dak.wrote.utility.fromNav
import com.dak.wrote.utility.toNav
import compose.icons.FeatherIcons
import compose.icons.feathericons.Book
import compose.icons.feathericons.FileText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ControllerDisplay(
    book: Book
) {
    val controller = rememberNavController()
    val showDrawer = rememberSaveable { mutableStateOf(true) }
    Scaffold(
        bottomBar = {
            if (showDrawer.value)
                ControllerBottomBar(
                    navController = controller,
                    book = book
                )
        }
    ) { padding ->
        NavigationHost(
            navController = controller,
            book = book,
            modifier = Modifier.padding(padding),
            showDrawer
        )
    }
}


@Composable
fun ControllerBottomBar(
    navController: NavHostController,
    book: Book
) {
    val prefix = stringResource(id = R.string.note_prefix)
    BottomNavigation {
        val backStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = backStackEntry?.destination?.route
        BottomNavigationItem(
            selected = currentRoute == NavigationScreens.Glossary.path,
            onClick = {
                navController.navigate(NavigationScreens.Glossary.path)
            },
            icon = {
                Icon(
                    imageVector = FeatherIcons.Book,
                    contentDescription = stringResource(id = R.string.glossary_title)
                )
            },
            label = {
                Text(
                    text = stringResource(id = R.string.glossary_title),
                )
            }
        )

        BottomNavigationItem(
            selected = currentRoute?.startsWith(prefix) ?: false,
            onClick = {
                navigateToSingleNoteNavigation(
                    navController,
                    prefix,
                    book.uniqueKey,
                    book.title
                )
            },
            icon = {
                Icon(
                    imageVector = FeatherIcons.FileText,
                    contentDescription = stringResource(id = R.string.note_title)
                )
            },
            label = {
                Text(
                    text = stringResource(id = R.string.note_title),
                )
            }
        )
    }
}

@Composable
fun NavigationHost(
    navController: NavHostController,
    book: Book,
    modifier: Modifier = Modifier,
    showDrawer: MutableState<Boolean>,
) {
    val application: Application = LocalContext.current.applicationContext as Application
    val notePrefix = stringResource(id = R.string.note_prefix)
    NavHost(
        navController = navController,
        modifier = modifier,
        startDestination = NavigationScreens.Glossary.path
//        notePrefix +
//                NavigationScreens.NoteNavigation.path +
//                "/${book.uniqueKey.replace('/', '\\')}" +
//                "/${book.title.replace('/', '\\')}"
    ) {
        composable(
            route = notePrefix + "${NavigationScreens.NoteNavigation.path}/{noteKey}/{noteTitle}",
            arguments = listOf(
                navArgument("noteKey") {
                    type = NavType.StringType
                },
                navArgument("noteTitle") {
                    type = NavType.StringType
                }
            )
        ) { entry ->
            showDrawer.value = true
            val noteKey = (entry.arguments?.getString("noteKey") ?: "")
                .replace('\\', '/')
            val noteTitle = (entry.arguments?.getString("noteTitle") ?: "")
                .replace('\\', '/')
            NoteNavigation(
                initialNote = NavigationNote(noteKey, noteTitle),
                onEnterButton = {
                    navController.navigate("$notePrefix${NavigationScreens.Editor.path}/${it.toNav()}")
                },
                onDeleteBookButton = { navController.popBackStack() }
            )
        }

        composable(NavigationScreens.Glossary.path) {
//            GlossaryScreen()
            Text("Glossary")
        }

        composable(notePrefix + NavigationScreens.Editor.path + "/{noteId}") {
            val id = it.arguments!!.getString("noteId")!!.fromNav()
            showDrawer.value = false
            EditorScreen(navigateUp = { navController.popBackStack() }, selectedNote = id)
        }

    }
}

private fun navigateToSingleNoteNavigation(
    navController: NavController,
    prefix: String,
    noteKey: String,
    noteTitle: String
) {
    navController.navigate(
        prefix +
                "${NavigationScreens.NoteNavigation.path}/" +
                "${noteKey.replace('/', '\\')}/" +
                noteTitle.replace('/', '\\')
    )

}