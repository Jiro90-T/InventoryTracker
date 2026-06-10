package com.jiro.inventorytracker

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.jiro.inventorytracker.media.PhotoMaintenance
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class InventoryApp : Application(), Configuration.Provider {

    @Inject lateinit var workerFactory: HiltWorkerFactory
    @Inject lateinit var photoMaintenance: PhotoMaintenance

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        // Reclaim disk space from photos that were removed from items (or app
        // crashes that left orphan camera files behind).
        photoMaintenance.sweepOnAppStart()
    }
}
