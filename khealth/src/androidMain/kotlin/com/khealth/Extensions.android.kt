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

import androidx.health.connect.client.permission.HealthPermission
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
import androidx.health.connect.client.records.MealType
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
import androidx.health.connect.client.records.metadata.Device
import androidx.health.connect.client.records.metadata.Metadata
import androidx.health.connect.client.units.BloodGlucose
import androidx.health.connect.client.units.Energy
import androidx.health.connect.client.units.Length
import androidx.health.connect.client.units.Mass
import androidx.health.connect.client.units.Power
import androidx.health.connect.client.units.Pressure
import androidx.health.connect.client.units.Temperature
import androidx.health.connect.client.units.Velocity
import androidx.health.connect.client.units.Volume
import androidx.health.connect.client.units.calories
import androidx.health.connect.client.units.celsius
import androidx.health.connect.client.units.fahrenheit
import androidx.health.connect.client.units.fluidOuncesUs
import androidx.health.connect.client.units.grams
import androidx.health.connect.client.units.inches
import androidx.health.connect.client.units.joules
import androidx.health.connect.client.units.kilocalories
import androidx.health.connect.client.units.kilocaloriesPerDay
import androidx.health.connect.client.units.kilojoules
import androidx.health.connect.client.units.kilometersPerHour
import androidx.health.connect.client.units.liters
import androidx.health.connect.client.units.meters
import androidx.health.connect.client.units.metersPerSecond
import androidx.health.connect.client.units.miles
import androidx.health.connect.client.units.milesPerHour
import androidx.health.connect.client.units.millimetersOfMercury
import androidx.health.connect.client.units.ounces
import androidx.health.connect.client.units.percent
import androidx.health.connect.client.units.pounds
import androidx.health.connect.client.units.watts
import kotlinx.datetime.toJavaInstant
import kotlinx.datetime.toKotlinInstant
import kotlin.reflect.KClass

internal fun Record.toKHRecordOrNull(request: KHReadRequest): KHRecord? = when (this) {
    is ActiveCaloriesBurnedRecord -> {
        val unit = (request as KHReadRequest.ActiveCaloriesBurned).unit
        KHRecord.ActiveCaloriesBurned(
            unit = unit,
            value = this.energy toDoubleValueFor unit,
            startTime = this.startTime.toKotlinInstant(),
            endTime = this.endTime.toKotlinInstant()
        )
    }

    is BasalMetabolicRateRecord -> {
        val unit = (request as KHReadRequest.BasalMetabolicRate).unit
        KHRecord.BasalMetabolicRate(
            unit = unit,
            value = this.basalMetabolicRate toDoubleValueFor unit.left,
            time = this.time.toKotlinInstant()
        )
    }

    is BloodGlucoseRecord -> {
        val unit = (request as KHReadRequest.BloodGlucose).unit
        KHRecord.BloodGlucose(
            unit = unit,
            value = this.level toDoubleValueFor unit,
            time = this.time.toKotlinInstant()
        )
    }

    is BloodPressureRecord -> {
        val unit = (request as KHReadRequest.BloodPressure).unit
        KHRecord.BloodPressure(
            unit = unit,
            systolicValue = this.systolic toDoubleValueFor unit,
            diastolicValue = this.diastolic toDoubleValueFor unit,
            time = this.time.toKotlinInstant(),
        )
    }

    is BodyFatRecord -> KHRecord.BodyFat(
        percentage = this.percentage.value,
        time = this.time.toKotlinInstant(),
    )

    is BodyTemperatureRecord -> {
        val unit = (request as KHReadRequest.BodyTemperature).unit
        KHRecord.BodyTemperature(
            unit = unit,
            value = this.temperature toDoubleValueFor unit,
            time = this.time.toKotlinInstant()
        )
    }

    is BodyWaterMassRecord -> {
        val unit = (request as KHReadRequest.BodyWaterMass).unit
        KHRecord.BodyWaterMass(
            unit = unit,
            value = this.mass toDoubleValueFor unit,
            time = this.time.toKotlinInstant()
        )
    }

    is BoneMassRecord -> {
        val unit = (request as KHReadRequest.BoneMass).unit
        KHRecord.BoneMass(
            unit = unit,
            value = this.mass toDoubleValueFor unit,
            time = this.time.toKotlinInstant()
        )
    }

    is CervicalMucusRecord -> KHRecord.CervicalMucus(
        appearance = when (this.appearance) {
            CervicalMucusRecord.APPEARANCE_CREAMY -> KHCervicalMucusAppearance.Watery
            CervicalMucusRecord.APPEARANCE_DRY -> KHCervicalMucusAppearance.Dry
            CervicalMucusRecord.APPEARANCE_EGG_WHITE -> KHCervicalMucusAppearance.EggWhite
            CervicalMucusRecord.APPEARANCE_STICKY -> KHCervicalMucusAppearance.Sticky
            CervicalMucusRecord.APPEARANCE_WATERY -> KHCervicalMucusAppearance.Watery
            else -> throw Exception("Unknown Cervical Mucus appearance!")
        },
        time = this.time.toKotlinInstant()
    )

    is CyclingPedalingCadenceRecord -> KHRecord.CyclingPedalingCadence(
        samples = this.samples.map { sample ->
            KHCyclingPedalingCadenceSample(
                revolutionsPerMinute = sample.revolutionsPerMinute,
                time = sample.time.toKotlinInstant(),
            )
        },
        startTime = this.startTime.toKotlinInstant(),
        endTime = this.endTime.toKotlinInstant()
    )

    is DistanceRecord -> {
        val unit = (request as KHReadRequest.Distance).unit
        KHRecord.Distance(
            unit = unit,
            value = this.distance toDoubleValueFor unit,
            startTime = this.startTime.toKotlinInstant(),
            endTime = this.endTime.toKotlinInstant(),
        )
    }

    is ElevationGainedRecord -> {
        val unit = (request as KHReadRequest.ElevationGained).unit
        KHRecord.ElevationGained(
            unit = unit,
            value = this.elevation toDoubleValueFor unit,
            startTime = this.startTime.toKotlinInstant(),
            endTime = this.endTime.toKotlinInstant(),
        )
    }

    is ExerciseSessionRecord -> exerciseType.toKHExerciseTypeOrNull()?.let { safeExerciseType ->
        KHRecord.Exercise(
            startTime = startTime.toKotlinInstant(),
            endTime = endTime.toKotlinInstant(),
            type = safeExerciseType,
        )
    }

    is FloorsClimbedRecord -> KHRecord.FloorsClimbed(
        floors = this.floors,
        startTime = this.startTime.toKotlinInstant(),
        endTime = this.endTime.toKotlinInstant(),
    )

    is HeartRateRecord -> KHRecord.HeartRate(
        samples = this.samples.map { sample ->
            KHHeartRateSample(
                beatsPerMinute = sample.beatsPerMinute,
                time = sample.time.toKotlinInstant()
            )
        }
    )

    is HeartRateVariabilityRmssdRecord -> KHRecord.HeartRateVariability(
        heartRateVariabilityMillis = this.heartRateVariabilityMillis,
        time = this.time.toKotlinInstant()
    )

    is HeightRecord -> {
        val unit = (request as KHReadRequest.Height).unit
        KHRecord.Height(
            unit = unit,
            value = this.height toDoubleValueFor unit,
            time = this.time.toKotlinInstant()
        )
    }

    is HydrationRecord -> {
        val unit = (request as KHReadRequest.Hydration).unit
        KHRecord.Hydration(
            unit = unit,
            value = this.volume toDoubleValueFor unit,
            startTime = this.startTime.toKotlinInstant(),
            endTime = this.endTime.toKotlinInstant(),
        )
    }

    is IntermenstrualBleedingRecord -> KHRecord.IntermenstrualBleeding(
        time = this.time.toKotlinInstant()
    )

    is MenstruationPeriodRecord -> KHRecord.MenstruationPeriod(
        startTime = this.startTime.toKotlinInstant(),
        endTime = this.endTime.toKotlinInstant(),
    )

    is LeanBodyMassRecord -> {
        val unit = (request as KHReadRequest.LeanBodyMass).unit
        KHRecord.LeanBodyMass(
            unit = unit,
            value = this.mass toDoubleValueFor unit,
            time = this.time.toKotlinInstant()
        )
    }

    is MenstruationFlowRecord -> KHRecord.MenstruationFlow(
        type = when (this.flow) {
            MenstruationFlowRecord.FLOW_UNKNOWN -> KHMenstruationFlowType.Unknown
            MenstruationFlowRecord.FLOW_LIGHT -> KHMenstruationFlowType.Light
            MenstruationFlowRecord.FLOW_MEDIUM -> KHMenstruationFlowType.Medium
            MenstruationFlowRecord.FLOW_HEAVY -> KHMenstruationFlowType.Heavy
            else -> throw Exception("Unknown menstruation flow type!")
        },
        time = this.time.toKotlinInstant(),
    )

    is NutritionRecord -> {
        val solidUnit = (request as KHReadRequest.Nutrition).solidUnit
        val energyUnit = request.energyUnit
        KHRecord.Nutrition(
            name = name,
            startTime = startTime.toKotlinInstant(),
            endTime = endTime.toKotlinInstant(),
            solidUnit = solidUnit,
            energyUnit = energyUnit,
            mealType = mealType.toKHMealType(),
            biotin = biotin?.toDoubleValueFor(solidUnit),
            caffeine = caffeine?.toDoubleValueFor(solidUnit),
            calcium = calcium?.toDoubleValueFor(solidUnit),
            chloride = chloride?.toDoubleValueFor(solidUnit),
            cholesterol = cholesterol?.toDoubleValueFor(solidUnit),
            chromium = chromium?.toDoubleValueFor(solidUnit),
            copper = copper?.toDoubleValueFor(solidUnit),
            dietaryFiber = dietaryFiber?.toDoubleValueFor(solidUnit),
            energy = energy?.toDoubleValueFor(energyUnit),
            folicAcid = folicAcid?.toDoubleValueFor(solidUnit),
            iodine = iodine?.toDoubleValueFor(solidUnit),
            iron = iron?.toDoubleValueFor(solidUnit),
            magnesium = magnesium?.toDoubleValueFor(solidUnit),
            manganese = manganese?.toDoubleValueFor(solidUnit),
            molybdenum = molybdenum?.toDoubleValueFor(solidUnit),
            monounsaturatedFat = monounsaturatedFat?.toDoubleValueFor(solidUnit),
            niacin = niacin?.toDoubleValueFor(solidUnit),
            pantothenicAcid = pantothenicAcid?.toDoubleValueFor(solidUnit),
            phosphorus = phosphorus?.toDoubleValueFor(solidUnit),
            polyunsaturatedFat = polyunsaturatedFat?.toDoubleValueFor(solidUnit),
            potassium = potassium?.toDoubleValueFor(solidUnit),
            protein = protein?.toDoubleValueFor(solidUnit),
            riboflavin = riboflavin?.toDoubleValueFor(solidUnit),
            saturatedFat = saturatedFat?.toDoubleValueFor(solidUnit),
            selenium = selenium?.toDoubleValueFor(solidUnit),
            sodium = sodium?.toDoubleValueFor(solidUnit),
            sugar = sugar?.toDoubleValueFor(solidUnit),
            thiamin = thiamin?.toDoubleValueFor(solidUnit),
            totalCarbohydrate = totalCarbohydrate?.toDoubleValueFor(solidUnit),
            totalFat = totalFat?.toDoubleValueFor(solidUnit),
            vitaminA = vitaminA?.toDoubleValueFor(solidUnit),
            vitaminB12 = vitaminB12?.toDoubleValueFor(solidUnit),
            vitaminB6 = vitaminB6?.toDoubleValueFor(solidUnit),
            vitaminC = vitaminC?.toDoubleValueFor(solidUnit),
            vitaminD = vitaminD?.toDoubleValueFor(solidUnit),
            vitaminE = vitaminE?.toDoubleValueFor(solidUnit),
            vitaminK = vitaminK?.toDoubleValueFor(solidUnit),
            zinc = zinc?.toDoubleValueFor(solidUnit),
        )
    }

    is OvulationTestRecord -> KHRecord.OvulationTest(
        result = when (this.result) {
            OvulationTestRecord.RESULT_HIGH -> KHOvulationTestResult.High
            OvulationTestRecord.RESULT_NEGATIVE -> KHOvulationTestResult.Negative
            OvulationTestRecord.RESULT_POSITIVE -> KHOvulationTestResult.Positive
            OvulationTestRecord.RESULT_INCONCLUSIVE -> KHOvulationTestResult.Inconclusive
            else -> throw Exception("Unknown ovulation test result!")
        },
        time = this.time.toKotlinInstant()
    )

    is OxygenSaturationRecord -> KHRecord.OxygenSaturation(
        percentage = this.percentage.value,
        time = this.time.toKotlinInstant()
    )

    is PowerRecord -> {
        val unit = (request as KHReadRequest.Power).unit
        KHRecord.Power(
            samples = this.samples.map { sample ->
                KHPowerSample(
                    unit = unit,
                    value = sample.power toDoubleValueFor unit,
                    time = sample.time.toKotlinInstant(),
                )
            }
        )
    }

    is RespiratoryRateRecord -> KHRecord.RespiratoryRate(
        rate = this.rate,
        time = this.time.toKotlinInstant()
    )

    is RestingHeartRateRecord -> KHRecord.RestingHeartRate(
        beatsPerMinute = this.beatsPerMinute,
        time = this.time.toKotlinInstant()
    )

    is SexualActivityRecord -> KHRecord.SexualActivity(
        didUseProtection = this.protectionUsed == SexualActivityRecord.PROTECTION_USED_PROTECTED,
        time = this.time.toKotlinInstant()
    )

    is SleepSessionRecord -> KHRecord.SleepSession(
        samples = this.stages.map { sample ->
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
                startTime = this.startTime.toKotlinInstant(),
                endTime = this.endTime.toKotlinInstant(),
            )
        }
    )

    is SpeedRecord -> {
        val unit = (request as KHReadRequest.Speed).unit
        KHRecord.Speed(
            samples = this.samples.map { sample ->
                KHSpeedSample(
                    unit = unit,
                    value = sample.speed toDoubleValueFor unit,
                    time = sample.time.toKotlinInstant()
                )
            }
        )
    }

    is StepsRecord -> KHRecord.StepCount(
        count = this.count,
        startTime = this.startTime.toKotlinInstant(),
        endTime = this.endTime.toKotlinInstant()
    )

    is Vo2MaxRecord -> KHRecord.Vo2Max(
        vo2MillilitersPerMinuteKilogram = this.vo2MillilitersPerMinuteKilogram,
        time = this.time.toKotlinInstant()
    )

    is WeightRecord -> {
        val unit = (request as KHReadRequest.Weight).unit
        KHRecord.Weight(
            unit = unit,
            value = this.weight toDoubleValueFor unit,
            time = this.time.toKotlinInstant()
        )
    }

    is WheelchairPushesRecord -> KHRecord.WheelChairPushes(
        count = this.count,
        startTime = this.startTime.toKotlinInstant(),
        endTime = this.endTime.toKotlinInstant()
    )

    else -> throw Exception("Unknown record type ($this)!")
}

