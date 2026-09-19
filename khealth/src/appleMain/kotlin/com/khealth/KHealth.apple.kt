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

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import platform.Foundation.NSError
import platform.HealthKit.HKHealthStore
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

actual fun KHealth(): KHealth = AppleKHealth()

internal fun KHealth(store: HKHealthStore, isHealthStoreAvailable: Boolean): KHealth =
    AppleKHealth(store, isHealthStoreAvailable)

internal class AppleKHealth : KHealth {
    constructor() {
        this.store = HKHealthStore()
    }

    internal constructor(store: HKHealthStore, isHealthStoreAvailable: Boolean) {
        this.store = store
        this.testIsHealthStoreAvailable = isHealthStoreAvailable
    }

    private var store: HKHealthStore
    private var testIsHealthStoreAvailable: Boolean? = null

    override val isHealthStoreAvailable: Boolean
        get() = testIsHealthStoreAvailable ?: try {
            HKHealthStore.isHealthDataAvailable()
        } catch (t: Throwable) {
            false
        }

    private fun verifyHealthStoreAvailability() {
        if (!isHealthStoreAvailable) throw HealthStoreNotAvailableException
    }

    @Deprecated("KHealth is auto-initialized on Android and doesn't require initialization on Apple.")
    override fun initialise() = Unit

    override suspend fun checkPermissions(vararg permissions: KHPermission): Set<KHPermission> {
        return try {
            verifyHealthStoreAvailability()
            permissions.map { ApplePermissionMapper.checkPermission(store, it) }.toSet()
        } catch (t: Throwable) {
            logError(t, methodName = "checkPermissions")
            emptySet()
        }
    }

    override suspend fun requestPermissions(
        vararg permissions: KHPermission
    ): Set<KHPermission> = suspendCoroutine { continuation ->
        try {
            verifyHealthStoreAvailability()
            val (readPermissions, writePermissions) = ApplePermissionMapper.mapPermissions(permissions)
            val coroutineScope = CoroutineScope(continuation.context)

            store.requestAuthorizationToShareTypes(
                typesToShare = writePermissions,
                readTypes = readPermissions,
            ) { _, error ->
                if (error != null) {
                    logError(throwable = error.toException(), methodName = "requestPermissions [NS]")
                    continuation.resumeWithException(error.toException())
                } else {
                    coroutineScope.launch { continuation.resume(checkPermissions(*permissions)) }
                }
            }
        } catch (t: Throwable) {
            continuation.resumeWithException(t)
        }
    }

    override suspend fun writeRecords(vararg records: KHRecord): KHWriteResponse = suspendCoroutine { continuation ->
        try {
            verifyHealthStoreAvailability()
            val samples = records.flatMap { AppleRecordWriter.toSamples(it) }

            store.saveObjects(samples) { success, error ->
                when {
                    error != null -> {
                        val exception = error.toException()
                        val parsedException = when {
                            exception.message?.contains(HKNotAuthorizedMessage) == true -> NoWriteAccessException()
                            else -> exception
                        }
                        logError(parsedException, methodName = "writeRecords [NS]")
                        continuation.resume(KHWriteResponse.Failed(parsedException))
                    }
                    success -> continuation.resume(KHWriteResponse.Success)
                    else -> continuation.resume(KHWriteResponse.Failed(throwable = NoWriteAccessException()))
                }
            }
        } catch (t: Throwable) {
            logError(t, methodName = "writeRecords")
            continuation.resume(KHWriteResponse.Failed(t))
        }
    }

    override suspend fun <T : KHRecord> readRecords(request: KHReadRequest<T>): List<T> {
        return try {
            verifyHealthStoreAvailability()
            AppleRecordReader.readRecords(store, request)
        } catch (t: Throwable) {
            logError(throwable = t, methodName = "readRecords")
            emptyList()
        }
    }
}

internal fun NSError.toException() = Exception(this.localizedDescription)
internal const val HKNotAuthorizedMessage = "Authorization is not determined"
