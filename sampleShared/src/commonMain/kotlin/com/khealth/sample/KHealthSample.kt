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

import com.khealth.KHCervicalMucusAppearance
import com.khealth.KHCyclingPedalingCadenceSample
import com.khealth.KHEither
import com.khealth.KHExerciseType
import com.khealth.KHHeartRateSample
import com.khealth.KHMealType
import com.khealth.KHMenstruationFlowType
import com.khealth.KHOvulationTestResult
import com.khealth.KHPermission
import com.khealth.KHPowerSample
import com.khealth.KHReadRequest
import com.khealth.KHRecord
import com.khealth.KHSleepStage
import com.khealth.KHSleepStageSample
import com.khealth.KHSpeedSample
import com.khealth.KHUnit
import com.khealth.KHealth
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes

val permissions = arrayOf(
    KHPermission.ActiveCaloriesBurned(read = true, write = true),
    KHPermission.BasalMetabolicRate(read = true, write = true),
    KHPermission.BloodGlucose(read = true, write = true),
    KHPermission.BloodPressure(
        readSystolic = true,
        writeSystolic = true,
        readDiastolic = true,
        writeDiastolic = true
    ),
    KHPermission.BodyFat(read = true, write = true),
    KHPermission.BodyTemperature(read = true, write = true),
    KHPermission.BodyWaterMass(read = true, write = true),
    KHPermission.BoneMass(read = true, write = true),
    KHPermission.CervicalMucus(read = true, write = true),
    KHPermission.CyclingPedalingCadence(read = true, write = true),
    KHPermission.CyclingSpeed(read = true, write = true),
    KHPermission.Distance(read = true, write = true),
    KHPermission.ElevationGained(read = true, write = true),
    KHPermission.Exercise(read = true, write = true),
    KHPermission.FloorsClimbed(read = true, write = true),
    KHPermission.HeartRate(read = true, write = true),
    KHPermission.HeartRateVariability(read = true, write = true),
    KHPermission.Height(read = true, write = true),
    KHPermission.Hydration(read = true, write = true),
    KHPermission.IntermenstrualBleeding(read = true, write = true),
    KHPermission.LeanBodyMass(read = true, write = true),
    KHPermission.MenstruationPeriod(read = true, write = true),
    KHPermission.MenstruationFlow(read = true, write = true),
    KHPermission.Nutrition(
        readBiotin = true,
        writeBiotin = true,
        readCaffeine = true,
        writeCaffeine = true
    ),
    KHPermission.OvulationTest(read = true, write = true),
    KHPermission.OxygenSaturation(read = true, write = true),
    KHPermission.Power(read = true, write = true),
    KHPermission.RespiratoryRate(read = true, write = true),
    KHPermission.RestingHeartRate(read = true, write = true),
    KHPermission.RunningSpeed(read = true, write = true),
    KHPermission.SexualActivity(read = true, write = true),
    KHPermission.SleepSession(read = true, write = true),
    KHPermission.Speed(read = true, write = true),
    KHPermission.StepCount(read = true, write = true),
    KHPermission.Vo2Max(read = true, write = true),
    KHPermission.Weight(read = true, write = true),
    KHPermission.WheelChairPushes(read = true, write = true),
)

private val coroutineScope = MainScope()

fun sampleCheckAllPerms(kHealth: KHealth) {
    coroutineScope.launch {
        val response = kHealth.checkPermissions(*permissions)
        println("Check Permissions Response: $response")
    }
}

fun sampleRequestAllPerms(kHealth: KHealth) {
    coroutineScope.launch {
        try {
            val response = kHealth.requestPermissions(*permissions)
            println("Request Permissions Response: $response")
        } catch (t: Throwable) {
            println("Request Permissions Error: $t")
        }
    }
}

