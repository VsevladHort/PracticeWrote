package com.dak.wrote.frontend.noteNavigation

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Text
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
    modifier: Modifier,
    onCreateButton: () -> Unit
) {

    Button(
        onClick = onCreateButton,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Material3.customColors.primary,
            contentColor = Material3.colorScheme.onPrimary
        ),
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
        shape = RoundedCornerShape(50.dp),
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

@Composable
fun ColoredIconButton(
    modifier: Modifier = Modifier,
    imageVector: ImageVector,
    description: String,
    buttonEnabled: Boolean = true,
    onClick: () -> Unit
) {
    Button(
        onClick = { onClick() },
        shape = CircleShape,
        modifier = modifier
            .size(45.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = if (buttonEnabled) Material3.customColors.primary else Material3.customColors.surface,
            contentColor = Material3.customColors.onPrimary,
            disabledContentColor = Material3.customColors.onSurface
        ),
        enabled = buttonEnabled
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = description,
            modifier = Modifier
                .scale(2.0f),
        )
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
        elevation = ButtonDefaults.elevation(
            defaultElevation = 0.dp
        ),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Material3.colorScheme.background,
            contentColor = Material3.colorScheme.onPrimary
        )
    ) {
        Text(
            text = text,
            color = Material3.colorScheme.primary,
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

    Button(
        onClick = { onNoteClicked(note) },
        colors = ButtonDefaults.buttonColors(
            backgroundColor = color,
            contentColor = color,
        ),
        shape = RoundedCornerShape(10),
        elevation = ButtonDefaults.elevation(
            defaultElevation = 10.dp
        ),
        interactionSource = interactionSource,
        modifier = Modifier
            .sizeIn(minWidth = 100.dp, minHeight = 100.dp)


    ) {
        Text(
            text = note.title,
            textAlign = TextAlign.Center,
            color = Material3.colorScheme.onBackground,
            style = Material3.typography.bodyMedium
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
            backgroundColor = color,
            contentColor = color,
        ),
        shape = RoundedCornerShape(25),
        elevation = ButtonDefaults.elevation(
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