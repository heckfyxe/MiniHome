package me.heckfyxe.mihome

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.annotation.KoinApplication
import org.koin.plugin.module.dsl.startKoin
import timber.log.Timber

@KoinApplication
class App : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin<App> {
            androidLogger()
            androidContext(this@App)
        }

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}