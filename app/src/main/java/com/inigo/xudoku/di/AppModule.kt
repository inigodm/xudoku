package com.inigo.xudoku.di

import androidx.room.Room
import com.inigo.xudoku.data.local.XudokuDatabase
import com.inigo.xudoku.data.repository.GameHistoryRepository
import com.inigo.xudoku.data.repository.RoomGameHistoryRepository
import com.inigo.xudoku.data.repository.ProgressionRepository
import com.inigo.xudoku.data.repository.SharedPreferencesProgressionRepository
import com.inigo.xudoku.model.progression.ProgressionManager
import com.inigo.xudoku.ui.GameViewModel
import com.inigo.xudoku.ui.ProgressionViewModel
import com.inigo.xudoku.ui.StatsViewModel
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    
    // Database
    single {
        Room.databaseBuilder(
            androidContext(),
            XudokuDatabase::class.java,
            "xudoku_database"
        ).build()
    }

    // DAO
    single { get<XudokuDatabase>().sudokuGameResultDao() }

    // Repository
    single<GameHistoryRepository> { RoomGameHistoryRepository(get()) }
    single<ProgressionRepository> { SharedPreferencesProgressionRepository(androidContext()) }
    
    // Core Managers
    single { ProgressionManager() }

    // ViewModel
    viewModel { GameViewModel(ioDispatcher = Dispatchers.IO, historyRepo = get(), progressionRepo = get<ProgressionRepository>(), progressionManager = get<ProgressionManager>()) }
    viewModel { StatsViewModel(historyRepo = get()) }
    viewModel { ProgressionViewModel(progressionRepository = get(), progressionManager = get()) }
}
