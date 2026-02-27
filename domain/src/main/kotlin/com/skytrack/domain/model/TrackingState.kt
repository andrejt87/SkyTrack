package com.skytrack.domain.model

enum class TrackingState {
    INITIALIZING,
    ACQUIRING,
    TRACKING,
    GPS_LOST,
    ARRIVED,
    PARKED
}
