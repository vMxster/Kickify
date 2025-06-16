package it.unibo.kickify

import android.app.Application
import android.content.Context
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.osmdroid.config.Configuration

class KickifyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // OSM configuration
        val ctx = applicationContext
        Configuration.getInstance().load(
            ctx, ctx.getSharedPreferences("osmdroid_prefs", Context.MODE_PRIVATE)
        )

        startKoin {
            androidLogger()
            androidContext(this@KickifyApplication)
            modules(appModule)
        }
    }
}