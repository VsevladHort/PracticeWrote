package com.dak.wrote.frontend.noteNavigation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dak.wrote.ui.theme.SoftBlue
import com.dak.wrote.ui.theme.SoftBlueTransparent

@Composable
fun CreateButton(
    modifier: Modifier,
    onCreateButton: () -> Unit
) {
    Button(
        onClick = { onCreateButton() },
        colors = ButtonDefaults.buttonColors(
            backgroundColor = SoftBlue,
            contentColor = Color.White
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
            color = Color.White,
            fontSize = 26.sp,
            style = MaterialTheme.typography.button
        )
    }
}

@Composable
fun NavigationButton(
    label: String,
    modifier: Modifier,
    onButtonClicked: () -> Unit
) {
    Button(
        onClick = { onButtonClicked() },
        shape = RoundedCornerShape(50.dp),

        modifier = modifier,
        colors = ButtonDefaults.outlinedButtonColors(
            backgroundColor = SoftBlueTransparent,
            contentColor = Color.Red
        )
    ) {
        Text(
            text = label,
            color = Color.White,
            fontSize = 24.sp,
            style = MaterialTheme.typography.button
        )
    }
}

@Composable
fun GridButton(
    title: String,
    onNoteTapped: (String) -> Unit = {}
) {
    Button(
        onClick = { onNoteTapped(title) },
        modifier = Modifier
            .sizeIn(minWidth = 100.dp, minHeight = 100.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Color.White,
            contentColor = Color.Red
        ),
        shape = RoundedCornerShape(10),
        elevation = ButtonDefaults.elevation(
            defaultElevation = 10.dp
        )
    ) {
        Text(
            text = title,
            textAlign = TextAlign.Center,
            color = Color.Black,
            style = MaterialTheme.typography.subtitle1
        )
    }
}