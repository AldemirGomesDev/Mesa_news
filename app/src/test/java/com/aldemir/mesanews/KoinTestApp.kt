package com.aldemir.mesanews

import android.app.Application
import com.aldemir.mesanews.di.databaseModuleTest
import com.aldemir.mesanews.di.mainModuleTest
import com.aldemir.mesanews.di.networkModuleTest
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.core.context.unloadKoinModules
import org.koin.core.module.Module

class KoinTestApp: Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@KoinTestApp)
            modules(
                mainModuleTest,
                networkModuleTest,
                databaseModuleTest
            )
        }
    }

    internal fun loadModules(module: Module, block: () -> Unit) {
        loadKoinModules(module)
        block()
        unloadKoinModules(module)
    }
}