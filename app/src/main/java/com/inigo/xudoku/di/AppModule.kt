package com.inigo.xudoku.di

import androidx.room.Room
import com.inigo.xudoku.data.local.XudokuDatabase
import com.inigo.xudoku.data.repository.GameHistoryRepository
import com.inigo.xudoku.data.repository.RoomGameHistoryRepository
import com.inigo.xudoku.ui.GameViewModel
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

    // ViewModel
    viewModel { GameViewModel(ioDispatcher = Dispatchers.IO, historyRepo = get()) }
}
