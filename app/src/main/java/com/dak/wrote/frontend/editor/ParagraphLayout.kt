package com.dak.wrote.frontend.editor

import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dak.wrote.backend.contracts.entities.BaseNote
import com.dak.wrote.frontend.viewmodel.EditorViewModel
import kotlinx.serialization.Serializable

class ParagraphLayout(title: String, column: List<DataLayout>) {
    private var title: MutableState<String> = mutableStateOf(title)
    private val columns = mutableStateListOf<DataLayout>().apply {
        addAll(column.map { it })
    }

    @Composable
    fun DrawEdit(editorViewModel: EditorViewModel) {
        Column(Modifier.wrapContentSize()) {
            for (i in columns)
                i.DrawEdit(editorViewModel)
        }
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
    Column(Modifier.fillMaxWidth()) {
        Row() {
//            Text(text = )
        }
        Divider()
    }
}

@Composable
fun ParagraphView(editorViewModel: EditorViewModel, title: String, columns: List<DataLayout>) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, 5.dp)
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


var testDataLayout = listOf(
    TextDataLayout("Hello"),
    ItemListLayout(listOf("Я", "Не", "Знаю"))
)

