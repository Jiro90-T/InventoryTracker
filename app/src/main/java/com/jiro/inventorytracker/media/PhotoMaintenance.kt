package com.jiro.inventorytracker.media

import android.content.Context
import com.jiro.inventorytracker.domain.ItemRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Owns the on-disk photo directory lifecycle. Scans the photos directory on app
 * start and removes files that no item references. Safe to run repeatedly.
 */
@Singleton
class PhotoMaintenance @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: ItemRepository
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun sweepOnAppStart() {
        scope.launch { sweep() }
    }

    /**
     * Removes any file under [PhotoStorage.photosDir] that is not in
     * [com.jiro.inventorytracker.domain.ItemRepository.allReferencedPhotoPaths].
     * Returns the number of files deleted (mostly for tests/logging).
     */
    suspend fun sweep(): Int {
        val dir = PhotoStorage.photosDir(context)
        val files = dir.listFiles() ?: return 0
        if (files.isEmpty()) return 0
        val referenced = repository.allReferencedPhotoPaths()
        var deleted = 0
        for (file in files) {
            if (!referenced.contains(file.absolutePath)) {
                if (file.delete()) deleted++
            }
        }
        return deleted
    }
}
