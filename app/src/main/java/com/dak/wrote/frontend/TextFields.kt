@file:OptIn(ExperimentalFoundationApi::class)

package com.dak.wrote.frontend

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.shape.ZeroCornerSize
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AligningTextField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions(),
    singleLine: Boolean = false,
    maxLines: Int = Int.MAX_VALUE,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape = MaterialTheme.shapes.small.copy(
        bottomEnd = ZeroCornerSize,
        bottomStart = ZeroCornerSize
    ),
    colors: TextFieldColors = TextFieldDefaults.textFieldColors()
) {
    val relocation = remember { BringIntoViewRequester() }
    val scope = rememberCoroutineScope()
    TextField(
        value = value,
        onValueChange = { onValueChange(it); scope.launch { delay(300); relocation.bringIntoView() } },
        modifier = modifier
            .bringIntoViewRequester(relocation)
            .onFocusEvent {
                if (it.isFocused) scope.launch { delay(300); relocation.bringIntoView() }
            },

        enabled,
        readOnly,
        textStyle,
        label,
        placeholder,
        leadingIcon,
        trailingIcon,
        isError,
        visualTransformation,
        keyboardOptions,
        keyboardActions,
        singleLine,
        maxLines,
        interactionSource, shape, colors
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AligningTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions(),
    singleLine: Boolean = false,
    maxLines: Int = Int.MAX_VALUE,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape =
        MaterialTheme.shapes.small.copy(bottomEnd = ZeroCornerSize, bottomStart = ZeroCornerSize),
    colors: TextFieldColors = TextFieldDefaults.textFieldColors()
) {
    val relocation = remember { BringIntoViewRequester() }
    val scope = rememberCoroutineScope()
    TextField(
        value = value,
        onValueChange = { onValueChange(it); scope.launch { delay(300); relocation.bringIntoView() } },
        modifier = modifier
            .bringIntoViewRequester(relocation)
            .onFocusEvent {
                if (it.isFocused) scope.launch { delay(300); relocation.bringIntoView() }
            },
        enabled,
        readOnly,
        textStyle,
        label,
        placeholder,
        leadingIcon,
        trailingIcon,
        isError,
        visualTransformation,
        keyboardOptions,
        keyboardActions,
        singleLine,
        maxLines,
        interactionSource,
        shape,
        colors
    )
}

@Composable
fun AligningOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions(),
    singleLine: Boolean = false,
    maxLines: Int = Int.MAX_VALUE,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape =
        MaterialTheme.shapes.small.copy(bottomEnd = ZeroCornerSize, bottomStart = ZeroCornerSize),
    colors: TextFieldColors = TextFieldDefaults.outlinedTextFieldColors()
) {
    val relocation = remember { BringIntoViewRequester() }
    val scope = rememberCoroutineScope()
    OutlinedTextField(
        value = value,
        onValueChange = { onValueChange(it); scope.launch { delay(300); relocation.bringIntoView() } },
        modifier = modifier
            .bringIntoViewRequester(relocation)
            .onFocusEvent {
                if (it.isFocused) scope.launch { delay(300); relocation.bringIntoView() }
            },
        enabled,
        readOnly,
        textStyle,
        label,
        placeholder,
        leadingIcon,
        trailingIcon,
        isError,
        visualTransformation,
        keyboardOptions,
        keyboardActions,
        singleLine,
        maxLines,
        interactionSource,
        shape,
        colors
    )
}

@Composable
fun AligningBasicTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = TextStyle.Default,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = false,
    maxLines: Int = Int.MAX_VALUE,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    cursorBrush: Brush = SolidColor(Color.Black),
    decorationBox: @Composable (innerTextField: @Composable () -> Unit) -> Unit =
        @Composable { innerTextField -> innerTextField() }
) {
    val relocation = remember { BringIntoViewRequester() }
    val scope = rememberCoroutineScope()
    BasicTextField(
        value = value,
        onValueChange = { onValueChange(it); scope.launch { delay(300); relocation.bringIntoView() } },
        modifier = modifier
            .bringIntoViewRequester(relocation)
            .onFocusEvent {
                if (it.isFocused) scope.launch { delay(300); relocation.bringIntoView() }
            },
        enabled,
        readOnly,
        textStyle,
        keyboardOptions,
        keyboardActions,
        singleLine,
        maxLines,
        visualTransformation,
        onTextLayout,
        interactionSource,
        cursorBrush,
        decorationBox
    )
}