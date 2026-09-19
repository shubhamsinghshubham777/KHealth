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
import kotlin.reflect.KClass

internal object AndroidPermissionMapper {

    fun toRecordClass(permission: KHPermission): KClass<out Record>? = when (permission) {
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

    fun toPermissions(permission: KHPermission): Set<Pair<String, KHPermissionType>> {
        return buildSet {
            toRecordClass(permission)?.let { safeRecord ->
                val isReadGranted = when (permission) {
                    is KHPermission.ActiveCaloriesBurned -> permission.read
                    is KHPermission.BasalMetabolicRate -> permission.read
                    is KHPermission.BloodGlucose -> permission.read
                    is KHPermission.BloodPressure -> permission.readSystolic || permission.readDiastolic
                    is KHPermission.BodyFat -> permission.read
                    is KHPermission.BodyTemperature -> permission.read
                    is KHPermission.BodyWaterMass -> permission.read
                    is KHPermission.BoneMass -> permission.read
                    is KHPermission.CervicalMucus -> permission.read
                    is KHPermission.CyclingPedalingCadence -> permission.read
                    is KHPermission.CyclingSpeed -> permission.read
                    is KHPermission.Distance -> permission.read
                    is KHPermission.ElevationGained -> permission.read
                    is KHPermission.Exercise -> permission.read
                    is KHPermission.FloorsClimbed -> permission.read
                    is KHPermission.HeartRate -> permission.read
                    is KHPermission.HeartRateVariability -> permission.read
                    is KHPermission.Height -> permission.read
                    is KHPermission.Hydration -> permission.read
                    is KHPermission.IntermenstrualBleeding -> permission.read
                    is KHPermission.LeanBodyMass -> permission.read
                    is KHPermission.MenstruationFlow -> permission.read
                    is KHPermission.MenstruationPeriod -> permission.read
                    is KHPermission.Nutrition -> permission.readBiotin ||
                            permission.readCaffeine ||
                            permission.readCalcium ||
                            permission.readChloride ||
                            permission.readCholesterol ||
                            permission.readChromium ||
                            permission.readCopper ||
                            permission.readDietaryFiber ||
                            permission.readEnergy ||
                            permission.readFolicAcid ||
                            permission.readIodine ||
                            permission.readIron ||
                            permission.readMagnesium ||
                            permission.readManganese ||
                            permission.readMolybdenum ||
                            permission.readMonounsaturatedFat ||
                            permission.readNiacin ||
                            permission.readPantothenicAcid ||
                            permission.readPhosphorus ||
                            permission.readPolyunsaturatedFat ||
                            permission.readPotassium ||
                            permission.readProtein ||
                            permission.readRiboflavin ||
                            permission.readSaturatedFat ||
                            permission.readSelenium ||
                            permission.readSodium ||
                            permission.readSugar ||
                            permission.readThiamin ||
                            permission.readTotalCarbohydrate ||
                            permission.readTotalFat ||
                            permission.readVitaminA ||
                            permission.readVitaminB12 ||
                            permission.readVitaminB6 ||
                            permission.readVitaminC ||
                            permission.readVitaminD ||
                            permission.readVitaminE ||
                            permission.readVitaminK ||
                            permission.readZinc

                    is KHPermission.OvulationTest -> permission.read
                    is KHPermission.OxygenSaturation -> permission.read
                    is KHPermission.Power -> permission.read
                    is KHPermission.RespiratoryRate -> permission.read
                    is KHPermission.RestingHeartRate -> permission.read
                    is KHPermission.RunningSpeed -> permission.read
                    is KHPermission.SexualActivity -> permission.read
                    is KHPermission.SleepSession -> permission.read
                    is KHPermission.Speed -> permission.read
                    is KHPermission.StepCount -> permission.read
                    is KHPermission.Vo2Max -> permission.read
                    is KHPermission.Weight -> permission.read
                    is KHPermission.WheelChairPushes -> permission.read
                }

                val isWriteGranted = when (permission) {
                    is KHPermission.ActiveCaloriesBurned -> permission.write
                    is KHPermission.BasalMetabolicRate -> permission.write
                    is KHPermission.BloodGlucose -> permission.write
                    is KHPermission.BloodPressure -> permission.writeSystolic || permission.writeDiastolic
                    is KHPermission.BodyFat -> permission.write
                    is KHPermission.BodyTemperature -> permission.write
                    is KHPermission.BodyWaterMass -> permission.write
                    is KHPermission.BoneMass -> permission.write
                    is KHPermission.CervicalMucus -> permission.write
                    is KHPermission.CyclingPedalingCadence -> permission.write
                    is KHPermission.CyclingSpeed -> permission.write
                    is KHPermission.Distance -> permission.write
                    is KHPermission.ElevationGained -> permission.write
                    is KHPermission.Exercise -> permission.write
                    is KHPermission.FloorsClimbed -> permission.write
                    is KHPermission.HeartRate -> permission.write
                    is KHPermission.HeartRateVariability -> permission.write
                    is KHPermission.Height -> permission.write
                    is KHPermission.Hydration -> permission.write
                    is KHPermission.IntermenstrualBleeding -> permission.write
                    is KHPermission.LeanBodyMass -> permission.write
                    is KHPermission.MenstruationFlow -> permission.write
                    is KHPermission.MenstruationPeriod -> permission.write
                    is KHPermission.Nutrition -> permission.writeBiotin ||
                            permission.writeCaffeine ||
                            permission.writeCalcium ||
                            permission.writeChloride ||
                            permission.writeCholesterol ||
                            permission.writeChromium ||
                            permission.writeCopper ||
                            permission.writeDietaryFiber ||
                            permission.writeEnergy ||
                            permission.writeFolicAcid ||
                            permission.writeIodine ||
                            permission.writeIron ||
                            permission.writeMagnesium ||
                            permission.writeManganese ||
                            permission.writeMolybdenum ||
                            permission.writeMonounsaturatedFat ||
                            permission.writeNiacin ||
                            permission.writePantothenicAcid ||
                            permission.writePhosphorus ||
                            permission.writePolyunsaturatedFat ||
                            permission.writePotassium ||
                            permission.writeProtein ||
                            permission.writeRiboflavin ||
                            permission.writeSaturatedFat ||
                            permission.writeSelenium ||
                            permission.writeSodium ||
                            permission.writeSugar ||
                            permission.writeThiamin ||
                            permission.writeTotalCarbohydrate ||
                            permission.writeTotalFat ||
                            permission.writeVitaminA ||
                            permission.writeVitaminB12 ||
                            permission.writeVitaminB6 ||
                            permission.writeVitaminC ||
                            permission.writeVitaminD ||
                            permission.writeVitaminE ||
                            permission.writeVitaminK ||
                            permission.writeZinc

                    is KHPermission.OvulationTest -> permission.write
                    is KHPermission.OxygenSaturation -> permission.write
                    is KHPermission.Power -> permission.write
                    is KHPermission.RespiratoryRate -> permission.write
                    is KHPermission.RestingHeartRate -> permission.write
                    is KHPermission.RunningSpeed -> permission.write
                    is KHPermission.SexualActivity -> permission.write
                    is KHPermission.SleepSession -> permission.write
                    is KHPermission.Speed -> permission.write
                    is KHPermission.StepCount -> permission.write
                    is KHPermission.Vo2Max -> permission.write
                    is KHPermission.Weight -> permission.write
                    is KHPermission.WheelChairPushes -> permission.write
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

    fun toPermissionsWithStatuses(
        permissions: Array<out KHPermission>,
        grantedPermissions: Set<String>
    ): List<KHPermission> = permissions.map { entry ->
        val permissionSet = toPermissions(entry)

        val readGranted = permissionSet.firstOrNull { it.second == KHPermissionType.Read }
            ?.first
            ?.let(grantedPermissions::contains)
            ?: false

        val writeGranted = permissionSet.firstOrNull { it.second == KHPermissionType.Write }
            ?.first
            ?.let(grantedPermissions::contains)
            ?: false

        when (entry) {
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
                writeZinc = writeGranted
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
}
