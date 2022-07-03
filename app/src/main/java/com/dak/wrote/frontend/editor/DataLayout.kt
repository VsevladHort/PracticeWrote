package com.dak.wrote.frontend.editor

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.spring
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dak.wrote.backend.contracts.entities.BaseNote
import com.dak.wrote.frontend.AligningBasicTextField
import com.dak.wrote.frontend.viewmodel.EditorViewModel
import com.dak.wrote.frontend.viewmodel.UpdateHolder
import com.dak.wrote.ui.theme.Material3
import com.dak.wrote.ui.theme.Shapes
import com.dak.wrote.ui.theme.WroteTheme
import compose.icons.FeatherIcons
import compose.icons.feathericons.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable


abstract class DataLayout {
    @Composable
    abstract fun DrawEdit()

    @Composable
    abstract fun DrawNormal()

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
    fun DrawEdit() {
        paragraphs.forEachIndexed { index, paragraphLayout ->
            ParagraphAdditionBox {
                paragraphs.add(index, ParagraphLayout("", listOf()))
            }
            Surface(
                elevation = 3.dp,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.padding(vertical = 10.dp)
            ) {
                Column() {
                    ItemNavigation(
                        if (index != 0) ({
                            paragraphs.moveUp(index)
                        }) else null,
                        if (index != paragraphs.lastIndex) ({
                            paragraphs.moveDown(index)
                        }) else null,
                        { paragraphs.removeAt(index) },
                        50.dp,
                        40.dp
                    )
                    paragraphLayout.DrawEdit()
                }
            }
        }
        ParagraphAdditionBox {
            paragraphs.add(ParagraphLayout("", listOf()))
        }
    }

    @Composable
    fun DrawNormal() {
        paragraphs.forEach { paragraphLayout ->
            Surface(
                elevation = 3.dp,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.padding(vertical = 10.dp)
            ) {
                Column() {
                    paragraphLayout.DrawNormal()
                }
            }
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
fun PageView(
    name: String,
    alternateNames: List<String>,
    attributes: List<String>,
    page: PageLayout
) {
    Column(
        Modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 10.dp)
    ) {
        Column(Modifier.padding(vertical = 10.dp)) {
            Text(
                text = name,
                style = Material3.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
            )
        }
        AdditionalValuesView(alternateNames = alternateNames, attributes = attributes)
        Divider(thickness = 6.dp, color = MaterialTheme.colors.primaryVariant)
        Spacer(modifier = Modifier.height(20.dp))

        page.DrawNormal()
    }
}


@Composable
fun AdditionalValuesView(
    alternateNames: List<String>,
    attributes: List<String>,
    expandedState: MutableState<Boolean> = mutableStateOf(false)
) {
    var expanded by expandedState
    Surface(
        elevation = 2.dp,
        shape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
    ) {

        Column(
            Modifier
                .padding(10.dp)
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
                    Text(text = "Alternate names", fontWeight = FontWeight.Bold, fontSize = 24.sp)
                    alternateNames.forEach {
                        Text(text = it, fontSize = 20.sp)
                    }
                }
                Column(Modifier.padding(10.dp)) {
                    Text(text = "Attributes", fontWeight = FontWeight.Bold, fontSize = 24.sp)
                    attributes.forEach {
                        Text(text = it, fontSize = 20.sp)
                    }
                }
            }
        }
    }
}


@Composable
fun PageEdit(
    name: MutableState<String>,
    alternateNames: SnapshotStateList<UpdateHolder<String?>>,
    attributes: SnapshotStateList<UpdateHolder<String?>>,
    page: PageLayout
) {
    Column(
        Modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 10.dp)
    ) {
        Column(Modifier.padding(vertical = 10.dp)) {
            AligningBasicTextField(
                value = name.value,
                name.component2(),
                textStyle = Material3.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Material3.colorScheme.onBackground
                ),
                modifier = Modifier.fillMaxWidth(),
                cursorBrush = SolidColor(Material3.colorScheme.primary)
            )
        }
        AdditionalValuesEdit(alternateNames = alternateNames, attributes = attributes)
        Divider(thickness = 6.dp, color = MaterialTheme.colors.primaryVariant)
        Spacer(modifier = Modifier.height(20.dp))

        page.DrawEdit()
    }
}

fun <T> SnapshotStateList<T>.moveUp(a: Int) = swap(a, a - 1)

fun <T> SnapshotStateList<T>.moveDown(a: Int) = swap(a, a + 1)

fun <T> SnapshotStateList<T>.swap(a: Int, b: Int) {
    val h = get(a)
    set(a, get(b))
    set(b, h)
}

