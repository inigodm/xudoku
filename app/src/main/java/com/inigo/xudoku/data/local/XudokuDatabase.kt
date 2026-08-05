package com.inigo.xudoku.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.inigo.xudoku.model.history.SudokuGameResult

@Database(entities = [SudokuGameResult::class], version = 1, exportSchema = false)
@TypeConverters(RoomConverters::class)
abstract class XudokuDatabase : RoomDatabase() {
    abstract fun sudokuGameResultDao(): SudokuGameResultDao
}
