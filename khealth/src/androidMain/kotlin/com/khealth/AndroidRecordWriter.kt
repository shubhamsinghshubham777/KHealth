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

import androidx.health.connect.client.records.ActiveCaloriesBurnedRecord
import androidx.health.connect.client.records.BasalMetabolicRateRecord
import androidx.health.connect.client.records.BloodGlucoseRecord
import androidx.health.connect.client.records.BloodPressureRecord
import androidx.health.connect.client.records.BodyFatRecord
import androidx.health.connect.client.records.BodyTemperatureRecord
import androidx.health.connect.client.records.BodyWaterMassRecord
import androidx.health.connect.client.records.BoneMassRecord
import androidx.health.connect.client.records.CervicalMucusRecord
import androidx.health.connect.client.records.CyclingPedalingCadenceRecord
import androidx.health.connect.client.records.DistanceRecord
import androidx.health.connect.client.records.ElevationGainedRecord
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.records.FloorsClimbedRecord
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.HeartRateVariabilityRmssdRecord
import androidx.health.connect.client.records.HeightRecord
import androidx.health.connect.client.records.HydrationRecord
import androidx.health.connect.client.records.IntermenstrualBleedingRecord
import androidx.health.connect.client.records.LeanBodyMassRecord
import androidx.health.connect.client.records.MenstruationFlowRecord
import androidx.health.connect.client.records.MenstruationPeriodRecord
import androidx.health.connect.client.records.NutritionRecord
import androidx.health.connect.client.records.OvulationTestRecord
import androidx.health.connect.client.records.OxygenSaturationRecord
import androidx.health.connect.client.records.PowerRecord
import androidx.health.connect.client.records.Record
import androidx.health.connect.client.records.RespiratoryRateRecord
import androidx.health.connect.client.records.RestingHeartRateRecord
import androidx.health.connect.client.records.SexualActivityRecord
import androidx.health.connect.client.records.SleepSessionRecord
import androidx.health.connect.client.records.SpeedRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.Vo2MaxRecord
import androidx.health.connect.client.records.WeightRecord
import androidx.health.connect.client.records.WheelchairPushesRecord
import androidx.health.connect.client.records.metadata.Metadata
import androidx.health.connect.client.units.Percentage
import androidx.health.connect.client.units.percent
import kotlin.time.toJavaInstant

internal object AndroidRecordWriter {

