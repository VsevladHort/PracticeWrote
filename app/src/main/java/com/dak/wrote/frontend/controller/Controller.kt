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
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
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
import com.dak.wrote.frontend.viewmodel.NoteNavigationViewModel
import com.dak.wrote.frontend.viewmodel.NoteNavigationViewModelFactory
import com.dak.wrote.utility.fromNav
import com.dak.wrote.utility.navigateToSingleNoteNavigation
import com.dak.wrote.utility.toNav
import compose.icons.FeatherIcons
import compose.icons.feathericons.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ControllerDisplay(
    book: Book,
    goUp: () -> Unit
) {
    val controller = rememberNavController()
    val showDrawer = rememberSaveable { mutableStateOf(true) }
    val controllerViewModel = viewModel<ControllerViewModel>()
    Scaffold(

        bottomBar = {
            if (showDrawer.value)
                ControllerBottomBar(
                    controllerViewModel,
                    navController = controller,
                    book = book
                )
        }
    ) { padding ->
        NavigationHost(
            controllerViewModel,
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
    controllerViewModel: ControllerViewModel,
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
                if (currentRoute != NavigationScreens.Glossary.path) {
                    navController.navigate(NavigationScreens.Glossary.path) {
                        popUpTo(navController.currentBackStackEntry!!.destination.route!!) {
                            inclusive = true
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
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
    controllerViewModel: ControllerViewModel,
    navController: NavHostController,
    book: Book,
    modifier: Modifier = Modifier,
    showDrawer: MutableState<Boolean>,
    goUp: () -> Unit
) {
    val notePrefix = stringResource(id = R.string.note_prefix)
    NavHost(
        navController = navController,
        modifier = modifier,
        startDestination =
        "$notePrefix${NavigationScreens.NoteNavigation.path}/{noteKey}?noteTitle={noteTitle}"
    ) {
        composable(
            route = notePrefix + "${NavigationScreens.NoteNavigation.path}/{noteKey}?noteTitle={noteTitle}",
            arguments = listOf(
                navArgument("noteKey") {
                    type = NavType.StringType
                },
                navArgument("noteTitle") {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) { entry ->
            showDrawer.value = true
            val noteKey = (entry.arguments?.getString("noteKey") ?: book.uniqueKey)
                .fromNav()
            val noteTitle = (entry.arguments?.getString("noteTitle") ?: book.title)
                .fromNav()
            val application = LocalContext.current.applicationContext as Application
            val factory = NoteNavigationViewModelFactory(
                application = application,
                NavigationNote(noteKey, noteTitle),
                controllerViewModel.updateNotes
            )

            val navigationViewModel: NoteNavigationViewModel =
                viewModel(key = null, factory = factory)

            entry.lifecycleScope.launchWhenStarted {
                if (controllerViewModel.checkNavigation.value) {
                    navigationViewModel.startupUpdate(
                        controllerViewModel.currentNote.value?.uniqueKey ?: noteKey,
                        controllerViewModel.currentNote.value?.title ?: noteTitle
                    )
                    controllerViewModel.checkNavigation.value = false
                }
            }
            NoteNavigation(
                navigationViewModel,
                controllerViewModel.updatePresets,
                modifier = Modifier,
                onEnterButton = {
                    navController.navigate("$notePrefix${NavigationScreens.Editor.path}/${it.toNav()}")
                },
                onDeleteBookButton = goUp,
            )
        }

        composable(
            NavigationScreens.Glossary.path,
        ) {
            showDrawer.value = true
            GlossaryScreen(
                book.uniqueKey,
                { goUp() },
                { id, name ->
                    println("Actually passed $id")
                    controllerViewModel.currentNote.value = NavigationNote(id, name)
                    controllerViewModel.checkNavigation.value = true
                    navigateToSingleNoteNavigation(navController, notePrefix, id, name, true)
                }, controllerViewModel.updateNotes,
            )
        }

        composable(notePrefix + NavigationScreens.Editor.path + "/{noteId}") {
            val id = it.arguments!!.getString("noteId")!!.fromNav()
            showDrawer.value = false
            EditorScreen(
                navigateUp = { controllerViewModel.callUpdate(); navController.popBackStack() },
                presetUpdate = controllerViewModel.updatePresets,
                selectedNote = id
            )
        }

    }
}