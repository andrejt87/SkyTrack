package com.skytrack.domain.repository

interface MapTilesRepository {
    suspend fun getTile(z: Int, x: Int, y: Int): ByteArray?
    fun getStyleJson(): String
    suspend fun isIntegrityValid(): Boolean
}
