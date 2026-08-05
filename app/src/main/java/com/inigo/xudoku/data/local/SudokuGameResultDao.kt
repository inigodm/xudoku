package com.inigo.xudoku.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.inigo.xudoku.model.history.SudokuGameResult
import kotlinx.coroutines.flow.Flow

@Dao
interface SudokuGameResultDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(result: SudokuGameResult): Long

    @Query("SELECT * FROM game_results ORDER BY fechaHoraFin DESC")
    fun getAll(): List<SudokuGameResult>
    
    @Query("SELECT * FROM game_results ORDER BY fechaHoraFin DESC")
    fun getAllFlow(): Flow<List<SudokuGameResult>>

    @Query("SELECT * FROM game_results WHERE id = :id")
    fun getById(id: String): SudokuGameResult?

    @Query("DELETE FROM game_results WHERE id = :id")
    fun deleteById(id: String): Int

    @Query("DELETE FROM game_results")
    fun deleteAll(): Int

    @Query("SELECT * FROM game_results WHERE completado = 1 ORDER BY puntuacionFinal DESC LIMIT :limit")
    fun getTopScores(limit: Int): List<SudokuGameResult>

    @Query("SELECT * FROM game_results WHERE completado = 1 AND dificultad = :dificultad ORDER BY tiempoEmpleado ASC LIMIT 1")
    fun getBestTimeByDifficulty(dificultad: String): SudokuGameResult?

    @Query("SELECT * FROM game_results WHERE dificultad = :dificultad ORDER BY fechaHoraFin DESC")
    fun getByDifficulty(dificultad: String): List<SudokuGameResult>

    @Query("SELECT * FROM game_results WHERE fechaHoraFin >= :startDate AND fechaHoraFin <= :endDate ORDER BY fechaHoraFin DESC")
    fun getByDateRange(startDate: Long, endDate: Long): List<SudokuGameResult>
    
    @Query("SELECT * FROM game_results WHERE nivel = :nivel ORDER BY fechaHoraFin DESC")
    fun getByLevel(nivel: Int): List<SudokuGameResult>
}
