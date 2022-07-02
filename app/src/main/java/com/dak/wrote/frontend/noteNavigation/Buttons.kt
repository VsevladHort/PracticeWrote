package com.dak.wrote.frontend.noteNavigation

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dak.wrote.backend.contracts.entities.Book
import com.dak.wrote.ui.theme.Material3
import com.dak.wrote.ui.theme.customColors

@Composable
fun CreateButton(
    modifier: Modifier = Modifier,
    onCreateButton: () -> Unit,
) {

    Button(
        onClick = onCreateButton,
//        colors =
//        ButtonDefaults.buttonColors(
//            containerColor = Material3.customColors.primary,
//            contentColor = Material3.colorScheme.onPrimary
//        ),
        modifier = modifier
            .fillMaxWidth()
            .padding(
                start = 16.dp,
                end = 16.dp,
                bottom = 8.dp,
            ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Text(
            text = "Create",
            textAlign = TextAlign.Center,
            color = Material3.colorScheme.onPrimary,
            fontSize = 26.sp,
            style = Material3.typography.labelMedium
        )
    }
}

@Composable
fun NavigationButton(
    label: String,
    modifier: Modifier = Modifier,
    buttonEnabled: Boolean = true,
    onButtonClicked: () -> Unit
) {
    Button(
        onClick = { onButtonClicked() },
//        shape = RoundedCornerShape(50.dp),
        enabled = buttonEnabled,
        modifier = modifier,
//        colors =
//        ButtonDefaults.buttonColors(
//            backgroundColor = SoftBlueTransparent,
//            disabledBackgroundColor = Color.LightGray,
//            contentColor = Color.White
//        )
    ) {
        Text(
            text = label,
            color = if (buttonEnabled) Material3.colorScheme.onPrimary else Material3.colorScheme.onSurface,
            fontSize = 24.sp,
            style = Material3.typography.labelMedium
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColoredIconButton(
    modifier: Modifier = Modifier,
    imageVector: ImageVector,
    description: String,
    buttonEnabled: Boolean = true,
    onClick: () -> Unit
) {
    FilledIconButton(onClick = onClick, modifier.size(45.dp), enabled = buttonEnabled) {
       Icon(imageVector = imageVector, contentDescription = description, modifier = Modifier.size(30.dp))
    }
}

@Composable
fun DialogButton(
    text: String,
    onClick: () -> Unit
) {
    Button(
        onClick = {
            onClick()
        },
        shape = RoundedCornerShape(50.dp),
    ) {
        Text(
            text = text,
            fontSize = 20.sp,
            style = Material3.typography.labelMedium
        )
    }
}

@Composable
fun GridButton(
    note: NavigationNote,
    onNoteClicked: (NavigationNote) -> Unit = {}
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val color =
        if (isPressed) Material3.colorScheme.primaryContainer else Material3.colorScheme.background

//    Button(
//        onClick = { onNoteClicked(note) },
//        colors = ButtonDefaults.buttonColors(
//            backgroundColor = color,
//            contentColor = color,
//        ),
//        shape = RoundedCornerShape(10),
//        elevation = ButtonDefaults.elevation(
//            defaultElevation = 10.dp
//        ),
//        interactionSource = interactionSource,
//        modifier = Modifier
//            .sizeIn(minWidth = 100.dp, minHeight = 100.dp)
//
//
//    ) {
//        Text(
//            text = note.title,
//            textAlign = TextAlign.Center,
//            color = Material3.colorScheme.onBackground,
//            style = Material3.typography.bodyMedium
//        )
//    }
    ElevatedButton(onClick = { onNoteClicked(note) }, modifier = Modifier.sizeIn(100.dp, 100.dp),
    shape = RoundedCornerShape(10.dp)) {
        Text(
            text = note.title,
            textAlign = TextAlign.Center,
            color = Material3.colorScheme.onBackground,
            style = Material3.typography.bodyMedium,
            fontSize = 26.sp
        )
    }
}

@Composable
fun ColumnButton(
    modifier: Modifier = Modifier,
    book: Book,
    onBookClicked: (Book) -> Unit = {}
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val color =
        if (isPressed) Material3.colorScheme.primaryContainer else Material3.colorScheme.background

    Button(
        onClick = { onBookClicked(book) },
        colors = ButtonDefaults.buttonColors(
            containerColor = color,
            contentColor = color,
        ),
        shape = RoundedCornerShape(25),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 10.dp
        ),
        interactionSource = interactionSource,
        modifier = modifier
            .sizeIn(minHeight = 50.dp)
    ) {
        Text(
            text = book.title,
            textAlign = TextAlign.Center,
            color = Material3.colorScheme.onBackground,
            style = Material3.typography.headlineSmall
        )
    }
}