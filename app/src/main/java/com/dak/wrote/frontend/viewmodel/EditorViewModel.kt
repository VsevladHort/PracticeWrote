package com.dak.wrote.frontend.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.traceEventEnd
import androidx.lifecycle.ViewModel
import com.dak.wrote.backend.contracts.entities.UniqueEntity
import com.dak.wrote.frontend.editor.PageLayout
import com.dak.wrote.frontend.editor.SerializablePageLayout

class ChangedValue<T> {
    var changed: Boolean = true
        private set
    private var _field: T? = null
    var field: T
        get() = _field as T
        set(value) {
            changed = true
            _field = value
        }
}

class EditorViewModel() : ViewModel() {
    val page: ChangedValue<SerializablePageLayout> = ChangedValue()
    lateinit var displayPage: MutableState<PageLayout>
    lateinit var currentId: String

    fun updateInit() {
        if (page.changed) {
            displayPage.value = page.field.toDisplayable()
        }
    }

}