internal fun KHPermission.toPermissions(): Set<Pair<String, KHPermissionType>> {
    val entry = this@toPermissions
    return buildSet {
        entry.toRecordClass()?.let { safeRecord ->
            val isReadGranted = when (entry) {
                is KHPermission.ActiveCaloriesBurned -> entry.read
                is KHPermission.BasalMetabolicRate -> entry.read
                is KHPermission.BloodGlucose -> entry.read
                is KHPermission.BloodPressure -> entry.readSystolic || entry.readDiastolic
                is KHPermission.BodyFat -> entry.read
                is KHPermission.BodyTemperature -> entry.read
                is KHPermission.BodyWaterMass -> entry.read
                is KHPermission.BoneMass -> entry.read
                is KHPermission.CervicalMucus -> entry.read
                is KHPermission.CyclingPedalingCadence -> entry.read
                is KHPermission.CyclingSpeed -> entry.read
                is KHPermission.Distance -> entry.read
                is KHPermission.ElevationGained -> entry.read
                is KHPermission.Exercise -> entry.read
                is KHPermission.FloorsClimbed -> entry.read
                is KHPermission.HeartRate -> entry.read
                is KHPermission.HeartRateVariability -> entry.read
                is KHPermission.Height -> entry.read
                is KHPermission.Hydration -> entry.read
                is KHPermission.IntermenstrualBleeding -> entry.read
                is KHPermission.LeanBodyMass -> entry.read
                is KHPermission.MenstruationFlow -> entry.read
                is KHPermission.MenstruationPeriod -> entry.read
                is KHPermission.Nutrition -> entry.readBiotin ||
                        entry.readCaffeine ||
                        entry.readCalcium ||
                        entry.readChloride ||
                        entry.readCholesterol ||
                        entry.readChromium ||
                        entry.readCopper ||
                        entry.readDietaryFiber ||
                        entry.readEnergy ||
                        entry.readFolicAcid ||
                        entry.readIodine ||
                        entry.readIron ||
                        entry.readMagnesium ||
                        entry.readManganese ||
                        entry.readMolybdenum ||
                        entry.readMonounsaturatedFat ||
                        entry.readNiacin ||
                        entry.readPantothenicAcid ||
                        entry.readPhosphorus ||
                        entry.readPolyunsaturatedFat ||
                        entry.readPotassium ||
                        entry.readProtein ||
                        entry.readRiboflavin ||
                        entry.readSaturatedFat ||
                        entry.readSelenium ||
                        entry.readSodium ||
                        entry.readSugar ||
                        entry.readThiamin ||
                        entry.readTotalCarbohydrate ||
                        entry.readTotalFat ||
                        entry.readVitaminA ||
                        entry.readVitaminB12 ||
                        entry.readVitaminB6 ||
                        entry.readVitaminC ||
                        entry.readVitaminD ||
                        entry.readVitaminE ||
                        entry.readVitaminK ||
                        entry.readZinc

                is KHPermission.OvulationTest -> entry.read
                is KHPermission.OxygenSaturation -> entry.read
                is KHPermission.Power -> entry.read
                is KHPermission.RespiratoryRate -> entry.read
                is KHPermission.RestingHeartRate -> entry.read
                is KHPermission.RunningSpeed -> entry.read
                is KHPermission.SexualActivity -> entry.read
                is KHPermission.SleepSession -> entry.read
                is KHPermission.Speed -> entry.read
                is KHPermission.StepCount -> entry.read
                is KHPermission.Vo2Max -> entry.read
                is KHPermission.Weight -> entry.read
                is KHPermission.WheelChairPushes -> entry.read
            }

            val isWriteGranted = when (entry) {
                is KHPermission.ActiveCaloriesBurned -> entry.write
                is KHPermission.BasalMetabolicRate -> entry.write
                is KHPermission.BloodGlucose -> entry.write
                is KHPermission.BloodPressure -> entry.writeSystolic || entry.writeDiastolic
                is KHPermission.BodyFat -> entry.write
                is KHPermission.BodyTemperature -> entry.write
                is KHPermission.BodyWaterMass -> entry.write
                is KHPermission.BoneMass -> entry.write
                is KHPermission.CervicalMucus -> entry.write
                is KHPermission.CyclingPedalingCadence -> entry.write
                is KHPermission.CyclingSpeed -> entry.write
                is KHPermission.Distance -> entry.write
                is KHPermission.ElevationGained -> entry.write
                is KHPermission.Exercise -> entry.write
                is KHPermission.FloorsClimbed -> entry.write
                is KHPermission.HeartRate -> entry.write
                is KHPermission.HeartRateVariability -> entry.write
                is KHPermission.Height -> entry.write
                is KHPermission.Hydration -> entry.write
                is KHPermission.IntermenstrualBleeding -> entry.write
                is KHPermission.LeanBodyMass -> entry.write
                is KHPermission.MenstruationFlow -> entry.write
                is KHPermission.MenstruationPeriod -> entry.write
                is KHPermission.Nutrition -> entry.writeBiotin ||
                        entry.writeCaffeine ||
                        entry.writeCalcium ||
                        entry.writeChloride ||
                        entry.writeCholesterol ||
                        entry.writeChromium ||
                        entry.writeCopper ||
                        entry.writeDietaryFiber ||
                        entry.writeEnergy ||
                        entry.writeFolicAcid ||
                        entry.writeIodine ||
                        entry.writeIron ||
                        entry.writeMagnesium ||
                        entry.writeManganese ||
                        entry.writeMolybdenum ||
                        entry.writeMonounsaturatedFat ||
                        entry.writeNiacin ||
                        entry.writePantothenicAcid ||
                        entry.writePhosphorus ||
                        entry.writePolyunsaturatedFat ||
                        entry.writePotassium ||
                        entry.writeProtein ||
                        entry.writeRiboflavin ||
                        entry.writeSaturatedFat ||
                        entry.writeSelenium ||
                        entry.writeSodium ||
                        entry.writeSugar ||
                        entry.writeThiamin ||
                        entry.writeTotalCarbohydrate ||
                        entry.writeTotalFat ||
                        entry.writeVitaminA ||
                        entry.writeVitaminB12 ||
                        entry.writeVitaminB6 ||
                        entry.writeVitaminC ||
                        entry.writeVitaminD ||
                        entry.writeVitaminE ||
                        entry.writeVitaminK ||
                        entry.writeZinc

                is KHPermission.OvulationTest -> entry.write
                is KHPermission.OxygenSaturation -> entry.write
                is KHPermission.Power -> entry.write
                is KHPermission.RespiratoryRate -> entry.write
                is KHPermission.RestingHeartRate -> entry.write
                is KHPermission.RunningSpeed -> entry.write
                is KHPermission.SexualActivity -> entry.write
                is KHPermission.SleepSession -> entry.write
                is KHPermission.Speed -> entry.write
                is KHPermission.StepCount -> entry.write
                is KHPermission.Vo2Max -> entry.write
                is KHPermission.Weight -> entry.write
                is KHPermission.WheelChairPushes -> entry.write
            }

            if (isReadGranted) {
                add(HealthPermission.getReadPermission(safeRecord) to KHPermissionType.Read)
            }
            if (isWriteGranted) {
                add(HealthPermission.getWritePermission(safeRecord) to KHPermissionType.Write)
            }
        }
    }
}

