package com.dak.wrote.frontend.viewmodel

import android.app.Application
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dak.wrote.backend.contracts.entities.Book
import com.dak.wrote.backend.implementations.file_system_impl.dao.WroteDaoFileSystemImpl
import com.dak.wrote.backend.implementations.file_system_impl.dao.getDAO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class BookNavigationViewModel() : ViewModel() {
    private val _bookState = MutableLiveData(listOf<Book>())
    val bookState: LiveData<List<Book>> = _bookState


    fun updateBooks(application: Application) {
        viewModelScope.launch(Dispatchers.IO) {
            _bookState.postValue(getDAO(application).getBooks())
        }
    }
}