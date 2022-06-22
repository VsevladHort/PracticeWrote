package com.dak.wrote.frontend

sealed class NavigationScreens(val path: String) {
    object BookNavigation : NavigationScreens("book_navigation")
    object Controller : NavigationScreens("controller")
    object NoteNavigation : NavigationScreens("note_navigation")
    object Editor : NavigationScreens("editor")
    object Reference : NavigationScreens("reference")
    object Glossary : NavigationScreens("glossary")
}
