package com.skytrack.app.ui.screens.debug

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skytrack.app.ui.theme.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

enum class LogLevel { INFO, WARN, ERROR, GPS, TRACK, WATCHDOG }

data class DebugLogEntry(
    val timestamp: Long = System.currentTimeMillis(),
    val level: LogLevel,
    val tag: String,
    val message: String
)

/**
 * Global debug log buffer. Call DebugLog.log() from anywhere.
 */
object DebugLog {
    private val _entries = mutableListOf<DebugLogEntry>()
    val entries: List<DebugLogEntry> get() = _entries.toList()

    private val _flow = kotlinx.coroutines.flow.MutableStateFlow(0L) // tick
    val updateFlow: kotlinx.coroutines.flow.StateFlow<Long> = _flow

    fun log(level: LogLevel, tag: String, message: String) {
        synchronized(_entries) {
            _entries.add(DebugLogEntry(level = level, tag = tag, message = message))
            // Keep max 500 entries
            if (_entries.size > 500) _entries.removeAt(0)
        }
        _flow.value = System.currentTimeMillis()
    }

    fun clear() {
        synchronized(_entries) { _entries.clear() }
        _flow.value = System.currentTimeMillis()
    }

    // Convenience methods
    fun gps(tag: String, msg: String) = log(LogLevel.GPS, tag, msg)
    fun track(tag: String, msg: String) = log(LogLevel.TRACK, tag, msg)
    fun watchdog(tag: String, msg: String) = log(LogLevel.WATCHDOG, tag, msg)
    fun info(tag: String, msg: String) = log(LogLevel.INFO, tag, msg)
    fun warn(tag: String, msg: String) = log(LogLevel.WARN, tag, msg)
    fun error(tag: String, msg: String) = log(LogLevel.ERROR, tag, msg)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DebugScreen(gpsAccuracyM: Float = 0f) {
    val tick by DebugLog.updateFlow.collectAsState()
    val entries = remember(tick) { DebugLog.entries }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val dateFormat = remember { SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault()) }

    // Auto-scroll to bottom
    LaunchedEffect(tick) {
        if (entries.isNotEmpty()) {
            listState.animateScrollToItem(entries.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        TopAppBar(
            title = { Text("Debug", color = TextPrimary) },
            actions = {
                Text(
                    "${entries.size} entries",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(end = 8.dp)
                )
                IconButton(onClick = { DebugLog.clear() }) {
                    Icon(Icons.Default.Delete, "Clear", tint = TextSecondary)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkSurface)
        )

        if (entries.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No log entries yet", color = TextSecondary, fontSize = 14.sp)
            }
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                items(entries) { entry ->
                    val color = when (entry.level) {
                        LogLevel.GPS -> Color(0xFF4CAF50)      // green
                        LogLevel.TRACK -> Color(0xFF2196F3)    // blue
                        LogLevel.WATCHDOG -> Color(0xFFFF9800) // orange
                        LogLevel.WARN -> Color(0xFFFF9800)     // orange
                        LogLevel.ERROR -> Color(0xFFF44336)    // red
                        LogLevel.INFO -> TextSecondary
                    }
                    val levelTag = when (entry.level) {
                        LogLevel.GPS -> "GPS"
                        LogLevel.TRACK -> "TRK"
                        LogLevel.WATCHDOG -> "WDG"
                        LogLevel.WARN -> "WRN"
                        LogLevel.ERROR -> "ERR"
                        LogLevel.INFO -> "INF"
                    }
                    Text(
                        text = "${dateFormat.format(Date(entry.timestamp))} [$levelTag] ${entry.tag}: ${entry.message}",
                        color = color,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        lineHeight = 14.sp
                    )
                }
            }
        }
    }
}
