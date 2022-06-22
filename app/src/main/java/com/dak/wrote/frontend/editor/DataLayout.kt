package com.dak.wrote.frontend.editor

import android.widget.Space
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dak.wrote.backend.contracts.entities.BaseNote
import com.dak.wrote.frontend.viewmodel.EditorViewModel
import compose.icons.FeatherIcons
import compose.icons.feathericons.ChevronDown
import compose.icons.feathericons.ChevronUp
import kotlinx.serialization.Serializable


abstract class DataLayout {
    @Composable
    abstract fun DrawEdit(editorViewModel: EditorViewModel)

    @Composable
    abstract fun DrawNormal(editorViewModel: EditorViewModel)

    abstract fun onSubmit(node: BaseNote)

    abstract fun toSerializable(): SerializableDataLayout
}

@Serializable
sealed class SerializableDataLayout {
    abstract fun toDisplayable(): DataLayout
}

/**
 * Entry point for layout
 */
class PageLayout(paragraphs: List<SerializableParagraphLayout>) {
    private val paragraphs = mutableStateListOf<ParagraphLayout>().apply {
        addAll(paragraphs.map { it.toDisplayable() })
    }

    @Composable
    fun DrawEdit(editorViewModel: EditorViewModel) {
        Column(Modifier.fillMaxSize()) {
            for (i in paragraphs)
                i.DrawEdit(editorViewModel)
        }
    }

    @Composable
    fun DrawNormal(editorViewModel: EditorViewModel) {
        Column(Modifier.fillMaxSize()) {
            for (i in paragraphs)
                i.DrawNormal(editorViewModel)
        }
    }

    fun onSubmit(node: BaseNote) {
    }

    fun toSerializable(): SerializablePageLayout {
        return SerializablePageLayout(paragraphs.map { it.toSerializable() })
    }
}

@Serializable
class SerializablePageLayout(val paragraphs: List<SerializableParagraphLayout>) {
    fun toDisplayable(): PageLayout {
        return PageLayout(paragraphs)
    }
}

@Composable
private fun PageView(
    editorViewModel: EditorViewModel,
    name: String,
    alternateNames: List<String>,
    attributes: List<String>,
    parapraphs: List<ParagraphLayout>
) {
    Column(
        Modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 10.dp)
    ) {
        Column(Modifier.padding(vertical = 10.dp)) {
            Text(
                text = name,
                style = MaterialTheme.typography.h3.copy(fontWeight = FontWeight.Bold)
            )
        }
        AdditionalValuesView(alternateNames = alternateNames, attributes = attributes)
        Divider(thickness = 6.dp, color = MaterialTheme.colors.primaryVariant)
        Spacer(modifier = Modifier.height(20.dp))

        parapraphs.forEach {
            Surface(elevation = 3.dp, shape = RoundedCornerShape(10.dp)) {
                Column() {
                    it.DrawNormal(editorViewModel = editorViewModel)
                }
            }
        }
    }
}

@Composable
fun AdditionalValuesView(alternateNames: List<String>, attributes: List<String>) {
    var expanded by remember { mutableStateOf(true) }
    Surface(elevation = 2.dp, shape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp), modifier = Modifier.fillMaxWidth(), ) {

        Column(
            Modifier
                .padding(10.dp)
                .animateContentSize()
        ) {
            TextButton(onClick = { expanded = !expanded }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Additional", fontSize = 15.sp)
                    Icon(
                        imageVector = if (!expanded) FeatherIcons.ChevronDown else FeatherIcons.ChevronUp,
                        contentDescription = "Expand",
                        Modifier.padding(horizontal = 10.dp)
                    )
                }
            }
            if (expanded) {
                Column(Modifier.padding(10.dp)) {
                    Text(text = "Alternate names", fontWeight = FontWeight.Bold)
                    alternateNames.forEach {
                        Text(text = it)
                    }
                }
                Column(Modifier.padding(10.dp)) {
                    Text(text = "Attributes", fontWeight = FontWeight.Bold)
                    attributes.forEach {
                        Text(text = it)
                    }
                }
            }
        }
    }
}

@Preview(showSystemUi = true, device = Devices.PIXEL_3)
@Composable
fun PageViewPreview() {
    PageView(
        viewModel(),
        name = "Augustus Floral of the night",
        alternateNames = listOf("King of the florals", "Horn of the tribe"),
        attributes = listOf("character", "king", "floral"),
        parapraphs = listOf(ParagraphLayout("History", testDataLayout))
    )
}