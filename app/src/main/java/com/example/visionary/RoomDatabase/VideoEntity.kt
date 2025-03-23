package com.example.visionary.RoomDatabase

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "video_table")
data class VideoEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val videoName: String,
    val videoPath: String
)
