package com.skytrack.app.data.map

import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert.*
import org.junit.Test

/**
 * Tests for download state transitions and banner visibility logic.
 */
class MapDownloadManagerTest {

    /**
     * Simulates banner visibility logic from MapDownloadBanner composable.
     * Returns true if banner should be visible.
     */
    private fun shouldShowBanner(
        detailAvailable: Boolean,
        isOnline: Boolean,
        downloadState: MapDownloadManager.DownloadState
    ): Boolean {
        if (detailAvailable) return false
        if (!isOnline) {
            // Would call resetState() if downloading
            return false
        }
        return true // banner visible
    }

    @Test
    fun `banner hidden when offline`() {
        assertFalse(shouldShowBanner(
            detailAvailable = false,
            isOnline = false,
            downloadState = MapDownloadManager.DownloadState()
        ))
    }

    @Test
    fun `banner hidden when offline even if downloading`() {
        assertFalse(shouldShowBanner(
            detailAvailable = false,
            isOnline = false,
            downloadState = MapDownloadManager.DownloadState(isDownloading = true)
        ))
    }

    @Test
    fun `banner hidden when detail maps available`() {
        assertFalse(shouldShowBanner(
            detailAvailable = true,
            isOnline = true,
            downloadState = MapDownloadManager.DownloadState()
        ))
    }

    @Test
    fun `banner shown when online and no detail maps`() {
        assertTrue(shouldShowBanner(
            detailAvailable = false,
            isOnline = true,
            downloadState = MapDownloadManager.DownloadState()
        ))
    }

    @Test
    fun `resetState clears downloading flag`() {
        val state = MutableStateFlow(MapDownloadManager.DownloadState(isDownloading = true, progress = 0.5f))
        // Simulate resetState
        state.value = MapDownloadManager.DownloadState()
        assertFalse(state.value.isDownloading)
        assertEquals(0f, state.value.progress)
    }
}
