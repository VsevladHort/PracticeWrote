package com.dak.wrote.frontend.editor

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dak.wrote.backend.contracts.entities.BaseNote
import com.dak.wrote.frontend.AligningBasicTextField
import com.dak.wrote.frontend.viewmodel.EditorViewModel
import com.dak.wrote.utility.EasyKeyId
import compose.icons.FeatherIcons
import compose.icons.feathericons.Plus
import kotlinx.serialization.Serializable

class ItemListLayout(items: List<String>) : DataLayout() {
    private val easyKeyId = EasyKeyId()
    private val list =
        mutableStateListOf(*easyKeyId.map(items.map { mutableStateOf(it) }).toTypedArray());

    @Composable
    override fun DrawEdit(editorViewModel: EditorViewModel) {
        ListEdit(list = list, keyId = easyKeyId)
    }

    @Composable
    override fun DrawNormal(editorViewModel: EditorViewModel) {
        ListView(list.map { it.second.value })
    }

    override fun onSubmit(node: BaseNote) {
        TODO("Not yet implemented")
    }

    override fun toSerializable(): SerializableItemListLayout {
        return SerializableItemListLayout(list.map { it.second.value })
    }
}

@Serializable
class SerializableItemListLayout(val items: List<String>) : SerializableDataLayout() {
    override fun toDisplayable(): ItemListLayout {
        return ItemListLayout(items)
    }

}

@Composable
private fun ListEdit(keyId: EasyKeyId, list: SnapshotStateList<Pair<Int, MutableState<String>>>) {
    fun addItem() {
        list.add(keyId.take to mutableStateOf(""))
    }
    ListSurface {
        list.forEachIndexed { index, item ->
            val text = item.second
            Column {
                ItemNavigation(
                    if (index != 0) ({
                        list.moveUp(index)
                    }) else null,
                    if (index != list.lastIndex) ({
                        list.moveDown(index)
                    }) else null,
                    { list.removeAt(index) }, 40.dp, 35.dp, 1.dp)
                EditTextField(text = text)
                Divider()
            }
        }


        Box(Modifier.fillMaxWidth(), Alignment.TopEnd) {
            IconButton(onClick = { addItem() }) {
                Icon(
                    imageVector = FeatherIcons.Plus,
                    contentDescription = "Add item",
                    Modifier.size(45.dp),
                    tint = MaterialTheme.colors.primary,
                )
            }
        }
    }
}

@Composable
private fun EditTextField(text: MutableState<String>) {
    AligningBasicTextField(
        value = text.value, onValueChange = { text.value = it },
        textStyle = listEditStyle(),
        modifier = Modifier.fillMaxWidth()
    )

}

@Composable
private fun ListView(list: List<String>) {
    ListSurface {
        list.forEachIndexed { index, text ->
            Column {
                Text(text = text, style = listEditStyle())
//                Divider()
            }
        }
    }
}

@Composable
private fun listEditStyle() =
    MaterialTheme.typography.body1.copy(fontSize = 20.sp)

@Composable
private fun ListSurface(content: @Composable ColumnScope.() -> Unit) {
    Column(
        Modifier
            .fillMaxWidth()
            .wrapContentHeight()
//            .background(MaterialTheme.colors.primary, RoundedCornerShape(20.dp))
            .border(1.5.dp, MaterialTheme.colors.primaryVariant, RoundedCornerShape(20.dp))
            .padding(start = 30.dp, end = 30.dp, top = 25.dp, bottom = 25.dp),
        verticalArrangement = Arrangement.spacedBy(30.dp),
        content = content
    )
}


@Preview(device = Devices.PIXEL, heightDp = 600)
@Composable
fun ListEditPreview() {
    val keyId = EasyKeyId()
    val list = remember {
        mutableStateListOf(
            *keyId.map(
                listOf(
                    "я",
                    "не",
                    "Знаю"
                ).map { mutableStateOf(it) }).toTypedArray()
        )
    }
    LazyColumn(Modifier.fillMaxSize()) {
        item {
            Box(
                Modifier
                    .wrapContentSize()
                    .padding(10.dp)
            ) {

                ListEdit(list = list, keyId = keyId)

            }
        }
    }
}


@Preview(device = Devices.PIXEL, heightDp = 600)
@Composable
fun ListViewPreview() {
    val keyId = EasyKeyId()
    val list = remember {
        mutableStateListOf(
            *keyId.map(
                listOf(
                    "я",
                    "не",
                    "Знаю"
                ).map { mutableStateOf(it) }).toTypedArray()
        )
    }
    LazyColumn(Modifier.fillMaxSize()) {
        item {
            Box(
                Modifier
                    .wrapContentSize()
                    .padding(10.dp)
            ) {

                ListView(list = list.map { it.second.value })

            }
        }
    }
}

//@Preview
//@Composable
//fun ListPreview() {
//    val pageVM = viewModel<EditorViewModel>()
//    LaunchedEffect(key1 = Unit, block = {
//        val page =
//            SerializablePageLayout(
//                listOf(
//                    SerializableParagraphLayout(
//                        "Hello",
//                        listOf(
//                            SerializableItemListLayout(
//                                listOf(
//                                    "Яа",
//                                    "Не", "Знаю"
//                                )
//                            )
//                        )
//                    )
//                )
//            )
//        pageVM.page.field = page
//        pageVM.displayPage.value = page.toDisplayable()
//    })
//    pageVM.displayPage.value.let {
//        it.DrawEdit(pageVM)
//    }
//}
