package com.inigo.xudoku.model.history

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.inigo.xudoku.model.Difficulty
import java.util.Date

@Entity(tableName = "game_results")
data class SudokuGameResult(
    @PrimaryKey
    val id: String, // UUID String
    
    // Información temporal
    val fechaHoraInicio: Date,
    val fechaHoraFin: Date,
    val tiempoEmpleado: Long, // segundos
    val tiempoPausado: Long, // segundos
    
    // Información del Sudoku
    val dificultad: Difficulty,
    val nivel: Int,
    val identificadorSudoku: String?,
    val seed: Long?,
    val tamanoTablero: Int,
    
    // Información de puntuación
    val puntuacionPartida: Int,
    val puntuacionFinal: Int,
    val multiplicadorDificultad: Float,
    val multiplicadorTiempo: Float,
    
    // Ayudas
    val ayudasMostrarNumero: Int,
    val ayudasResolverCasilla: Int,
    val ayudasComprobarErrores: Int,
    val totalAyudas: Int,
    
    // Errores
    val erroresCometidos: Int,
    val partidaPerfecta: Boolean,
    
    // Movimientos
    val movimientosTotales: Int,
    
    // Información de resolución
    val numerosColocados: Int,
    val porcentajeCompletadoManual: Float,
    val porcentajeCompletadoConAyudas: Float,
    
    // Resultado
    val completado: Boolean,
    val abandono: Boolean,
    val victoria: Boolean,
    
    // Información del jugador
    val userId: String = "",
    val userName: String = "Local Player",
    
    // Sincronización futura
    val isSynced: Boolean = false,
    val serverId: String? = null,
    val fechaSincronizacion: Date? = null,
    
    // Versionado
    val versionJuego: Int,
    val versionAlgoritmoPuntuacion: Int,
    
    // Metadata JSON
    val metadata: String // JSON
)