    fun toHCRecord(record: KHRecord): Record? = when (record) {
        is KHRecord.ActiveCaloriesBurned -> ActiveCaloriesBurnedRecord(
            startTime = record.startTime.toJavaInstant(),
            endTime = record.endTime.toJavaInstant(),
            startZoneOffset = null,
            endZoneOffset = null,
            energy = record.unit toNativeEnergyFor record.value,
            metadata = Metadata.manualEntry(),
        )

        is KHRecord.BasalMetabolicRate -> BasalMetabolicRateRecord(
            time = record.time.toJavaInstant(),
            zoneOffset = null,
            basalMetabolicRate = record.unit toNativePowerFor record.value,
            metadata = Metadata.manualEntry(),
        )

        is KHRecord.BloodGlucose -> BloodGlucoseRecord(
            time = record.time.toJavaInstant(),
            zoneOffset = null,
            level = record.unit toNativeBloodGlucoseFor record.value,
            metadata = Metadata.manualEntry(),
        )

        is KHRecord.BloodPressure -> BloodPressureRecord(
            time = record.time.toJavaInstant(),
            zoneOffset = null,
            systolic = record.unit toNativePressureFor record.systolicValue,
            diastolic = record.unit toNativePressureFor record.diastolicValue,
            metadata = Metadata.manualEntry(),
        )

        is KHRecord.BodyFat -> BodyFatRecord(
            time = record.time.toJavaInstant(),
            zoneOffset = null,
            percentage = record.percentage.percent,
            metadata = Metadata.manualEntry(),
        )

        is KHRecord.BodyTemperature -> BodyTemperatureRecord(
            time = record.time.toJavaInstant(),
            zoneOffset = null,
            temperature = record.unit toNativeTemperatureFor record.value,
            metadata = Metadata.manualEntry(),
        )

        is KHRecord.BodyWaterMass -> BodyWaterMassRecord(
            time = record.time.toJavaInstant(),
            zoneOffset = null,
            mass = record.unit toNativeMassFor record.value,
            metadata = Metadata.manualEntry(),
        )

        is KHRecord.BoneMass -> BoneMassRecord(
            time = record.time.toJavaInstant(),
            zoneOffset = null,
            mass = record.unit toNativeMassFor record.value,
            metadata = Metadata.manualEntry(),
        )

        is KHRecord.CervicalMucus -> CervicalMucusRecord(
            time = record.time.toJavaInstant(),
            zoneOffset = null,
            appearance = record.appearance.toNativeCervicalMucusAppearance(),
            sensation = CervicalMucusRecord.SENSATION_UNKNOWN,
            metadata = Metadata.manualEntry(),
        )

        is KHRecord.CyclingPedalingCadence -> {
            if (record.samples.isEmpty()) null
            else CyclingPedalingCadenceRecord(
                startTime = record.startTime.toJavaInstant(),
                startZoneOffset = null,
                endTime = record.endTime.toJavaInstant(),
                endZoneOffset = null,
                samples = record.samples.map { sample ->
                    CyclingPedalingCadenceRecord.Sample(
                        time = sample.time.toJavaInstant(),
                        revolutionsPerMinute = sample.revolutionsPerMinute,
                    )
                },
                metadata = Metadata.manualEntry(),
            )
        }

        is KHRecord.CyclingSpeed -> null

        is KHRecord.Distance -> DistanceRecord(
            startTime = record.startTime.toJavaInstant(),
            startZoneOffset = null,
            endTime = record.endTime.toJavaInstant(),
            endZoneOffset = null,
            distance = record.unit toNativeLengthFor record.value,
            metadata = Metadata.manualEntry(),
        )

        is KHRecord.ElevationGained -> ElevationGainedRecord(
            startTime = record.startTime.toJavaInstant(),
            startZoneOffset = null,
            endTime = record.endTime.toJavaInstant(),
            endZoneOffset = null,
            elevation = record.unit toNativeLengthFor record.value,
            metadata = Metadata.manualEntry(),
        )

        is KHRecord.Exercise -> record.type.toNativeExerciseTypeOrNull()?.let { exerciseType ->
            ExerciseSessionRecord(
                startTime = record.startTime.toJavaInstant(),
                startZoneOffset = null,
                endTime = record.endTime.toJavaInstant(),
                endZoneOffset = null,
                exerciseType = exerciseType,
                title = null,
                notes = null,
                metadata = Metadata.manualEntry(),
                exerciseRoute = null,
            )
        }

        is KHRecord.FloorsClimbed -> FloorsClimbedRecord(
            startTime = record.startTime.toJavaInstant(),
            startZoneOffset = null,
            endTime = record.endTime.toJavaInstant(),
            endZoneOffset = null,
            floors = record.floors,
            metadata = Metadata.manualEntry(),
        )

        is KHRecord.HeartRate -> {
            if (record.samples.isEmpty()) null
            else HeartRateRecord(
                startTime = record.samples.first().time.toJavaInstant(),
                startZoneOffset = null,
                endTime = record.samples.last().time.toJavaInstant(),
                endZoneOffset = null,
                samples = record.samples.map { sample ->
                    HeartRateRecord.Sample(
                        time = sample.time.toJavaInstant(),
                        beatsPerMinute = sample.beatsPerMinute,
                    )
                },
                metadata = Metadata.manualEntry(),
            )
        }

        is KHRecord.HeartRateVariability -> HeartRateVariabilityRmssdRecord(
            time = record.time.toJavaInstant(),
            zoneOffset = null,
            heartRateVariabilityMillis = record.heartRateVariabilityMillis,
            metadata = Metadata.manualEntry(),
        )

        is KHRecord.Height -> HeightRecord(
            time = record.time.toJavaInstant(),
            zoneOffset = null,
            height = record.unit toNativeLengthFor record.value,
            metadata = Metadata.manualEntry(),
        )

        is KHRecord.Hydration -> HydrationRecord(
            startTime = record.startTime.toJavaInstant(),
            startZoneOffset = null,
            endTime = record.endTime.toJavaInstant(),
            endZoneOffset = null,
            volume = record.unit toNativeVolumeFor record.value,
            metadata = Metadata.manualEntry(),
        )

        is KHRecord.IntermenstrualBleeding -> IntermenstrualBleedingRecord(
            time = record.time.toJavaInstant(),
            zoneOffset = null,
            metadata = Metadata.manualEntry(),
        )

        is KHRecord.LeanBodyMass -> LeanBodyMassRecord(
            time = record.time.toJavaInstant(),
            zoneOffset = null,
            mass = record.unit toNativeMassFor record.value,
            metadata = Metadata.manualEntry(),
        )

        is KHRecord.MenstruationFlow -> MenstruationFlowRecord(
            time = record.time.toJavaInstant(),
            zoneOffset = null,
            flow = record.type.toNativeMenstruationFlow(),
            metadata = Metadata.manualEntry(),
        )

        is KHRecord.MenstruationPeriod -> MenstruationPeriodRecord(
            startTime = record.startTime.toJavaInstant(),
            startZoneOffset = null,
            endTime = record.endTime.toJavaInstant(),
            endZoneOffset = null,
            metadata = Metadata.manualEntry(),
        )

        is KHRecord.Nutrition -> NutritionRecord(
            startTime = record.startTime.toJavaInstant(),
            startZoneOffset = null,
            endTime = record.endTime.toJavaInstant(),
            endZoneOffset = null,
            biotin = record.biotin?.let { record.solidUnit toNativeMassFor it },
            caffeine = record.caffeine?.let { record.solidUnit toNativeMassFor it },
            calcium = record.calcium?.let { record.solidUnit toNativeMassFor it },
            energy = record.energy?.let { record.energyUnit toNativeEnergyFor it },
            energyFromFat = null,
            chloride = record.chloride?.let { record.solidUnit toNativeMassFor it },
            cholesterol = record.cholesterol?.let { record.solidUnit toNativeMassFor it },
            chromium = record.chromium?.let { record.solidUnit toNativeMassFor it },
            copper = record.copper?.let { record.solidUnit toNativeMassFor it },
            dietaryFiber = record.dietaryFiber?.let { record.solidUnit toNativeMassFor it },
            folate = null,
            folicAcid = record.folicAcid?.let { record.solidUnit toNativeMassFor it },
            iodine = record.iodine?.let { record.solidUnit toNativeMassFor it },
            iron = record.iron?.let { record.solidUnit toNativeMassFor it },
            magnesium = record.magnesium?.let { record.solidUnit toNativeMassFor it },
            manganese = record.manganese?.let { record.solidUnit toNativeMassFor it },
            molybdenum = record.molybdenum?.let { record.solidUnit toNativeMassFor it },
            monounsaturatedFat = record.monounsaturatedFat?.let { record.solidUnit toNativeMassFor it },
            niacin = record.niacin?.let { record.solidUnit toNativeMassFor it },
            pantothenicAcid = record.pantothenicAcid?.let { record.solidUnit toNativeMassFor it },
            phosphorus = record.phosphorus?.let { record.solidUnit toNativeMassFor it },
            polyunsaturatedFat = record.polyunsaturatedFat?.let { record.solidUnit toNativeMassFor it },
            potassium = record.potassium?.let { record.solidUnit toNativeMassFor it },
            protein = record.protein?.let { record.solidUnit toNativeMassFor it },
            riboflavin = record.riboflavin?.let { record.solidUnit toNativeMassFor it },
            saturatedFat = record.saturatedFat?.let { record.solidUnit toNativeMassFor it },
            selenium = record.selenium?.let { record.solidUnit toNativeMassFor it },
            sodium = record.sodium?.let { record.solidUnit toNativeMassFor it },
            sugar = record.sugar?.let { record.solidUnit toNativeMassFor it },
            thiamin = record.thiamin?.let { record.solidUnit toNativeMassFor it },
            totalCarbohydrate = record.totalCarbohydrate?.let { record.solidUnit toNativeMassFor it },
            totalFat = record.totalFat?.let { record.solidUnit toNativeMassFor it },
            transFat = null,
            vitaminA = record.vitaminA?.let { record.solidUnit toNativeMassFor it },
            vitaminB12 = record.vitaminB12?.let { record.solidUnit toNativeMassFor it },
            vitaminB6 = record.vitaminB6?.let { record.solidUnit toNativeMassFor it },
            vitaminC = record.vitaminC?.let { record.solidUnit toNativeMassFor it },
            vitaminD = record.vitaminD?.let { record.solidUnit toNativeMassFor it },
            vitaminE = record.vitaminE?.let { record.solidUnit toNativeMassFor it },
            vitaminK = record.vitaminK?.let { record.solidUnit toNativeMassFor it },
            zinc = record.zinc?.let { record.solidUnit toNativeMassFor it },
            name = record.name,
            mealType = record.mealType.toHCMealType(),
            metadata = Metadata.manualEntry(),
        )

        is KHRecord.OvulationTest -> OvulationTestRecord(
            time = record.time.toJavaInstant(),
            zoneOffset = null,
            result = record.result.toNativeOvulationTestResult(),
            metadata = Metadata.manualEntry(),
        )

        is KHRecord.OxygenSaturation -> OxygenSaturationRecord(
            time = record.time.toJavaInstant(),
            zoneOffset = null,
            percentage = Percentage(record.percentage),
            metadata = Metadata.manualEntry(),
        )

        is KHRecord.Power -> {
            if (record.samples.isEmpty()) null
            else PowerRecord(
                startTime = record.samples.first().time.toJavaInstant(),
                startZoneOffset = null,
                endTime = record.samples.last().time.toJavaInstant(),
                endZoneOffset = null,
                samples = record.samples.map { sample ->
                    PowerRecord.Sample(
                        time = sample.time.toJavaInstant(),
                        power = sample.unit toNativePowerFor sample.value
                    )
                },
                metadata = Metadata.manualEntry(),
            )
        }

        is KHRecord.RespiratoryRate -> RespiratoryRateRecord(
            time = record.time.toJavaInstant(),
            zoneOffset = null,
            rate = record.rate,
            metadata = Metadata.manualEntry(),
        )

        is KHRecord.RestingHeartRate -> RestingHeartRateRecord(
            time = record.time.toJavaInstant(),
            zoneOffset = null,
            beatsPerMinute = record.beatsPerMinute,
            metadata = Metadata.manualEntry(),
        )

        is KHRecord.RunningSpeed -> null

        is KHRecord.SexualActivity -> SexualActivityRecord(
            time = record.time.toJavaInstant(),
            zoneOffset = null,
            protectionUsed = record.didUseProtection.toNativeProtectionUsed(),
            metadata = Metadata.manualEntry(),
        )

        is KHRecord.SleepSession -> {
            if (record.samples.isEmpty()) null
            else SleepSessionRecord(
                startTime = record.samples.first().startTime.toJavaInstant(),
                startZoneOffset = null,
                endTime = record.samples.last().endTime.toJavaInstant(),
                endZoneOffset = null,
                stages = record.samples.map { sample ->
                    SleepSessionRecord.Stage(
                        startTime = sample.startTime.toJavaInstant(),
                        endTime = sample.endTime.toJavaInstant(),
                        stage = sample.stage.toNativeSleepStage()
                    )
                },
                metadata = Metadata.manualEntry(),
            )
        }

        is KHRecord.Speed -> {
            if (record.samples.isEmpty()) null
            else SpeedRecord(
                startTime = record.samples.first().time.toJavaInstant(),
                startZoneOffset = null,
                endTime = record.samples.last().time.toJavaInstant(),
                endZoneOffset = null,
                samples = record.samples.map { sample ->
                    SpeedRecord.Sample(
                        time = sample.time.toJavaInstant(),
                        speed = sample.unit toNativeVelocityFor sample.value
                    )
                },
                metadata = Metadata.manualEntry(),
            )
        }

        is KHRecord.StepCount -> StepsRecord(
            startTime = record.startTime.toJavaInstant(),
            startZoneOffset = null,
            endTime = record.endTime.toJavaInstant(),
            endZoneOffset = null,
            count = record.count,
            metadata = Metadata.manualEntry(),
        )

        is KHRecord.Vo2Max -> Vo2MaxRecord(
            time = record.time.toJavaInstant(),
            zoneOffset = null,
            vo2MillilitersPerMinuteKilogram = record.vo2MillilitersPerMinuteKilogram,
            metadata = Metadata.manualEntry(),
        )

        is KHRecord.Weight -> WeightRecord(
            time = record.time.toJavaInstant(),
            zoneOffset = null,
            weight = record.unit toNativeMassFor record.value,
            metadata = Metadata.manualEntry(),
        )

        is KHRecord.WheelChairPushes -> WheelchairPushesRecord(
            startTime = record.startTime.toJavaInstant(),
            startZoneOffset = null,
            endTime = record.endTime.toJavaInstant(),
            endZoneOffset = null,
            count = record.count,
            metadata = Metadata.manualEntry(),
        )
    }

