package com.dak.wrote.frontend.noteNavigation

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
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
import com.dak.wrote.ui.theme.customColors

@Composable
fun CreateButton(
    modifier: Modifier,
    onCreateButton: () -> Unit
) {
    Button(
        onClick = { onCreateButton() },
        colors = ButtonDefaults.buttonColors(
            backgroundColor = MaterialTheme.customColors.primaryVariant,
            contentColor = MaterialTheme.customColors.onPrimary
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
            color = MaterialTheme.customColors.onPrimary,
            fontSize = 26.sp,
            style = MaterialTheme.typography.button
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
        colors = ButtonDefaults.buttonColors(
            backgroundColor = MaterialTheme.customColors.primary,
            disabledBackgroundColor = MaterialTheme.customColors.secondary,
            contentColor = MaterialTheme.customColors.background
        )
    ) {
        Text(
            text = label,
            color = MaterialTheme.customColors.onPrimary,
            fontSize = 24.sp,
            style = MaterialTheme.typography.button
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
        enabled = buttonEnabled,
        shape = CircleShape,
//        border = BorderStroke(1.dp, Color.Transparent),
        modifier = modifier
            .size(45.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = MaterialTheme.customColors.primaryVariant,
            disabledBackgroundColor = MaterialTheme.customColors.secondary,
            contentColor = MaterialTheme.customColors.onPrimary
        )
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = description,
            modifier = Modifier
                .scale(2.0f),
            //.padding(top = 3.dp, end = 8.dp),
            tint = MaterialTheme.customColors.onPrimary
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

        colors = ButtonDefaults.buttonColors(
            backgroundColor = MaterialTheme.customColors.primary,
            contentColor = MaterialTheme.customColors.onPrimary
        )
    ) {
        Text(
            text = text,
            color = MaterialTheme.customColors.onPrimary,
            style = MaterialTheme.typography.button
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
        if (isPressed) MaterialTheme.customColors.primary else MaterialTheme.customColors.background

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
            color = MaterialTheme.customColors.onBackground,
            style = MaterialTheme.typography.subtitle1
        )
    }
}

@Composable
fun ColumnButton(
    book: Book,
    onBookClicked: (Book) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val color =
        if (isPressed) MaterialTheme.customColors.primary else MaterialTheme.customColors.background

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
            color = MaterialTheme.customColors.onBackground,
            style = MaterialTheme.typography.subtitle1
        )
    }
}