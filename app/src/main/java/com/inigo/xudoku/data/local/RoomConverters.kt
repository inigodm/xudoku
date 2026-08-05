package com.inigo.xudoku.data.local

import androidx.room.TypeConverter
import com.inigo.xudoku.model.Difficulty
import java.util.Date

class RoomConverters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun fromDifficulty(difficulty: Difficulty): String {
        return difficulty.name
    }

    @TypeConverter
    fun toDifficulty(name: String): Difficulty {
        return try {
            Difficulty.valueOf(name)
        } catch (e: IllegalArgumentException) {
            Difficulty.MEDIUM
        }
    }
}