    private fun KHCervicalMucusAppearance.toNativeCervicalMucusAppearance(): Int = when (this) {
        KHCervicalMucusAppearance.Creamy -> CervicalMucusRecord.APPEARANCE_CREAMY
        KHCervicalMucusAppearance.Dry -> CervicalMucusRecord.APPEARANCE_DRY
        KHCervicalMucusAppearance.EggWhite -> CervicalMucusRecord.APPEARANCE_EGG_WHITE
        KHCervicalMucusAppearance.Sticky -> CervicalMucusRecord.APPEARANCE_STICKY
        KHCervicalMucusAppearance.Watery -> CervicalMucusRecord.APPEARANCE_WATERY
    }

    private fun KHMenstruationFlowType.toNativeMenstruationFlow(): Int = when (this) {
        KHMenstruationFlowType.Unknown -> MenstruationFlowRecord.FLOW_UNKNOWN
        KHMenstruationFlowType.Light -> MenstruationFlowRecord.FLOW_LIGHT
        KHMenstruationFlowType.Medium -> MenstruationFlowRecord.FLOW_MEDIUM
        KHMenstruationFlowType.Heavy -> MenstruationFlowRecord.FLOW_HEAVY
    }

    private fun KHOvulationTestResult.toNativeOvulationTestResult(): Int = when (this) {
        KHOvulationTestResult.High -> OvulationTestRecord.RESULT_HIGH
        KHOvulationTestResult.Negative -> OvulationTestRecord.RESULT_NEGATIVE
        KHOvulationTestResult.Positive -> OvulationTestRecord.RESULT_POSITIVE
        KHOvulationTestResult.Inconclusive -> OvulationTestRecord.RESULT_INCONCLUSIVE
    }

    private fun Boolean.toNativeProtectionUsed(): Int =
        if (this) SexualActivityRecord.PROTECTION_USED_PROTECTED
        else SexualActivityRecord.PROTECTION_USED_UNPROTECTED

    private fun KHSleepStage.toNativeSleepStage(): Int = when (this) {
        KHSleepStage.Awake -> SleepSessionRecord.STAGE_TYPE_AWAKE
        KHSleepStage.AwakeInBed -> SleepSessionRecord.STAGE_TYPE_AWAKE_IN_BED
        KHSleepStage.AwakeOutOfBed -> SleepSessionRecord.STAGE_TYPE_OUT_OF_BED
        KHSleepStage.Deep -> SleepSessionRecord.STAGE_TYPE_DEEP
        KHSleepStage.Light -> SleepSessionRecord.STAGE_TYPE_LIGHT
        KHSleepStage.REM -> SleepSessionRecord.STAGE_TYPE_REM
        KHSleepStage.Sleeping -> SleepSessionRecord.STAGE_TYPE_SLEEPING
        KHSleepStage.Unknown -> SleepSessionRecord.STAGE_TYPE_UNKNOWN
    }
}