fun sampleWriteData(kHealth: KHealth) {
    coroutineScope.launch {
        val insertResponse = kHealth.writeRecords(
            KHRecord.ActiveCaloriesBurned(
                unit = KHUnit.Energy.KiloCalorie,
                value = 3.0,
                startTime = Clock.System.now().minus(10.minutes),
                endTime = Clock.System.now(),
            ),
            KHRecord.BasalMetabolicRate(
                unit = KHEither(left = KHUnit.Power.Watt, right = KHUnit.Energy.KiloCalorie),
                value = 4.0,
                time = Clock.System.now(),
            ),
            KHRecord.BloodGlucose(
                unit = KHUnit.BloodGlucose.MillimolesPerLiter,
                value = 2.5,
                time = Clock.System.now(),
            ),
            KHRecord.BloodPressure(
                unit = KHUnit.Pressure.MillimeterOfMercury,
                systolicValue = 121.0,
                diastolicValue = 79.0,
                time = Clock.System.now(),
            ),
            KHRecord.BodyFat(
                percentage = 15.0,
                time = Clock.System.now(),
            ),
            KHRecord.BodyTemperature(
                unit = KHUnit.Temperature.Fahrenheit,
                value = 98.3,
                time = Clock.System.now(),
            ),
            KHRecord.BodyWaterMass(
                unit = KHUnit.Mass.Gram,
                value = 192.5,
                time = Clock.System.now(),
            ),
            KHRecord.BoneMass(
                unit = KHUnit.Mass.Gram,
                value = 93.77,
                time = Clock.System.now(),
            ),
            KHRecord.CervicalMucus(
                appearance = KHCervicalMucusAppearance.EggWhite,
                time = Clock.System.now(),
            ),
            KHRecord.CyclingPedalingCadence(
                samples = listOf(
                    KHCyclingPedalingCadenceSample(
                        revolutionsPerMinute = 48.0,
                        time = Clock.System.now() - 9.minutes,
                    ),
                    KHCyclingPedalingCadenceSample(
                        revolutionsPerMinute = 56.0,
                        time = Clock.System.now(),
                    ),
                ),
                startTime = Clock.System.now() - 10.minutes,
                endTime = Clock.System.now(),
            ),
            KHRecord.CyclingSpeed(
                samples = listOf(
                    KHSpeedSample(
                        unit = KHUnit.Velocity.KilometersPerHour,
                        value = 30.0,
                        time = Clock.System.now(),
                    )
                ),
            ),
            KHRecord.Distance(
                unit = KHUnit.Length.Meter,
                value = 4.9,
                startTime = Clock.System.now().minus(2.minutes),
                endTime = Clock.System.now(),
            ),
            KHRecord.ElevationGained(
                unit = KHUnit.Length.Meter,
                value = 2.2,
                startTime = Clock.System.now().minus(2.minutes),
                endTime = Clock.System.now(),
            ),
            KHRecord.Exercise(
                type = KHExerciseType.AmericanFootball,
                startTime = Clock.System.now().minus(40.minutes),
                endTime = Clock.System.now(),
            ),
            KHRecord.Exercise(
                type = KHExerciseType.Archery,
                startTime = Clock.System.now().minus(30.minutes),
                endTime = Clock.System.now(),
            ),
            KHRecord.FloorsClimbed(
                floors = 5.0,
                startTime = Clock.System.now().minus(2.minutes),
                endTime = Clock.System.now(),
            ),
            KHRecord.HeartRate(
                samples = listOf(
                    KHHeartRateSample(beatsPerMinute = 135, time = Clock.System.now())
                ),
            ),
            KHRecord.HeartRateVariability(
                heartRateVariabilityMillis = 51.9,
                time = Clock.System.now(),
            ),
            KHRecord.Height(
                unit = KHUnit.Length.Meter,
                value = 1.78,
                time = Clock.System.now(),
            ),
            KHRecord.Hydration(
                unit = KHUnit.Volume.Liter,
                value = 3.3,
                startTime = Clock.System.now().minus(2.minutes),
                endTime = Clock.System.now(),
            ),
            KHRecord.IntermenstrualBleeding(time = Clock.System.now()),
            KHRecord.LeanBodyMass(
                unit = KHUnit.Mass.Gram,
                value = 1120.0,
                time = Clock.System.now(),
            ),
            KHRecord.MenstruationPeriod(
                startTime = Clock.System.now().minus(2.minutes),
                endTime = Clock.System.now(),
            ),
            KHRecord.MenstruationFlow(
                type = KHMenstruationFlowType.Medium,
                time = Clock.System.now(),
            ),
            KHRecord.Nutrition(
                name = "KHealth Sample Meal",
                startTime = Clock.System.now() - 10.minutes,
                endTime = Clock.System.now(),
                mealType = KHMealType.Snack,
                solidUnit = KHUnit.Mass.Gram,
                biotin = 0.00003,
                caffeine = 0.45,
            ),
            KHRecord.OvulationTest(
                result = KHOvulationTestResult.Positive,
                time = Clock.System.now(),
            ),
            KHRecord.OxygenSaturation(
                percentage = 66.37,
                time = Clock.System.now(),
            ),
            KHRecord.Power(
                samples = listOf(
                    KHPowerSample(
                        unit = KHUnit.Power.Watt,
                        value = 50.0,
                        time = Clock.System.now(),
                    )
                )
            ),
            KHRecord.RespiratoryRate(
                rate = 131.0,
                time = Clock.System.now(),
            ),
            KHRecord.RestingHeartRate(
                beatsPerMinute = 112,
                time = Clock.System.now(),
            ),
            KHRecord.RunningSpeed(
                samples = listOf(
                    KHSpeedSample(
                        unit = KHUnit.Velocity.KilometersPerHour,
                        value = 10.0,
                        time = Clock.System.now(),
                    )
                ),
            ),
            KHRecord.SexualActivity(
                didUseProtection = true,
                time = Clock.System.now(),
            ),
            KHRecord.SleepSession(
                samples = listOf(
                    KHSleepStageSample(
                        stage = KHSleepStage.Sleeping,
                        startTime = Clock.System.now() - 16.minutes,
                        endTime = Clock.System.now() - 15.minutes,
                    ),
                    KHSleepStageSample(
                        stage = KHSleepStage.REM,
                        startTime = Clock.System.now() - 14.minutes,
                        endTime = Clock.System.now() - 13.minutes,
                    ),
                    KHSleepStageSample(
                        stage = KHSleepStage.Deep,
                        startTime = Clock.System.now() - 12.minutes,
                        endTime = Clock.System.now() - 11.minutes,
                    ),
                    KHSleepStageSample(
                        stage = KHSleepStage.Awake,
                        startTime = Clock.System.now() - 10.minutes,
                        endTime = Clock.System.now() - 9.minutes,
                    ),
                    KHSleepStageSample(
                        stage = KHSleepStage.Light,
                        startTime = Clock.System.now() - 8.minutes,
                        endTime = Clock.System.now() - 7.minutes,
                    ),
                    KHSleepStageSample(
                        stage = KHSleepStage.AwakeInBed,
                        startTime = Clock.System.now() - 6.minutes,
                        endTime = Clock.System.now() - 5.minutes,
                    ),
                    KHSleepStageSample(
                        stage = KHSleepStage.AwakeOutOfBed,
                        startTime = Clock.System.now() - 4.minutes,
                        endTime = Clock.System.now() - 3.minutes,
                    ),
                    KHSleepStageSample(
                        stage = KHSleepStage.Unknown,
                        startTime = Clock.System.now() - 2.minutes,
                        endTime = Clock.System.now() - 1.minutes,
                    )
                ),
            ),
            KHRecord.Speed(
                samples = listOf(
                    KHSpeedSample(
                        unit = KHUnit.Velocity.KilometersPerHour,
                        value = 14.6,
                        time = Clock.System.now(),
                    )
                )
            ),
            KHRecord.StepCount(
                count = 24,
                startTime = Clock.System.now().minus(2.minutes),
                endTime = Clock.System.now(),
            ),
            KHRecord.Vo2Max(
                vo2MillilitersPerMinuteKilogram = 55.3,
                time = Clock.System.now(),
            ),
            KHRecord.Weight(
                unit = KHUnit.Mass.Pound,
                value = 180.33,
                time = Clock.System.now(),
            ),
            KHRecord.WheelChairPushes(
                count = 50,
                startTime = Clock.System.now().minus(2.minutes),
                endTime = Clock.System.now(),
            ),
        )
        println("Data insert response: $insertResponse")
    }
}