internal fun Array<out KHPermission>.toPermissionsWithStatuses(
    grantedPermissions: Set<String>
): List<KHPermission> = this.mapIndexed { index, entry ->
    val permissionSet = entry.toPermissions()

    val readGranted = permissionSet.firstOrNull { it.second == KHPermissionType.Read }
        ?.first
        ?.let(grantedPermissions::contains)
        ?: false

    val writeGranted = permissionSet.firstOrNull { it.second == KHPermissionType.Write }
        ?.first
        ?.let(grantedPermissions::contains)
        ?: false

    when (this[index]) {
        is KHPermission.ActiveCaloriesBurned ->
            KHPermission.ActiveCaloriesBurned(read = readGranted, write = writeGranted)

        is KHPermission.BasalMetabolicRate ->
            KHPermission.BasalMetabolicRate(read = readGranted, write = writeGranted)

        is KHPermission.BloodGlucose ->
            KHPermission.BloodGlucose(read = readGranted, write = writeGranted)

        is KHPermission.BloodPressure -> KHPermission.BloodPressure(
            readSystolic = readGranted,
            writeSystolic = writeGranted,
            readDiastolic = readGranted,
            writeDiastolic = writeGranted
        )

        is KHPermission.BodyFat ->
            KHPermission.BodyFat(read = readGranted, write = writeGranted)

        is KHPermission.BodyTemperature ->
            KHPermission.BodyTemperature(read = readGranted, write = writeGranted)

        is KHPermission.BodyWaterMass ->
            KHPermission.BodyWaterMass(read = readGranted, write = writeGranted)

        is KHPermission.BoneMass ->
            KHPermission.BoneMass(read = readGranted, write = writeGranted)

        is KHPermission.CervicalMucus ->
            KHPermission.CervicalMucus(read = readGranted, write = writeGranted)

        is KHPermission.CyclingPedalingCadence ->
            KHPermission.CyclingPedalingCadence(read = readGranted, write = writeGranted)

        is KHPermission.CyclingSpeed ->
            KHPermission.CyclingSpeed(read = readGranted, write = writeGranted)

        is KHPermission.Distance ->
            KHPermission.Distance(read = readGranted, write = writeGranted)

        is KHPermission.ElevationGained ->
            KHPermission.ElevationGained(read = readGranted, write = writeGranted)

        is KHPermission.Exercise ->
            KHPermission.Exercise(read = readGranted, write = writeGranted)

        is KHPermission.FloorsClimbed ->
            KHPermission.FloorsClimbed(read = readGranted, write = writeGranted)

        is KHPermission.HeartRate ->
            KHPermission.HeartRate(read = readGranted, write = writeGranted)

        is KHPermission.HeartRateVariability ->
            KHPermission.HeartRateVariability(read = readGranted, write = writeGranted)

        is KHPermission.Height ->
            KHPermission.Height(read = readGranted, write = writeGranted)

        is KHPermission.Hydration ->
            KHPermission.Hydration(read = readGranted, write = writeGranted)

        is KHPermission.IntermenstrualBleeding ->
            KHPermission.IntermenstrualBleeding(read = readGranted, write = writeGranted)

        is KHPermission.LeanBodyMass ->
            KHPermission.LeanBodyMass(read = readGranted, write = writeGranted)

        is KHPermission.MenstruationFlow ->
            KHPermission.MenstruationFlow(read = readGranted, write = writeGranted)

        is KHPermission.MenstruationPeriod ->
            KHPermission.MenstruationPeriod(read = readGranted, write = writeGranted)

        is KHPermission.Nutrition -> KHPermission.Nutrition(
            readBiotin = readGranted,
            writeBiotin = writeGranted,
            readCaffeine = readGranted,
            writeCaffeine = writeGranted,
            readCalcium = readGranted,
            writeCalcium = writeGranted,
            readChloride = readGranted,
            writeChloride = writeGranted,
            readCholesterol = readGranted,
            writeCholesterol = writeGranted,
            readChromium = readGranted,
            writeChromium = writeGranted,
            readCopper = readGranted,
            writeCopper = writeGranted,
            readDietaryFiber = readGranted,
            writeDietaryFiber = writeGranted,
            readEnergy = readGranted,
            writeEnergy = writeGranted,
            readFolicAcid = readGranted,
            writeFolicAcid = writeGranted,
            readIodine = readGranted,
            writeIodine = writeGranted,
            readIron = readGranted,
            writeIron = writeGranted,
            readMagnesium = readGranted,
            writeMagnesium = writeGranted,
            readManganese = readGranted,
            writeManganese = writeGranted,
            readMolybdenum = readGranted,
            writeMolybdenum = writeGranted,
            readMonounsaturatedFat = readGranted,
            writeMonounsaturatedFat = writeGranted,
            readNiacin = readGranted,
            writeNiacin = writeGranted,
            readPantothenicAcid = readGranted,
            writePantothenicAcid = writeGranted,
            readPhosphorus = readGranted,
            writePhosphorus = writeGranted,
            readPolyunsaturatedFat = readGranted,
            writePolyunsaturatedFat = writeGranted,
            readPotassium = readGranted,
            writePotassium = writeGranted,
            readProtein = readGranted,
            writeProtein = writeGranted,
            readRiboflavin = readGranted,
            writeRiboflavin = writeGranted,
            readSaturatedFat = readGranted,
            writeSaturatedFat = writeGranted,
            readSelenium = readGranted,
            writeSelenium = writeGranted,
            readSodium = readGranted,
            writeSodium = writeGranted,
            readSugar = readGranted,
            writeSugar = writeGranted,
            readThiamin = readGranted,
            writeThiamin = writeGranted,
            readTotalCarbohydrate = readGranted,
            writeTotalCarbohydrate = writeGranted,
            readTotalFat = readGranted,
            writeTotalFat = writeGranted,
            readVitaminA = readGranted,
            writeVitaminA = writeGranted,
            readVitaminB12 = readGranted,
            writeVitaminB12 = writeGranted,
            readVitaminB6 = readGranted,
            writeVitaminB6 = writeGranted,
            readVitaminC = readGranted,
            writeVitaminC = writeGranted,
            readVitaminD = readGranted,
            writeVitaminD = writeGranted,
            readVitaminE = readGranted,
            writeVitaminE = writeGranted,
            readVitaminK = readGranted,
            writeVitaminK = writeGranted,
            readZinc = readGranted,
            writeZinc = writeGranted,
        )

        is KHPermission.OvulationTest ->
            KHPermission.OvulationTest(read = readGranted, write = writeGranted)

        is KHPermission.OxygenSaturation ->
            KHPermission.OxygenSaturation(read = readGranted, write = writeGranted)

        is KHPermission.Power ->
            KHPermission.Power(read = readGranted, write = writeGranted)

        is KHPermission.RespiratoryRate ->
            KHPermission.RespiratoryRate(read = readGranted, write = writeGranted)

        is KHPermission.RestingHeartRate ->
            KHPermission.RestingHeartRate(read = readGranted, write = writeGranted)

        is KHPermission.RunningSpeed ->
            KHPermission.RunningSpeed(read = readGranted, write = writeGranted)

        is KHPermission.SexualActivity ->
            KHPermission.SexualActivity(read = readGranted, write = writeGranted)

        is KHPermission.SleepSession ->
            KHPermission.SleepSession(read = readGranted, write = writeGranted)

        is KHPermission.Speed ->
            KHPermission.Speed(read = readGranted, write = writeGranted)

        is KHPermission.StepCount ->
            KHPermission.StepCount(read = readGranted, write = writeGranted)

        is KHPermission.Vo2Max ->
            KHPermission.Vo2Max(read = readGranted, write = writeGranted)

        is KHPermission.Weight ->
            KHPermission.Weight(read = readGranted, write = writeGranted)

        is KHPermission.WheelChairPushes ->
            KHPermission.WheelChairPushes(read = readGranted, write = writeGranted)
    }
}

