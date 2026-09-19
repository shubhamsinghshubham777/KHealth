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
import platform.HealthKit.HKAuthorizationStatusSharingAuthorized
import platform.HealthKit.HKHealthStore
import platform.HealthKit.HKObjectType
import platform.darwin.NSInteger

internal object ApplePermissionMapper {
    @OptIn(UnsafeNumber::class)
    private val NSInteger.isGranted get() = this == HKAuthorizationStatusSharingAuthorized

    @OptIn(UnsafeNumber::class)
    fun checkPermission(store: HKHealthStore, permission: KHPermission): KHPermission = when (permission) {
        is KHPermission.ActiveCaloriesBurned -> KHPermission.ActiveCaloriesBurned(
            write = if (permission.write) store.authorizationStatusForType(ObjectType.Quantity.ActiveCaloriesBurned).isGranted else false
        )

        is KHPermission.BasalMetabolicRate -> KHPermission.BasalMetabolicRate(
            write = if (permission.write) store.authorizationStatusForType(ObjectType.Quantity.BasalMetabolicRate).isGranted else false
        )

        is KHPermission.BloodGlucose -> KHPermission.BloodGlucose(
            write = if (permission.write) store.authorizationStatusForType(ObjectType.Quantity.BloodGlucose).isGranted else false
        )

        is KHPermission.BloodPressure -> KHPermission.BloodPressure(
            writeSystolic = if (permission.writeSystolic) store.authorizationStatusForType(ObjectType.Quantity.BloodPressureSystolic).isGranted else false,
            writeDiastolic = if (permission.writeDiastolic) store.authorizationStatusForType(ObjectType.Quantity.BloodPressureDiastolic).isGranted else false,
        )

        is KHPermission.BodyFat -> KHPermission.BodyFat(
            write = if (permission.write) store.authorizationStatusForType(ObjectType.Quantity.BodyFat).isGranted else false
        )

        is KHPermission.BodyTemperature -> KHPermission.BodyTemperature(
            write = if (permission.write) store.authorizationStatusForType(ObjectType.Quantity.BodyTemperature).isGranted else false
        )

        is KHPermission.BodyWaterMass -> KHPermission.BodyWaterMass(write = false)
        is KHPermission.BoneMass -> KHPermission.BoneMass(write = false)

        is KHPermission.CervicalMucus -> KHPermission.CervicalMucus(
            write = if (permission.write) store.authorizationStatusForType(ObjectType.Category.CervicalMucus).isGranted else false
        )

        is KHPermission.CyclingPedalingCadence -> KHPermission.CyclingPedalingCadence(write = false)

        is KHPermission.CyclingSpeed -> KHPermission.CyclingSpeed(
            write = if (permission.write) store.authorizationStatusForType(ObjectType.Quantity.CyclingSpeed).isGranted else false
        )

        is KHPermission.Distance -> KHPermission.Distance(
            write = if (permission.write) store.authorizationStatusForType(ObjectType.Quantity.Distance).isGranted else false
        )

        is KHPermission.ElevationGained -> KHPermission.ElevationGained(write = false)

        is KHPermission.Exercise -> KHPermission.Exercise(
            write = if (permission.write) store.authorizationStatusForType(ObjectType.Exercise).isGranted else false
        )

        is KHPermission.FloorsClimbed -> KHPermission.FloorsClimbed(
            write = if (permission.write) store.authorizationStatusForType(ObjectType.Quantity.FloorsClimbed).isGranted else false
        )

        is KHPermission.HeartRate -> KHPermission.HeartRate(
            write = if (permission.write) store.authorizationStatusForType(ObjectType.Quantity.HeartRate).isGranted else false
        )

        is KHPermission.HeartRateVariability -> KHPermission.HeartRateVariability(
            write = if (permission.write) store.authorizationStatusForType(ObjectType.Quantity.HeartRateVariability).isGranted else false
        )

        is KHPermission.Height -> KHPermission.Height(
            write = if (permission.write) store.authorizationStatusForType(ObjectType.Quantity.Height).isGranted else false
        )

        is KHPermission.Hydration -> KHPermission.Hydration(
            write = if (permission.write) store.authorizationStatusForType(ObjectType.Quantity.Hydration).isGranted else false
        )

        is KHPermission.IntermenstrualBleeding -> KHPermission.IntermenstrualBleeding(
            write = if (permission.write) store.authorizationStatusForType(ObjectType.Category.IntermenstrualBleeding).isGranted else false
        )

        is KHPermission.LeanBodyMass -> KHPermission.LeanBodyMass(
            write = if (permission.write) store.authorizationStatusForType(ObjectType.Quantity.LeanBodyMass).isGranted else false
        )

        is KHPermission.MenstruationFlow -> KHPermission.MenstruationFlow(
            write = if (permission.write) store.authorizationStatusForType(ObjectType.Category.MenstruationFlow).isGranted else false
        )

        is KHPermission.MenstruationPeriod -> KHPermission.MenstruationPeriod(write = false)

        is KHPermission.Nutrition -> KHPermission.Nutrition(
            writeBiotin = if (permission.writeBiotin) store.authorizationStatusForType(ObjectType.Food.Biotin).isGranted else false,
            writeCaffeine = if (permission.writeCaffeine) store.authorizationStatusForType(ObjectType.Food.Caffeine).isGranted else false,
            writeCalcium = if (permission.writeCalcium) store.authorizationStatusForType(ObjectType.Food.Calcium).isGranted else false,
            writeChloride = if (permission.writeChloride) store.authorizationStatusForType(ObjectType.Food.Chloride).isGranted else false,
            writeCholesterol = if (permission.writeCholesterol) store.authorizationStatusForType(ObjectType.Food.Cholesterol).isGranted else false,
            writeChromium = if (permission.writeChromium) store.authorizationStatusForType(ObjectType.Food.Chromium).isGranted else false,
            writeCopper = if (permission.writeCopper) store.authorizationStatusForType(ObjectType.Food.Copper).isGranted else false,
            writeDietaryFiber = if (permission.writeDietaryFiber) store.authorizationStatusForType(ObjectType.Food.Fiber).isGranted else false,
            writeEnergy = if (permission.writeEnergy) store.authorizationStatusForType(ObjectType.Food.EnergyConsumed).isGranted else false,
            writeFolicAcid = if (permission.writeFolicAcid) store.authorizationStatusForType(ObjectType.Food.Folate).isGranted else false,
            writeIodine = if (permission.writeIodine) store.authorizationStatusForType(ObjectType.Food.Iodine).isGranted else false,
            writeIron = if (permission.writeIron) store.authorizationStatusForType(ObjectType.Food.Iron).isGranted else false,
            writeMagnesium = if (permission.writeMagnesium) store.authorizationStatusForType(ObjectType.Food.Magnesium).isGranted else false,
            writeManganese = if (permission.writeManganese) store.authorizationStatusForType(ObjectType.Food.Manganese).isGranted else false,
            writeMolybdenum = if (permission.writeMolybdenum) store.authorizationStatusForType(ObjectType.Food.Molybdenum).isGranted else false,
            writeMonounsaturatedFat = if (permission.writeMonounsaturatedFat) store.authorizationStatusForType(ObjectType.Food.FatMonounsaturated).isGranted else false,
            writeNiacin = if (permission.writeNiacin) store.authorizationStatusForType(ObjectType.Food.Niacin).isGranted else false,
            writePantothenicAcid = if (permission.writePantothenicAcid) store.authorizationStatusForType(ObjectType.Food.PantothenicAcid).isGranted else false,
            writePhosphorus = if (permission.writePhosphorus) store.authorizationStatusForType(ObjectType.Food.Phosphorus).isGranted else false,
            writePolyunsaturatedFat = if (permission.writePolyunsaturatedFat) store.authorizationStatusForType(ObjectType.Food.FatPolyunsaturated).isGranted else false,
            writePotassium = if (permission.writePotassium) store.authorizationStatusForType(ObjectType.Food.Potassium).isGranted else false,
            writeProtein = if (permission.writeProtein) store.authorizationStatusForType(ObjectType.Food.Protein).isGranted else false,
            writeRiboflavin = if (permission.writeRiboflavin) store.authorizationStatusForType(ObjectType.Food.Riboflavin).isGranted else false,
            writeSaturatedFat = if (permission.writeSaturatedFat) store.authorizationStatusForType(ObjectType.Food.FatSaturated).isGranted else false,
            writeSelenium = if (permission.writeSelenium) store.authorizationStatusForType(ObjectType.Food.Selenium).isGranted else false,
            writeSodium = if (permission.writeSodium) store.authorizationStatusForType(ObjectType.Food.Sodium).isGranted else false,
            writeSugar = if (permission.writeSugar) store.authorizationStatusForType(ObjectType.Food.Sugar).isGranted else false,
            writeThiamin = if (permission.writeThiamin) store.authorizationStatusForType(ObjectType.Food.Thiamin).isGranted else false,
            writeTotalCarbohydrate = if (permission.writeTotalCarbohydrate) store.authorizationStatusForType(ObjectType.Food.Carbohydrates).isGranted else false,
            writeTotalFat = if (permission.writeTotalFat) store.authorizationStatusForType(ObjectType.Food.FatTotal).isGranted else false,
            writeVitaminA = if (permission.writeVitaminA) store.authorizationStatusForType(ObjectType.Food.VitaminA).isGranted else false,
            writeVitaminB12 = if (permission.writeVitaminB12) store.authorizationStatusForType(ObjectType.Food.VitaminB12).isGranted else false,
            writeVitaminB6 = if (permission.writeVitaminB6) store.authorizationStatusForType(ObjectType.Food.VitaminB6).isGranted else false,
            writeVitaminC = if (permission.writeVitaminC) store.authorizationStatusForType(ObjectType.Food.VitaminC).isGranted else false,
            writeVitaminD = if (permission.writeVitaminD) store.authorizationStatusForType(ObjectType.Food.VitaminD).isGranted else false,
            writeVitaminE = if (permission.writeVitaminE) store.authorizationStatusForType(ObjectType.Food.VitaminE).isGranted else false,
            writeVitaminK = if (permission.writeVitaminK) store.authorizationStatusForType(ObjectType.Food.VitaminK).isGranted else false,
            writeZinc = if (permission.writeZinc) store.authorizationStatusForType(ObjectType.Food.Zinc).isGranted else false,
        )

        is KHPermission.OvulationTest -> KHPermission.OvulationTest(
            write = if (permission.write) store.authorizationStatusForType(ObjectType.Category.OvulationTest).isGranted else false
        )

        is KHPermission.OxygenSaturation -> KHPermission.OxygenSaturation(
            write = if (permission.write) store.authorizationStatusForType(ObjectType.Quantity.OxygenSaturation).isGranted else false
        )

        is KHPermission.Power -> KHPermission.Power(
            write = if (permission.write) store.authorizationStatusForType(ObjectType.Quantity.Power).isGranted else false
        )

        is KHPermission.RespiratoryRate -> KHPermission.RespiratoryRate(
            write = if (permission.write) store.authorizationStatusForType(ObjectType.Quantity.RespiratoryRate).isGranted else false
        )

        is KHPermission.RestingHeartRate -> KHPermission.RestingHeartRate(
            write = if (permission.write) store.authorizationStatusForType(ObjectType.Quantity.RestingHeartRate).isGranted else false
        )

        is KHPermission.RunningSpeed -> KHPermission.RunningSpeed(
            write = if (permission.write) store.authorizationStatusForType(ObjectType.Quantity.RunningSpeed).isGranted else false
        )

        is KHPermission.SexualActivity -> KHPermission.SexualActivity(
            write = if (permission.write) store.authorizationStatusForType(ObjectType.Category.SexualActivity).isGranted else false
        )

        is KHPermission.SleepSession -> KHPermission.SleepSession(
            write = if (permission.write) store.authorizationStatusForType(ObjectType.Category.SleepSession).isGranted else false
        )

        is KHPermission.Speed -> KHPermission.Speed(write = false)

        is KHPermission.StepCount -> KHPermission.StepCount(
            write = if (permission.write) store.authorizationStatusForType(ObjectType.Quantity.StepCount).isGranted else false
        )

        is KHPermission.Vo2Max -> KHPermission.Vo2Max(
            write = if (permission.write) store.authorizationStatusForType(ObjectType.Quantity.Vo2Max).isGranted else false
        )

        is KHPermission.Weight -> KHPermission.Weight(
            write = if (permission.write) store.authorizationStatusForType(ObjectType.Quantity.Weight).isGranted else false
        )

        is KHPermission.WheelChairPushes -> KHPermission.WheelChairPushes(
            write = if (permission.write) store.authorizationStatusForType(ObjectType.Quantity.WheelChairPushes).isGranted else false
        )
    }

