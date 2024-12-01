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

import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.datetime.toJavaInstant

actual class KHealth {
    constructor(activity: ComponentActivity) {
        this.activity = activity
        this.coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        this.permissionsChannel = Channel()
    }

    internal constructor(
        client: HealthConnectClient,
        coroutineScope: CoroutineScope,
        isHealthStoreAvailable: Boolean,
        permissionsChannel: Channel<Set<String>>
    ) {
        this.client = client
        this.coroutineScope = coroutineScope
        this.testIsHealthStoreAvailable = isHealthStoreAvailable
        this.permissionsChannel = permissionsChannel
        this.isTestMode = true
    }

    private var activity: ComponentActivity? = null

    private lateinit var client: HealthConnectClient
    private val coroutineScope: CoroutineScope
    private var testIsHealthStoreAvailable: Boolean? = null
    private val permissionsChannel: Channel<Set<String>>
    private var isTestMode = false

    private lateinit var permissionsLauncher: ActivityResultLauncher<Set<String>>

    actual fun initialise() {
        if (!::client.isInitialized) client = HealthConnectClient.getOrCreate(activity!!)
        if (!::permissionsLauncher.isInitialized) {
            val permissionContract = PermissionController.createRequestPermissionResultContract()
            permissionsLauncher = activity!!.registerForActivityResult(permissionContract) {
                coroutineScope.launch {
                    permissionsChannel.send(it)
                }
            }
        }
    }

    actual val isHealthStoreAvailable: Boolean
        get() = testIsHealthStoreAvailable
            ?: (HealthConnectClient.getSdkStatus(activity!!) == HealthConnectClient.SDK_AVAILABLE)

    private fun verifyHealthStoreAvailability() {
        if (!isHealthStoreAvailable) throw HealthStoreNotAvailableException
    }

    actual suspend fun checkPermissions(vararg permissions: KHPermission): Set<KHPermission> {
        verifyHealthStoreAvailability()
        val grantedPermissions = client.permissionController.getGrantedPermissions()
        return permissions.toPermissionsWithStatuses(grantedPermissions).toSet()
    }

    actual suspend fun requestPermissions(vararg permissions: KHPermission): Set<KHPermission> {
        verifyHealthStoreAvailability()
        val permissionSets = permissions.map { entry -> entry.toPermissions() }

        if (::permissionsLauncher.isInitialized) {
            permissionsLauncher.launch(permissionSets.flatten().map { it.first }.toSet())
        } else if (!isTestMode) {
            logError(
                throwable = HealthStoreNotInitialisedException,
                methodName = "requestPermissions"
            )
        }

        val grantedPermissions = permissionsChannel.receive()
        return permissions.toPermissionsWithStatuses(grantedPermissions).toSet()
    }

    actual suspend fun writeRecords(vararg records: KHRecord): KHWriteResponse {
        try {
            verifyHealthStoreAvailability()
            val hcRecords = records.mapNotNull { record -> record.toHCRecord() }
            logDebug("Inserting ${hcRecords.size} records...")
            val responseIDs = client.insertRecords(hcRecords).recordIdsList
            logDebug("Inserted ${responseIDs.size} records")
            return when {
                responseIDs.size != hcRecords.size && responseIDs.isEmpty() -> {
                    KHWriteResponse.Failed(Exception("No records were written!"))
                }

                responseIDs.size != hcRecords.size -> KHWriteResponse.SomeFailed

                else -> KHWriteResponse.Success
            }
        } catch (t: Throwable) {
            val parsedThrowable = when (t) {
                is SecurityException -> NoWriteAccessException(t.message?.extractHealthPermission())
                else -> t
            }
            logError(throwable = t, methodName = "writeRecords")
            return KHWriteResponse.Failed(parsedThrowable)
        }
    }

    actual suspend fun readRecords(request: KHReadRequest): List<KHRecord> {
        return try {
            val recordClass = request.toRecordClass() ?: return emptyList()

            val hcRecords = client.readRecords(
                request = ReadRecordsRequest(
                    recordType = recordClass,
                    timeRangeFilter = TimeRangeFilter.between(
                        startTime = request.startDateTime.toJavaInstant(),
                        endTime = request.endDateTime.toJavaInstant()
                    ),
                )
            ).records

            hcRecords.mapNotNull { record -> record.toKHRecordOrNull(request) }
        } catch (t: Throwable) {
            logError(throwable = t, methodName = "readRecords")
            emptyList()
        }
    }
}

internal enum class KHPermissionType { Read, Write }
