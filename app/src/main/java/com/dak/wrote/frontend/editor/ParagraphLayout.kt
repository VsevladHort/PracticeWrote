package com.dak.wrote.frontend.editor

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dak.wrote.backend.contracts.entities.BaseNote
import com.dak.wrote.frontend.AligningBasicTextField
import com.dak.wrote.frontend.viewmodel.EditorViewModel
import compose.icons.FeatherIcons
import compose.icons.feathericons.Italic
import compose.icons.feathericons.List
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

class ParagraphLayout(title: String, column: List<DataLayout>) {
    private var title: MutableState<String> = mutableStateOf(title)
    private val columns = mutableStateListOf<DataLayout>().apply {
        addAll(column.map { it })
    }

    @Composable
    fun DrawEdit(editorViewModel: EditorViewModel) {
        ParagraphEdit(editorViewModel = editorViewModel, title = title, columns = columns)
    }

    @Composable
    fun DrawNormal(editorViewModel: EditorViewModel) {
//        ParagraphView()
//        Column(Modifier.wrapContentSize()) {
//            for (i in columns)
//                i.DrawNormal()
//        }
        ParagraphView(editorViewModel = editorViewModel, title = title.value, columns = columns)
    }

    fun onSubmit(node: BaseNote) {
    }

    fun toSerializable(): SerializableParagraphLayout {
        return SerializableParagraphLayout(title.value, columns.map { it.toSerializable() })
    }
}

@Serializable
class SerializableParagraphLayout(val title: String, val column: List<SerializableDataLayout>) {
    fun toDisplayable(): ParagraphLayout {
        return ParagraphLayout(title, column.map { it.toDisplayable() })
    }

}


@Composable
fun ParagraphEdit(
    editorViewModel: EditorViewModel,
    title: MutableState<String>,
    columns: SnapshotStateList<DataLayout>
) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, 30.dp)
    ) {
        Column(
            Modifier
                .fillMaxWidth()
        ) {
            Box(modifier = Modifier.padding(start = 10.dp)) {
                AligningBasicTextField(
                    value = title.value,
                    { title.value = it },
                    textStyle = MaterialTheme.typography.h4,
                )
            }
            Divider(
                thickness = 3.dp,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
            )
        }
        Column(
            Modifier
                .fillMaxWidth()
                .padding(top = 10.dp, start = 5.dp),
            Arrangement.spacedBy(10.dp)
        ) {
            columns.forEachIndexed { i, layout ->
                Column() {
                    DataLayoutAdditionBox(addLayout = { columns.add(i, it) })
                    Spacer(Modifier.height(10.dp))
                    ItemNavigation(
                        if (i != 0) ({
                            columns.moveUp(i)
                        }) else null,
                        if (i != columns.lastIndex) ({
                            columns.moveDown(i)
                        }) else null,
                        { columns.removeAt(i) },
                        45.dp,
                        30.dp
                    )
                    layout.DrawEdit(editorViewModel = editorViewModel)
                }
            }

        }
    }
}


@Composable
fun DataLayoutAdditionBox(addLayout: (DataLayout) -> Unit) {
    val expanded = remember { mutableStateOf(false) }
    LaunchedEffect(expanded.value) {
        if (expanded.value)
            launch {
                delay(2500)
                expanded.value = false
            }.start()
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {
        Box(
            modifier = Modifier
                .padding(10.dp)
                .defaultMinSize(minHeight = 20.dp)
                .fillMaxWidth()
                .clip(CircleShape)
                .clipToBounds()
                .clickable(
                    remember { MutableInteractionSource() },
                    rememberRipple(),
                    onClick = { expanded.value = true })
                .background(
                    color = MaterialTheme.colors.secondary,
                )
                .padding(horizontal = 25.dp, vertical = 10.dp)
        ) {
            @Composable
            fun item(imageVector: ImageVector, text: String, onClick: () -> Unit) {
                TextButton(onClick = onClick) {
                    Icon(imageVector = imageVector, contentDescription = null)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(text = text)
                }
            }
            if (expanded.value)
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    item(
                        imageVector = FeatherIcons.Italic,
                        text = "Text"
                    ) { addLayout(TextDataLayout("")) }
                    item(imageVector = FeatherIcons.List, text = "List") {
                        addLayout(
                            ItemListLayout(
                                emptyList()
                            )
                        )
                    }
                }
        }
    }
}

@Composable
fun ParagraphView(editorViewModel: EditorViewModel, title: String, columns: List<DataLayout>) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, 30.dp)
    ) {
        Column(
            Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.h4,
                modifier = Modifier.padding(start = 10.dp)
            )
            Divider(
                thickness = 3.dp,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
            )
        }
        Column(
            Modifier
                .fillMaxWidth()
                .padding(top = 10.dp, start = 5.dp)
        ) {
            columns.forEach {
                Box(Modifier.padding(10.dp)) {
                    it.DrawNormal(editorViewModel = editorViewModel)
                }
            }
        }
    }
}

@Preview(device = Devices.PIXEL, widthDp = 400, heightDp = 600)
@Composable
fun ParagraphViewPreview() {
    ParagraphView(
        viewModel(),
        title = remember { mutableStateOf("Hehe") }.value,
        columns = listOf(
            TextDataLayout("Hello"),
            ItemListLayout(listOf("Я", "Не", "Знаю"))
        )
    )
}

@Preview(device = Devices.PIXEL, widthDp = 400, heightDp = 600)
@Composable
fun ParagraphEditPreview() {
    ParagraphEdit(
        viewModel(),
        title = remember { mutableStateOf("Hehe") },
        columns = remember {
            mutableStateListOf(
                *testDataLayout.toTypedArray()
            )
        }
    )
}


val testDataLayout
    get() = listOf(
        TextDataLayout("Hello"),
        ItemListLayout(listOf("Я", "Не", "Знаю"))
    )

