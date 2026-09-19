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

package com.khealth.sample.android

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.khealth.KHealth
import com.khealth.sample.HealthDashboardManager
import com.khealth.sample.HealthDashboardState

class MainActivity : ComponentActivity() {
    private val kHealth by lazy { KHealth(this) }
    private val dashboardManager by lazy { HealthDashboardManager(kHealth) }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        @Suppress("DEPRECATION")
        kHealth.initialise()

        setContent {
            val context = LocalContext.current
            val isDark = isSystemInDarkTheme()
            val colorScheme = when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && isDark -> dynamicDarkColorScheme(context)
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !isDark -> dynamicLightColorScheme(context)
                isDark -> darkColorScheme()
                else -> lightColorScheme()
            }

            val state by dashboardManager.state.collectAsState()
            val snackbarHostState = remember { SnackbarHostState() }

            LaunchedEffect(state.statusMessage) {
                state.statusMessage?.let { snackbarHostState.showSnackbar(it) }
            }
            LaunchedEffect(state.errorMessage) {
                state.errorMessage?.let { snackbarHostState.showSnackbar(it) }
            }

            MaterialTheme(colorScheme = colorScheme) {
                Scaffold(
                    snackbarHost = { SnackbarHost(snackbarHostState) },
                    topBar = {
                        Column {
                            TopAppBar(
                                title = {
                                    Text(
                                        "KHealth Dashboard",
                                        fontWeight = FontWeight.Bold
                                    )
                                },
                                actions = {
                                    androidx.compose.material3.TextButton(
                                        onClick = { dashboardManager.refreshDashboardData() }
                                    ) {
                                        Text("Refresh", fontWeight = FontWeight.SemiBold)
                                    }
                                },
                                colors = TopAppBarDefaults.topAppBarColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                )
                            )
                            if (state.isLoading) {
                                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                            }
                        }
                    }
                ) { innerPadding ->
                    HealthDashboardContent(
                        state = state,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        onRequestPermissions = { dashboardManager.requestPermissions() },
                        onLogSteps = { count -> dashboardManager.logSteps(count) },
                        onLogHydration = { liters -> dashboardManager.logHydration(liters) },
                        onLogHeartRate = { bpm -> dashboardManager.logHeartRate(bpm) },
                        onLogWorkout = { cals, dist -> dashboardManager.logWorkout(cals, dist) }
                    )
                }
            }
        }
    }
}

@Composable
fun HealthDashboardContent(
    state: HealthDashboardState,
    modifier: Modifier = Modifier,
    onRequestPermissions: () -> Unit,
    onLogSteps: (Long) -> Unit,
    onLogHydration: (Double) -> Unit,
    onLogHeartRate: (Double) -> Unit,
    onLogWorkout: (Double, Double) -> Unit
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Availability Warning Banner
        if (!state.isHealthStoreAvailable) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "⚠️",
                        fontSize = 24.sp
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            "Health Connect Unavailable",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Text(
                            "Please install or enable Health Connect from the Play Store.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }
        }

        // Permissions Banner
        Card(
            colors = CardDefaults.cardColors(
                containerColor = if (state.isPermissionGranted)
                    MaterialTheme.colorScheme.secondaryContainer
                else
                    MaterialTheme.colorScheme.surfaceVariant
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        if (state.isPermissionGranted) "✅" else "🔒",
                        fontSize = 22.sp
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = if (state.isPermissionGranted) "Permissions Active" else "Permissions Needed",
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = if (state.isPermissionGranted) "Reading and writing health data" else "Access required to track metrics",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
                if (!state.isPermissionGranted) {
                    Button(onClick = onRequestPermissions) {
                        Text("Grant")
                    }
                }
            }
        }

        // Step Count Card
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    "Steps Today",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        "${state.stepsToday}",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        " / ${state.stepGoal} steps",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                        modifier = Modifier.padding(start = 8.dp, bottom = 6.dp)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                LinearProgressIndicator(
                    progress = { state.stepProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilledTonalButton(onClick = { onLogSteps(500) }) {
                        Text("+500 Steps")
                    }
                    FilledTonalButton(onClick = { onLogSteps(1000) }) {
                        Text("+1,000 Steps")
                    }
                }
            }
        }

        // Heart Rate Card
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f)
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "❤️",
                        fontSize = 22.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Heart Rate",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = state.latestHeartRateBpm?.let { "${it.toInt()} BPM" } ?: "-- BPM",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedButton(onClick = { onLogHeartRate(72.0) }) {
                    Text("Record 72 BPM")
                }
            }
        }

        // Hydration Card
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    "Hydration",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "${state.hydrationMl} mL",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilledTonalButton(onClick = { onLogHydration(0.25) }) {
                        Text("+250 mL (Glass)")
                    }
                    FilledTonalButton(onClick = { onLogHydration(0.50) }) {
                        Text("+500 mL (Bottle)")
                    }
                }
            }
        }

        // Workout & Calories Card
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    "Activity & Workout",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Active Burn", style = MaterialTheme.typography.bodySmall)
                        Text(
                            "${state.activeCaloriesKcal.toInt()} kcal",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Column {
                        Text("Distance", style = MaterialTheme.typography.bodySmall)
                        val distFormatted = ((state.distanceKm * 10).toInt()) / 10.0
                        Text(
                            "$distFormatted km",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { onLogWorkout(150.0, 1200.0) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Log Workout (150 kcal, 1.2 km)")
                }
            }
        }

        // Recent Actions List
        if (state.recentLogs.isNotEmpty()) {
            Text(
                "Recent Actions",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            state.recentLogs.take(5).forEach { log ->
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "• $log",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }
    }
}
