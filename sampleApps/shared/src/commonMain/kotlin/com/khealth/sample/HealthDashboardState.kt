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

/**
 * UI State for the Health Dashboard across Android, iOS, and watchOS.
 */
data class HealthDashboardState(
    val isHealthStoreAvailable: Boolean = true,
    val isLoading: Boolean = false,
    val isPermissionGranted: Boolean = false,
    val stepsToday: Long = 0,
    val stepGoal: Long = 10000,
    val latestHeartRateBpm: Double? = null,
    val activeCaloriesKcal: Double = 0.0,
    val hydrationLiters: Double = 0.0,
    val distanceMeters: Double = 0.0,
    val lastSyncTime: String? = null,
    val statusMessage: String? = null,
    val errorMessage: String? = null,
    val recentLogs: List<String> = emptyList()
) {
    val stepProgress: Float
        get() = if (stepGoal > 0) (stepsToday.toFloat() / stepGoal.toFloat()).coerceIn(0f, 1f) else 0f

    val distanceKm: Double
        get() = distanceMeters / 1000.0

    val hydrationMl: Int
        get() = (hydrationLiters * 1000).toInt()
}