    fun mapPermissions(permissions: Array<out KHPermission>): Pair<Set<HKObjectType>, Set<HKObjectType>> {
        val read = mutableSetOf<HKObjectType>()
        val write = mutableSetOf<HKObjectType>()
        for (permission in permissions) {
            val (r, w) = toAuthorizationTypes(permission)
            read.addAll(r)
            write.addAll(w)
        }
        return Pair(read, write)
    }

    fun toAuthorizationTypes(permission: KHPermission): Pair<Set<HKObjectType>, Set<HKObjectType>> {
        val read = mutableSetOf<HKObjectType>()
        val write = mutableSetOf<HKObjectType>()

        when (permission) {
            is KHPermission.ActiveCaloriesBurned -> {
                if (permission.read) read.add(ObjectType.Quantity.ActiveCaloriesBurned)
                if (permission.write) write.add(ObjectType.Quantity.ActiveCaloriesBurned)
            }

            is KHPermission.BasalMetabolicRate -> {
                if (permission.read) read.add(ObjectType.Quantity.BasalMetabolicRate)
                if (permission.write) write.add(ObjectType.Quantity.BasalMetabolicRate)
            }

            is KHPermission.BloodGlucose -> {
                if (permission.read) read.add(ObjectType.Quantity.BloodGlucose)
                if (permission.write) write.add(ObjectType.Quantity.BloodGlucose)
            }

            is KHPermission.BloodPressure -> {
                if (permission.readSystolic) read.add(ObjectType.Quantity.BloodPressureSystolic)
                if (permission.writeSystolic) write.add(ObjectType.Quantity.BloodPressureSystolic)
                if (permission.readDiastolic) read.add(ObjectType.Quantity.BloodPressureDiastolic)
                if (permission.writeDiastolic) write.add(ObjectType.Quantity.BloodPressureDiastolic)
            }

            is KHPermission.BodyFat -> {
                if (permission.read) read.add(ObjectType.Quantity.BodyFat)
                if (permission.write) write.add(ObjectType.Quantity.BodyFat)
            }

            is KHPermission.BodyTemperature -> {
                if (permission.read) read.add(ObjectType.Quantity.BodyTemperature)
                if (permission.write) write.add(ObjectType.Quantity.BodyTemperature)
            }

            is KHPermission.BodyWaterMass -> Unit
            is KHPermission.BoneMass -> Unit

            is KHPermission.CervicalMucus -> {
                if (permission.read) read.add(ObjectType.Category.CervicalMucus)
                if (permission.write) write.add(ObjectType.Category.CervicalMucus)
            }

            is KHPermission.CyclingPedalingCadence -> Unit

            is KHPermission.CyclingSpeed -> {
                if (permission.read) read.add(ObjectType.Quantity.CyclingSpeed)
                if (permission.write) write.add(ObjectType.Quantity.CyclingSpeed)
            }

            is KHPermission.Distance -> {
                if (permission.read) read.add(ObjectType.Quantity.Distance)
                if (permission.write) write.add(ObjectType.Quantity.Distance)
            }

            is KHPermission.ElevationGained -> Unit

            is KHPermission.Exercise -> {
                if (permission.read) read.add(ObjectType.Exercise)
                if (permission.write) write.add(ObjectType.Exercise)
            }

            is KHPermission.FloorsClimbed -> {
                if (permission.read) read.add(ObjectType.Quantity.FloorsClimbed)
                if (permission.write) write.add(ObjectType.Quantity.FloorsClimbed)
            }

            is KHPermission.HeartRate -> {
                if (permission.read) read.add(ObjectType.Quantity.HeartRate)
                if (permission.write) write.add(ObjectType.Quantity.HeartRate)
            }

            is KHPermission.HeartRateVariability -> {
                if (permission.read) read.add(ObjectType.Quantity.HeartRateVariability)
                if (permission.write) write.add(ObjectType.Quantity.HeartRateVariability)
            }

            is KHPermission.Height -> {
                if (permission.read) read.add(ObjectType.Quantity.Height)
                if (permission.write) write.add(ObjectType.Quantity.Height)
            }

            is KHPermission.Hydration -> {
                if (permission.read) read.add(ObjectType.Quantity.Hydration)
                if (permission.write) write.add(ObjectType.Quantity.Hydration)
            }

            is KHPermission.IntermenstrualBleeding -> {
                if (permission.read) read.add(ObjectType.Category.IntermenstrualBleeding)
                if (permission.write) write.add(ObjectType.Category.IntermenstrualBleeding)
            }

            is KHPermission.LeanBodyMass -> {
                if (permission.read) read.add(ObjectType.Quantity.LeanBodyMass)
                if (permission.write) write.add(ObjectType.Quantity.LeanBodyMass)
            }

            is KHPermission.MenstruationFlow -> {
                if (permission.read) read.add(ObjectType.Category.MenstruationFlow)
                if (permission.write) write.add(ObjectType.Category.MenstruationFlow)
            }

            is KHPermission.MenstruationPeriod -> Unit

            is KHPermission.Nutrition -> {
                if (permission.readBiotin) read.add(ObjectType.Food.Biotin)
                if (permission.writeBiotin) write.add(ObjectType.Food.Biotin)
                if (permission.readCaffeine) read.add(ObjectType.Food.Caffeine)
                if (permission.writeCaffeine) write.add(ObjectType.Food.Caffeine)
                if (permission.readCalcium) read.add(ObjectType.Food.Calcium)
                if (permission.writeCalcium) write.add(ObjectType.Food.Calcium)
                if (permission.readChloride) read.add(ObjectType.Food.Chloride)
                if (permission.writeChloride) write.add(ObjectType.Food.Chloride)
                if (permission.readCholesterol) read.add(ObjectType.Food.Cholesterol)
                if (permission.writeCholesterol) write.add(ObjectType.Food.Cholesterol)
                if (permission.readChromium) read.add(ObjectType.Food.Chromium)
                if (permission.writeChromium) write.add(ObjectType.Food.Chromium)
                if (permission.readCopper) read.add(ObjectType.Food.Copper)
                if (permission.writeCopper) write.add(ObjectType.Food.Copper)
                if (permission.readDietaryFiber) read.add(ObjectType.Food.Fiber)
                if (permission.writeDietaryFiber) write.add(ObjectType.Food.Fiber)
                if (permission.readEnergy) read.add(ObjectType.Food.EnergyConsumed)
                if (permission.writeEnergy) write.add(ObjectType.Food.EnergyConsumed)
                if (permission.readFolicAcid) read.add(ObjectType.Food.Folate)
                if (permission.writeFolicAcid) write.add(ObjectType.Food.Folate)
                if (permission.readIodine) read.add(ObjectType.Food.Iodine)
                if (permission.writeIodine) write.add(ObjectType.Food.Iodine)
                if (permission.readIron) read.add(ObjectType.Food.Iron)
                if (permission.writeIron) write.add(ObjectType.Food.Iron)
                if (permission.readMagnesium) read.add(ObjectType.Food.Magnesium)
                if (permission.writeMagnesium) write.add(ObjectType.Food.Magnesium)
                if (permission.readManganese) read.add(ObjectType.Food.Manganese)
                if (permission.writeManganese) write.add(ObjectType.Food.Manganese)
                if (permission.readMolybdenum) read.add(ObjectType.Food.Molybdenum)
                if (permission.writeMolybdenum) write.add(ObjectType.Food.Molybdenum)
                if (permission.readMonounsaturatedFat) read.add(ObjectType.Food.FatMonounsaturated)
                if (permission.writeMonounsaturatedFat) write.add(ObjectType.Food.FatMonounsaturated)
                if (permission.readNiacin) read.add(ObjectType.Food.Niacin)
                if (permission.writeNiacin) write.add(ObjectType.Food.Niacin)
                if (permission.readPantothenicAcid) read.add(ObjectType.Food.PantothenicAcid)
                if (permission.writePantothenicAcid) write.add(ObjectType.Food.PantothenicAcid)
                if (permission.readPhosphorus) read.add(ObjectType.Food.Phosphorus)
                if (permission.writePhosphorus) write.add(ObjectType.Food.Phosphorus)
                if (permission.readPolyunsaturatedFat) read.add(ObjectType.Food.FatPolyunsaturated)
                if (permission.writePolyunsaturatedFat) write.add(ObjectType.Food.FatPolyunsaturated)
                if (permission.readPotassium) read.add(ObjectType.Food.Potassium)
                if (permission.writePotassium) write.add(ObjectType.Food.Potassium)
                if (permission.readProtein) read.add(ObjectType.Food.Protein)
                if (permission.writeProtein) write.add(ObjectType.Food.Protein)
                if (permission.readRiboflavin) read.add(ObjectType.Food.Riboflavin)
                if (permission.writeRiboflavin) write.add(ObjectType.Food.Riboflavin)
                if (permission.readSaturatedFat) read.add(ObjectType.Food.FatSaturated)
                if (permission.writeSaturatedFat) write.add(ObjectType.Food.FatSaturated)
                if (permission.readSelenium) read.add(ObjectType.Food.Selenium)
                if (permission.writeSelenium) write.add(ObjectType.Food.Selenium)
                if (permission.readSodium) read.add(ObjectType.Food.Sodium)
                if (permission.writeSodium) write.add(ObjectType.Food.Sodium)
                if (permission.readSugar) read.add(ObjectType.Food.Sugar)
                if (permission.writeSugar) write.add(ObjectType.Food.Sugar)
                if (permission.readThiamin) read.add(ObjectType.Food.Thiamin)
                if (permission.writeThiamin) write.add(ObjectType.Food.Thiamin)
                if (permission.readTotalCarbohydrate) read.add(ObjectType.Food.Carbohydrates)
                if (permission.writeTotalCarbohydrate) write.add(ObjectType.Food.Carbohydrates)
                if (permission.readTotalFat) read.add(ObjectType.Food.FatTotal)
                if (permission.writeTotalFat) write.add(ObjectType.Food.FatTotal)
                if (permission.readVitaminA) read.add(ObjectType.Food.VitaminA)
                if (permission.writeVitaminA) write.add(ObjectType.Food.VitaminA)
                if (permission.readVitaminB12) read.add(ObjectType.Food.VitaminB12)
                if (permission.writeVitaminB12) write.add(ObjectType.Food.VitaminB12)
                if (permission.readVitaminB6) read.add(ObjectType.Food.VitaminB6)
                if (permission.writeVitaminB6) write.add(ObjectType.Food.VitaminB6)
                if (permission.readVitaminC) read.add(ObjectType.Food.VitaminC)
                if (permission.writeVitaminC) write.add(ObjectType.Food.VitaminC)
                if (permission.readVitaminD) read.add(ObjectType.Food.VitaminD)
                if (permission.writeVitaminD) write.add(ObjectType.Food.VitaminD)
                if (permission.readVitaminE) read.add(ObjectType.Food.VitaminE)
                if (permission.writeVitaminE) write.add(ObjectType.Food.VitaminE)
                if (permission.readVitaminK) read.add(ObjectType.Food.VitaminK)
                if (permission.writeVitaminK) write.add(ObjectType.Food.VitaminK)
                if (permission.readZinc) read.add(ObjectType.Food.Zinc)
                if (permission.writeZinc) write.add(ObjectType.Food.Zinc)
            }

            is KHPermission.OvulationTest -> {
                if (permission.read) read.add(ObjectType.Category.OvulationTest)
                if (permission.write) write.add(ObjectType.Category.OvulationTest)
            }

            is KHPermission.OxygenSaturation -> {
                if (permission.read) read.add(ObjectType.Quantity.OxygenSaturation)
                if (permission.write) write.add(ObjectType.Quantity.OxygenSaturation)
            }

            is KHPermission.Power -> {
                if (permission.read) read.add(ObjectType.Quantity.Power)
                if (permission.write) write.add(ObjectType.Quantity.Power)
            }

            is KHPermission.RespiratoryRate -> {
                if (permission.read) read.add(ObjectType.Quantity.RespiratoryRate)
                if (permission.write) write.add(ObjectType.Quantity.RespiratoryRate)
            }

            is KHPermission.RestingHeartRate -> {
                if (permission.read) read.add(ObjectType.Quantity.RestingHeartRate)
                if (permission.write) write.add(ObjectType.Quantity.RestingHeartRate)
            }

            is KHPermission.RunningSpeed -> {
                if (permission.read) read.add(ObjectType.Quantity.RunningSpeed)
                if (permission.write) write.add(ObjectType.Quantity.RunningSpeed)
            }

            is KHPermission.SexualActivity -> {
                if (permission.read) read.add(ObjectType.Category.SexualActivity)
                if (permission.write) write.add(ObjectType.Category.SexualActivity)
            }

            is KHPermission.SleepSession -> {
                if (permission.read) read.add(ObjectType.Category.SleepSession)
                if (permission.write) write.add(ObjectType.Category.SleepSession)
            }

            is KHPermission.Speed -> Unit

            is KHPermission.StepCount -> {
                if (permission.read) read.add(ObjectType.Quantity.StepCount)
                if (permission.write) write.add(ObjectType.Quantity.StepCount)
            }

            is KHPermission.Vo2Max -> {
                if (permission.read) read.add(ObjectType.Quantity.Vo2Max)
                if (permission.write) write.add(ObjectType.Quantity.Vo2Max)
            }

            is KHPermission.Weight -> {
                if (permission.read) read.add(ObjectType.Quantity.Weight)
                if (permission.write) write.add(ObjectType.Quantity.Weight)
            }

            is KHPermission.WheelChairPushes -> {
                if (permission.read) read.add(ObjectType.Quantity.WheelChairPushes)
                if (permission.write) write.add(ObjectType.Quantity.WheelChairPushes)
            }
        }

        return Pair(read, write)
    }
}
