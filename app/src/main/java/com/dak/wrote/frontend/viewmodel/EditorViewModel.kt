package com.dak.wrote.frontend.viewmodel

import android.app.Application
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.traceEventEnd
import androidx.lifecycle.*
import com.dak.wrote.backend.contracts.entities.Attribute
import com.dak.wrote.backend.contracts.entities.UniqueEntity
import com.dak.wrote.backend.implementations.file_system_impl.dao.getDAO
import com.dak.wrote.frontend.editor.PageLayout
import com.dak.wrote.frontend.editor.SerializablePageLayout
import com.dak.wrote.frontend.editor.mutStateListOf
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import java.io.ByteArrayInputStream
import java.io.InputStream

class ChangedValue<T> {
    var changed: Boolean = true
        private set
    private var _field: T? = null

    @Suppress("UNCHECKED_CAST")
    var field: T
        get() = _field as T
        set(value) {
            changed = true
            _field = value
        }
}


class UpdateHolder<T>(old: T) {
    var old: T = old
        private set

    constructor(old: T, next: T) : this(old) {
        this.next.value = next
    }

    var next = mutableStateOf(old)
    val updated
        get() = old == next.value

    fun refresh() {
        old = next.value
    }
}


@OptIn(ExperimentalSerializationApi::class)
class EditorViewModel(application: Application) : AndroidViewModel(application) {
    data class ObjectNote(
        var name: MutableState<String>,
        val dAlternateNames : SnapshotStateList<UpdateHolder<String?>>,
        val dAttributes: SnapshotStateList<UpdateHolder<String?>>,
        val alternateNames: List<String>,
        val attributes : List<Attribute>,
        var sPage: SerializablePageLayout,
        var page: PageLayout,
        var inEdit : MutableState<Boolean> = mutableStateOf(false)
    )

    var currentId: MutableStateFlow<String?> = MutableStateFlow(null)

    val rep = getDAO(application)

    var note: MutableStateFlow<ObjectNote?> = MutableStateFlow(null)


    init {
        viewModelScope.launch {
            currentId.filterNotNull().collect { id ->
                val page =
                    Json.decodeFromStream<SerializablePageLayout>(
                        ByteArrayInputStream(
                            rep.getNoteSaveData(
                                id
                            )
                        )
                    )
                val attributes = rep.getAttributes(id).toList()
                val alternateNames = rep.getAlternateTitles(id)
                note.value =
                    ObjectNote(
                        mutableStateOf(rep.getName(id)),
                        mutStateListOf(
                            mutStateListOf(attributes) {UpdateHolder(it.name)}),
                        mutStateListOf(alternateNames) {UpdateHolder(it)},
                        alternateNames,
                        attributes,
                        page,
                        page.toDisplayable()
                    )
            }
        }
    }

    fun onSave() {

    }



}