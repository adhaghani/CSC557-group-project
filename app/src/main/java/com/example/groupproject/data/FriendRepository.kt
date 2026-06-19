package com.example.groupproject.data

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import com.example.groupproject.util.ImageUtils

class FriendRepository(context: Context) {

    private val dao: FriendDao = FriendDatabase.getInstance(context).friendDao()

    val allFriends: LiveData<List<Friend>> = dao.getAll()

    fun search(query: String): LiveData<List<Friend>> = dao.search(query)

    fun getGenderCounts(): LiveData<List<CountResult>> = dao.getGenderCounts()

    fun getStateCounts(): LiveData<List<CountResult>> = dao.getStateCounts()

    suspend fun getById(id: Int): Friend? = dao.getById(id)

    suspend fun insert(friend: Friend) {
        dao.insert(friend)
    }

    suspend fun update(friend: Friend) {
        dao.update(friend)
    }

    suspend fun delete(friend: Friend) {
        friend.photoPath?.let { ImageUtils.deleteImage(it) }
        dao.delete(friend)
    }

    fun saveImage(context: Context, uri: Uri): String {
        return ImageUtils.saveImageToInternalStorage(context, uri)
    }
}
