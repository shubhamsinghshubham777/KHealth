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
 * It is an expect class whose instance can normally be created without needing to pass any
 * parameters on Apple platforms, but on Android, it requires the reference to a ComponentActivity
 * to be able to subscribe to Health Connect permission events and detect whether the user has
 * granted a permission or not. Hence, its instance needs to be created separately on different
 * platforms and then passed on to the common/shared code.
 */
expect class KHealth {
    /**
     * This is an Android-only method required to set up KHealth's internal mechanism to allow it
     * to observe the user's permission selection (granted/denied) and make [checkPermissions] work
     * as expected.
     *
     * On Android, invoke it before the `onResume` method of the `ComponentActivity` (that the
     * current KHealth object is a part of) is called.
     * For example:
     *
     * ```kotlin
     * class MainActivity : ComponentActivity() {
     *     private val kHealth = KHealth(this)
     *
     *     override fun onCreate(savedInstanceState: Bundle?) {
     *         super.onCreate(savedInstanceState)
     *         // This is REQUIRED for the library to work properly (on Android only)
     *         kHealth.initialise()
     *         ... // Rest of your code
     *     }
     * }
     * ```
     *
     * > **Warning! -** Invoking this method after the ComponentActivity has reached its RESUMED
     * state will lead to errors like: `LifecycleOwner is attempting to register while current state
     * is STARTED. LifecycleOwners must call register before they are STARTED`. To avoid this, call
     * this method either inside `onCreate` or `onStart` lifecycle method of the ComponentActivity.
     *
     * On Apple, this method does nothing and should not be invoked.
     */
    fun initialise()

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
     * > **Note:** On Android, this method will only work if [initialise] has been called before
     * this method's invocation. On Apple, it has no such requirements.
     *
     * Code Example:
     * ```kotlin
     * val statuses = kHealth.checkPermissions(
     *     KHPermission.ActiveCaloriesBurned(read = true, write = true),
     *     KHPermission.HeartRate(read = true, write = false),
     * )
     * statuses.map { permWithStat ->
     *     println(
     *         "For permission: ${permWithStat.permission}, " +
     *                 "the read status is: ${permWithStat.readStatus} " +
     *                 "and write status is: ${permWithStat.writeStatus}"
     *     )
     * }
     * ```
     *
     * @param permissions [KHPermission]s for which the statuses will be fetched
     * @return Statuses clubbed with their requested permissions
     */
    suspend fun checkPermissions(vararg permissions: KHPermission): Set<KHPermission>

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
     * statuses.map { permWithStat ->
     *     println(
     *         "For permission: ${permWithStat.permission}, " +
     *                 "the read status is: ${permWithStat.readStatus} " +
     *                 "and write status is: ${permWithStat.writeStatus}"
     *     )
     * }
     * ```
     *
     * @param permissions [KHPermission]s for which the statuses will be fetched
     * @return Statuses clubbed with their requested permissions
     */
    suspend fun requestPermissions(vararg permissions: KHPermission): Set<KHPermission>

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
     *      // Add as many records as you need
     * )
     * when (insertResponse) {
     *     is KHWriteResponse.Failed ->
     *         println("Data insertion failed")
     *
     *     KHWriteResponse.SomeFailed ->
     *         println("Data insertion failed for some types")
     *
     *     KHWriteResponse.Success ->
     *         println("Data insertion successful")
     * }
     * ```
     *
     * @param records Data entries that needs to be written to the health store
     * @return The data insertion status (`Failed`/`SomeFailed`/`Success`)
     */
    suspend fun writeRecords(vararg records: KHRecord): KHWriteResponse

    /**
     * Returns the list of data records for the input read request sorted in ascending order on the
     * basis of the start date.
     *
     * @param request Responsible for filtering out data from the health store
     * @return The data records filtered on the basis of the input [KHReadRequest]
     */
    suspend fun readRecords(request: KHReadRequest): List<KHRecord>
}
