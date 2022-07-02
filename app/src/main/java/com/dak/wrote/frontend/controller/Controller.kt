package com.dak.wrote.frontend.controller

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.TopAppBar
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
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
import com.dak.wrote.frontend.noteNavigation.ColoredIconButton
import com.dak.wrote.frontend.noteNavigation.NavigationNote
import com.dak.wrote.frontend.noteNavigation.NoteNavigation
import com.dak.wrote.frontend.viewmodel.ControllerViewModel
import com.dak.wrote.ui.theme.Material3
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationHost(
    navController: NavHostController,
    book: Book,
    modifier: Modifier = Modifier,
    showDrawer: MutableState<Boolean>,
    goUp: () -> Unit
) {
    val notePrefix = stringResource(id = R.string.note_prefix)
    val controllerViewModel = viewModel<ControllerViewModel>()
    NavHost(
        navController = navController,
        modifier = modifier,
        startDestination =
        "$notePrefix${NavigationScreens.NoteNavigation.path}/{noteKey}/{noteTitle}"
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
                .fromNav()
            val noteTitle = (entry.arguments?.getString("noteTitle") ?: book.title)
                .fromNav()
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
//                        Text(
//                            text = "Back to books",
//                            text = book.title,
//                            textAlign = TextAlign.Center,
//                            modifier = Modifier.fillMaxWidth(),
//                            color = Material3.colorScheme.onBackground,
//                            style = Material3.typography.headlineMedium
//                        )
                        },
                        navigationIcon = {
                            ColoredIconButton(
                                modifier = Modifier.padding(start = 16.dp),
                                onClick = goUp,
                                imageVector = FeatherIcons.ArrowLeft,
                                description = "Back"
                            )
                        },

                        backgroundColor = Material3.colorScheme.surface,
                        elevation = 10.dp
                    )
//                {
//                    Row(
//                        modifier = Modifier.fillMaxWidth(),
//                        verticalAlignment = Alignment.CenterVertically,
//                        horizontalArrangement = Arrangement.SpaceBetween
//                    ) {
//                        ColoredIconButton(
//                            onClick = goUp,
//                            imageVector = FeatherIcons.ArrowLeft,
//                            description = "Back"
//                        )
//                        Text(
//                            text = book.title,
//                            textAlign = TextAlign.Center,
//                            modifier = Modifier.wrapContentSize(),
//                            color = Material3.colorScheme.onBackground,
//                            style = Material3.typography.headlineMedium
//                        )
//                        Spacer(modifier = Modifier.size(45.dp))
//                    }
//                }
                },
            ) { padding ->
                NoteNavigation(
                    modifier = Modifier.padding(padding),
                    initialNote = NavigationNote(noteKey, noteTitle),
                    onEnterButton = {
                        navController.navigate("$notePrefix${NavigationScreens.Editor.path}/${it.toNav()}")
                    },
                    onDeleteBookButton = { goUp() },
                    controllerViewModel.update
                )
            }

        }

        composable(
            NavigationScreens.Glossary.path,
        ) {
            showDrawer.value = true
            GlossaryScreen(
                book.uniqueKey,
                { goUp() },
                { id, name ->
                    navigateToSingleNoteNavigation(navController, notePrefix, id, name, false)
                }, controllerViewModel.update
            )
            Text("Glossary")
        }

        composable(notePrefix + NavigationScreens.Editor.path + "/{noteId}") {
            val id = it.arguments!!.getString("noteId")!!.fromNav()
            showDrawer.value = false
            EditorScreen(
                navigateUp = { controllerViewModel.callUpdate(); navController.popBackStack() },
                selectedNote = id
            )
        }

    }
}