package com.inigo.xudoku

import android.app.Application
import com.inigo.xudoku.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class XudokuApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        startKoin {
            androidLogger()
            androidContext(this@XudokuApplication)
            modules(appModule)
        }
    }
}
