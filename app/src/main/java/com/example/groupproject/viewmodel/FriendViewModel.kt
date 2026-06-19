package com.example.groupproject.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.example.groupproject.data.CountResult
import com.example.groupproject.data.Friend
import com.example.groupproject.data.FriendRepository
import kotlinx.coroutines.launch

class FriendViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = FriendRepository(application)

    val genderCounts: LiveData<List<CountResult>> = repository.getGenderCounts()
    val stateCounts: LiveData<List<CountResult>> = repository.getStateCounts()

    private val searchQuery = MutableLiveData("")

    val displayedFriends: LiveData<List<Friend>> = searchQuery.switchMap { query ->
        if (query.isBlank()) {
            repository.allFriends
        } else {
            repository.search(query)
        }
    }

    fun search(query: String) {
        searchQuery.value = query
    }

    fun insert(friend: Friend) {
        viewModelScope.launch {
            repository.insert(friend)
        }
    }

    fun update(friend: Friend) {
        viewModelScope.launch {
            repository.update(friend)
        }
    }

    fun delete(friend: Friend) {
        viewModelScope.launch {
            repository.delete(friend)
        }
    }

    suspend fun getById(id: Int): Friend? {
        return repository.getById(id)
    }

    fun saveImage(uri: Uri): String {
        return repository.saveImage(getApplication(), uri)
    }
}
