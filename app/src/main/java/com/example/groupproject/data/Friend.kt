package com.example.groupproject.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "friends")
data class Friend(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val gender: String,
    val phoneNumber: String,
    val emailAddress: String,
    val photoPath: String?,
    val addressLine1: String,
    val addressLine2: String,
    val addressLine3: String,
    val addressLine4: String
)
