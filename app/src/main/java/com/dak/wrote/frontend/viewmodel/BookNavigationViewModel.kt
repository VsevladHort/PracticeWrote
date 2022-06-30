package com.dak.wrote.frontend.viewmodel

import android.app.Application
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.*
import com.dak.wrote.backend.contracts.database.EntryType
import com.dak.wrote.backend.contracts.entities.Book
import com.dak.wrote.backend.implementations.file_system_impl.dao.WroteDaoFileSystemImpl
import com.dak.wrote.backend.implementations.file_system_impl.dao.getDAO
import com.dak.wrote.backend.implementations.file_system_impl.database.getKeyGen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class BookNavigationViewModel(application: Application) : AndroidViewModel(application) {
    private val _bookState: MutableLiveData<List<Book>?> = MutableLiveData(null)
    val bookState: LiveData<List<Book>?> = _bookState

    val rep = getDAO(application)
    val gen = getKeyGen(application)

    fun updateBooks() {
        viewModelScope.launch(Dispatchers.IO) {
            _bookState.postValue(rep.getBooks())
        }
    }

    fun createBook(name: String) {
        viewModelScope.launch {
            val book = Book(gen.getKey(null, EntryType.BOOK), name)
            rep.insertBook(book)
            updateBooks()
        }
    }

    init {
        updateBooks()
    }
}