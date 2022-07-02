package com.dak.wrote.frontend.controller

import android.app.Application
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
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
import com.dak.wrote.frontend.glossary.GlossaryScreen
import com.dak.wrote.frontend.noteNavigation.NavigationNote
import com.dak.wrote.frontend.noteNavigation.NoteNavigation
import com.dak.wrote.frontend.viewmodel.ControllerViewModel
import com.dak.wrote.utility.fromNav
import com.dak.wrote.utility.toNav
import compose.icons.FeatherIcons
import compose.icons.feathericons.Book
import compose.icons.feathericons.FileText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ControllerDisplay(
    book: Book,
    goUp: () -> Unit
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
            showDrawer,
            goUp
        )
    }
}


@Composable
fun ControllerBottomBar(
    navController: NavHostController,
    book: Book
) {
    val prefix = stringResource(id = R.string.note_prefix)
    NavigationBar {
        val backStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = backStackEntry?.destination?.route
        NavigationBarItem(
            selected = currentRoute == NavigationScreens.Glossary.path,
            onClick = {
                if (currentRoute != NavigationScreens.Glossary.path)
                    navController.navigate(NavigationScreens.Glossary.path) {
                        popUpTo(navController.currentBackStackEntry!!.destination.route!!) {
                            inclusive = true
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true

                    }
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


        NavigationBarItem(
            selected = currentRoute?.startsWith(prefix) ?: false,
            onClick = {
                if (currentRoute?.startsWith(prefix) != true) {
                    println("hohoho")
                    navigateToSingleNoteNavigation(
                        navController,
                        prefix,
                        book.uniqueKey.toNav(),
                        book.title.toNav(),
                        true
                    )
//                    navController.navigate(
//                        prefix +
//                                "${NavigationScreens.NoteNavigation.path}/" +
//                                "${book.uniqueKey.replace('/', '\\')}/" +
//                                book.title.replace('/', '\\')
//                    ) {
//                        println("Well")
////                        popUpTo(navController.graph.findStartDestination().id) {
////                            saveState = true
//////                            inclusive = true
////                        }
//////                        launchSingleTop = true
//                        restoreState = true
//                    }
                }
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
    goUp: () -> Unit
) {
    val application: Application = LocalContext.current.applicationContext as Application
    val notePrefix = stringResource(id = R.string.note_prefix)
    val controllerViewModel = viewModel<ControllerViewModel>()
    NavHost(
        navController = navController,
        modifier = modifier,
        startDestination =
//        NavigationScreens.Glossary.path
        "$notePrefix${NavigationScreens.NoteNavigation.path}/{noteKey}/{noteTitle}"

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
            val noteKey = (entry.arguments?.getString("noteKey") ?: book.uniqueKey)
                .replace('\\', '/')
            val noteTitle = (entry.arguments?.getString("noteTitle") ?: book.title)
                .replace('\\', '/')
            NoteNavigation(
                initialNote = NavigationNote(noteKey, noteTitle),
                onEnterButton = {
                    navController.navigate("$notePrefix${NavigationScreens.Editor.path}/${it.toNav()}")
                },
                onDeleteBookButton = { goUp() },
                controllerViewModel.update
            )
        }

        composable(
            NavigationScreens.Glossary.path,
        ) {
            showDrawer.value = true
            GlossaryScreen(
                book.uniqueKey,
                { navController.popBackStack() },
                { id, name ->
                    navigateToSingleNoteNavigation(navController, notePrefix, id, name, false)
                }, controllerViewModel.update)
            Text("Glossary")
        }

        composable(notePrefix + NavigationScreens.Editor.path + "/{noteId}") {
            val id = it.arguments!!.getString("noteId")!!.fromNav()
            showDrawer.value = false
            EditorScreen(navigateUp = { controllerViewModel.callUpdate(); navController.popBackStack() }, selectedNote = id)
        }

    }
}

private fun navigateToSingleNoteNavigation(
    navController: NavController,
    prefix: String,
    noteKey: String,
    noteTitle: String,
    restore: Boolean
) {
    navController.navigate(
        prefix +
                "${NavigationScreens.NoteNavigation.path}/" +
                "${noteKey.replace('/', '\\')}/" +
                noteTitle.replace('/', '\\')
    ) {
        println("Well")
        popUpTo(navController.currentBackStackEntry!!.destination.route!!) {
            saveState = true
            inclusive = true
        }
        launchSingleTop = true
        restoreState = restore
    }

}