fun sampleReadData(kHealth: KHealth) {
    coroutineScope.launch {
        val startTime = Clock.System.now().minus(1.days)
        val endTime = Clock.System.now()
        val allRecords = with(kHealth) {
            readRecords(
                KHReadRequest.ActiveCaloriesBurned(
                    unit = KHUnit.Energy.Calorie,
                    startTime = startTime,
                    endTime = endTime
                )
            ) +
                    readRecords(
                        KHReadRequest.BasalMetabolicRate(
                            unit = KHEither(
                                left = KHUnit.Power.Watt,
                                right = KHUnit.Energy.Calorie
                            ),
                            startTime = startTime,
                            endTime = endTime
                        )
                    ) +
                    readRecords(
                        KHReadRequest.BloodGlucose(
                            unit = KHUnit.BloodGlucose.MilligramsPerDeciliter,
                            startTime = startTime,
                            endTime = endTime
                        )
                    ) +
                    readRecords(
                        KHReadRequest.BloodPressure(
                            unit = KHUnit.Pressure.MillimeterOfMercury,
                            startTime = startTime,
                            endTime = endTime
                        )
                    ) +
                    readRecords(
                        KHReadRequest.BodyFat(startTime = startTime, endTime = endTime)
                    ) +
                    readRecords(
                        KHReadRequest.BodyTemperature(
                            unit = KHUnit.Temperature.Fahrenheit,
                            startTime = startTime,
                            endTime = endTime
                        )
                    ) +
                    readRecords(
                        KHReadRequest.BodyWaterMass(
                            unit = KHUnit.Mass.Gram,
                            startTime = startTime,
                            endTime = endTime
                        )
                    ) +
                    readRecords(
                        KHReadRequest.BoneMass(
                            unit = KHUnit.Mass.Gram,
                            startTime = startTime,
                            endTime = endTime
                        )
                    ) +
                    readRecords(
                        KHReadRequest.CervicalMucus(
                            startTime = startTime,
                            endTime = endTime
                        )
                    ) +
                    readRecords(
                        KHReadRequest.CyclingPedalingCadence(
                            startTime = startTime,
                            endTime = endTime
                        )
                    ) +
                    readRecords(
                        KHReadRequest.Distance(
                            unit = KHUnit.Length.Meter,
                            startTime = startTime,
                            endTime = endTime
                        )
                    ) +
                    readRecords(
                        KHReadRequest.ElevationGained(
                            unit = KHUnit.Length.Meter,
                            startTime = startTime,
                            endTime = endTime
                        )
                    ) +
                    readRecords(
                        KHReadRequest.Exercise(startTime = startTime, endTime = endTime)
                    ) +
                    readRecords(
                        KHReadRequest.FloorsClimbed(startTime = startTime, endTime = endTime)
                    ) +
                    readRecords(
                        KHReadRequest.HeartRate(startTime = startTime, endTime = endTime)
                    ) +
                    readRecords(
                        KHReadRequest.HeartRateVariability(startTime = startTime, endTime = endTime)
                    ) +
                    readRecords(
                        KHReadRequest.Height(
                            unit = KHUnit.Length.Meter,
                            startTime = startTime,
                            endTime = endTime
                        )
                    ) +
                    readRecords(
                        KHReadRequest.Hydration(
                            unit = KHUnit.Volume.Liter,
                            startTime = startTime,
                            endTime = endTime
                        )
                    ) +
                    readRecords(
                        KHReadRequest.IntermenstrualBleeding(
                            startTime = startTime,
                            endTime = endTime
                        )
                    ) +
                    readRecords(
                        KHReadRequest.LeanBodyMass(
                            unit = KHUnit.Mass.Gram,
                            startTime = startTime,
                            endTime = endTime
                        )
                    ) +
                    readRecords(
                        KHReadRequest.MenstruationPeriod(startTime = startTime, endTime = endTime)
                    ) +
                    readRecords(
                        KHReadRequest.MenstruationFlow(startTime = startTime, endTime = endTime)
                    ) +
                    readRecords(
                        KHReadRequest.Nutrition(startTime = startTime, endTime = endTime)
                    ) +
                    readRecords(
                        KHReadRequest.OvulationTest(startTime = startTime, endTime = endTime)
                    ) +
                    readRecords(
                        KHReadRequest.OxygenSaturation(startTime = startTime, endTime = endTime)
                    ) +
                    readRecords(
                        KHReadRequest.Power(
                            unit = KHUnit.Power.Watt,
                            startTime = startTime,
                            endTime = endTime
                        )
                    ) +
                    readRecords(
                        KHReadRequest.RespiratoryRate(startTime = startTime, endTime = endTime)
                    ) +
                    readRecords(
                        KHReadRequest.RestingHeartRate(startTime = startTime, endTime = endTime)
                    ) +
                    readRecords(
                        KHReadRequest.SexualActivity(startTime = startTime, endTime = endTime)
                    ) +
                    readRecords(
                        KHReadRequest.SleepSession(startTime = startTime, endTime = endTime)
                    ) +
                    readRecords(
                        KHReadRequest.RunningSpeed(
                            unit = KHUnit.Velocity.MetersPerSecond,
                            startTime = startTime,
                            endTime = endTime
                        )
                    ) +
                    readRecords(
                        KHReadRequest.CyclingSpeed(
                            unit = KHUnit.Velocity.MetersPerSecond,
                            startTime = startTime,
                            endTime = endTime
                        )
                    ) +
                    readRecords(
                        KHReadRequest.StepCount(startTime = startTime, endTime = endTime)
                    ) +
                    readRecords(
                        KHReadRequest.Vo2Max(startTime = startTime, endTime = endTime)
                    ) +
                    readRecords(
                        KHReadRequest.Weight(
                            unit = KHUnit.Mass.Gram,
                            startTime = startTime,
                            endTime = endTime
                        )
                    ) +
                    readRecords(
                        KHReadRequest.WheelChairPushes(startTime = startTime, endTime = endTime)
                    )
        }
        println("All records: $allRecords")
    }
}