internal fun KHReadRequest.toRecordClass(): KClass<out Record>? = when (this) {
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

internal fun KHPermission.toRecordClass(): KClass<out Record>? = when (this) {
    is KHPermission.ActiveCaloriesBurned -> ActiveCaloriesBurnedRecord::class
    is KHPermission.BasalMetabolicRate -> BasalMetabolicRateRecord::class
    is KHPermission.BloodGlucose -> BloodGlucoseRecord::class
    is KHPermission.BloodPressure -> BloodPressureRecord::class
    is KHPermission.BodyFat -> BodyFatRecord::class
    is KHPermission.BodyTemperature -> BodyTemperatureRecord::class
    is KHPermission.BodyWaterMass -> BodyWaterMassRecord::class
    is KHPermission.BoneMass -> BoneMassRecord::class
    is KHPermission.CervicalMucus -> CervicalMucusRecord::class
    is KHPermission.CyclingPedalingCadence -> CyclingPedalingCadenceRecord::class
    is KHPermission.CyclingSpeed -> null
    is KHPermission.Distance -> DistanceRecord::class
    is KHPermission.ElevationGained -> ElevationGainedRecord::class
    is KHPermission.Exercise -> ExerciseSessionRecord::class
    is KHPermission.FloorsClimbed -> FloorsClimbedRecord::class
    is KHPermission.HeartRate -> HeartRateRecord::class
    is KHPermission.HeartRateVariability -> HeartRateVariabilityRmssdRecord::class
    is KHPermission.Height -> HeightRecord::class
    is KHPermission.Hydration -> HydrationRecord::class
    is KHPermission.IntermenstrualBleeding -> IntermenstrualBleedingRecord::class
    is KHPermission.LeanBodyMass -> LeanBodyMassRecord::class
    is KHPermission.MenstruationFlow -> MenstruationFlowRecord::class
    is KHPermission.MenstruationPeriod -> MenstruationPeriodRecord::class
    is KHPermission.Nutrition -> NutritionRecord::class
    is KHPermission.OvulationTest -> OvulationTestRecord::class
    is KHPermission.OxygenSaturation -> OxygenSaturationRecord::class
    is KHPermission.Power -> PowerRecord::class
    is KHPermission.RespiratoryRate -> RespiratoryRateRecord::class
    is KHPermission.RestingHeartRate -> RestingHeartRateRecord::class
    is KHPermission.RunningSpeed -> null
    is KHPermission.SexualActivity -> SexualActivityRecord::class
    is KHPermission.SleepSession -> SleepSessionRecord::class
    is KHPermission.Speed -> SpeedRecord::class
    is KHPermission.StepCount -> StepsRecord::class
    is KHPermission.Vo2Max -> Vo2MaxRecord::class
    is KHPermission.Weight -> WeightRecord::class
    is KHPermission.WheelChairPushes -> WheelchairPushesRecord::class
}

internal fun KHRecord.toHCRecord(): Record? {
    return when (this) {
        is KHRecord.ActiveCaloriesBurned -> ActiveCaloriesBurnedRecord(
            startTime = startTime.toJavaInstant(),
            endTime = endTime.toJavaInstant(),
            startZoneOffset = null,
            endZoneOffset = null,
            energy = unit toNativeEnergyFor value,
            // TODO: Figure out how to fetch the correct value (and use the same for all records)
            metadata = Metadata.autoRecorded(device = Device(type = Device.TYPE_PHONE))
        )

        is KHRecord.BasalMetabolicRate -> BasalMetabolicRateRecord(
            time = time.toJavaInstant(),
            zoneOffset = null,
            basalMetabolicRate = unit.left toNativePowerFor value,
            metadata = Metadata.autoRecorded(device = Device(type = Device.TYPE_PHONE))
        )

        is KHRecord.BloodGlucose -> BloodGlucoseRecord(
            time = time.toJavaInstant(),
            zoneOffset = null,
            level = unit toNativeBloodGlucoseFor value,
            metadata = Metadata.autoRecorded(device = Device(type = Device.TYPE_PHONE))
        )

        is KHRecord.BloodPressure -> BloodPressureRecord(
            time = time.toJavaInstant(),
            zoneOffset = null,
            systolic = unit toNativePressureFor systolicValue,
            diastolic = unit toNativePressureFor diastolicValue,
            metadata = Metadata.autoRecorded(device = Device(type = Device.TYPE_PHONE))
        )

        is KHRecord.BodyFat -> BodyFatRecord(
            time = time.toJavaInstant(),
            zoneOffset = null,
            percentage = percentage.percent,
            metadata = Metadata.autoRecorded(device = Device(type = Device.TYPE_PHONE))
        )

        is KHRecord.BodyTemperature -> BodyTemperatureRecord(
            time = time.toJavaInstant(),
            zoneOffset = null,
            temperature = unit toNativeTemperatureFor value,
            metadata = Metadata.autoRecorded(device = Device(type = Device.TYPE_PHONE))
        )

        is KHRecord.BodyWaterMass -> BodyWaterMassRecord(
            time = time.toJavaInstant(),
            zoneOffset = null,
            mass = unit toNativeMassFor value,
            metadata = Metadata.autoRecorded(device = Device(type = Device.TYPE_PHONE))
        )

        is KHRecord.BoneMass -> BoneMassRecord(
            time = time.toJavaInstant(),
            zoneOffset = null,
            mass = unit toNativeMassFor value,
            metadata = Metadata.autoRecorded(device = Device(type = Device.TYPE_PHONE))
        )

        is KHRecord.CervicalMucus -> CervicalMucusRecord(
            time = time.toJavaInstant(),
            zoneOffset = null,
            appearance = when (appearance) {
                KHCervicalMucusAppearance.Creamy -> CervicalMucusRecord.APPEARANCE_CREAMY
                KHCervicalMucusAppearance.Dry -> CervicalMucusRecord.APPEARANCE_DRY
                KHCervicalMucusAppearance.EggWhite -> CervicalMucusRecord.APPEARANCE_EGG_WHITE
                KHCervicalMucusAppearance.Sticky -> CervicalMucusRecord.APPEARANCE_STICKY
                KHCervicalMucusAppearance.Watery -> CervicalMucusRecord.APPEARANCE_WATERY
            },
            metadata = Metadata.autoRecorded(device = Device(type = Device.TYPE_PHONE))
        )

        is KHRecord.CyclingPedalingCadence -> CyclingPedalingCadenceRecord(
            startTime = startTime.toJavaInstant(),
            startZoneOffset = null,
            endTime = endTime.toJavaInstant(),
            endZoneOffset = null,
            samples = samples.map { sample ->
                CyclingPedalingCadenceRecord.Sample(
                    time = sample.time.toJavaInstant(),
                    revolutionsPerMinute = sample.revolutionsPerMinute,
                )
            },
            metadata = Metadata.autoRecorded(device = Device(type = Device.TYPE_PHONE))
        )

        is KHRecord.CyclingSpeed -> null

        is KHRecord.Distance -> DistanceRecord(
            startTime = startTime.toJavaInstant(),
            startZoneOffset = null,
            endTime = endTime.toJavaInstant(),
            endZoneOffset = null,
            distance = unit toNativeLengthFor value,
            metadata = Metadata.autoRecorded(device = Device(type = Device.TYPE_PHONE))
        )

        is KHRecord.ElevationGained -> ElevationGainedRecord(
            startTime = startTime.toJavaInstant(),
            startZoneOffset = null,
            endTime = endTime.toJavaInstant(),
            endZoneOffset = null,
            elevation = unit toNativeLengthFor value,
            metadata = Metadata.autoRecorded(device = Device(type = Device.TYPE_PHONE))
        )

        // TODO: Figure out how ExerciseSessionRecord works now
        is KHRecord.Exercise -> null

//        is KHRecord.Exercise -> type.toNativeExerciseTypeOrNull()?.let { exerciseType ->
//            ExerciseSessionRecord(
//                startTime = startTime.toJavaInstant(),
//                startZoneOffset = null,
//                endTime = endTime.toJavaInstant(),
//                endZoneOffset = null,
//                exerciseType = exerciseType,
//                metadata = Metadata.autoRecorded(device = Device(type = Device.TYPE_PHONE))
//            )
//        }

        is KHRecord.FloorsClimbed -> FloorsClimbedRecord(
            startTime = startTime.toJavaInstant(),
            startZoneOffset = null,
            endTime = endTime.toJavaInstant(),
            endZoneOffset = null,
            floors = floors,
            metadata = Metadata.autoRecorded(device = Device(type = Device.TYPE_PHONE))
        )

        is KHRecord.HeartRate -> HeartRateRecord(
            startTime = samples.first().time.toJavaInstant(),
            startZoneOffset = null,
            endTime = samples.last().time.toJavaInstant(),
            endZoneOffset = null,
            samples = samples.map { sample ->
                HeartRateRecord.Sample(
                    time = sample.time.toJavaInstant(),
                    beatsPerMinute = sample.beatsPerMinute,
                )
            },
            metadata = Metadata.autoRecorded(device = Device(type = Device.TYPE_PHONE))
        )

        is KHRecord.HeartRateVariability -> HeartRateVariabilityRmssdRecord(
            time = time.toJavaInstant(),
            zoneOffset = null,
            heartRateVariabilityMillis = heartRateVariabilityMillis,
            metadata = Metadata.autoRecorded(device = Device(type = Device.TYPE_PHONE))
        )

        is KHRecord.Height -> HeightRecord(
            time = time.toJavaInstant(),
            zoneOffset = null,
            height = unit toNativeLengthFor value,
            metadata = Metadata.autoRecorded(device = Device(type = Device.TYPE_PHONE))
        )

        is KHRecord.Hydration -> HydrationRecord(
            startTime = startTime.toJavaInstant(),
            startZoneOffset = null,
            endTime = endTime.toJavaInstant(),
            endZoneOffset = null,
            volume = unit toNativeVolumeFor value,
            metadata = Metadata.autoRecorded(device = Device(type = Device.TYPE_PHONE))
        )

        is KHRecord.IntermenstrualBleeding -> IntermenstrualBleedingRecord(
            time = time.toJavaInstant(),
            zoneOffset = null,
            metadata = Metadata.autoRecorded(device = Device(type = Device.TYPE_PHONE))
        )

        is KHRecord.LeanBodyMass -> LeanBodyMassRecord(
            time = time.toJavaInstant(),
            zoneOffset = null,
            mass = unit toNativeMassFor value,
            metadata = Metadata.autoRecorded(device = Device(type = Device.TYPE_PHONE))
        )

        is KHRecord.MenstruationPeriod -> MenstruationPeriodRecord(
            startTime = startTime.toJavaInstant(),
            startZoneOffset = null,
            endTime = endTime.toJavaInstant(),
            endZoneOffset = null,
            metadata = Metadata.autoRecorded(device = Device(type = Device.TYPE_PHONE))
        )

        is KHRecord.MenstruationFlow -> MenstruationFlowRecord(
            time = time.toJavaInstant(),
            zoneOffset = null,
            flow = when (type) {
                KHMenstruationFlowType.Unknown -> MenstruationFlowRecord.FLOW_UNKNOWN
                KHMenstruationFlowType.Light -> MenstruationFlowRecord.FLOW_LIGHT
                KHMenstruationFlowType.Medium -> MenstruationFlowRecord.FLOW_MEDIUM
                KHMenstruationFlowType.Heavy -> MenstruationFlowRecord.FLOW_HEAVY
            },
            metadata = Metadata.autoRecorded(device = Device(type = Device.TYPE_PHONE))
        )

        is KHRecord.Nutrition -> NutritionRecord(
            name = name,
            startTime = startTime.toJavaInstant(),
            startZoneOffset = null,
            endTime = endTime.toJavaInstant(),
            endZoneOffset = null,
            mealType = mealType.toHCMealType(),
            biotin = biotin?.let(solidUnit::toNativeMassFor),
            caffeine = caffeine?.let(solidUnit::toNativeMassFor),
            calcium = calcium?.let(solidUnit::toNativeMassFor),
            chloride = chloride?.let(solidUnit::toNativeMassFor),
            cholesterol = cholesterol?.let(solidUnit::toNativeMassFor),
            chromium = chromium?.let(solidUnit::toNativeMassFor),
            copper = copper?.let(solidUnit::toNativeMassFor),
            dietaryFiber = dietaryFiber?.let(solidUnit::toNativeMassFor),
            energy = energy?.let(energyUnit::toNativeEnergyFor),
            folicAcid = folicAcid?.let(solidUnit::toNativeMassFor),
            iodine = iodine?.let(solidUnit::toNativeMassFor),
            iron = iron?.let(solidUnit::toNativeMassFor),
            magnesium = magnesium?.let(solidUnit::toNativeMassFor),
            manganese = manganese?.let(solidUnit::toNativeMassFor),
            molybdenum = molybdenum?.let(solidUnit::toNativeMassFor),
            monounsaturatedFat = monounsaturatedFat?.let(solidUnit::toNativeMassFor),
            niacin = niacin?.let(solidUnit::toNativeMassFor),
            pantothenicAcid = pantothenicAcid?.let(solidUnit::toNativeMassFor),
            phosphorus = phosphorus?.let(solidUnit::toNativeMassFor),
            polyunsaturatedFat = polyunsaturatedFat?.let(solidUnit::toNativeMassFor),
            potassium = potassium?.let(solidUnit::toNativeMassFor),
            protein = protein?.let(solidUnit::toNativeMassFor),
            riboflavin = riboflavin?.let(solidUnit::toNativeMassFor),
            saturatedFat = saturatedFat?.let(solidUnit::toNativeMassFor),
            selenium = selenium?.let(solidUnit::toNativeMassFor),
            sodium = sodium?.let(solidUnit::toNativeMassFor),
            sugar = sugar?.let(solidUnit::toNativeMassFor),
            thiamin = thiamin?.let(solidUnit::toNativeMassFor),
            totalCarbohydrate = totalCarbohydrate?.let(solidUnit::toNativeMassFor),
            totalFat = totalFat?.let(solidUnit::toNativeMassFor),
            vitaminA = vitaminA?.let(solidUnit::toNativeMassFor),
            vitaminB12 = vitaminB12?.let(solidUnit::toNativeMassFor),
            vitaminB6 = vitaminB6?.let(solidUnit::toNativeMassFor),
            vitaminC = vitaminC?.let(solidUnit::toNativeMassFor),
            vitaminD = vitaminD?.let(solidUnit::toNativeMassFor),
            vitaminE = vitaminE?.let(solidUnit::toNativeMassFor),
            vitaminK = vitaminK?.let(solidUnit::toNativeMassFor),
            zinc = zinc?.let(solidUnit::toNativeMassFor),
            metadata = Metadata.autoRecorded(device = Device(type = Device.TYPE_PHONE))
        )

        is KHRecord.OvulationTest -> OvulationTestRecord(
            time = time.toJavaInstant(),
            zoneOffset = null,
            result = when (result) {
                KHOvulationTestResult.High -> OvulationTestRecord.RESULT_HIGH
                KHOvulationTestResult.Negative -> OvulationTestRecord.RESULT_NEGATIVE
                KHOvulationTestResult.Positive -> OvulationTestRecord.RESULT_POSITIVE
                KHOvulationTestResult.Inconclusive -> OvulationTestRecord.RESULT_INCONCLUSIVE
            },
            metadata = Metadata.autoRecorded(device = Device(type = Device.TYPE_PHONE))
        )

        is KHRecord.OxygenSaturation -> OxygenSaturationRecord(
            time = time.toJavaInstant(),
            zoneOffset = null,
            percentage = percentage.percent,
            metadata = Metadata.autoRecorded(device = Device(type = Device.TYPE_PHONE))
        )

        is KHRecord.Power -> PowerRecord(
            startTime = samples.first().time.toJavaInstant(),
            startZoneOffset = null,
            endTime = samples.last().time.toJavaInstant(),
            endZoneOffset = null,
            samples = samples.map { sample ->
                PowerRecord.Sample(
                    time = sample.time.toJavaInstant(),
                    power = sample.unit toNativePowerFor sample.value
                )
            },
            metadata = Metadata.autoRecorded(device = Device(type = Device.TYPE_PHONE))
        )

        is KHRecord.RespiratoryRate -> RespiratoryRateRecord(
            time = time.toJavaInstant(),
            zoneOffset = null,
            rate = rate,
            metadata = Metadata.autoRecorded(device = Device(type = Device.TYPE_PHONE))
        )

        is KHRecord.RestingHeartRate -> RestingHeartRateRecord(
            time = time.toJavaInstant(),
            zoneOffset = null,
            beatsPerMinute = beatsPerMinute,
            metadata = Metadata.autoRecorded(device = Device(type = Device.TYPE_PHONE))
        )

        is KHRecord.RunningSpeed -> null

        is KHRecord.SexualActivity -> SexualActivityRecord(
            time = time.toJavaInstant(),
            zoneOffset = null,
            protectionUsed = if (didUseProtection) SexualActivityRecord.PROTECTION_USED_PROTECTED
            else SexualActivityRecord.PROTECTION_USED_UNPROTECTED,
            metadata = Metadata.autoRecorded(device = Device(type = Device.TYPE_PHONE))
        )

        is KHRecord.SleepSession -> SleepSessionRecord(
            startTime = samples.first().startTime.toJavaInstant(),
            startZoneOffset = null,
            endTime = samples.last().endTime.toJavaInstant(),
            endZoneOffset = null,
            stages = samples.map { sample ->
                SleepSessionRecord.Stage(
                    startTime = sample.startTime.toJavaInstant(),
                    endTime = sample.endTime.toJavaInstant(),
                    stage = when (sample.stage) {
                        KHSleepStage.Awake -> SleepSessionRecord.STAGE_TYPE_AWAKE
                        KHSleepStage.AwakeInBed -> SleepSessionRecord.STAGE_TYPE_AWAKE_IN_BED
                        KHSleepStage.AwakeOutOfBed -> SleepSessionRecord.STAGE_TYPE_OUT_OF_BED
                        KHSleepStage.Deep -> SleepSessionRecord.STAGE_TYPE_DEEP
                        KHSleepStage.Light -> SleepSessionRecord.STAGE_TYPE_LIGHT
                        KHSleepStage.REM -> SleepSessionRecord.STAGE_TYPE_REM
                        KHSleepStage.Sleeping -> SleepSessionRecord.STAGE_TYPE_SLEEPING
                        KHSleepStage.Unknown -> SleepSessionRecord.STAGE_TYPE_UNKNOWN
                    }
                )
            },
            metadata = Metadata.autoRecorded(device = Device(type = Device.TYPE_PHONE))
        )

        is KHRecord.Speed -> SpeedRecord(
            startTime = samples.first().time.toJavaInstant(),
            startZoneOffset = null,
            endTime = samples.last().time.toJavaInstant(),
            endZoneOffset = null,
            samples = samples.map { sample ->
                SpeedRecord.Sample(
                    time = sample.time.toJavaInstant(),
                    speed = sample.unit toNativeVelocityFor sample.value,
                )
            },
            metadata = Metadata.autoRecorded(device = Device(type = Device.TYPE_PHONE))
        )

        is KHRecord.StepCount -> StepsRecord(
            startTime = startTime.toJavaInstant(),
            startZoneOffset = null,
            endTime = endTime.toJavaInstant(),
            endZoneOffset = null,
            count = count,
            metadata = Metadata.autoRecorded(device = Device(type = Device.TYPE_PHONE))
        )

        is KHRecord.Vo2Max -> Vo2MaxRecord(
            time = time.toJavaInstant(),
            zoneOffset = null,
            vo2MillilitersPerMinuteKilogram = vo2MillilitersPerMinuteKilogram,
            metadata = Metadata.autoRecorded(device = Device(type = Device.TYPE_PHONE))
        )

        is KHRecord.Weight -> WeightRecord(
            time = time.toJavaInstant(),
            zoneOffset = null,
            weight = unit toNativeMassFor value,
            metadata = Metadata.autoRecorded(device = Device(type = Device.TYPE_PHONE))
        )

        is KHRecord.WheelChairPushes -> WheelchairPushesRecord(
            startTime = startTime.toJavaInstant(),
            startZoneOffset = null,
            endTime = endTime.toJavaInstant(),
            endZoneOffset = null,
            count = count,
            metadata = Metadata.autoRecorded(device = Device(type = Device.TYPE_PHONE))
        )
    }
}

internal infix fun KHUnit.BloodGlucose.toNativeBloodGlucoseFor(value: Double): BloodGlucose {
    return when (this) {
        KHUnit.BloodGlucose.MilligramsPerDeciliter -> BloodGlucose.milligramsPerDeciliter(value)
        KHUnit.BloodGlucose.MillimolesPerLiter -> BloodGlucose.millimolesPerLiter(value)
    }
}

internal infix fun BloodGlucose.toDoubleValueFor(bloodGlucose: KHUnit.BloodGlucose): Double {
    return when (bloodGlucose) {
        KHUnit.BloodGlucose.MilligramsPerDeciliter -> this.inMilligramsPerDeciliter
        KHUnit.BloodGlucose.MillimolesPerLiter -> this.inMillimolesPerLiter
    }
}

internal infix fun KHUnit.Energy.toNativeEnergyFor(value: Double): Energy = when (this) {
    KHUnit.Energy.Calorie -> value.calories
    KHUnit.Energy.Joule -> value.joules
    KHUnit.Energy.KiloCalorie -> value.kilocalories
    KHUnit.Energy.KiloJoule -> value.kilojoules
}

internal infix fun Energy.toDoubleValueFor(energy: KHUnit.Energy): Double = when (energy) {
    KHUnit.Energy.Calorie -> this.inCalories
    KHUnit.Energy.Joule -> this.inJoules
    KHUnit.Energy.KiloCalorie -> this.inKilocalories
    KHUnit.Energy.KiloJoule -> this.inKilojoules
}

internal infix fun KHUnit.Length.toNativeLengthFor(value: Double): Length = when (this) {
    is KHUnit.Length.Inch -> value.inches
    is KHUnit.Length.Meter -> value.meters
    is KHUnit.Length.Mile -> value.miles
}

internal infix fun Length.toDoubleValueFor(length: KHUnit.Length): Double = when (length) {
    KHUnit.Length.Inch -> this.inInches
    KHUnit.Length.Meter -> this.inMeters
    KHUnit.Length.Mile -> this.inMiles
}

internal infix fun KHUnit.Mass.toNativeMassFor(value: Double): Mass = when (this) {
    is KHUnit.Mass.Gram -> value.grams
    is KHUnit.Mass.Ounce -> value.ounces
    is KHUnit.Mass.Pound -> value.pounds
}

internal infix fun Mass.toDoubleValueFor(mass: KHUnit.Mass): Double = when (mass) {
    KHUnit.Mass.Gram -> this.inGrams
    KHUnit.Mass.Ounce -> this.inOunces
    KHUnit.Mass.Pound -> this.inPounds
}

internal infix fun KHUnit.Power.toNativePowerFor(value: Double): Power = when (this) {
    is KHUnit.Power.KilocaloriePerDay -> value.kilocaloriesPerDay
    is KHUnit.Power.Watt -> value.watts
}

internal infix fun Power.toDoubleValueFor(power: KHUnit.Power): Double = when (power) {
    KHUnit.Power.KilocaloriePerDay -> this.inKilocaloriesPerDay
    KHUnit.Power.Watt -> this.inWatts
}

internal infix fun KHUnit.Temperature.toNativeTemperatureFor(value: Double): Temperature {
    return when (this) {
        KHUnit.Temperature.Celsius -> value.celsius
        KHUnit.Temperature.Fahrenheit -> value.fahrenheit
    }
}

internal infix fun Temperature.toDoubleValueFor(temperature: KHUnit.Temperature): Double {
    return when (temperature) {
        KHUnit.Temperature.Celsius -> this.inCelsius
        KHUnit.Temperature.Fahrenheit -> this.inFahrenheit
    }
}

internal infix fun KHUnit.Velocity.toNativeVelocityFor(value: Double): Velocity = when (this) {
    is KHUnit.Velocity.KilometersPerHour -> value.kilometersPerHour
    is KHUnit.Velocity.MetersPerSecond -> value.metersPerSecond
    is KHUnit.Velocity.MilesPerHour -> value.milesPerHour
}

internal infix fun Velocity.toDoubleValueFor(velocity: KHUnit.Velocity): Double = when (velocity) {
    KHUnit.Velocity.KilometersPerHour -> this.inKilometersPerHour
    KHUnit.Velocity.MetersPerSecond -> this.inMetersPerSecond
    KHUnit.Velocity.MilesPerHour -> this.inMilesPerHour
}

internal infix fun KHUnit.Pressure.toNativePressureFor(value: Double): Pressure = when (this) {
    is KHUnit.Pressure.MillimeterOfMercury -> value.millimetersOfMercury
}

internal infix fun Pressure.toDoubleValueFor(pressure: KHUnit.Pressure): Double = when (pressure) {
    KHUnit.Pressure.MillimeterOfMercury -> this.inMillimetersOfMercury
}

internal infix fun KHUnit.Volume.toNativeVolumeFor(value: Double): Volume = when (this) {
    KHUnit.Volume.FluidOunceUS -> value.fluidOuncesUs
    KHUnit.Volume.Liter -> value.liters
}

internal infix fun Volume.toDoubleValueFor(volume: KHUnit.Volume): Double = when (volume) {
    KHUnit.Volume.FluidOunceUS -> this.inFluidOuncesUs
    KHUnit.Volume.Liter -> this.inLiters
}

internal fun KHMealType.toHCMealType(): Int = when (this) {
    KHMealType.Unknown -> MealType.MEAL_TYPE_UNKNOWN
    KHMealType.Breakfast -> MealType.MEAL_TYPE_BREAKFAST
    KHMealType.Lunch -> MealType.MEAL_TYPE_LUNCH
    KHMealType.Dinner -> MealType.MEAL_TYPE_DINNER
    KHMealType.Snack -> MealType.MEAL_TYPE_SNACK
}

internal fun Int.toKHMealType(): KHMealType = when (this) {
    MealType.MEAL_TYPE_UNKNOWN -> KHMealType.Unknown
    MealType.MEAL_TYPE_BREAKFAST -> KHMealType.Breakfast
    MealType.MEAL_TYPE_LUNCH -> KHMealType.Lunch
    MealType.MEAL_TYPE_DINNER -> KHMealType.Dinner
    MealType.MEAL_TYPE_SNACK -> KHMealType.Snack
    else -> throw IllegalStateException("Unknown meal type!")
}

internal fun KHExerciseType.toNativeExerciseTypeOrNull() = when (this) {
    KHExerciseType.AmericanFootball -> ExerciseSessionRecord.EXERCISE_TYPE_FOOTBALL_AMERICAN
    KHExerciseType.AustralianFootball -> ExerciseSessionRecord.EXERCISE_TYPE_FOOTBALL_AUSTRALIAN
    KHExerciseType.Badminton -> ExerciseSessionRecord.EXERCISE_TYPE_BADMINTON
    KHExerciseType.Baseball -> ExerciseSessionRecord.EXERCISE_TYPE_BASEBALL
    KHExerciseType.Basketball -> ExerciseSessionRecord.EXERCISE_TYPE_BASKETBALL
    KHExerciseType.Biking -> ExerciseSessionRecord.EXERCISE_TYPE_BIKING
    KHExerciseType.BikingStationary -> ExerciseSessionRecord.EXERCISE_TYPE_BIKING_STATIONARY
    KHExerciseType.BootCamp -> ExerciseSessionRecord.EXERCISE_TYPE_BOOT_CAMP
    KHExerciseType.Boxing -> ExerciseSessionRecord.EXERCISE_TYPE_BOXING
    KHExerciseType.Calisthenics -> ExerciseSessionRecord.EXERCISE_TYPE_CALISTHENICS
    KHExerciseType.Cricket -> ExerciseSessionRecord.EXERCISE_TYPE_CRICKET
    KHExerciseType.Dancing -> ExerciseSessionRecord.EXERCISE_TYPE_DANCING
    KHExerciseType.Elliptical -> ExerciseSessionRecord.EXERCISE_TYPE_ELLIPTICAL
    KHExerciseType.ExerciseClass -> ExerciseSessionRecord.EXERCISE_TYPE_EXERCISE_CLASS
    KHExerciseType.Fencing -> ExerciseSessionRecord.EXERCISE_TYPE_FENCING
    KHExerciseType.FrisbeeDisc -> ExerciseSessionRecord.EXERCISE_TYPE_FRISBEE_DISC
    KHExerciseType.Golf -> ExerciseSessionRecord.EXERCISE_TYPE_GOLF
    KHExerciseType.GuidedBreathing -> ExerciseSessionRecord.EXERCISE_TYPE_GUIDED_BREATHING
    KHExerciseType.Gymnastics -> ExerciseSessionRecord.EXERCISE_TYPE_GYMNASTICS
    KHExerciseType.Handball -> ExerciseSessionRecord.EXERCISE_TYPE_HANDBALL
    KHExerciseType.HighIntensityIntervalTraining -> ExerciseSessionRecord.EXERCISE_TYPE_HIGH_INTENSITY_INTERVAL_TRAINING
    KHExerciseType.Hiking -> ExerciseSessionRecord.EXERCISE_TYPE_HIKING
    KHExerciseType.IceHockey -> ExerciseSessionRecord.EXERCISE_TYPE_ICE_HOCKEY
    KHExerciseType.IceSkating -> ExerciseSessionRecord.EXERCISE_TYPE_ICE_SKATING
    KHExerciseType.MartialArts -> ExerciseSessionRecord.EXERCISE_TYPE_MARTIAL_ARTS
    KHExerciseType.Other -> ExerciseSessionRecord.EXERCISE_TYPE_OTHER_WORKOUT
    KHExerciseType.Paddling -> ExerciseSessionRecord.EXERCISE_TYPE_PADDLING
    KHExerciseType.Paragliding -> ExerciseSessionRecord.EXERCISE_TYPE_PARAGLIDING
    KHExerciseType.Pilates -> ExerciseSessionRecord.EXERCISE_TYPE_PILATES
    KHExerciseType.Racquetball -> ExerciseSessionRecord.EXERCISE_TYPE_RACQUETBALL
    KHExerciseType.RockClimbing -> ExerciseSessionRecord.EXERCISE_TYPE_ROCK_CLIMBING
    KHExerciseType.RollerHockey -> ExerciseSessionRecord.EXERCISE_TYPE_ROLLER_HOCKEY
    KHExerciseType.Rowing -> ExerciseSessionRecord.EXERCISE_TYPE_ROWING
    KHExerciseType.RowingMachine -> ExerciseSessionRecord.EXERCISE_TYPE_ROWING_MACHINE
    KHExerciseType.Rugby -> ExerciseSessionRecord.EXERCISE_TYPE_RUGBY
    KHExerciseType.Running -> ExerciseSessionRecord.EXERCISE_TYPE_RUNNING
    KHExerciseType.RunningTreadmill -> ExerciseSessionRecord.EXERCISE_TYPE_RUNNING_TREADMILL
    KHExerciseType.Sailing -> ExerciseSessionRecord.EXERCISE_TYPE_SAILING
    KHExerciseType.ScubaDiving -> ExerciseSessionRecord.EXERCISE_TYPE_SCUBA_DIVING
    KHExerciseType.Skating -> ExerciseSessionRecord.EXERCISE_TYPE_SKATING
    KHExerciseType.Skiing -> ExerciseSessionRecord.EXERCISE_TYPE_SKIING
    KHExerciseType.Snowboarding -> ExerciseSessionRecord.EXERCISE_TYPE_SNOWBOARDING
    KHExerciseType.Snowshoeing -> ExerciseSessionRecord.EXERCISE_TYPE_SNOWSHOEING
    KHExerciseType.Soccer -> ExerciseSessionRecord.EXERCISE_TYPE_SOCCER
    KHExerciseType.Softball -> ExerciseSessionRecord.EXERCISE_TYPE_SOFTBALL
    KHExerciseType.Squash -> ExerciseSessionRecord.EXERCISE_TYPE_SQUASH
    KHExerciseType.StairClimbing -> ExerciseSessionRecord.EXERCISE_TYPE_STAIR_CLIMBING
    KHExerciseType.StairClimbingMachine -> ExerciseSessionRecord.EXERCISE_TYPE_STAIR_CLIMBING_MACHINE
    KHExerciseType.StrengthTraining -> ExerciseSessionRecord.EXERCISE_TYPE_STRENGTH_TRAINING
    KHExerciseType.Stretching -> ExerciseSessionRecord.EXERCISE_TYPE_STRETCHING
    KHExerciseType.Surfing -> ExerciseSessionRecord.EXERCISE_TYPE_SURFING
    KHExerciseType.SwimmingOpenWater -> ExerciseSessionRecord.EXERCISE_TYPE_SWIMMING_OPEN_WATER
    KHExerciseType.SwimmingPool -> ExerciseSessionRecord.EXERCISE_TYPE_SWIMMING_POOL
    KHExerciseType.TableTennis -> ExerciseSessionRecord.EXERCISE_TYPE_TABLE_TENNIS
    KHExerciseType.Tennis -> ExerciseSessionRecord.EXERCISE_TYPE_TENNIS
    KHExerciseType.Volleyball -> ExerciseSessionRecord.EXERCISE_TYPE_VOLLEYBALL
    KHExerciseType.Walking -> ExerciseSessionRecord.EXERCISE_TYPE_WALKING
    KHExerciseType.WaterPolo -> ExerciseSessionRecord.EXERCISE_TYPE_WATER_POLO
    KHExerciseType.Weightlifting -> ExerciseSessionRecord.EXERCISE_TYPE_WEIGHTLIFTING
    KHExerciseType.Wheelchair -> ExerciseSessionRecord.EXERCISE_TYPE_WHEELCHAIR
    KHExerciseType.Yoga -> ExerciseSessionRecord.EXERCISE_TYPE_YOGA
    else -> null
}

internal fun Int.toKHExerciseTypeOrNull(): KHExerciseType? = when (this) {
    ExerciseSessionRecord.EXERCISE_TYPE_BADMINTON -> KHExerciseType.Badminton
    ExerciseSessionRecord.EXERCISE_TYPE_BASEBALL -> KHExerciseType.Baseball
    ExerciseSessionRecord.EXERCISE_TYPE_BASKETBALL -> KHExerciseType.Basketball
    ExerciseSessionRecord.EXERCISE_TYPE_BIKING -> KHExerciseType.Biking
    ExerciseSessionRecord.EXERCISE_TYPE_BIKING_STATIONARY -> KHExerciseType.BikingStationary
    ExerciseSessionRecord.EXERCISE_TYPE_BOOT_CAMP -> KHExerciseType.BootCamp
    ExerciseSessionRecord.EXERCISE_TYPE_BOXING -> KHExerciseType.Boxing
    ExerciseSessionRecord.EXERCISE_TYPE_CALISTHENICS -> KHExerciseType.Calisthenics
    ExerciseSessionRecord.EXERCISE_TYPE_CRICKET -> KHExerciseType.Cricket
    ExerciseSessionRecord.EXERCISE_TYPE_DANCING -> KHExerciseType.Dancing
    ExerciseSessionRecord.EXERCISE_TYPE_ELLIPTICAL -> KHExerciseType.Elliptical
    ExerciseSessionRecord.EXERCISE_TYPE_EXERCISE_CLASS -> KHExerciseType.ExerciseClass
    ExerciseSessionRecord.EXERCISE_TYPE_FENCING -> KHExerciseType.Fencing
    ExerciseSessionRecord.EXERCISE_TYPE_FOOTBALL_AMERICAN -> KHExerciseType.AmericanFootball
    ExerciseSessionRecord.EXERCISE_TYPE_FOOTBALL_AUSTRALIAN -> KHExerciseType.AustralianFootball
    ExerciseSessionRecord.EXERCISE_TYPE_FRISBEE_DISC -> KHExerciseType.FrisbeeDisc
    ExerciseSessionRecord.EXERCISE_TYPE_GOLF -> KHExerciseType.Golf
    ExerciseSessionRecord.EXERCISE_TYPE_GUIDED_BREATHING -> KHExerciseType.GuidedBreathing
    ExerciseSessionRecord.EXERCISE_TYPE_GYMNASTICS -> KHExerciseType.Gymnastics
    ExerciseSessionRecord.EXERCISE_TYPE_HANDBALL -> KHExerciseType.Handball
    ExerciseSessionRecord.EXERCISE_TYPE_HIGH_INTENSITY_INTERVAL_TRAINING -> KHExerciseType.HighIntensityIntervalTraining
    ExerciseSessionRecord.EXERCISE_TYPE_HIKING -> KHExerciseType.Hiking
    ExerciseSessionRecord.EXERCISE_TYPE_ICE_HOCKEY -> KHExerciseType.IceHockey
    ExerciseSessionRecord.EXERCISE_TYPE_ICE_SKATING -> KHExerciseType.IceSkating
    ExerciseSessionRecord.EXERCISE_TYPE_MARTIAL_ARTS -> KHExerciseType.MartialArts
    ExerciseSessionRecord.EXERCISE_TYPE_OTHER_WORKOUT -> KHExerciseType.Other
    ExerciseSessionRecord.EXERCISE_TYPE_PADDLING -> KHExerciseType.Paddling
    ExerciseSessionRecord.EXERCISE_TYPE_PARAGLIDING -> KHExerciseType.Paragliding
    ExerciseSessionRecord.EXERCISE_TYPE_PILATES -> KHExerciseType.Pilates
    ExerciseSessionRecord.EXERCISE_TYPE_RACQUETBALL -> KHExerciseType.Racquetball
    ExerciseSessionRecord.EXERCISE_TYPE_ROCK_CLIMBING -> KHExerciseType.RockClimbing
    ExerciseSessionRecord.EXERCISE_TYPE_ROLLER_HOCKEY -> KHExerciseType.RollerHockey
    ExerciseSessionRecord.EXERCISE_TYPE_ROWING -> KHExerciseType.Rowing
    ExerciseSessionRecord.EXERCISE_TYPE_ROWING_MACHINE -> KHExerciseType.RowingMachine
    ExerciseSessionRecord.EXERCISE_TYPE_RUGBY -> KHExerciseType.Rugby
    ExerciseSessionRecord.EXERCISE_TYPE_RUNNING -> KHExerciseType.Running
    ExerciseSessionRecord.EXERCISE_TYPE_RUNNING_TREADMILL -> KHExerciseType.RunningTreadmill
    ExerciseSessionRecord.EXERCISE_TYPE_SAILING -> KHExerciseType.Sailing
    ExerciseSessionRecord.EXERCISE_TYPE_SCUBA_DIVING -> KHExerciseType.ScubaDiving
    ExerciseSessionRecord.EXERCISE_TYPE_SKATING -> KHExerciseType.Skating
    ExerciseSessionRecord.EXERCISE_TYPE_SKIING -> KHExerciseType.Skiing
    ExerciseSessionRecord.EXERCISE_TYPE_SNOWBOARDING -> KHExerciseType.Snowboarding
    ExerciseSessionRecord.EXERCISE_TYPE_SNOWSHOEING -> KHExerciseType.Snowshoeing
    ExerciseSessionRecord.EXERCISE_TYPE_SOCCER -> KHExerciseType.Soccer
    ExerciseSessionRecord.EXERCISE_TYPE_SOFTBALL -> KHExerciseType.Softball
    ExerciseSessionRecord.EXERCISE_TYPE_SQUASH -> KHExerciseType.Squash
    ExerciseSessionRecord.EXERCISE_TYPE_STAIR_CLIMBING -> KHExerciseType.StairClimbing
    ExerciseSessionRecord.EXERCISE_TYPE_STAIR_CLIMBING_MACHINE -> KHExerciseType.StairClimbingMachine
    ExerciseSessionRecord.EXERCISE_TYPE_STRENGTH_TRAINING -> KHExerciseType.StrengthTraining
    ExerciseSessionRecord.EXERCISE_TYPE_STRETCHING -> KHExerciseType.Stretching
    ExerciseSessionRecord.EXERCISE_TYPE_SURFING -> KHExerciseType.Surfing
    ExerciseSessionRecord.EXERCISE_TYPE_SWIMMING_OPEN_WATER -> KHExerciseType.SwimmingOpenWater
    ExerciseSessionRecord.EXERCISE_TYPE_SWIMMING_POOL -> KHExerciseType.SwimmingPool
    ExerciseSessionRecord.EXERCISE_TYPE_TABLE_TENNIS -> KHExerciseType.TableTennis
    ExerciseSessionRecord.EXERCISE_TYPE_TENNIS -> KHExerciseType.Tennis
    ExerciseSessionRecord.EXERCISE_TYPE_VOLLEYBALL -> KHExerciseType.Volleyball
    ExerciseSessionRecord.EXERCISE_TYPE_WALKING -> KHExerciseType.Walking
    ExerciseSessionRecord.EXERCISE_TYPE_WATER_POLO -> KHExerciseType.WaterPolo
    ExerciseSessionRecord.EXERCISE_TYPE_WEIGHTLIFTING -> KHExerciseType.Weightlifting
    ExerciseSessionRecord.EXERCISE_TYPE_WHEELCHAIR -> KHExerciseType.Wheelchair
    ExerciseSessionRecord.EXERCISE_TYPE_YOGA -> KHExerciseType.Yoga
    else -> null
}
