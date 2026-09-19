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

import android.content.Context
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
import kotlin.time.toJavaInstant

/**
 * Creates a [KHealth] instance on Android using auto-detected application context.
 */
actual fun KHealth(): KHealth = AndroidKHealth(KHealthContext.get())

/**
 * Creates a [KHealth] instance on Android using the provided [Context].
 */
fun KHealth(context: Context): KHealth = AndroidKHealth(context)

/**
 * Creates a [KHealth] instance on Android bound to the given [ComponentActivity].
 */
fun KHealth(activity: ComponentActivity): KHealth = AndroidKHealth(activity)

/**
 * Internal factory for tests.
 */
internal fun KHealth(
    client: HealthConnectClient,
    coroutineScope: CoroutineScope,
    isHealthStoreAvailable: Boolean,
    permissionsChannel: Channel<Set<String>>
): KHealth = AndroidKHealth(
    client = client,
    coroutineScope = coroutineScope,
    isHealthStoreAvailable = isHealthStoreAvailable,
    permissionsChannel = permissionsChannel
)

internal class AndroidKHealth : KHealth {
    private val appContext: Context
    private var activity: ComponentActivity? = null
    private lateinit var client: HealthConnectClient
    private val coroutineScope: CoroutineScope
    private var testIsHealthStoreAvailable: Boolean? = null
    private val permissionsChannel: Channel<Set<String>>
    private var isTestMode = false
    private var permissionsLauncher: ActivityResultLauncher<Set<String>>? = null

    constructor(context: Context) {
        this.appContext = context.applicationContext
        this.coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        this.permissionsChannel = Channel()
        this.client = HealthConnectClient.getOrCreate(appContext)
    }

    constructor(activity: ComponentActivity) {
        this.appContext = activity.applicationContext
        this.activity = activity
        this.coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        this.permissionsChannel = Channel()
        this.client = HealthConnectClient.getOrCreate(appContext)
    }

    internal constructor(
        client: HealthConnectClient,
        coroutineScope: CoroutineScope,
        isHealthStoreAvailable: Boolean,
        permissionsChannel: Channel<Set<String>>
    ) {
        this.appContext = client.javaClass.classLoader?.let { null } ?: run {
            try { KHealthContext.get() } catch (_: Throwable) { null }
        } ?: run {
            // Stub for test when context is not available
            null as Context?
        } ?: run {
            null as Any?
        }.let {
            // In test mode, appContext is not used
            null as Context?
        } ?: run {
            // fallback dummy context not needed in test mode
            object : android.content.ContextWrapper(null) {}
        }
        this.client = client
        this.coroutineScope = coroutineScope
        this.testIsHealthStoreAvailable = isHealthStoreAvailable
        this.permissionsChannel = permissionsChannel
        this.isTestMode = true
    }

    @Deprecated("KHealth is auto-initialized on Android via ContentProvider and doesn't require explicit initialization.")
    override fun initialise() {
        if (!::client.isInitialized) {
            client = HealthConnectClient.getOrCreate(appContext)
        }
        activity?.let { setupLauncher(it) }
    }

    fun bindActivity(activity: ComponentActivity) {
        this.activity = activity
        setupLauncher(activity)
    }

    private fun setupLauncher(activity: ComponentActivity) {
        if (permissionsLauncher == null) {
            val permissionContract = PermissionController.createRequestPermissionResultContract()
            permissionsLauncher = activity.registerForActivityResult(permissionContract) {
                coroutineScope.launch {
                    permissionsChannel.send(it)
                }
            }
        }
    }

    override val isHealthStoreAvailable: Boolean
        get() = testIsHealthStoreAvailable
            ?: (HealthConnectClient.getSdkStatus(appContext) == HealthConnectClient.SDK_AVAILABLE)

    private fun verifyHealthStoreAvailability() {
        if (!isHealthStoreAvailable) throw HealthStoreNotAvailableException
    }

    override suspend fun checkPermissions(vararg permissions: KHPermission): Set<KHPermission> {
        verifyHealthStoreAvailability()
        val grantedPermissions = client.permissionController.getGrantedPermissions()
        return permissions.toPermissionsWithStatuses(grantedPermissions).toSet()
    }

    override suspend fun requestPermissions(vararg permissions: KHPermission): Set<KHPermission> {
        verifyHealthStoreAvailability()
        val permissionSets = permissions.map { entry -> entry.toPermissions() }

        val launcher = permissionsLauncher
        if (launcher != null) {
            launcher.launch(permissionSets.flatten().map { it.first }.toSet())
        } else if (!isTestMode) {
            activity?.let {
                setupLauncher(it)
                permissionsLauncher?.launch(permissionSets.flatten().map { it.first }.toSet())
            } ?: run {
                logError(
                    throwable = HealthStoreNotInitialisedException,
                    methodName = "requestPermissions"
                )
            }
        }

        val grantedPermissions = permissionsChannel.receive()
        return permissions.toPermissionsWithStatuses(grantedPermissions).toSet()
    }

    override suspend fun writeRecords(vararg records: KHRecord): KHWriteResponse {
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

    @Suppress("UNCHECKED_CAST")
    override suspend fun <T : KHRecord> readRecords(request: KHReadRequest<T>): List<T> {
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

            hcRecords.mapNotNull { record -> record.toKHRecordOrNull(request) } as List<T>
        } catch (t: Throwable) {
            logError(throwable = t, methodName = "readRecords")
            emptyList()
        }
    }
}

internal enum class KHPermissionType { Read, Write }
