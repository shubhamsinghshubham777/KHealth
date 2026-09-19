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
import kotlin.time.toKotlinInstant
import kotlin.reflect.KClass

internal object AndroidRecordReader {

    fun toRecordClass(request: KHReadRequest<*>): KClass<out Record>? = when (request) {
        is KHReadRequest.ActiveCaloriesBurned -> ActiveCaloriesBurnedRecord::class
        is KHReadRequest.BasalMetabolicRate -> BasalMetabolicRateRecord::class
        is KHReadRequest.BloodGlucose -> BloodGlucoseRecord::class
        is KHReadRequest.BloodPressure -> BloodPressureRecord::class
        is KHReadRequest.BodyFat -> BodyFatRecord::class
        is KHReadRequest.BodyTemperature -> BodyTemperatureRecord::class
        is KHReadRequest.BodyWaterMass -> BodyWaterMassRecord::class
        is KHReadRequest.BoneMass -> BoneMassRecord::class
        is KHReadRequest.CervicalMucus -> CervicalMucusRecord::class
        is KHReadRequest.CyclingPedalingCadence -> CyclingPedalingCadenceRecord::class
        is KHReadRequest.CyclingSpeed -> null
        is KHReadRequest.Distance -> DistanceRecord::class
        is KHReadRequest.ElevationGained -> ElevationGainedRecord::class
        is KHReadRequest.Exercise -> ExerciseSessionRecord::class
        is KHReadRequest.FloorsClimbed -> FloorsClimbedRecord::class
        is KHReadRequest.HeartRate -> HeartRateRecord::class
        is KHReadRequest.HeartRateVariability -> HeartRateVariabilityRmssdRecord::class
        is KHReadRequest.Height -> HeightRecord::class
        is KHReadRequest.Hydration -> HydrationRecord::class
        is KHReadRequest.IntermenstrualBleeding -> IntermenstrualBleedingRecord::class
        is KHReadRequest.LeanBodyMass -> LeanBodyMassRecord::class
        is KHReadRequest.MenstruationFlow -> MenstruationFlowRecord::class
        is KHReadRequest.MenstruationPeriod -> MenstruationPeriodRecord::class
        is KHReadRequest.Nutrition -> NutritionRecord::class
        is KHReadRequest.OvulationTest -> OvulationTestRecord::class
        is KHReadRequest.OxygenSaturation -> OxygenSaturationRecord::class
        is KHReadRequest.Power -> PowerRecord::class
        is KHReadRequest.RespiratoryRate -> RespiratoryRateRecord::class
        is KHReadRequest.RestingHeartRate -> RestingHeartRateRecord::class
        is KHReadRequest.RunningSpeed -> null
        is KHReadRequest.SexualActivity -> SexualActivityRecord::class
        is KHReadRequest.SleepSession -> SleepSessionRecord::class
        is KHReadRequest.Speed -> SpeedRecord::class
        is KHReadRequest.StepCount -> StepsRecord::class
        is KHReadRequest.Vo2Max -> Vo2MaxRecord::class
        is KHReadRequest.Weight -> WeightRecord::class
        is KHReadRequest.WheelChairPushes -> WheelchairPushesRecord::class
    }

