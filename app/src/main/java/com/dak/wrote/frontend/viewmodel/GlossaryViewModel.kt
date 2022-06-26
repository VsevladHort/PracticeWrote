package com.dak.wrote.frontend.viewmodel

import android.app.Application
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toLowerCase
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.dak.wrote.backend.contracts.entities.Attribute
import com.dak.wrote.backend.implementations.file_system_impl.dao.getDAO
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.*

class GlossaryViewModel(val bookId: String, application: Application) :
    AndroidViewModel(application) {
    data class PartialNote(
        val title: String,
        val alternateNames: Set<String>,
        val attributes: Set<Attribute>,
        val keyId: String
    ) : Comparable<PartialNote> {
        override fun compareTo(other: PartialNote): Int {
            return keyId.compareTo(other.keyId)
        }

    }

    data class Data(
        val allAttributes: SortedMap<String, Attribute>,
        val allNotes: SortedMap<String, PartialNote>,
        val allNames: SortedMap<String, List<PartialNote>>,
        val searchedName: MutableState<String> = mutableStateOf(""),
        val searchedAttributes: SnapshotStateList<MutableState<String>> = SnapshotStateList<MutableState<String>>(),
        val foundNotes: MutableState<List<PartialNote>?> = mutableStateOf(null),
        var searchJob: Job? = null,
    )

    val rep = getDAO(application)
    val data = MutableStateFlow<Data?>(null)

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val attributes = kotlin.run {
                val a =
                    rep.getAttributes(bookId)
                TreeMap<String, Attribute>().apply {
                    putAll(a.map { it.name to it })
                }
            }

            val (allNotes, allNames) = kotlin.run {
                val ac = TreeMap<String, PartialNote>()
                val names = TreeMap<String, MutableList<PartialNote>>()
                attributes.forEach { attribute ->
                    attribute.value.associatedEntities.forEach { id ->
                        if (!ac.contains(id)) {
                            val note =
                                PartialNote(
                                    rep.getName(id),
                                    rep.getAlternateTitles(id).toSet(),
                                    rep.getAttributes(id),
                                    id
                                )
                            ac[id] = note

                            fun addToName(name: String, note: PartialNote) {
                                val lowerCaseName = name.toLowerCase(Locale.current)
                                val list = names[lowerCaseName]
                                if (list == null) {
                                    names[lowerCaseName] = mutableListOf(note)
                                } else list.add(note)
                            }
                            addToName(note.title, note)
                            note.alternateNames.forEach { addToName(it, note) }
                        }
                    }
                }
                ac to (names.mapValues { it.value as List<PartialNote> }.toSortedMap())
            }
            data.value = Data(
                attributes,
                allNotes,
                allNames,
            )
        }
    }

    fun searchAnew(data: Data) {
        fun nextStr(text: String): String {
            return text.substring(0 until text.lastIndex) + text.last().inc()
        }
        data.searchJob?.cancel()
        data.searchJob = viewModelScope.launch {
            val filtered = data.searchedAttributes.filter { it.value.isNotBlank() }
            val name = data.searchedName.value.toLowerCase(Locale.current)
            if (filtered.isNotEmpty() && name.isNotBlank()) {
                val result = when {
                    filtered.isEmpty() -> {
                        data.allNames.subMap(name, nextStr(name)).toList().flatMap { it.second!! }
                            .sortedBy { it.title }
                    }
                    else -> {
                        val attributes = filtered.mapNotNull {
                            data.allAttributes[it.value.trim()]
                        }
                        val notes =
                            attributes.flatMap { it.associatedEntities.map { entity -> data.allNotes[entity]!! } }
                                .filter { it.title.startsWith(name) }
                                .sortedBy { it.title }
                        notes
                    }
                }
                data.searchJob = null
                data.foundNotes.value = result
            }
        }
    }

}

class GlossaryViewModelFactory(
    private val selectedBook: String,
    private val application: Application
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return GlossaryViewModel(selectedBook, application) as T
    }
}