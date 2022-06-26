package com.dak.wrote.frontend.glossary

import android.app.Application
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dak.wrote.frontend.AligningBasicTextField
import com.dak.wrote.frontend.AligningOutlinedTextField
import com.dak.wrote.frontend.AligningTextField
import com.dak.wrote.frontend.editor.AdditionalValuesView
import com.dak.wrote.frontend.noteNavigation.ColoredIconButton
import com.dak.wrote.frontend.viewmodel.GlossaryViewModel
import com.dak.wrote.frontend.viewmodel.GlossaryViewModelFactory
import compose.icons.FeatherIcons
import compose.icons.feathericons.*

@Composable
fun GlossaryScreen(currentBookId: String, back: () -> Unit, open: (name: String) -> Unit) {
    val viewModel = viewModel<GlossaryViewModel>(
        factory = GlossaryViewModelFactory(
            currentBookId,
            LocalContext.current.applicationContext as Application
        )
    )
    viewModel.data.collectAsState().value.let { data ->
        when (data) {
            null -> {
                CircularProgressIndicator(color = MaterialTheme.colors.primaryVariant)
            }
            else -> {
            }
        }
    }

}

@Composable
fun GlossaryScreenImpl(
    back: () -> Unit, open: (name: String) -> Unit,
    data: GlossaryViewModel.Data,
    searchAnew: () -> Unit
) {
    Column(Modifier.padding(10.dp)) {
        SuggestionSearch(
            textState = data.searchedName,
            attributesState = data.searchedAttributes,
            updateSearch = searchAnew,
            back
        )
        Divider(Modifier.padding(10.dp), MaterialTheme.colors.primaryVariant, 3.dp)
        data.foundNotes.value.let { foundNotes ->
            if (foundNotes != null)
                SuggestionList(suggestions = foundNotes, onClick = open)
            else
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Text(text = "Nothing to show", style = MaterialTheme.typography.h2)
                }
        }
    }
}

@Preview(device = Devices.PIXEL_3, showSystemUi = true)
@Composable
fun GlossaryScreenPreview() {
    GlossaryScreenImpl(back = {  }, open = {}, data = GlossaryViewModel.Data(
        sortedMapOf(), sortedMapOf(), sortedMapOf()
    )
    ) {

    }
}

@Preview
@Composable
fun SuggestionSearchPreview() {
    SuggestionSearch(
        textState = remember { mutableStateOf("") },
        attributesState = remember { mutableStateListOf(mutableStateOf("")) }, {}) { }
}

@Composable
fun SuggestionSearch(
    textState: MutableState<String>,
    attributesState: SnapshotStateList<MutableState<String>>,
    updateSearch: () -> Unit,
    back: () -> Unit
) {
    fun updateText(text: String) {
        textState.value = text
        updateSearch()
    }

    fun addAttribute() {
        attributesState.add(mutableStateOf(""))
    }

    fun deleteAttribute(i: Int) {
        if (attributesState[i].value.isNotBlank()) {
            attributesState.removeAt(i)
            updateSearch()
        } else
            attributesState.removeAt(i)
    }

    fun updateAttributeText(i: Int, text: String) {
        if (text.isNotBlank() || i == attributesState.lastIndex) {
            attributesState[i].value = text
            updateSearch()
        } else
            deleteAttribute(i)
    }

    LaunchedEffect(key1 = attributesState, block = {
        if (attributesState.size == 0)
            addAttribute()
    })

    Column(
        Modifier
            .fillMaxWidth()
            .padding(top = 5.dp, start = 10.dp, end = 10.dp)
    ) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ColoredIconButton(
                modifier = Modifier.wrapContentSize(Alignment.Center),
                imageVector = FeatherIcons.ArrowLeft,
                description = "Back",
                onClick = back
            )
            AligningOutlinedTextField(
                value = textState.value, onValueChange = ::updateText, label = {
                    Text(
                        text = "Text"
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.small.copy(CornerSize(20.dp))
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        Column(
            Modifier
                .defaultMinSize(minHeight = 100.dp)
                .fillMaxHeight(0.2f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            Arrangement.spacedBy(10.dp),

            ) {
            Text(
                text = "Attributes", fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                fontSize = 20.sp
            )

            @Composable
            fun AttributeDisplay(i: Int, text: String, onDelete: () -> Unit) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.animateContentSize()
                ) {
                    IconButton(onClick = onDelete) {
                        Icon(
                            imageVector = FeatherIcons.Trash2,
                            contentDescription = "Delete",
                            tint = MaterialTheme.colors.secondaryVariant
                        )
                    }
                    AligningOutlinedTextField(
                        value = text,
                        onValueChange = { updateAttributeText(i, it) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.small.copy(CornerSize(20.dp)),
                        singleLine = true
                    )
//                    Box(
//                        Modifier
//                            .fillMaxWidth()
//                            .border(
//                                1.dp,
//                                MaterialTheme.colors.onBackground,
//                                shape = RoundedCornerShape(20.dp)
//                            )
//                            .padding(7.dp)
//                    ) {
//                        AligningBasicTextField(
//                            value = text,
//                            onValueChange = { updateAttributeText(i, it) },
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .padding(7.dp),
//                            textStyle = TextStyle.Default.copy(fontSize = 20.sp),
//                            singleLine = true
//                        )
//                    }
                }
            }
            if (attributesState.size > 1)
                attributesState.forEachIndexed { i, state ->
                    AttributeDisplay(i = i, text = state.value) { deleteAttribute(i) }
                }
            else if (attributesState.size == 1) {
                val state = attributesState.single()
                AttributeDisplay(i = 0, text = state.value) { updateAttributeText(0, "") }
            }
        }
        TextButton(onClick = { addAttribute() }, Modifier.padding(horizontal = 15.dp)) {
            Icon(FeatherIcons.Plus, contentDescription = null)
            Spacer(modifier = Modifier.width(10.dp))
            Text(text = "Add attribute")
        }
    }
}

@Composable
fun SuggestionList(
    suggestions: List<GlossaryViewModel.PartialNote>,
    onClick: (id: String) -> Unit
) {
    Column(Modifier.padding(10.dp), Arrangement.spacedBy(15.dp)) {
        suggestions.forEach { suggestion ->
            Surface(
                shape = RoundedCornerShape(30.dp),
                elevation = 3.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(10.dp)) {

                    Text(
                        text = suggestion.title,
                        Modifier
                            .fillMaxWidth()
                            .clickable(
                                remember { MutableInteractionSource() },
                                rememberRipple(),
                                onClick = { onClick(suggestion.keyId) }
                            ),
                        style = MaterialTheme.typography.h5
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    AdditionalValuesView(
                        alternateNames = suggestion.alternateNames.toList(),
                        attributes = suggestion.attributes.map { at -> at.name }.toList()
                    )
                }
            }
        }
    }
}