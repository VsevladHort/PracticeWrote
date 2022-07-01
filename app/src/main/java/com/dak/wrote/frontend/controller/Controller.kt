package com.dak.wrote.frontend.controller

import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.TopAppBar
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
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
import com.dak.wrote.frontend.noteNavigation.ColoredIconButton
import com.dak.wrote.frontend.noteNavigation.NavigationNote
import com.dak.wrote.frontend.noteNavigation.NoteNavigation
import com.dak.wrote.ui.theme.Material3
import com.dak.wrote.utility.fromNav
import com.dak.wrote.utility.navigateToSingleNoteNavigation
import com.dak.wrote.utility.toNav
import compose.icons.FeatherIcons
import compose.icons.feathericons.ArrowLeft
import compose.icons.feathericons.Book
import compose.icons.feathericons.FileText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ControllerDisplay(
    book: Book,
    onBackToBookDisplay: () -> Unit
) {
    val controller = rememberNavController()
    val showDrawer = rememberSaveable { mutableStateOf(true) }
    Scaffold(
        topBar = {
            if (showDrawer.value)
                TopAppBar(
/*
                    title = {
                        Text(
//                            text = "Back to books",
                            text = book.title,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(),
                            color = Material3.colorScheme.onBackground,
                            style = Material3.typography.headlineMedium
                        )
                    },
                    navigationIcon = {
//                        Button(
//                            onClick = onBackToBookDisplay,
////                            shape = RoundedCornerShape(50.dp),
//                            modifier = Modifier.width(350.dp)
//                        ) {
//                            Row(
//                                modifier = Modifier.fillMaxWidth(),
//
//                                ) {
//                                Icon(
//                                    imageVector = FeatherIcons.ArrowLeft,
//                                    contentDescription = "Back"
//                                )
//                                Text(
//                                    text = "To book",
//                                    color = Material3.colorScheme.onPrimary,
//                                    fontSize = 24.sp,
//                                    style = Material3.typography.labelMedium
//                                )
//                            }
//                        }

//                        NavigationButton(
//                            label = "Back to books",
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .widthIn(min = 350.dp),
//                            onButtonClicked = onBackToBookDisplay
//                        )
                        ColoredIconButton(
                            onClick = onBackToBookDisplay,
                            imageVector = FeatherIcons.ArrowLeft,
                            description = "Back"
                        )
                    },
*/
                    backgroundColor = Material3.colorScheme.surface,
                    elevation = 10.dp
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        ColoredIconButton(
                            onClick = onBackToBookDisplay,
                            imageVector = FeatherIcons.ArrowLeft,
                            description = "Back"
                        )
                        Text(
                            text = book.title,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.wrapContentSize(),
                            color = Material3.colorScheme.onBackground,
                            style = Material3.typography.headlineMedium
                        )
                        Spacer(modifier = Modifier.size(45.dp))
                    }
                }
        },
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
    NavigationBar {
        val backStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = backStackEntry?.destination?.route
        NavigationBarItem(
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

        NavigationBarItem(
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
//    val application: Application = LocalContext.current.applicationContext as Application
    val notePrefix = stringResource(id = R.string.note_prefix)
    NavHost(
        navController = navController,
        modifier = modifier,
        startDestination =
//        NavigationScreens.Glossary.path
        NavigationScreens.StartPlaceholder.path
    ) {
        composable(NavigationScreens.StartPlaceholder.path) {
            LaunchedEffect(rememberCoroutineScope()) {
                navController.navigate(
                    notePrefix +
                            NavigationScreens.NoteNavigation.path +
                            "/${book.uniqueKey.toNav()}" +
                            "/${book.title.toNav()}"
                ) {
                    popUpTo(0) // delete start placeholder(this composable) from back stack
                }
            }
        }
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
                .fromNav()
//            val noteTitle = (entry.arguments?.getString("noteTitle") ?: "")
//                .fromNav()
            NoteNavigation(
//                initialNote = NavigationNote(noteKey, noteTitle),
                initialNote = NavigationNote(
                    noteKey,
                    ""
                ), // book title is in top bar, not in navigation
                onEnterButton = {
                    navController.navigate("$notePrefix${NavigationScreens.Editor.path}/${it.toNav()}")
                },
                onDeleteBookButton = { navController.popBackStack() }
            )
        }

        composable(NavigationScreens.Glossary.path) {
            showDrawer.value = true
//            GlossaryScreen()
            Text("Glossary")
        }

        composable(notePrefix + NavigationScreens.Editor.path + "/{noteId}") {
            val id = it.arguments!!.getString("noteId")!!.fromNav()
            EditorScreen(navigateUp = { navController.popBackStack() }, selectedNote = id)
            showDrawer.value = false
        }

    }
}