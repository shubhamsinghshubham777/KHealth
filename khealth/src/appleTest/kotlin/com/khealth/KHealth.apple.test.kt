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

import kotlinx.cinterop.UnsafeNumber
import kotlinx.coroutines.test.runTest
import platform.Foundation.NSError
import platform.HealthKit.HKAuthorizationStatus
import platform.HealthKit.HKAuthorizationStatusNotDetermined
import platform.HealthKit.HKAuthorizationStatusSharingAuthorized
import platform.HealthKit.HKAuthorizationStatusSharingDenied
import platform.HealthKit.HKHealthStore
import platform.HealthKit.HKObjectType
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class KHealthTests {
    private lateinit var store: HKHealthStore
    private lateinit var sut: KHealth

    @BeforeTest
    fun setUp() {
        store = object : HKHealthStore() {}
        sut = KHealth(store = store, isHealthStoreAvailable = true)
    }

    @Test
    fun showsCorrectHealthStoreAvailability() {
        assertTrue(sut.isHealthStoreAvailable)
        sut = KHealth(store = store, isHealthStoreAvailable = false)
        assertFalse(sut.isHealthStoreAvailable)
    }

    @OptIn(UnsafeNumber::class)
    @Test
    fun checkPermissionsWorksAsExpectedInAllScenarios() = runTest {
        val allGrantedPermission = KHPermission.ActiveCaloriesBurned(read = true, write = true)
        val readOnlyPermission = KHPermission.ActiveCaloriesBurned(read = true, write = false)
        val writeOnlyPermission = KHPermission.ActiveCaloriesBurned(read = false, write = true)
        val noPermission = KHPermission.ActiveCaloriesBurned(read = false, write = false)
        val grantStore = object : HKHealthStore() {
            override fun authorizationStatusForType(type: HKObjectType): HKAuthorizationStatus {
                return HKAuthorizationStatusSharingAuthorized
            }
        }
        val denyStore = object : HKHealthStore() {
            override fun authorizationStatusForType(type: HKObjectType): HKAuthorizationStatus {
                return HKAuthorizationStatusSharingDenied
            }
        }
        val notDeterminedStore = object : HKHealthStore() {
            override fun authorizationStatusForType(type: HKObjectType): HKAuthorizationStatus {
                return HKAuthorizationStatusNotDetermined
            }
        }

        // Case 1: When system grants WRITE permission
        sut = KHealth(store = grantStore, isHealthStoreAvailable = true)
        // Then allGranted request results in Denied-Granted response
        assertEquals(setOf(writeOnlyPermission), sut.checkPermissions(allGrantedPermission))
        // And read-only request results in Denied-Denied response
        assertEquals(setOf(noPermission), sut.checkPermissions(readOnlyPermission))
        // And write-only request results in Denied-Granted response
        assertEquals(setOf(writeOnlyPermission), sut.checkPermissions(writeOnlyPermission))
        // And no-permission request results in Denied-Denied response
        assertEquals(setOf(noPermission), sut.checkPermissions(noPermission))

        // Case 2: When system denies WRITE permission
        sut = KHealth(store = denyStore, isHealthStoreAvailable = true)
        // Then allGranted request results in Denied-Denied response
        assertEquals(setOf(noPermission), sut.checkPermissions(allGrantedPermission))
        // And read-only request results in Denied-Denied response
        assertEquals(setOf(noPermission), sut.checkPermissions(readOnlyPermission))
        // And write-only request results in Denied-Denied response
        assertEquals(setOf(noPermission), sut.checkPermissions(writeOnlyPermission))
        // And no-permission request results in Denied-Denied response
        assertEquals(setOf(noPermission), sut.checkPermissions(noPermission))

        // Case 3: When system does not determine WRITE permission
        sut = KHealth(store = notDeterminedStore, isHealthStoreAvailable = true)
        // Then allGranted request results in Denied-Denied response
        assertEquals(setOf(noPermission), sut.checkPermissions(allGrantedPermission))
        // And read-only request results in Denied-Denied response
        assertEquals(setOf(noPermission), sut.checkPermissions(readOnlyPermission))
        // And write-only request results in Denied-Denied response
        assertEquals(setOf(noPermission), sut.checkPermissions(writeOnlyPermission))
        // And no-permission request results in Denied-Denied response
        assertEquals(setOf(noPermission), sut.checkPermissions(noPermission))
    }

    @OptIn(UnsafeNumber::class)
    @Test
    fun requestPermissionsWorksAsExpectedInAllScenarios() = runTest {
        val caloriesPermission = KHPermission.ActiveCaloriesBurned(read = true, write = true)
        val heartRatePermission = KHPermission.HeartRate(read = true, write = true)
        sut = KHealth(
            store = object : HKHealthStore() {
                override fun authorizationStatusForType(type: HKObjectType): HKAuthorizationStatus {
                    return if (type == ObjectType.Quantity.ActiveCaloriesBurned) HKAuthorizationStatusSharingAuthorized
                    else HKAuthorizationStatusSharingDenied
                }

                override fun requestAuthorizationToShareTypes(
                    typesToShare: Set<*>?,
                    readTypes: Set<*>?,
                    completion: (Boolean, NSError?) -> Unit
                ) = completion(true, null)
            },
            isHealthStoreAvailable = true
        )
        assertEquals(
            setOf(
                caloriesPermission.copy(read = false),
                heartRatePermission.copy(read = false, write = false)
            ),
            sut.requestPermissions(caloriesPermission, heartRatePermission)
        )
    }
}