    fun toKHRecordOrNull(record: Record, request: KHReadRequest<*>): KHRecord? = when (record) {
        is ActiveCaloriesBurnedRecord -> {
            val unit = (request as KHReadRequest.ActiveCaloriesBurned).unit
            KHRecord.ActiveCaloriesBurned(
                unit = unit,
                value = record.energy toDoubleValueFor unit,
                startTime = record.startTime.toKotlinInstant(),
                endTime = record.endTime.toKotlinInstant()
            )
        }

        is BasalMetabolicRateRecord -> {
            val unit = (request as KHReadRequest.BasalMetabolicRate).unit
            KHRecord.BasalMetabolicRate(
                unit = unit,
                value = record.basalMetabolicRate toDoubleValueFor unit,
                time = record.time.toKotlinInstant()
            )
        }

        is BloodGlucoseRecord -> {
            val unit = (request as KHReadRequest.BloodGlucose).unit
            KHRecord.BloodGlucose(
                unit = unit,
                value = record.level toDoubleValueFor unit,
                time = record.time.toKotlinInstant()
            )
        }

        is BloodPressureRecord -> {
            val unit = (request as KHReadRequest.BloodPressure).unit
            KHRecord.BloodPressure(
                unit = unit,
                systolicValue = record.systolic toDoubleValueFor unit,
                diastolicValue = record.diastolic toDoubleValueFor unit,
                time = record.time.toKotlinInstant(),
            )
        }

        is BodyFatRecord -> KHRecord.BodyFat(
            percentage = record.percentage.value,
            time = record.time.toKotlinInstant(),
        )

        is BodyTemperatureRecord -> {
            val unit = (request as KHReadRequest.BodyTemperature).unit
            KHRecord.BodyTemperature(
                unit = unit,
                value = record.temperature toDoubleValueFor unit,
                time = record.time.toKotlinInstant()
            )
        }

        is BodyWaterMassRecord -> {
            val unit = (request as KHReadRequest.BodyWaterMass).unit
            KHRecord.BodyWaterMass(
                unit = unit,
                value = record.mass toDoubleValueFor unit,
                time = record.time.toKotlinInstant()
            )
        }

        is BoneMassRecord -> {
            val unit = (request as KHReadRequest.BoneMass).unit
            KHRecord.BoneMass(
                unit = unit,
                value = record.mass toDoubleValueFor unit,
                time = record.time.toKotlinInstant()
            )
        }

        is CervicalMucusRecord -> KHRecord.CervicalMucus(
            appearance = when (record.appearance) {
                CervicalMucusRecord.APPEARANCE_CREAMY -> KHCervicalMucusAppearance.Watery
                CervicalMucusRecord.APPEARANCE_DRY -> KHCervicalMucusAppearance.Dry
                CervicalMucusRecord.APPEARANCE_EGG_WHITE -> KHCervicalMucusAppearance.EggWhite
                CervicalMucusRecord.APPEARANCE_STICKY -> KHCervicalMucusAppearance.Sticky
                CervicalMucusRecord.APPEARANCE_WATERY -> KHCervicalMucusAppearance.Watery
                else -> throw Exception("Unknown Cervical Mucus appearance!")
            },
            time = record.time.toKotlinInstant()
        )

        is CyclingPedalingCadenceRecord -> KHRecord.CyclingPedalingCadence(
            samples = record.samples.map { sample ->
                KHCyclingPedalingCadenceSample(
                    revolutionsPerMinute = sample.revolutionsPerMinute,
                    time = sample.time.toKotlinInstant(),
                )
            },
            startTime = record.startTime.toKotlinInstant(),
            endTime = record.endTime.toKotlinInstant()
        )

        is DistanceRecord -> {
            val unit = (request as KHReadRequest.Distance).unit
            KHRecord.Distance(
                unit = unit,
                value = record.distance toDoubleValueFor unit,
                startTime = record.startTime.toKotlinInstant(),
                endTime = record.endTime.toKotlinInstant(),
            )
        }

        is ElevationGainedRecord -> {
            val unit = (request as KHReadRequest.ElevationGained).unit
            KHRecord.ElevationGained(
                unit = unit,
                value = record.elevation toDoubleValueFor unit,
                startTime = record.startTime.toKotlinInstant(),
                endTime = record.endTime.toKotlinInstant(),
            )
        }

        is ExerciseSessionRecord -> record.exerciseType.toKHExerciseTypeOrNull()?.let { safeExerciseType ->
            KHRecord.Exercise(
                startTime = record.startTime.toKotlinInstant(),
                endTime = record.endTime.toKotlinInstant(),
                type = safeExerciseType,
            )
        }

        is FloorsClimbedRecord -> KHRecord.FloorsClimbed(
            floors = record.floors,
            startTime = record.startTime.toKotlinInstant(),
            endTime = record.endTime.toKotlinInstant(),
        )

        is HeartRateRecord -> KHRecord.HeartRate(
            samples = record.samples.map { sample ->
                KHHeartRateSample(
                    beatsPerMinute = sample.beatsPerMinute,
                    time = sample.time.toKotlinInstant()
                )
            }
        )

        is HeartRateVariabilityRmssdRecord -> KHRecord.HeartRateVariability(
            heartRateVariabilityMillis = record.heartRateVariabilityMillis,
            time = record.time.toKotlinInstant()
        )

        is HeightRecord -> {
            val unit = (request as KHReadRequest.Height).unit
            KHRecord.Height(
                unit = unit,
                value = record.height toDoubleValueFor unit,
                time = record.time.toKotlinInstant()
            )
        }

        is HydrationRecord -> {
            val unit = (request as KHReadRequest.Hydration).unit
            KHRecord.Hydration(
                unit = unit,
                value = record.volume toDoubleValueFor unit,
                startTime = record.startTime.toKotlinInstant(),
                endTime = record.endTime.toKotlinInstant(),
            )
        }

        is IntermenstrualBleedingRecord -> KHRecord.IntermenstrualBleeding(
            time = record.time.toKotlinInstant()
        )

        is MenstruationPeriodRecord -> KHRecord.MenstruationPeriod(
            startTime = record.startTime.toKotlinInstant(),
            endTime = record.endTime.toKotlinInstant(),
        )

        is LeanBodyMassRecord -> {
            val unit = (request as KHReadRequest.LeanBodyMass).unit
            KHRecord.LeanBodyMass(
                unit = unit,
                value = record.mass toDoubleValueFor unit,
                time = record.time.toKotlinInstant()
            )
        }

        is MenstruationFlowRecord -> KHRecord.MenstruationFlow(
            type = when (record.flow) {
                MenstruationFlowRecord.FLOW_UNKNOWN -> KHMenstruationFlowType.Unknown
                MenstruationFlowRecord.FLOW_LIGHT -> KHMenstruationFlowType.Light
                MenstruationFlowRecord.FLOW_MEDIUM -> KHMenstruationFlowType.Medium
                MenstruationFlowRecord.FLOW_HEAVY -> KHMenstruationFlowType.Heavy
                else -> throw Exception("Unknown menstruation flow type!")
            },
            time = record.time.toKotlinInstant(),
        )

        is NutritionRecord -> {
            val solidUnit = (request as KHReadRequest.Nutrition).solidUnit
            val energyUnit = request.energyUnit
            KHRecord.Nutrition(
                name = record.name,
                startTime = record.startTime.toKotlinInstant(),
                endTime = record.endTime.toKotlinInstant(),
                solidUnit = solidUnit,
                energyUnit = energyUnit,
                mealType = record.mealType.toKHMealType(),
                biotin = record.biotin?.toDoubleValueFor(solidUnit),
                caffeine = record.caffeine?.toDoubleValueFor(solidUnit),
                calcium = record.calcium?.toDoubleValueFor(solidUnit),
                chloride = record.chloride?.toDoubleValueFor(solidUnit),
                cholesterol = record.cholesterol?.toDoubleValueFor(solidUnit),
                chromium = record.chromium?.toDoubleValueFor(solidUnit),
                copper = record.copper?.toDoubleValueFor(solidUnit),
                dietaryFiber = record.dietaryFiber?.toDoubleValueFor(solidUnit),
                energy = record.energy?.toDoubleValueFor(energyUnit),
                folicAcid = record.folicAcid?.toDoubleValueFor(solidUnit),
                iodine = record.iodine?.toDoubleValueFor(solidUnit),
                iron = record.iron?.toDoubleValueFor(solidUnit),
                magnesium = record.magnesium?.toDoubleValueFor(solidUnit),
                manganese = record.manganese?.toDoubleValueFor(solidUnit),
                molybdenum = record.molybdenum?.toDoubleValueFor(solidUnit),
                monounsaturatedFat = record.monounsaturatedFat?.toDoubleValueFor(solidUnit),
                niacin = record.niacin?.toDoubleValueFor(solidUnit),
                pantothenicAcid = record.pantothenicAcid?.toDoubleValueFor(solidUnit),
                phosphorus = record.phosphorus?.toDoubleValueFor(solidUnit),
                polyunsaturatedFat = record.polyunsaturatedFat?.toDoubleValueFor(solidUnit),
                potassium = record.potassium?.toDoubleValueFor(solidUnit),
                protein = record.protein?.toDoubleValueFor(solidUnit),
                riboflavin = record.riboflavin?.toDoubleValueFor(solidUnit),
                saturatedFat = record.saturatedFat?.toDoubleValueFor(solidUnit),
                selenium = record.selenium?.toDoubleValueFor(solidUnit),
                sodium = record.sodium?.toDoubleValueFor(solidUnit),
                sugar = record.sugar?.toDoubleValueFor(solidUnit),
                thiamin = record.thiamin?.toDoubleValueFor(solidUnit),
                totalCarbohydrate = record.totalCarbohydrate?.toDoubleValueFor(solidUnit),
                totalFat = record.totalFat?.toDoubleValueFor(solidUnit),
                vitaminA = record.vitaminA?.toDoubleValueFor(solidUnit),
                vitaminB12 = record.vitaminB12?.toDoubleValueFor(solidUnit),
                vitaminB6 = record.vitaminB6?.toDoubleValueFor(solidUnit),
                vitaminC = record.vitaminC?.toDoubleValueFor(solidUnit),
                vitaminD = record.vitaminD?.toDoubleValueFor(solidUnit),
                vitaminE = record.vitaminE?.toDoubleValueFor(solidUnit),
                vitaminK = record.vitaminK?.toDoubleValueFor(solidUnit),
                zinc = record.zinc?.toDoubleValueFor(solidUnit),
            )
        }

        is OvulationTestRecord -> KHRecord.OvulationTest(
            result = when (record.result) {
                OvulationTestRecord.RESULT_HIGH -> KHOvulationTestResult.High
                OvulationTestRecord.RESULT_NEGATIVE -> KHOvulationTestResult.Negative
                OvulationTestRecord.RESULT_POSITIVE -> KHOvulationTestResult.Positive
                OvulationTestRecord.RESULT_INCONCLUSIVE -> KHOvulationTestResult.Inconclusive
                else -> throw Exception("Unknown ovulation test result!")
            },
            time = record.time.toKotlinInstant()
        )

        is OxygenSaturationRecord -> KHRecord.OxygenSaturation(
            percentage = record.percentage.value,
            time = record.time.toKotlinInstant()
        )

        is PowerRecord -> {
            val unit = (request as KHReadRequest.Power).unit
            KHRecord.Power(
                samples = record.samples.map { sample ->
                    KHPowerSample(
                        unit = unit,
                        value = sample.power toDoubleValueFor unit,
                        time = sample.time.toKotlinInstant(),
                    )
                }
            )
        }

        is RespiratoryRateRecord -> KHRecord.RespiratoryRate(
            rate = record.rate,
            time = record.time.toKotlinInstant()
        )

        is RestingHeartRateRecord -> KHRecord.RestingHeartRate(
            beatsPerMinute = record.beatsPerMinute,
            time = record.time.toKotlinInstant()
        )

        is SexualActivityRecord -> KHRecord.SexualActivity(
            didUseProtection = record.protectionUsed == SexualActivityRecord.PROTECTION_USED_PROTECTED,
            time = record.time.toKotlinInstant()
        )

        is SleepSessionRecord -> KHRecord.SleepSession(
            samples = record.stages.map { sample ->
                KHSleepStageSample(
                    stage = when (sample.stage) {
                        SleepSessionRecord.STAGE_TYPE_AWAKE -> KHSleepStage.Awake
                        SleepSessionRecord.STAGE_TYPE_AWAKE_IN_BED -> KHSleepStage.AwakeInBed
                        SleepSessionRecord.STAGE_TYPE_OUT_OF_BED -> KHSleepStage.AwakeOutOfBed
                        SleepSessionRecord.STAGE_TYPE_DEEP -> KHSleepStage.Deep
                        SleepSessionRecord.STAGE_TYPE_LIGHT -> KHSleepStage.Light
                        SleepSessionRecord.STAGE_TYPE_REM -> KHSleepStage.REM
                        SleepSessionRecord.STAGE_TYPE_SLEEPING -> KHSleepStage.Sleeping
                        SleepSessionRecord.STAGE_TYPE_UNKNOWN -> KHSleepStage.Unknown
                        else -> throw Exception("Unknown sleep stage!")
                    },
                    startTime = sample.startTime.toKotlinInstant(),
                    endTime = sample.endTime.toKotlinInstant(),
                )
            }
        )

        is SpeedRecord -> {
            val unit = (request as KHReadRequest.Speed).unit
            KHRecord.Speed(
                samples = record.samples.map { sample ->
                    KHSpeedSample(
                        unit = unit,
                        value = sample.speed toDoubleValueFor unit,
                        time = sample.time.toKotlinInstant()
                    )
                }
            )
        }

        is StepsRecord -> KHRecord.StepCount(
            count = record.count,
            startTime = record.startTime.toKotlinInstant(),
            endTime = record.endTime.toKotlinInstant()
        )

        is Vo2MaxRecord -> KHRecord.Vo2Max(
            vo2MillilitersPerMinuteKilogram = record.vo2MillilitersPerMinuteKilogram,
            time = record.time.toKotlinInstant()
        )

        is WeightRecord -> {
            val unit = (request as KHReadRequest.Weight).unit
            KHRecord.Weight(
                unit = unit,
                value = record.weight toDoubleValueFor unit,
                time = record.time.toKotlinInstant()
            )
        }

        is WheelchairPushesRecord -> KHRecord.WheelChairPushes(
            count = record.count,
            startTime = record.startTime.toKotlinInstant(),
            endTime = record.endTime.toKotlinInstant()
        )

        else -> throw Exception("Unknown record type ($record)!")
    }
}
