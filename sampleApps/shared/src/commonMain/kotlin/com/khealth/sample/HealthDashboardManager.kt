/*
 * Copyright (c) 2024 Shubham Singh
 *
 * This library is licensed under the Apache 2.0 License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.khealth.sample

import com.khealth.KHHeartRateSample
import com.khealth.KHPermission
import com.khealth.KHReadRequest
import com.khealth.KHRecord
import com.khealth.KHUnit
import com.khealth.KHWriteResponse
import com.khealth.KHealth
import com.khealth.createKHealth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

class HealthDashboardManager(
    val kHealth: KHealth = createKHealth(),
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
) {
    constructor() : this(createKHealth())

    private val _state = MutableStateFlow(
        HealthDashboardState(
            isHealthStoreAvailable = try {
                kHealth.isHealthStoreAvailable
            } catch (_: Throwable) {
                false
            }
        )
    )
    val state: StateFlow<HealthDashboardState> = _state.asStateFlow()

    val permissions = arrayOf(
        KHPermission.StepCount(read = true, write = true),
        KHPermission.HeartRate(read = true, write = true),
        KHPermission.ActiveCaloriesBurned(read = true, write = true),
        KHPermission.Hydration(read = true, write = true),
        KHPermission.Distance(read = true, write = true),
    )

    init {
        checkPermissions()
    }

    fun checkPermissions() {
        scope.launch {
            try {
                val storeAvailable = kHealth.isHealthStoreAvailable
                if (!storeAvailable) {
                    _state.update {
                        it.copy(
                            isHealthStoreAvailable = false,
                            errorMessage = "Health service is not available on this device."
                        )
                    }
                    return@launch
                }

                val granted = kHealth.checkPermissions(*permissions)
                val hasAnyAccess = granted.any { perm ->
                    when (perm) {
                        is KHPermission.StepCount -> perm.read || perm.write
                        is KHPermission.HeartRate -> perm.read || perm.write
                        is KHPermission.ActiveCaloriesBurned -> perm.read || perm.write
                        is KHPermission.Hydration -> perm.read || perm.write
                        is KHPermission.Distance -> perm.read || perm.write
                        else -> false
                    }
                }
                _state.update {
                    it.copy(
                        isHealthStoreAvailable = true,
                        isPermissionGranted = hasAnyAccess,
                        errorMessage = null
                    )
                }

                if (hasAnyAccess) {
                    refreshDashboardData()
                }
            } catch (t: Throwable) {
                _state.update {
                    it.copy(errorMessage = "Failed to check permissions: ${t.message}")
                }
            }
        }
    }

    fun requestPermissions() {
        scope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val granted = kHealth.requestPermissions(*permissions)
                val hasAccess = granted.any { perm ->
                    when (perm) {
                        is KHPermission.StepCount -> perm.read || perm.write
                        is KHPermission.HeartRate -> perm.read || perm.write
                        is KHPermission.ActiveCaloriesBurned -> perm.read || perm.write
                        is KHPermission.Hydration -> perm.read || perm.write
                        is KHPermission.Distance -> perm.read || perm.write
                        else -> false
                    }
                }
                _state.update {
                    it.copy(
                        isPermissionGranted = hasAccess,
                        statusMessage = if (hasAccess) "Permissions granted!" else "Permissions not granted",
                        isLoading = false
                    )
                }
                refreshDashboardData()
            } catch (t: Throwable) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Permission request failed: ${t.message}"
                    )
                }
            }
        }
    }

    fun refreshDashboardData() {
        scope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val now = Clock.System.now()
                val startTime = now - 24.hours

                val steps = try {
                    kHealth.readRecords(
                        KHReadRequest.StepCount(startTime = startTime, endTime = now)
                    ).sumOf { it.count }
                } catch (_: Throwable) {
                    0L
                }

                val heartRates = try {
                    kHealth.readRecords(
                        KHReadRequest.HeartRate(startTime = startTime, endTime = now)
                    )
                } catch (_: Throwable) {
                    emptyList()
                }
                val latestBpm = heartRates
                    .flatMap { it.samples }
                    .maxByOrNull { it.time }
                    ?.beatsPerMinute
                    ?.toDouble()

                val calories = try {
                    kHealth.readRecords(
                        KHReadRequest.ActiveCaloriesBurned(
                            unit = KHUnit.Energy.KiloCalorie,
                            startTime = startTime,
                            endTime = now
                        )
                    ).sumOf { it.value }
                } catch (_: Throwable) {
                    0.0
                }

                val hydration = try {
                    kHealth.readRecords(
                        KHReadRequest.Hydration(
                            unit = KHUnit.Volume.Liter,
                            startTime = startTime,
                            endTime = now
                        )
                    ).sumOf { it.value }
                } catch (_: Throwable) {
                    0.0
                }

                val distance = try {
                    kHealth.readRecords(
                        KHReadRequest.Distance(
                            unit = KHUnit.Length.Meter,
                            startTime = startTime,
                            endTime = now
                        )
                    ).sumOf { it.value }
                } catch (_: Throwable) {
                    0.0
                }

                _state.update { current ->
                    current.copy(
                        isLoading = false,
                        stepsToday = steps,
                        latestHeartRateBpm = latestBpm ?: current.latestHeartRateBpm,
                        activeCaloriesKcal = calories,
                        hydrationLiters = hydration,
                        distanceMeters = distance,
                        lastSyncTime = "Just now",
                        statusMessage = "Dashboard updated"
                    )
                }
            } catch (t: Throwable) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Failed to load health metrics: ${t.message}"
                    )
                }
            }
        }
    }

    fun logSteps(count: Long = 500) {
        scope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            val now = Clock.System.now()
            val record = KHRecord.StepCount(
                count = count,
                startTime = now - 5.minutes,
                endTime = now
            )
            val result = kHealth.writeRecords(record)
            handleWriteResult(result, "Logged +$count steps")
        }
    }

    fun logHydration(liters: Double = 0.25) {
        scope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            val now = Clock.System.now()
            val record = KHRecord.Hydration(
                unit = KHUnit.Volume.Liter,
                value = liters,
                startTime = now - 1.minutes,
                endTime = now
            )
            val result = kHealth.writeRecords(record)
            val ml = (liters * 1000).toInt()
            handleWriteResult(result, "Logged +${ml}mL water")
        }
    }

    fun logHeartRate(bpm: Double = 72.0) {
        scope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            val now = Clock.System.now()
            val record = KHRecord.HeartRate(
                samples = listOf(
                    KHHeartRateSample(
                        beatsPerMinute = bpm.toLong(),
                        time = now
                    )
                )
            )
            val result = kHealth.writeRecords(record)
            handleWriteResult(result, "Recorded ${bpm.toInt()} BPM")
        }
    }

    fun logWorkout(caloriesKcal: Double = 150.0, distanceMeters: Double = 1200.0) {
        scope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            val now = Clock.System.now()
            val duration = 20.minutes
            val calsRecord = KHRecord.ActiveCaloriesBurned(
                unit = KHUnit.Energy.KiloCalorie,
                value = caloriesKcal,
                startTime = now - duration,
                endTime = now
            )
            val distanceRecord = KHRecord.Distance(
                unit = KHUnit.Length.Meter,
                value = distanceMeters,
                startTime = now - duration,
                endTime = now
            )
            val result = kHealth.writeRecords(calsRecord, distanceRecord)
            handleWriteResult(result, "Logged workout: ${caloriesKcal.toInt()} kcal, ${distanceMeters.toInt()}m")
        }
    }

    private fun handleWriteResult(response: KHWriteResponse, successLog: String) {
        when (response) {
            is KHWriteResponse.Success -> {
                _state.update {
                    it.copy(
                        statusMessage = successLog,
                        recentLogs = listOf(successLog) + it.recentLogs.take(9)
                    )
                }
                refreshDashboardData()
            }
            is KHWriteResponse.SomeFailed -> {
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Some records failed to write.",
                        statusMessage = successLog
                    )
                }
                refreshDashboardData()
            }
            is KHWriteResponse.Failed -> {
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Write failed: ${response.throwable.message}"
                    )
                }
            }
        }
    }

    /**
     * Swift-friendly observer for SwiftUI on iOS and watchOS.
     * Returns a cancellation lambda.
     */
    fun observeState(onUpdate: (HealthDashboardState) -> Unit): () -> Unit {
        val job = scope.launch {
            state.collect { onUpdate(it) }
        }
        return { job.cancel() }
    }
}
