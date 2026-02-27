package com.skytrack.app.data.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.skytrack.app.domain.AltitudeCalculator
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BarometerProvider @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val sensorManager: SensorManager =
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    private val pressureSensor: Sensor? =
        sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE)

    /** True if the device has a barometric pressure sensor */
    val isAvailable: Boolean
        get() = pressureSensor != null

    /**
     * Emits raw barometric pressure readings in hPa.
     * Emits nothing if the device has no pressure sensor.
     */
    val pressureFlow: Flow<Float> = callbackFlow {
        val sensor = pressureSensor
        if (sensor == null) {
            close()
            return@callbackFlow
        }
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                if (event.sensor.type == Sensor.TYPE_PRESSURE) {
                    trySend(event.values[0])
                }
            }
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) { /* ignored */ }
        }
        sensorManager.registerListener(
            listener,
            sensor,
            SensorManager.SENSOR_DELAY_NORMAL
        )
        awaitClose { sensorManager.unregisterListener(listener) }
    }

    /**
     * Emits barometer-derived altitude in metres.
     * Uses the ISA hypsometric formula with configurable QNH reference pressure.
     */
    fun altitudeFlow(qnhHpa: Float = AltitudeCalculator.SEA_LEVEL_PRESSURE_HPA): Flow<Double> =
        pressureFlow.map { pressure ->
            AltitudeCalculator.pressureToAltitude(pressure, qnhHpa)
        }

    /**
     * Snapshot data class for barometer readings.
     */
    data class BarometerReading(
        val pressureHpa: Float,
        val altitudeM: Double,
        val qnhHpa: Float
    )

    /**
     * Combined reading flow with both pressure and computed altitude.
     */
    fun readingFlow(qnhHpa: Float = AltitudeCalculator.SEA_LEVEL_PRESSURE_HPA): Flow<BarometerReading> =
        pressureFlow.map { pressure ->
            BarometerReading(
                pressureHpa = pressure,
                altitudeM = AltitudeCalculator.pressureToAltitude(pressure, qnhHpa),
                qnhHpa = qnhHpa
            )
        }
}
