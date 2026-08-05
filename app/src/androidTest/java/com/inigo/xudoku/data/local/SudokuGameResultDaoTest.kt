package com.inigo.xudoku.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.inigo.xudoku.model.Difficulty
import com.inigo.xudoku.model.history.SudokuGameResult
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.Date
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class SudokuGameResultDaoTest {

    private lateinit var database: XudokuDatabase
    private lateinit var dao: SudokuGameResultDao

    @Before
    fun setup() {
        // Usar base de datos en memoria para que no interfiera y se borre sola
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            XudokuDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = database.sudokuGameResultDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    private fun createDummyResult(
        id: String = UUID.randomUUID().toString(),
        dificultad: Difficulty = Difficulty.MEDIUM,
        puntuacionFinal: Int = 100,
        completado: Boolean = true
    ): SudokuGameResult {
        return SudokuGameResult(
            id = id,
            fechaHoraInicio = Date(1000000),
            fechaHoraFin = Date(2000000),
            tiempoEmpleado = 1000,
            tiempoPausado = 0,
            dificultad = dificultad,
            nivel = 1,
            identificadorSudoku = "123",
            seed = null,
            tamanoTablero = 9,
            puntuacionPartida = puntuacionFinal,
            puntuacionFinal = puntuacionFinal,
            multiplicadorDificultad = 1.0f,
            multiplicadorTiempo = 1.0f,
            ayudasMostrarNumero = 0,
            ayudasResolverCasilla = 0,
            ayudasComprobarErrores = 0,
            totalAyudas = 0,
            erroresCometidos = 0,
            partidaPerfecta = true,
            movimientosTotales = 50,
            numerosColocados = 40,
            porcentajeCompletadoManual = 1.0f,
            porcentajeCompletadoConAyudas = 0.0f,
            completado = completado,
            abandono = !completado,
            victoria = completado,
            versionJuego = 1,
            versionAlgoritmoPuntuacion = 1,
            metadata = "{}"
        )
    }

    @Test
    fun insertAndGetById() = runBlocking {
        val id = UUID.randomUUID().toString()
        val result = createDummyResult(id = id)
        
        dao.insert(result)
        val loaded = dao.getById(id)
        
        assertNotNull(loaded)
        assertEquals(result.id, loaded?.id)
        assertEquals(result.dificultad, loaded?.dificultad)
    }

    @Test
    fun deleteById() = runBlocking {
        val id = UUID.randomUUID().toString()
        val result = createDummyResult(id = id)
        
        dao.insert(result)
        dao.deleteById(id)
        val loaded = dao.getById(id)
        
        assertNull(loaded)
    }

    @Test
    fun getTopScores_returnsHighestScoresOnlyCompleted() = runBlocking {
        val r1 = createDummyResult(puntuacionFinal = 100, completado = true)
        val r2 = createDummyResult(puntuacionFinal = 300, completado = true)
        val r3 = createDummyResult(puntuacionFinal = 200, completado = true)
        val r4 = createDummyResult(puntuacionFinal = 500, completado = false) // No debe aparecer
        
        dao.insert(r1)
        dao.insert(r2)
        dao.insert(r3)
        dao.insert(r4)
        
        val topScores = dao.getTopScores(2)
        
        assertEquals(2, topScores.size)
        assertEquals(300, topScores[0].puntuacionFinal)
        assertEquals(200, topScores[1].puntuacionFinal)
    }
}