@Composable
fun AdditionalValuesEdit(
    alternateNames: SnapshotStateList<UpdateHolder<String?>>,
    attributes: SnapshotStateList<UpdateHolder<String?>>
) {
    var expanded by remember { mutableStateOf(false) }
    Surface(
        elevation = 2.dp,
        shape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp),
        modifier = Modifier
            .fillMaxWidth()
    ) {

        Column(
            Modifier
                .padding(10.dp)
                .animateContentSize(),
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
            val modifiableColumn: @Composable ColumnScope.(
                list: SnapshotStateList<UpdateHolder<String?>>,
                text: @Composable () -> Unit
            ) -> Unit =
                { list, text ->
                    Column(Modifier.padding(10.dp), Arrangement.spacedBy(15.dp)) {
                        text()
                        list.forEachIndexed { index, name ->
                            if (name.next.value != null)
                                Row() {
                                    RippleIcon(
                                        modifier = Modifier
                                            .size(30.dp)
                                            .padding(5.dp),
                                        onClick = { name.next.value = null },
                                        imageVector = FeatherIcons.Trash,
                                        description = "delete item"
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    AligningBasicTextField(
                                        value = name.next.value ?: "",
                                        { name.next.value = it },
                                        singleLine = true,
                                        textStyle = MaterialTheme.typography.subtitle1.copy(fontSize = 20.sp)
                                            .copy(Material3.colorScheme.onSurface),
                                        cursorBrush = SolidColor(Material3.colorScheme.primary)
                                    )
                                }
                        }
                        TextButton(onClick = { list.add(UpdateHolder(null, "")) }) {
                            Text(text = "Add", fontSize = 18.sp)
                        }
                    }
                }
            if (expanded) {
                modifiableColumn(alternateNames) {
                    Text(
                        text = "Alternate names",
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    )
                }

                modifiableColumn(attributes) {
                    Text(
                        text = "Attributes",
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    )
                }
            }
        }
    }
}

@Composable
fun ParagraphAdditionBox(addParagraph: () -> Unit) {
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
                .defaultMinSize(minHeight = 20.dp)
                .fillMaxWidth()
                .clip(CircleShape)
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

            if (expanded.value)
                TextButton(
                    onClick = addParagraph,
                ) {
                    Text(text = "Add paragraph")
                }
        }
    }
}

@Composable
fun ItemNavigation(
    up: (() -> Unit)?, down: (() -> Unit)?, delete: () -> Unit, navigationSize: Dp, deleteSize: Dp,
    space: Dp = 10.dp
) {
    val icon: @Composable (func: (() -> Unit)?, icon: ImageVector, size: Dp) -> Unit =
        { func, icon, size ->
            if (func != null)
                Icon(
                    imageVector = icon, contentDescription = "Move up",
                    Modifier
                        .clip(CircleShape)
                        .clickable(
                            onClick = func,
                            interactionSource = remember { MutableInteractionSource() },
                            indication = rememberRipple()
                        )
                        .size(size)
                        .padding(5.dp)
                )
            else
                Box(
                    modifier = Modifier
                        .size(size)
                        .padding(5.dp)
                )
        }
//    var deleteNum = remember { mutableStateOf(0) }
    Row(
        Modifier
            .fillMaxWidth()
            .padding(top = 10.dp, start = 10.dp, end = 10.dp),
        Arrangement.spacedBy(space, Alignment.End),
        Alignment.CenterVertically
    ) {
        icon(up, FeatherIcons.ChevronUp, navigationSize)
        icon(down, FeatherIcons.ChevronDown, navigationSize)
        icon(delete, FeatherIcons.Trash, deleteSize)
//        IconButton(onClick = delete, Modifier.wrapContentSize()) {
//        Icon(
//            imageVector = FeatherIcons.Trash,
//            contentDescription = "Move down",
//            Modifier.size(deleteSize)
//        )
//        }
    }
}

@Composable
fun RippleIcon(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    imageVector: ImageVector,
    description: String
) {
    Icon(
        imageVector = imageVector,
        description,
        modifier = modifier.clickable(remember {
            MutableInteractionSource()
        }, rememberRipple(), onClick = onClick),
    )
}

@Preview(showSystemUi = true, device = Devices.PIXEL_3, heightDp = 1000)
@Composable
fun PageViewPreview() {
    WroteTheme() {

        PageView(
            name = "Augustus Floral of the night",
            alternateNames = listOf("King of the florals", "Horn of the tribe"),
            attributes = listOf("character", "king", "floral"),
            page = remember {
                PageLayout(
                    listOf(
                        SerializableParagraphLayout("History", testSDataLayout),
                        SerializableParagraphLayout("History", testSDataLayout)
                    )
                )
            }
        )
    }
}

inline fun <T, reified G> mutStateListOf(items: List<T>, map: (T) -> G) =
    mutableStateListOf(*items.map(map).toTypedArray())

inline fun <reified T> mutStateListOf(items: List<T>) =
    mutableStateListOf(*items.toTypedArray())

@Preview(showSystemUi = true, heightDp = 1000)
@Composable
fun PageEditPreview() {
    WroteTheme() {
        PageEdit(
            name = remember { mutableStateOf("Augustus Floral of the night") },
            alternateNames = remember {
                mutStateListOf(
                    listOf(
                        "King of the florals",
                        "Horn of the tribe"
                    )
                ) { UpdateHolder(it) }
            },
            attributes = remember {
                mutStateListOf(listOf(
                    "character",
                    "king",
                    "floral"
                ), { UpdateHolder(it) })
            },
            page = remember {
                PageLayout(
                    listOf(
                        SerializableParagraphLayout("History", testSDataLayout),
                        SerializableParagraphLayout("History", testSDataLayout)
                    )
                )
            }
        )
    }
}
