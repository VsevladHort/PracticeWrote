package com.dak.wrote.frontend.editor

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dak.wrote.frontend.AligningBasicTextField
import com.dak.wrote.ui.theme.Material3
import com.dak.wrote.ui.theme.WroteTheme
import com.google.accompanist.flowlayout.FlowMainAxisAlignment
import com.google.accompanist.flowlayout.FlowRow
import compose.icons.FeatherIcons
import compose.icons.feathericons.Bold
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
    fun DrawEdit() {
        ParagraphEdit(title = title, columns = columns)
    }

    @Composable
    fun DrawNormal() {
        ParagraphView(title = title.value, columns = columns)
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
                    textStyle = Material3.typography.headlineLarge,
                )
            }
            Divider(
                thickness = 3.dp,
                color = Material3.colorScheme.onSurface.copy(alpha = 0.6f),
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
                    layout.DrawEdit()
                }
            }
            DataLayoutAdditionBox(addLayout = { columns.add(it) })

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
                .clip(RoundedCornerShape(30.dp))
                .clipToBounds()
                .clickable(
                    remember { MutableInteractionSource() },
                    rememberRipple(),
                    onClick = { expanded.value = true })
                .background(
                    color = Material3.colorScheme.primaryContainer,
                )
                .padding(horizontal = 25.dp, vertical = 10.dp)
        ) {
            @Composable
            fun item(imageVector: ImageVector, text: String, onClick: () -> Unit) {
                OutlinedButton(onClick = onClick, modifier = Modifier.padding(horizontal = 5.dp)) {
                    Icon(imageVector = imageVector, contentDescription = null)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(text = text)
                }
            }
            if (expanded.value)
                FlowRow(mainAxisAlignment = FlowMainAxisAlignment.Center) {
                    item(
                        imageVector = FeatherIcons.Italic,
                        text = "Text"
                    ) { addLayout(TextDataLayout("")) }
                    item(imageVector = FeatherIcons.Bold, text = "Bold") {
                        addLayout(BoldDL(""))
                    }
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
fun ParagraphView(title: String, columns: List<DataLayout>) {
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
                style = Material3.typography.headlineLarge,
                modifier = Modifier.padding(start = 10.dp)
            )
            Divider(
                thickness = 3.dp,
                color = Material3.colorScheme.onSurface.copy(alpha = 0.6f),
            )
        }
        Column(
            Modifier
                .fillMaxWidth()
                .padding(top = 10.dp, start = 5.dp)
        ) {
            columns.forEach {
                Box(Modifier.padding(10.dp)) {
                    it.DrawNormal()
                }
            }
        }
    }
}

@Preview(device = Devices.PIXEL, widthDp = 400, heightDp = 600)
@Composable
fun ParagraphViewPreview() {
    WroteTheme() {
        ParagraphView(
            title = remember { mutableStateOf("Hehe") }.value,
            columns = listOf(
                TextDataLayout("Hello"),
                ItemListLayout(listOf("Я", "Не", "Знаю"))
            )
        )
    }
}

@Preview(device = Devices.PIXEL, widthDp = 400, heightDp = 600)
@Composable
fun ParagraphEditPreview() {
    WroteTheme() {
        ParagraphEdit(
            title = remember { mutableStateOf("Hehe") },
            columns = remember {
                mutableStateListOf(
                    *testDataLayout.toTypedArray()
                )
            }
        )
    }
}


val testDataLayout
    get() = listOf(
        TextDataLayout("Hello"),
        ItemListLayout(listOf("Я", "Не", "Знаю"))
    )
val testSDataLayout
    get() = listOf(
        STextDL("Hello"),
        SerializableItemListLayout(listOf("Я", "Не", "Знаю"))
    )
