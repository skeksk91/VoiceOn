package com.example.voiceon

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "room")
data class Room (
    @PrimaryKey @NonNull val uid: String,
    @ColumnInfo(name = "uName") val uName: String?,
    @ColumnInfo(name = "is_like") var isLike: Boolean?
) : Serializable