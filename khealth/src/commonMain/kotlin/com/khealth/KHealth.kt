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

package com.khealth

/**
 * KHealth is a Kotlin Multiplatform wrapper around Android's Health Connect and Apple's HealthKit
 * APIs. It provides a set of convenient properties and methods to perform most common operations
 * easily (like checking/requesting permissions, reading/writing data, and more).
 *
 * It is an interface that can be easily mocked, tested, and injected. An instance can be created
 * directly in common code using [KHealth] or [createKHealth].
 */
interface KHealth {
    /**
     * Legacy initialization method on Android.
     * With the updated API, initialization happens automatically upon instantiation,
     * so manual invocation is no longer required.
     */
    @Deprecated(
        message = "initialise() is no longer required and is kept for backward compatibility.",
        replaceWith = ReplaceWith("")
    )
    fun initialise() = Unit

    /**
     * Returns whether the Health Connect SDK (on Android) and HealthKit SDK (on Apple) is available
     * on the current system.
     *
     * On Android versions below 14, the [Health Connect](https://play.google.com/store/apps/details?id=com.google.android.apps.healthdata&hl=en_IN)
     * app needs to be installed from Play Store for this property to return `true`.
     *
     * On Android versions 14 and above, Health Connect is a part of the operating system and
     * should always return `true`.
     */
    val isHealthStoreAvailable: Boolean

    /**
     * Returns the status of the requested permissions using the [KHPermission] object. Unsupported
     * permissions are silently discarded from the request list and are not included in the returned
     * set.
     *
     * Code Example:
     * ```kotlin
     * val statuses = kHealth.checkPermissions(
     *     KHPermission.ActiveCaloriesBurned(read = true, write = true),
     *     KHPermission.HeartRate(read = true, write = false),
     * )
     * ```
     *
     * @param permissions [KHPermission]s for which the statuses will be fetched
     * @return Statuses clubbed with their requested permissions
     */
    suspend fun checkPermissions(vararg permissions: KHPermission): Set<KHPermission>

    /**
     * Collection overload for [checkPermissions].
     */
    suspend fun checkPermissions(permissions: Collection<KHPermission>): Set<KHPermission> =
        checkPermissions(*permissions.toTypedArray())

    /**
     * Initiates a request to the operating system for the input list of [KHPermission]s. Similar
     * to [checkPermissions], this method also returns the set of requested permissions clubbed
     * with their statuses and omits permissions that are not supported on the current platform.
     *
     * Code Example:
     * ```kotlin
     * val statuses = kHealth.requestPermissions(
     *     KHPermission.ActiveCaloriesBurned(read = true, write = true),
     *     KHPermission.HeartRate(read = true, write = false),
     * )
     * ```
     *
     * @param permissions [KHPermission]s for which the statuses will be fetched
     * @return Statuses clubbed with their requested permissions
     */
    suspend fun requestPermissions(vararg permissions: KHPermission): Set<KHPermission>

    /**
     * Collection overload for [requestPermissions].
     */
    suspend fun requestPermissions(permissions: Collection<KHPermission>): Set<KHPermission> =
        requestPermissions(*permissions.toTypedArray())

    /**
     * Writes the provided [KHRecord]s into the health store and returns its insertion status.
     *
     * Code Example:
     * ```kotlin
     * val insertResponse = kHealth.writeRecords(
     *      KHRecord.ActiveCaloriesBurned(
     *          unit = KHUnit.Energy.KiloCalorie,
     *          value = 3.0,
     *          startTime = Clock.System.now().minus(10.minutes),
     *          endTime = Clock.System.now(),
     *      ),
     * )
     * ```
     *
     * @param records Data entries that needs to be written to the health store
     * @return The data insertion status (`Failed`/`SomeFailed`/`Success`)
     */
    suspend fun writeRecords(vararg records: KHRecord): KHWriteResponse

    /**
     * Collection overload for [writeRecords].
     */
    suspend fun writeRecords(records: Collection<KHRecord>): KHWriteResponse =
        writeRecords(*records.toTypedArray())

    /**
     * Returns the strongly-typed list of data records for the input read request sorted in ascending
     * order on the basis of the start date.
     *
     * Code Example:
     * ```kotlin
     * val steps: List<KHRecord.StepCount> = kHealth.readRecords(
     *     KHReadRequest.StepCount(
     *         startTime = Clock.System.now().minus(1.days),
     *         endTime = Clock.System.now()
     *     )
     * )
     * ```
     *
     * @param request Responsible for filtering out data from the health store
     * @return The data records filtered on the basis of the input [KHReadRequest]
     */
    suspend fun <T : KHRecord> readRecords(request: KHReadRequest<T>): List<T>
}

/**
 * Creates a platform-appropriate instance of [KHealth].
 *
 * - On Apple (iOS/watchOS), initializes HealthKit integration.
 * - On Android, initializes Health Connect integration using auto-detected application context.
 */
expect fun KHealth(): KHealth

/**
 * Factory function to create a [KHealth] instance in common code.
 */
fun createKHealth(): KHealth = KHealth()
