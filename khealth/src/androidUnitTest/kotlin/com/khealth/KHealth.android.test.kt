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

import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode.Companion.exactly
import dev.mokkery.verifySuspend
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class KHealthTests {
    private lateinit var sut: KHealth
    private lateinit var client: HealthConnectClient
    private lateinit var permissionController: PermissionController
    private lateinit var permissionsChannel: Channel<Set<String>>
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var coroutineScope: CoroutineScope

    @BeforeTest
    fun setUp() {
        client = mock<HealthConnectClient>()
        coroutineScope = CoroutineScope(testDispatcher)
        permissionController = mock<PermissionController>()
        permissionsChannel = Channel()

        every { client.permissionController } returns permissionController

        sut = KHealth(
            client = client,
            coroutineScope = coroutineScope,
            isHealthStoreAvailable = true,
            permissionsChannel = permissionsChannel,
        )
    }

    @AfterTest
    fun tearDown() {
        permissionsChannel.close()
        coroutineScope.cancel()
    }

    @Test
    fun showsCorrectHealthStoreAvailability() = runTest(testDispatcher) {
        assertEquals(true, sut.isHealthStoreAvailable)
        sut = KHealth(
            client = client,
            coroutineScope = coroutineScope,
            isHealthStoreAvailable = false,
            permissionsChannel = permissionsChannel,
        )
        assertEquals(false, sut.isHealthStoreAvailable)
    }

    @Test
    fun checkPermissionsFailsOnHealthStoreUnavailable() = runTest(testDispatcher) {
        sut = KHealth(
            client = client,
            coroutineScope = coroutineScope,
            isHealthStoreAvailable = false,
            permissionsChannel = permissionsChannel,
        )
        val wasSuccess = try {
            sut.checkPermissions()
            true
        } catch (t: Throwable) {
            false
        }
        assertEquals(false, wasSuccess)
    }

    @Test
    fun checkPermissionsWorksAsExpectedInAllScenarios() = runTest(testDispatcher) {
        val allGrantedPermission = KHPermission.ActiveCaloriesBurned(read = true, write = true)
        val writeOnlyPermission = KHPermission.ActiveCaloriesBurned(read = false, write = true)
        val readOnlyPermission = KHPermission.ActiveCaloriesBurned(read = true, write = false)

        // Case 1: When system grants all permissions
        everySuspend { permissionController.getGrantedPermissions() } returns setOf(
            READ_PERMISSION,
            WRITE_PERMISSION
        )
        // Then allGranted request results in allGranted response
        assertEquals(setOf(allGrantedPermission), sut.checkPermissions(allGrantedPermission))

        // Case 2: When system grants WRITE only permissions
        everySuspend { permissionController.getGrantedPermissions() } returns setOf(WRITE_PERMISSION)
        // Then allGranted request results in deniedRead response
        assertEquals(setOf(writeOnlyPermission), sut.checkPermissions(allGrantedPermission))

        // Case 3: When system grants READ only permissions
        everySuspend { permissionController.getGrantedPermissions() } returns setOf(READ_PERMISSION)
        // Then allGranted request results in deniedWrite response
        assertEquals(setOf(readOnlyPermission), sut.checkPermissions(allGrantedPermission))

        // Case 4: When system grants WRITE only permissions
        everySuspend { permissionController.getGrantedPermissions() } returns setOf(WRITE_PERMISSION)
        // Then writeOnly request results in Denied-Granted response
        assertEquals(setOf(writeOnlyPermission), sut.checkPermissions(writeOnlyPermission))

        // Case 5: When system grants READ only permissions
        everySuspend { permissionController.getGrantedPermissions() } returns setOf(READ_PERMISSION)
        // Then readOnly request results in Granted-Denied response
        assertEquals(setOf(readOnlyPermission), sut.checkPermissions(readOnlyPermission))

        verifySuspend(exactly(5)) {
            client.permissionController
            permissionController.getGrantedPermissions()
        }
    }

    @Test
    fun requestPermissionsWorksAsExpectedInAllScenarios() = runTest(testDispatcher) {
        var requestedPermission = KHPermission.ActiveCaloriesBurned(read = true, write = true)
        val allGrantedPermission = KHPermission.ActiveCaloriesBurned(read = true, write = true)
        val readOnlyPermission = KHPermission.ActiveCaloriesBurned(read = true, write = false)
        val writeOnlyPermission = KHPermission.ActiveCaloriesBurned(read = false, write = true)
        val noPermission = KHPermission.ActiveCaloriesBurned(read = false, write = false)

        // Case 1: App requested all perms & user granted all perms
        var expectedResult = setOf(allGrantedPermission)
        launch { permissionsChannel.send(setOf(READ_PERMISSION, WRITE_PERMISSION)) }
        assertEquals(expectedResult, sut.requestPermissions(requestedPermission))

        // Case 2: App requested all perms but user granted WRITE only
        expectedResult = setOf(writeOnlyPermission)
        launch { permissionsChannel.send(setOf(WRITE_PERMISSION)) }
        assertEquals(expectedResult, sut.requestPermissions(requestedPermission))

        // Case 3: App requested all perms but user granted READ only
        expectedResult = setOf(readOnlyPermission)
        launch { permissionsChannel.send(setOf(READ_PERMISSION)) }
        assertEquals(expectedResult, sut.requestPermissions(requestedPermission))

        // Case 4: App requested all perms but user granted none
        expectedResult = setOf(noPermission)
        launch { permissionsChannel.send(emptySet()) }
        assertEquals(expectedResult, sut.requestPermissions(requestedPermission))

        // Case 5: App requested READ only perm and user granted it
        requestedPermission = KHPermission.ActiveCaloriesBurned(read = true, write = false)
        expectedResult = setOf(readOnlyPermission)
        launch { permissionsChannel.send(setOf(READ_PERMISSION)) }
        assertEquals(expectedResult, sut.requestPermissions(requestedPermission))

        // Case 6: App requested WRITE only perm and user granted it
        requestedPermission = KHPermission.ActiveCaloriesBurned(read = false, write = true)
        expectedResult = setOf(writeOnlyPermission)
        launch { permissionsChannel.send(setOf(WRITE_PERMISSION)) }
        assertEquals(expectedResult, sut.requestPermissions(requestedPermission))

        // Case 7: App requested READ only perm and user denied it
        requestedPermission = KHPermission.ActiveCaloriesBurned(read = true, write = false)
        expectedResult = setOf(noPermission)
        launch { permissionsChannel.send(emptySet()) }
        assertEquals(expectedResult, sut.requestPermissions(requestedPermission))

        // Case 8: App requested WRITE only perm and user denied it
        requestedPermission = KHPermission.ActiveCaloriesBurned(read = false, write = true)
        expectedResult = setOf(noPermission)
        launch { permissionsChannel.send(emptySet()) }
        assertEquals(expectedResult, sut.requestPermissions(requestedPermission))

        // Case 9: App requested no perms
        requestedPermission = KHPermission.ActiveCaloriesBurned(read = false, write = false)
        expectedResult = setOf(noPermission)
        launch { permissionsChannel.send(emptySet()) }
        assertEquals(expectedResult, sut.requestPermissions(requestedPermission))
    }
}

private const val READ_PERMISSION = "android.permission.health.READ_ACTIVE_CALORIES_BURNED"
private const val WRITE_PERMISSION = "android.permission.health.WRITE_ACTIVE_CALORIES_BURNED"
