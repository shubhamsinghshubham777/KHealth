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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.datetime.toKotlinInstant
import kotlinx.datetime.toNSDate
import platform.Foundation.NSError
import platform.Foundation.NSSortDescriptor
import platform.HealthKit.HKAuthorizationStatusSharingAuthorized
import platform.HealthKit.HKCategorySample
import platform.HealthKit.HKCategoryValueNotApplicable
import platform.HealthKit.HKErrorAuthorizationNotDetermined
import platform.HealthKit.HKHealthStore
import platform.HealthKit.HKMetadataKeyFoodType
import platform.HealthKit.HKMetadataKeyMenstrualCycleStart
import platform.HealthKit.HKMetadataKeySexualActivityProtectionUsed
import platform.HealthKit.HKObjectQueryNoLimit
import platform.HealthKit.HKObjectType
import platform.HealthKit.HKQuantity
import platform.HealthKit.HKQuantitySample
import platform.HealthKit.HKQuery
import platform.HealthKit.HKQueryOptionStrictStartDate
import platform.HealthKit.HKSample
import platform.HealthKit.HKSampleQuery
import platform.HealthKit.HKSampleSortIdentifierStartDate
import platform.HealthKit.HKSampleType
import platform.HealthKit.predicateForSamplesWithStartDate
import platform.darwin.NSInteger
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

actual class KHealth {
    constructor() {
        this.store = HKHealthStore()
    }

    internal constructor(store: HKHealthStore, isHealthStoreAvailable: Boolean) {
        this.store = store
        this.testIsHealthStoreAvailable = isHealthStoreAvailable
    }

    private var store: HKHealthStore
    private var testIsHealthStoreAvailable: Boolean? = null

    actual val isHealthStoreAvailable: Boolean
        get() {
            return testIsHealthStoreAvailable ?: try {
                HKHealthStore.isHealthDataAvailable()
            } catch (t: Throwable) {
                false
            }
        }

    private fun verifyHealthStoreAvailability() {
        if (!isHealthStoreAvailable) throw HealthStoreNotAvailableException
    }

    actual fun initialise() = Unit

    @OptIn(UnsafeNumber::class)
    internal val NSInteger.isGranted get() = this == HKAuthorizationStatusSharingAuthorized

    @OptIn(UnsafeNumber::class)
    actual suspend fun checkPermissions(vararg permissions: KHPermission): Set<KHPermission> {
        return try {
            verifyHealthStoreAvailability()
            permissions.map { permission ->
                when (permission) {
                    is KHPermission.ActiveCaloriesBurned -> KHPermission.ActiveCaloriesBurned(
                        write = if (permission.write) {
                            store.authorizationStatusForType(ObjectType.Quantity.ActiveCaloriesBurned).isGranted
                        } else false
                    )

                    is KHPermission.BasalMetabolicRate -> KHPermission.BasalMetabolicRate(
                        write = if (permission.write) {
                            store.authorizationStatusForType(ObjectType.Quantity.BasalMetabolicRate).isGranted
                        } else false
                    )

                    is KHPermission.BloodGlucose -> KHPermission.BloodGlucose(
                        write = if (permission.write) {
                            store.authorizationStatusForType(ObjectType.Quantity.BasalMetabolicRate).isGranted
                        } else false
                    )

                    is KHPermission.BloodPressure -> KHPermission.BloodPressure(
                        writeSystolic = if (permission.writeSystolic) {
                            store.authorizationStatusForType(ObjectType.Quantity.BloodPressureSystolic).isGranted
                        } else false,
                        writeDiastolic = if (permission.writeDiastolic) {
                            store.authorizationStatusForType(ObjectType.Quantity.BloodPressureDiastolic).isGranted
                        } else false,
                    )

                    is KHPermission.BodyFat -> KHPermission.BodyFat(
                        write = if (permission.write) {
                            store.authorizationStatusForType(ObjectType.Quantity.BodyFat).isGranted
                        } else false
                    )

                    is KHPermission.BodyTemperature -> KHPermission.BodyTemperature(
                        write = if (permission.write) {
                            store.authorizationStatusForType(ObjectType.Quantity.BodyTemperature).isGranted
                        } else false
                    )

                    is KHPermission.BodyWaterMass -> KHPermission.BodyWaterMass(write = false)
                    is KHPermission.BoneMass -> KHPermission.BoneMass(write = false)

                    is KHPermission.CervicalMucus -> KHPermission.CervicalMucus(
                        write = if (permission.write) {
                            store.authorizationStatusForType(ObjectType.Category.CervicalMucus).isGranted
                        } else false
                    )

                    is KHPermission.CyclingPedalingCadence ->
                        KHPermission.CyclingPedalingCadence(write = false)

                    is KHPermission.CyclingSpeed -> KHPermission.CyclingSpeed(
                        write = if (permission.write) {
                            store.authorizationStatusForType(ObjectType.Quantity.CyclingSpeed).isGranted
                        } else false
                    )

                    is KHPermission.Distance -> KHPermission.Distance(
                        write = if (permission.write) {
                            store.authorizationStatusForType(ObjectType.Quantity.Distance).isGranted
                        } else false
                    )

                    is KHPermission.ElevationGained -> KHPermission.ElevationGained(write = false)

                    is KHPermission.FloorsClimbed -> KHPermission.FloorsClimbed(
                        write = if (permission.write) {
                            store.authorizationStatusForType(ObjectType.Quantity.FloorsClimbed).isGranted
                        } else false
                    )

                    is KHPermission.HeartRate -> KHPermission.HeartRate(
                        write = if (permission.write) {
                            store.authorizationStatusForType(ObjectType.Quantity.HeartRate).isGranted
                        } else false
                    )

                    is KHPermission.HeartRateVariability -> KHPermission.HeartRateVariability(
                        write = if (permission.write) {
                            store.authorizationStatusForType(ObjectType.Quantity.HeartRateVariability).isGranted
                        } else false
                    )

                    is KHPermission.Height -> KHPermission.Height(
                        write = if (permission.write) {
                            store.authorizationStatusForType(ObjectType.Quantity.Height).isGranted
                        } else false
                    )

                    is KHPermission.Hydration -> KHPermission.Hydration(
                        write = if (permission.write) {
                            store.authorizationStatusForType(ObjectType.Quantity.Hydration).isGranted
                        } else false
                    )

                    is KHPermission.IntermenstrualBleeding -> KHPermission.IntermenstrualBleeding(
                        write = if (permission.write) {
                            store.authorizationStatusForType(ObjectType.Category.IntermenstrualBleeding).isGranted
                        } else false
                    )

                    is KHPermission.LeanBodyMass -> KHPermission.LeanBodyMass(
                        write = if (permission.write) {
                            store.authorizationStatusForType(ObjectType.Quantity.LeanBodyMass).isGranted
                        } else false
                    )

                    is KHPermission.MenstruationFlow -> KHPermission.MenstruationFlow(
                        write = if (permission.write) {
                            store.authorizationStatusForType(ObjectType.Category.MenstruationFlow).isGranted
                        } else false
                    )

                    is KHPermission.MenstruationPeriod ->
                        KHPermission.MenstruationPeriod(write = false)

                    is KHPermission.Nutrition -> KHPermission.Nutrition(
                        writeBiotin = if (permission.writeBiotin) {
                            store.authorizationStatusForType(ObjectType.Food.Biotin).isGranted
                        } else false,
                        writeCaffeine = if (permission.writeCaffeine) {
                            store.authorizationStatusForType(ObjectType.Food.Caffeine).isGranted
                        } else false,
                        writeCalcium = if (permission.writeCalcium) {
                            store.authorizationStatusForType(ObjectType.Food.Calcium).isGranted
                        } else false,
                        writeChloride = if (permission.writeChloride) {
                            store.authorizationStatusForType(ObjectType.Food.Chloride).isGranted
                        } else false,
                        writeCholesterol = if (permission.writeCholesterol) {
                            store.authorizationStatusForType(ObjectType.Food.Cholesterol).isGranted
                        } else false,
                        writeChromium = if (permission.writeChromium) {
                            store.authorizationStatusForType(ObjectType.Food.Chromium).isGranted
                        } else false,
                        writeCopper = if (permission.writeCopper) {
                            store.authorizationStatusForType(ObjectType.Food.Copper).isGranted
                        } else false,
                        writeDietaryFiber = if (permission.writeDietaryFiber) {
                            store.authorizationStatusForType(ObjectType.Food.Fiber).isGranted
                        } else false,
                        writeEnergy = if (permission.writeEnergy) {
                            store.authorizationStatusForType(ObjectType.Food.EnergyConsumed).isGranted
                        } else false,
                        writeFolicAcid = if (permission.writeFolicAcid) {
                            store.authorizationStatusForType(ObjectType.Food.Folate).isGranted
                        } else false,
                        writeIodine = if (permission.writeIodine) {
                            store.authorizationStatusForType(ObjectType.Food.Iodine).isGranted
                        } else false,
                        writeIron = if (permission.writeIron) {
                            store.authorizationStatusForType(ObjectType.Food.Iron).isGranted
                        } else false,
                        writeMagnesium = if (permission.writeMagnesium) {
                            store.authorizationStatusForType(ObjectType.Food.Magnesium).isGranted
                        } else false,
                        writeManganese = if (permission.writeManganese) {
                            store.authorizationStatusForType(ObjectType.Food.Manganese).isGranted
                        } else false,
                        writeMolybdenum = if (permission.writeMolybdenum) {
                            store.authorizationStatusForType(ObjectType.Food.Molybdenum).isGranted
                        } else false,
                        writeMonounsaturatedFat = if (permission.writeMonounsaturatedFat) {
                            store.authorizationStatusForType(ObjectType.Food.FatMonounsaturated).isGranted
                        } else false,
                        writeNiacin = if (permission.writeNiacin) {
                            store.authorizationStatusForType(ObjectType.Food.Niacin).isGranted
                        } else false,
                        writePantothenicAcid = if (permission.writePantothenicAcid) {
                            store.authorizationStatusForType(ObjectType.Food.PantothenicAcid).isGranted
                        } else false,
                        writePhosphorus = if (permission.writePhosphorus) {
                            store.authorizationStatusForType(ObjectType.Food.Phosphorus).isGranted
                        } else false,
                        writePolyunsaturatedFat = if (permission.writePolyunsaturatedFat) {
                            store.authorizationStatusForType(ObjectType.Food.FatPolyunsaturated).isGranted
                        } else false,
                        writePotassium = if (permission.writePotassium) {
                            store.authorizationStatusForType(ObjectType.Food.Potassium).isGranted
                        } else false,
                        writeProtein = if (permission.writeProtein) {
                            store.authorizationStatusForType(ObjectType.Food.Protein).isGranted
                        } else false,
                        writeRiboflavin = if (permission.writeRiboflavin) {
                            store.authorizationStatusForType(ObjectType.Food.Riboflavin).isGranted
                        } else false,
                        writeSaturatedFat = if (permission.writeSaturatedFat) {
                            store.authorizationStatusForType(ObjectType.Food.FatSaturated).isGranted
                        } else false,
                        writeSelenium = if (permission.writeSelenium) {
                            store.authorizationStatusForType(ObjectType.Food.Selenium).isGranted
                        } else false,
                        writeSodium = if (permission.writeSodium) {
                            store.authorizationStatusForType(ObjectType.Food.Sodium).isGranted
                        } else false,
                        writeSugar = if (permission.writeSugar) {
                            store.authorizationStatusForType(ObjectType.Food.Sugar).isGranted
                        } else false,
                        writeThiamin = if (permission.writeThiamin) {
                            store.authorizationStatusForType(ObjectType.Food.Thiamin).isGranted
                        } else false,
                        writeTotalCarbohydrate = if (permission.writeTotalCarbohydrate) {
                            store.authorizationStatusForType(ObjectType.Food.Carbohydrates).isGranted
                        } else false,
                        writeTotalFat = if (permission.writeTotalFat) {
                            store.authorizationStatusForType(ObjectType.Food.FatTotal).isGranted
                        } else false,
                        writeVitaminA = if (permission.writeVitaminA) {
                            store.authorizationStatusForType(ObjectType.Food.VitaminA).isGranted
                        } else false,
                        writeVitaminB12 = if (permission.writeVitaminB12) {
                            store.authorizationStatusForType(ObjectType.Food.VitaminB12).isGranted
                        } else false,
                        writeVitaminB6 = if (permission.writeVitaminB6) {
                            store.authorizationStatusForType(ObjectType.Food.VitaminB6).isGranted
                        } else false,
                        writeVitaminC = if (permission.writeVitaminC) {
                            store.authorizationStatusForType(ObjectType.Food.VitaminC).isGranted
                        } else false,
                        writeVitaminD = if (permission.writeVitaminD) {
                            store.authorizationStatusForType(ObjectType.Food.VitaminD).isGranted
                        } else false,
                        writeVitaminE = if (permission.writeVitaminE) {
                            store.authorizationStatusForType(ObjectType.Food.VitaminE).isGranted
                        } else false,
                        writeVitaminK = if (permission.writeVitaminK) {
                            store.authorizationStatusForType(ObjectType.Food.VitaminK).isGranted
                        } else false,
                        writeZinc = if (permission.writeZinc) {
                            store.authorizationStatusForType(ObjectType.Food.Zinc).isGranted
                        } else false,
                    )

                    is KHPermission.OvulationTest -> KHPermission.OvulationTest(
                        write = if (permission.write) {
                            store.authorizationStatusForType(ObjectType.Category.OvulationTest).isGranted
                        } else false
                    )

                    is KHPermission.OxygenSaturation -> KHPermission.OxygenSaturation(
                        write = if (permission.write) {
                            store.authorizationStatusForType(ObjectType.Quantity.OxygenSaturation).isGranted
                        } else false
                    )

                    is KHPermission.Power -> KHPermission.Power(
                        write = if (permission.write) {
                            store.authorizationStatusForType(ObjectType.Quantity.Power).isGranted
                        } else false
                    )

                    is KHPermission.RespiratoryRate -> KHPermission.RespiratoryRate(
                        write = if (permission.write) {
                            store.authorizationStatusForType(ObjectType.Quantity.RespiratoryRate).isGranted
                        } else false
                    )

                    is KHPermission.RestingHeartRate -> KHPermission.RestingHeartRate(
                        write = if (permission.write) {
                            store.authorizationStatusForType(ObjectType.Quantity.RestingHeartRate).isGranted
                        } else false
                    )

                    is KHPermission.RunningSpeed -> KHPermission.RestingHeartRate(
                        write = if (permission.write) {
                            store.authorizationStatusForType(ObjectType.Quantity.RunningSpeed).isGranted
                        } else false
                    )

                    is KHPermission.SexualActivity -> KHPermission.SexualActivity(
                        write = if (permission.write) {
                            store.authorizationStatusForType(ObjectType.Category.SexualActivity).isGranted
                        } else false
                    )

                    is KHPermission.SleepSession -> KHPermission.SleepSession(
                        write = if (permission.write) {
                            store.authorizationStatusForType(ObjectType.Category.SleepSession).isGranted
                        } else false
                    )

                    is KHPermission.Speed -> KHPermission.Speed(write = false)

                    is KHPermission.StepCount -> KHPermission.StepCount(
                        write = if (permission.write) {
                            store.authorizationStatusForType(ObjectType.Quantity.StepCount).isGranted
                        } else false
                    )

                    is KHPermission.Vo2Max -> KHPermission.Vo2Max(
                        write = if (permission.write) {
                            store.authorizationStatusForType(ObjectType.Quantity.Vo2Max).isGranted
                        } else false
                    )

                    is KHPermission.Weight -> KHPermission.Weight(
                        write = if (permission.write) {
                            store.authorizationStatusForType(ObjectType.Quantity.Weight).isGranted
                        } else false
                    )

                    is KHPermission.WheelChairPushes -> KHPermission.WheelChairPushes(
                        write = if (permission.write) {
                            store.authorizationStatusForType(ObjectType.Quantity.WheelChairPushes).isGranted
                        } else false
                    )

                    else -> KHPermission.BasalMetabolicRate(write = false)
                }
            }.toSet()
        } catch (t: Throwable) {
            logError(t, methodName = "checkPermissions")
            emptySet()
        }
    }

    actual suspend fun requestPermissions(
        vararg permissions: KHPermission
    ): Set<KHPermission> = suspendCoroutine { continuation ->
        val coroutineScope = CoroutineScope(continuation.context)
        val readPermissions = mutableSetOf<HKObjectType>()
        val writePermissions = mutableSetOf<HKObjectType>()

        for (permission in permissions) {
            when (permission) {
                is KHPermission.ActiveCaloriesBurned -> {
                    if (permission.read) readPermissions.add(ObjectType.Quantity.ActiveCaloriesBurned)
                    if (permission.write) writePermissions.add(ObjectType.Quantity.ActiveCaloriesBurned)
                }

                is KHPermission.BasalMetabolicRate -> {
                    if (permission.read) readPermissions.add(ObjectType.Quantity.BasalMetabolicRate)
                    if (permission.write) writePermissions.add(ObjectType.Quantity.BasalMetabolicRate)
                }

                is KHPermission.BloodGlucose -> {
                    if (permission.read) readPermissions.add(ObjectType.Quantity.BloodGlucose)
                    if (permission.write) writePermissions.add(ObjectType.Quantity.BloodGlucose)
                }

                is KHPermission.BloodPressure -> {
                    if (permission.readSystolic) {
                        readPermissions.add(ObjectType.Quantity.BloodPressureSystolic)
                    }
                    if (permission.writeSystolic) {
                        writePermissions.add(ObjectType.Quantity.BloodPressureSystolic)
                    }
                    if (permission.readDiastolic) {
                        readPermissions.add(ObjectType.Quantity.BloodPressureDiastolic)
                    }
                    if (permission.writeDiastolic) {
                        writePermissions.add(ObjectType.Quantity.BloodPressureDiastolic)
                    }
                }

                is KHPermission.BodyFat -> {
                    if (permission.read) readPermissions.add(ObjectType.Quantity.BodyFat)
                    if (permission.write) writePermissions.add(ObjectType.Quantity.BodyFat)
                }

                is KHPermission.BodyTemperature -> {
                    if (permission.read) readPermissions.add(ObjectType.Quantity.BodyTemperature)
                    if (permission.write) writePermissions.add(ObjectType.Quantity.BodyTemperature)
                }

                is KHPermission.BodyWaterMass -> Unit

                is KHPermission.BoneMass -> Unit

                is KHPermission.CervicalMucus -> {
                    if (permission.read) readPermissions.add(ObjectType.Category.CervicalMucus)
                    if (permission.write) writePermissions.add(ObjectType.Category.CervicalMucus)
                }

                is KHPermission.CyclingPedalingCadence -> Unit

                is KHPermission.CyclingSpeed -> {
                    if (permission.read) readPermissions.add(ObjectType.Quantity.CyclingSpeed)
                    if (permission.write) writePermissions.add(ObjectType.Quantity.CyclingSpeed)
                }

                is KHPermission.Distance -> {
                    if (permission.read) readPermissions.add(ObjectType.Quantity.Distance)
                    if (permission.write) writePermissions.add(ObjectType.Quantity.Distance)
                }

                is KHPermission.ElevationGained -> Unit

                is KHPermission.FloorsClimbed -> {
                    if (permission.read) readPermissions.add(ObjectType.Quantity.FloorsClimbed)
                    if (permission.write) writePermissions.add(ObjectType.Quantity.FloorsClimbed)
                }

                is KHPermission.HeartRate -> {
                    if (permission.read) readPermissions.add(ObjectType.Quantity.HeartRate)
                    if (permission.write) writePermissions.add(ObjectType.Quantity.HeartRate)
                }

                is KHPermission.HeartRateVariability -> {
                    if (permission.read) readPermissions.add(ObjectType.Quantity.HeartRateVariability)
                    if (permission.write) writePermissions.add(ObjectType.Quantity.HeartRateVariability)
                }

                is KHPermission.Height -> {
                    if (permission.read) readPermissions.add(ObjectType.Quantity.Height)
                    if (permission.write) writePermissions.add(ObjectType.Quantity.Height)
                }

                is KHPermission.Hydration -> {
                    if (permission.read) readPermissions.add(ObjectType.Quantity.Hydration)
                    if (permission.write) writePermissions.add(ObjectType.Quantity.Hydration)
                }

                is KHPermission.IntermenstrualBleeding -> {
                    if (permission.read) readPermissions.add(ObjectType.Category.IntermenstrualBleeding)
                    if (permission.write) writePermissions.add(ObjectType.Category.IntermenstrualBleeding)
                }

                is KHPermission.LeanBodyMass -> {
                    if (permission.read) readPermissions.add(ObjectType.Quantity.LeanBodyMass)
                    if (permission.write) writePermissions.add(ObjectType.Quantity.LeanBodyMass)
                }

                is KHPermission.MenstruationFlow -> {
                    if (permission.read) readPermissions.add(ObjectType.Category.MenstruationFlow)
                    if (permission.write) writePermissions.add(ObjectType.Category.MenstruationFlow)
                }

                is KHPermission.MenstruationPeriod -> Unit

                is KHPermission.Nutrition -> {
                    if (permission.readBiotin) readPermissions.add(ObjectType.Food.Biotin)
                    if (permission.writeBiotin) writePermissions.add(ObjectType.Food.Biotin)
                    if (permission.readCaffeine) readPermissions.add(ObjectType.Food.Caffeine)
                    if (permission.writeCaffeine) writePermissions.add(ObjectType.Food.Caffeine)
                    if (permission.readCalcium) readPermissions.add(ObjectType.Food.Calcium)
                    if (permission.writeCalcium) writePermissions.add(ObjectType.Food.Calcium)
                    if (permission.readChloride) readPermissions.add(ObjectType.Food.Chloride)
                    if (permission.writeChloride) writePermissions.add(ObjectType.Food.Chloride)
                    if (permission.readCholesterol) readPermissions.add(ObjectType.Food.Cholesterol)
                    if (permission.writeCholesterol) writePermissions.add(ObjectType.Food.Cholesterol)
                    if (permission.readChromium) readPermissions.add(ObjectType.Food.Chromium)
                    if (permission.writeChromium) writePermissions.add(ObjectType.Food.Chromium)
                    if (permission.readCopper) readPermissions.add(ObjectType.Food.Copper)
                    if (permission.writeCopper) writePermissions.add(ObjectType.Food.Copper)
                    if (permission.readDietaryFiber) readPermissions.add(ObjectType.Food.Fiber)
                    if (permission.writeDietaryFiber) writePermissions.add(ObjectType.Food.Fiber)
                    if (permission.readEnergy) readPermissions.add(ObjectType.Food.EnergyConsumed)
                    if (permission.writeEnergy) writePermissions.add(ObjectType.Food.EnergyConsumed)
                    if (permission.readFolicAcid) readPermissions.add(ObjectType.Food.Folate)
                    if (permission.writeFolicAcid) writePermissions.add(ObjectType.Food.Folate)
                    if (permission.readIodine) readPermissions.add(ObjectType.Food.Iodine)
                    if (permission.writeIodine) writePermissions.add(ObjectType.Food.Iodine)
                    if (permission.readIron) readPermissions.add(ObjectType.Food.Iron)
                    if (permission.writeIron) writePermissions.add(ObjectType.Food.Iron)
                    if (permission.readMagnesium) readPermissions.add(ObjectType.Food.Magnesium)
                    if (permission.writeMagnesium) writePermissions.add(ObjectType.Food.Magnesium)
                    if (permission.readManganese) readPermissions.add(ObjectType.Food.Manganese)
                    if (permission.writeManganese) writePermissions.add(ObjectType.Food.Manganese)
                    if (permission.readMolybdenum) readPermissions.add(ObjectType.Food.Molybdenum)
                    if (permission.writeMolybdenum) writePermissions.add(ObjectType.Food.Molybdenum)
                    if (permission.readMonounsaturatedFat) readPermissions.add(ObjectType.Food.FatMonounsaturated)
                    if (permission.writeMonounsaturatedFat) writePermissions.add(ObjectType.Food.FatMonounsaturated)
                    if (permission.readNiacin) readPermissions.add(ObjectType.Food.Niacin)
                    if (permission.writeNiacin) writePermissions.add(ObjectType.Food.Niacin)
                    if (permission.readPantothenicAcid) readPermissions.add(ObjectType.Food.PantothenicAcid)
                    if (permission.writePantothenicAcid) writePermissions.add(ObjectType.Food.PantothenicAcid)
                    if (permission.readPhosphorus) readPermissions.add(ObjectType.Food.Phosphorus)
                    if (permission.writePhosphorus) writePermissions.add(ObjectType.Food.Phosphorus)
                    if (permission.readPolyunsaturatedFat) readPermissions.add(ObjectType.Food.FatPolyunsaturated)
                    if (permission.writePolyunsaturatedFat) writePermissions.add(ObjectType.Food.FatPolyunsaturated)
                    if (permission.readPotassium) readPermissions.add(ObjectType.Food.Potassium)
                    if (permission.writePotassium) writePermissions.add(ObjectType.Food.Potassium)
                    if (permission.readProtein) readPermissions.add(ObjectType.Food.Protein)
                    if (permission.writeProtein) writePermissions.add(ObjectType.Food.Protein)
                    if (permission.readRiboflavin) readPermissions.add(ObjectType.Food.Riboflavin)
                    if (permission.writeRiboflavin) writePermissions.add(ObjectType.Food.Riboflavin)
                    if (permission.readSaturatedFat) readPermissions.add(ObjectType.Food.FatSaturated)
                    if (permission.writeSaturatedFat) writePermissions.add(ObjectType.Food.FatSaturated)
                    if (permission.readSelenium) readPermissions.add(ObjectType.Food.Selenium)
                    if (permission.writeSelenium) writePermissions.add(ObjectType.Food.Selenium)
                    if (permission.readSodium) readPermissions.add(ObjectType.Food.Sodium)
                    if (permission.writeSodium) writePermissions.add(ObjectType.Food.Sodium)
                    if (permission.readSugar) readPermissions.add(ObjectType.Food.Sugar)
                    if (permission.writeSugar) writePermissions.add(ObjectType.Food.Sugar)
                    if (permission.readThiamin) readPermissions.add(ObjectType.Food.Thiamin)
                    if (permission.writeThiamin) writePermissions.add(ObjectType.Food.Thiamin)
                    if (permission.readTotalCarbohydrate) readPermissions.add(ObjectType.Food.Carbohydrates)
                    if (permission.writeTotalCarbohydrate) writePermissions.add(ObjectType.Food.Carbohydrates)
                    if (permission.readTotalFat) readPermissions.add(ObjectType.Food.FatTotal)
                    if (permission.writeTotalFat) writePermissions.add(ObjectType.Food.FatTotal)
                    if (permission.readVitaminA) readPermissions.add(ObjectType.Food.VitaminA)
                    if (permission.writeVitaminA) writePermissions.add(ObjectType.Food.VitaminA)
                    if (permission.readVitaminB12) readPermissions.add(ObjectType.Food.VitaminB12)
                    if (permission.writeVitaminB12) writePermissions.add(ObjectType.Food.VitaminB12)
                    if (permission.readVitaminB6) readPermissions.add(ObjectType.Food.VitaminB6)
                    if (permission.writeVitaminB6) writePermissions.add(ObjectType.Food.VitaminB6)
                    if (permission.readVitaminC) readPermissions.add(ObjectType.Food.VitaminC)
                    if (permission.writeVitaminC) writePermissions.add(ObjectType.Food.VitaminC)
                    if (permission.readVitaminD) readPermissions.add(ObjectType.Food.VitaminD)
                    if (permission.writeVitaminD) writePermissions.add(ObjectType.Food.VitaminD)
                    if (permission.readVitaminE) readPermissions.add(ObjectType.Food.VitaminE)
                    if (permission.writeVitaminE) writePermissions.add(ObjectType.Food.VitaminE)
                    if (permission.readVitaminK) readPermissions.add(ObjectType.Food.VitaminK)
                    if (permission.writeVitaminK) writePermissions.add(ObjectType.Food.VitaminK)
                    if (permission.readZinc) readPermissions.add(ObjectType.Food.Zinc)
                    if (permission.writeZinc) writePermissions.add(ObjectType.Food.Zinc)
                }

                is KHPermission.OvulationTest -> {
                    if (permission.read) readPermissions.add(ObjectType.Category.OvulationTest)
                    if (permission.write) writePermissions.add(ObjectType.Category.OvulationTest)
                }

                is KHPermission.OxygenSaturation -> {
                    if (permission.read) readPermissions.add(ObjectType.Quantity.OxygenSaturation)
                    if (permission.write) writePermissions.add(ObjectType.Quantity.OxygenSaturation)
                }

                is KHPermission.Power -> {
                    if (permission.read) readPermissions.add(ObjectType.Quantity.Power)
                    if (permission.write) writePermissions.add(ObjectType.Quantity.Power)
                }

                is KHPermission.RespiratoryRate -> {
                    if (permission.read) readPermissions.add(ObjectType.Quantity.RespiratoryRate)
                    if (permission.write) writePermissions.add(ObjectType.Quantity.RespiratoryRate)
                }

                is KHPermission.RestingHeartRate -> {
                    if (permission.read) readPermissions.add(ObjectType.Quantity.RestingHeartRate)
                    if (permission.write) writePermissions.add(ObjectType.Quantity.RestingHeartRate)
                }

                is KHPermission.RunningSpeed -> {
                    if (permission.read) readPermissions.add(ObjectType.Quantity.RunningSpeed)
                    if (permission.write) writePermissions.add(ObjectType.Quantity.RunningSpeed)
                }

                is KHPermission.SexualActivity -> {
                    if (permission.read) readPermissions.add(ObjectType.Category.SexualActivity)
                    if (permission.write) writePermissions.add(ObjectType.Category.SexualActivity)
                }

                is KHPermission.SleepSession -> {
                    if (permission.read) readPermissions.add(ObjectType.Category.SleepSession)
                    if (permission.write) writePermissions.add(ObjectType.Category.SleepSession)
                }

                is KHPermission.Speed -> Unit

                is KHPermission.StepCount -> {
                    if (permission.read) readPermissions.add(ObjectType.Quantity.StepCount)
                    if (permission.write) writePermissions.add(ObjectType.Quantity.StepCount)
                }

                is KHPermission.Vo2Max -> {
                    if (permission.read) readPermissions.add(ObjectType.Quantity.Vo2Max)
                    if (permission.write) writePermissions.add(ObjectType.Quantity.Vo2Max)
                }

                is KHPermission.Weight -> {
                    if (permission.read) readPermissions.add(ObjectType.Quantity.Weight)
                    if (permission.write) writePermissions.add(ObjectType.Quantity.Weight)
                }

                is KHPermission.WheelChairPushes -> {
                    if (permission.read) readPermissions.add(ObjectType.Quantity.WheelChairPushes)
                    if (permission.write) writePermissions.add(ObjectType.Quantity.WheelChairPushes)
                }
            }
        }

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
    }

    @OptIn(UnsafeNumber::class)
    actual suspend fun writeRecords(
        vararg records: KHRecord
    ): KHWriteResponse = suspendCoroutine { continuation ->
        try {
            val samples = mutableListOf<HKSample>()

            for (record in records) {
                when (record) {
                    is KHRecord.ActiveCaloriesBurned -> samples.add(
                        HKQuantitySample.quantitySampleWithType(
                            quantityType = ObjectType.Quantity.ActiveCaloriesBurned,
                            quantity = record.unit toNativeEnergyFor record.value,
                            startDate = record.startTime.toNSDate(),
                            endDate = record.endTime.toNSDate(),
                        )
                    )

                    is KHRecord.BasalMetabolicRate -> samples.add(
                        HKQuantitySample.quantitySampleWithType(
                            quantityType = ObjectType.Quantity.BasalMetabolicRate,
                            quantity = record.unit.right toNativeEnergyFor record.value,
                            startDate = record.time.toNSDate(),
                            endDate = record.time.toNSDate(),
                        )
                    )

                    is KHRecord.BloodGlucose -> samples.add(
                        HKQuantitySample.quantitySampleWithType(
                            quantityType = ObjectType.Quantity.BloodGlucose,
                            quantity = record.unit toNativeBloodGlucoseFor record.value,
                            startDate = record.time.toNSDate(),
                            endDate = record.time.toNSDate(),
                        )
                    )

                    is KHRecord.BloodPressure -> samples.addAll(
                        listOf(
                            HKQuantitySample.quantitySampleWithType(
                                quantityType = ObjectType.Quantity.BloodPressureSystolic,
                                quantity = record.unit toNativePressureFor record.systolicValue,
                                startDate = record.time.toNSDate(),
                                endDate = record.time.toNSDate(),
                            ),
                            HKQuantitySample.quantitySampleWithType(
                                quantityType = ObjectType.Quantity.BloodPressureDiastolic,
                                quantity = record.unit toNativePressureFor record.diastolicValue,
                                startDate = record.time.toNSDate(),
                                endDate = record.time.toNSDate(),
                            ),
                        )
                    )

                    is KHRecord.BodyFat -> samples.add(
                        HKQuantitySample.quantitySampleWithType(
                            quantityType = ObjectType.Quantity.BodyFat,
                            quantity = HKQuantity.quantityWithUnit(
                                unit = AppleUnits.percent,
                                doubleValue = record.percentage
                            ),
                            startDate = record.time.toNSDate(),
                            endDate = record.time.toNSDate(),
                        )
                    )

                    is KHRecord.BodyTemperature -> samples.add(
                        HKQuantitySample.quantitySampleWithType(
                            quantityType = ObjectType.Quantity.BodyTemperature,
                            quantity = record.unit toNativeTemperatureFor record.value,
                            startDate = record.time.toNSDate(),
                            endDate = record.time.toNSDate(),
                        )
                    )

                    is KHRecord.BodyWaterMass -> Unit
                    is KHRecord.BoneMass -> Unit

                    is KHRecord.CervicalMucus -> samples.add(
                        HKCategorySample.categorySampleWithType(
                            type = ObjectType.Category.CervicalMucus,
                            value = record.appearance.toNativeCervicalMucusQuality(),
                            startDate = record.time.toNSDate(),
                            endDate = record.time.toNSDate(),
                        )
                    )

                    is KHRecord.CyclingPedalingCadence -> Unit

                    is KHRecord.CyclingSpeed -> samples.addAll(
                        record.samples.map { sample ->
                            HKQuantitySample.quantitySampleWithType(
                                quantityType = ObjectType.Quantity.CyclingSpeed,
                                quantity = sample.unit toNativeVelocityFor sample.value,
                                startDate = sample.time.toNSDate(),
                                endDate = sample.time.toNSDate(),
                            )
                        }
                    )

                    is KHRecord.Distance -> samples.add(
                        HKQuantitySample.quantitySampleWithType(
                            quantityType = ObjectType.Quantity.Distance,
                            quantity = record.unit toNativeLengthFor record.value,
                            startDate = record.startTime.toNSDate(),
                            endDate = record.endTime.toNSDate(),
                        )
                    )

                    is KHRecord.ElevationGained -> Unit

                    is KHRecord.FloorsClimbed -> samples.add(
                        HKQuantitySample.quantitySampleWithType(
                            quantityType = ObjectType.Quantity.FloorsClimbed,
                            quantity = HKQuantity.quantityWithUnit(
                                unit = AppleUnits.count,
                                doubleValue = record.floors
                            ),
                            startDate = record.startTime.toNSDate(),
                            endDate = record.endTime.toNSDate(),
                        )
                    )

                    is KHRecord.HeartRate -> samples.addAll(
                        record.samples.map { sample ->
                            HKQuantitySample.quantitySampleWithType(
                                quantityType = ObjectType.Quantity.HeartRate,
                                quantity = HKQuantity.quantityWithUnit(
                                    unit = AppleUnits.beatsPerMinute,
                                    doubleValue = sample.beatsPerMinute.toDouble()
                                ),
                                startDate = sample.time.toNSDate(),
                                endDate = sample.time.toNSDate(),
                            )
                        }
                    )

                    is KHRecord.HeartRateVariability -> samples.add(
                        HKQuantitySample.quantitySampleWithType(
                            quantityType = ObjectType.Quantity.HeartRateVariability,
                            quantity = HKQuantity.quantityWithUnit(
                                unit = AppleUnits.millisecond,
                                doubleValue = record.heartRateVariabilityMillis
                            ),
                            startDate = record.time.toNSDate(),
                            endDate = record.time.toNSDate(),
                        ),
                    )

                    is KHRecord.Height -> samples.add(
                        HKQuantitySample.quantitySampleWithType(
                            quantityType = ObjectType.Quantity.Height,
                            quantity = record.unit toNativeLengthFor record.value,
                            startDate = record.time.toNSDate(),
                            endDate = record.time.toNSDate(),
                        )
                    )

                    is KHRecord.Hydration -> samples.add(
                        HKQuantitySample.quantitySampleWithType(
                            quantityType = ObjectType.Quantity.Hydration,
                            quantity = record.unit toNativeVolumeFor record.value,
                            startDate = record.startTime.toNSDate(),
                            endDate = record.endTime.toNSDate(),
                        )
                    )

                    is KHRecord.IntermenstrualBleeding -> samples.add(
                        HKCategorySample.categorySampleWithType(
                            type = ObjectType.Category.IntermenstrualBleeding,
                            value = HKCategoryValueNotApplicable,
                            startDate = record.time.toNSDate(),
                            endDate = record.time.toNSDate(),
                        ),
                    )

                    is KHRecord.LeanBodyMass -> samples.add(
                        HKQuantitySample.quantitySampleWithType(
                            quantityType = ObjectType.Quantity.LeanBodyMass,
                            quantity = record.unit toNativeMassFor record.value,
                            startDate = record.time.toNSDate(),
                            endDate = record.time.toNSDate(),
                        )
                    )

                    is KHRecord.MenstruationFlow -> samples.add(
                        HKCategorySample.categorySampleWithType(
                            type = ObjectType.Category.MenstruationFlow,
                            value = record.type.toNativeMenstrualFlow(),
                            startDate = record.time.toNSDate(),
                            endDate = record.time.toNSDate(),
                            metadata = mapOf(HKMetadataKeyMenstrualCycleStart to record.isStartOfCycle)
                        ),
                    )

                    is KHRecord.MenstruationPeriod -> Unit

                    is KHRecord.Nutrition -> samples.addAll(
                        buildList {
                            record.biotin?.let { value ->
                                add(
                                    HKQuantitySample.quantitySampleWithType(
                                        quantityType = ObjectType.Food.Biotin,
                                        quantity = record.solidUnit toNativeMassFor value,
                                        startDate = record.startTime.toNSDate(),
                                        endDate = record.endTime.toNSDate(),
                                        metadata = mapOf(HKMetadataKeyFoodType to record.mealType.name)
                                    )
                                )
                            }
                            record.caffeine?.let { value ->
                                add(
                                    HKQuantitySample.quantitySampleWithType(
                                        quantityType = ObjectType.Food.Caffeine,
                                        quantity = record.solidUnit toNativeMassFor value,
                                        startDate = record.startTime.toNSDate(),
                                        endDate = record.endTime.toNSDate(),
                                        metadata = mapOf(HKMetadataKeyFoodType to record.mealType.name)
                                    )
                                )
                            }
                            record.calcium?.let { value ->
                                add(
                                    HKQuantitySample.quantitySampleWithType(
                                        quantityType = ObjectType.Food.Calcium,
                                        quantity = record.solidUnit toNativeMassFor value,
                                        startDate = record.startTime.toNSDate(),
                                        endDate = record.endTime.toNSDate(),
                                        metadata = mapOf(HKMetadataKeyFoodType to record.mealType.name)
                                    )
                                )
                            }
                            record.energy?.let { value ->
                                add(
                                    HKQuantitySample.quantitySampleWithType(
                                        quantityType = ObjectType.Food.EnergyConsumed,
                                        quantity = record.energyUnit toNativeEnergyFor value,
                                        startDate = record.startTime.toNSDate(),
                                        endDate = record.endTime.toNSDate(),
                                        metadata = mapOf(HKMetadataKeyFoodType to record.mealType.name)
                                    )
                                )
                            }
                            record.chloride?.let { value ->
                                add(
                                    HKQuantitySample.quantitySampleWithType(
                                        quantityType = ObjectType.Food.Chloride,
                                        quantity = record.solidUnit toNativeMassFor value,
                                        startDate = record.startTime.toNSDate(),
                                        endDate = record.endTime.toNSDate(),
                                        metadata = mapOf(HKMetadataKeyFoodType to record.mealType.name)
                                    )
                                )
                            }
                            record.cholesterol?.let { value ->
                                add(
                                    HKQuantitySample.quantitySampleWithType(
                                        quantityType = ObjectType.Food.Cholesterol,
                                        quantity = record.solidUnit toNativeMassFor value,
                                        startDate = record.startTime.toNSDate(),
                                        endDate = record.endTime.toNSDate(),
                                        metadata = mapOf(HKMetadataKeyFoodType to record.mealType.name)
                                    )
                                )
                            }
                            record.chromium?.let { value ->
                                add(
                                    HKQuantitySample.quantitySampleWithType(
                                        quantityType = ObjectType.Food.Chromium,
                                        quantity = record.solidUnit toNativeMassFor value,
                                        startDate = record.startTime.toNSDate(),
                                        endDate = record.endTime.toNSDate(),
                                        metadata = mapOf(HKMetadataKeyFoodType to record.mealType.name)
                                    )
                                )
                            }
                            record.copper?.let { value ->
                                add(
                                    HKQuantitySample.quantitySampleWithType(
                                        quantityType = ObjectType.Food.Copper,
                                        quantity = record.solidUnit toNativeMassFor value,
                                        startDate = record.startTime.toNSDate(),
                                        endDate = record.endTime.toNSDate(),
                                        metadata = mapOf(HKMetadataKeyFoodType to record.mealType.name)
                                    )
                                )
                            }
                            record.dietaryFiber?.let { value ->
                                add(
                                    HKQuantitySample.quantitySampleWithType(
                                        quantityType = ObjectType.Food.Fiber,
                                        quantity = record.solidUnit toNativeMassFor value,
                                        startDate = record.startTime.toNSDate(),
                                        endDate = record.endTime.toNSDate(),
                                        metadata = mapOf(HKMetadataKeyFoodType to record.mealType.name)
                                    )
                                )
                            }
                            record.folicAcid?.let { value ->
                                add(
                                    HKQuantitySample.quantitySampleWithType(
                                        quantityType = ObjectType.Food.Folate,
                                        quantity = record.solidUnit toNativeMassFor value,
                                        startDate = record.startTime.toNSDate(),
                                        endDate = record.endTime.toNSDate(),
                                        metadata = mapOf(HKMetadataKeyFoodType to record.mealType.name)
                                    )
                                )
                            }
                            record.iodine?.let { value ->
                                add(
                                    HKQuantitySample.quantitySampleWithType(
                                        quantityType = ObjectType.Food.Iodine,
                                        quantity = record.solidUnit toNativeMassFor value,
                                        startDate = record.startTime.toNSDate(),
                                        endDate = record.endTime.toNSDate(),
                                        metadata = mapOf(HKMetadataKeyFoodType to record.mealType.name)
                                    )
                                )
                            }
                            record.iron?.let { value ->
                                add(
                                    HKQuantitySample.quantitySampleWithType(
                                        quantityType = ObjectType.Food.Iron,
                                        quantity = record.solidUnit toNativeMassFor value,
                                        startDate = record.startTime.toNSDate(),
                                        endDate = record.endTime.toNSDate(),
                                        metadata = mapOf(HKMetadataKeyFoodType to record.mealType.name)
                                    )
                                )
                            }
                            record.magnesium?.let { value ->
                                add(
                                    HKQuantitySample.quantitySampleWithType(
                                        quantityType = ObjectType.Food.Magnesium,
                                        quantity = record.solidUnit toNativeMassFor value,
                                        startDate = record.startTime.toNSDate(),
                                        endDate = record.endTime.toNSDate(),
                                        metadata = mapOf(HKMetadataKeyFoodType to record.mealType.name)
                                    )
                                )
                            }
                            record.manganese?.let { value ->
                                add(
                                    HKQuantitySample.quantitySampleWithType(
                                        quantityType = ObjectType.Food.Manganese,
                                        quantity = record.solidUnit toNativeMassFor value,
                                        startDate = record.startTime.toNSDate(),
                                        endDate = record.endTime.toNSDate(),
                                        metadata = mapOf(HKMetadataKeyFoodType to record.mealType.name)
                                    )
                                )
                            }
                            record.molybdenum?.let { value ->
                                add(
                                    HKQuantitySample.quantitySampleWithType(
                                        quantityType = ObjectType.Food.Molybdenum,
                                        quantity = record.solidUnit toNativeMassFor value,
                                        startDate = record.startTime.toNSDate(),
                                        endDate = record.endTime.toNSDate(),
                                        metadata = mapOf(HKMetadataKeyFoodType to record.mealType.name)
                                    )
                                )
                            }
                            record.monounsaturatedFat?.let { value ->
                                add(
                                    HKQuantitySample.quantitySampleWithType(
                                        quantityType = ObjectType.Food.FatMonounsaturated,
                                        quantity = record.solidUnit toNativeMassFor value,
                                        startDate = record.startTime.toNSDate(),
                                        endDate = record.endTime.toNSDate(),
                                        metadata = mapOf(HKMetadataKeyFoodType to record.mealType.name)
                                    )
                                )
                            }
                            record.niacin?.let { value ->
                                add(
                                    HKQuantitySample.quantitySampleWithType(
                                        quantityType = ObjectType.Food.Niacin,
                                        quantity = record.solidUnit toNativeMassFor value,
                                        startDate = record.startTime.toNSDate(),
                                        endDate = record.endTime.toNSDate(),
                                        metadata = mapOf(HKMetadataKeyFoodType to record.mealType.name)
                                    )
                                )
                            }
                            record.pantothenicAcid?.let { value ->
                                add(
                                    HKQuantitySample.quantitySampleWithType(
                                        quantityType = ObjectType.Food.PantothenicAcid,
                                        quantity = record.solidUnit toNativeMassFor value,
                                        startDate = record.startTime.toNSDate(),
                                        endDate = record.endTime.toNSDate(),
                                        metadata = mapOf(HKMetadataKeyFoodType to record.mealType.name)
                                    )
                                )
                            }
                            record.phosphorus?.let { value ->
                                add(
                                    HKQuantitySample.quantitySampleWithType(
                                        quantityType = ObjectType.Food.Phosphorus,
                                        quantity = record.solidUnit toNativeMassFor value,
                                        startDate = record.startTime.toNSDate(),
                                        endDate = record.endTime.toNSDate(),
                                        metadata = mapOf(HKMetadataKeyFoodType to record.mealType.name)
                                    )
                                )
                            }
                            record.polyunsaturatedFat?.let { value ->
                                add(
                                    HKQuantitySample.quantitySampleWithType(
                                        quantityType = ObjectType.Food.FatPolyunsaturated,
                                        quantity = record.solidUnit toNativeMassFor value,
                                        startDate = record.startTime.toNSDate(),
                                        endDate = record.endTime.toNSDate(),
                                        metadata = mapOf(HKMetadataKeyFoodType to record.mealType.name)
                                    )
                                )
                            }
                            record.potassium?.let { value ->
                                add(
                                    HKQuantitySample.quantitySampleWithType(
                                        quantityType = ObjectType.Food.Potassium,
                                        quantity = record.solidUnit toNativeMassFor value,
                                        startDate = record.startTime.toNSDate(),
                                        endDate = record.endTime.toNSDate(),
                                        metadata = mapOf(HKMetadataKeyFoodType to record.mealType.name)
                                    )
                                )
                            }
                            record.protein?.let { value ->
                                add(
                                    HKQuantitySample.quantitySampleWithType(
                                        quantityType = ObjectType.Food.Protein,
                                        quantity = record.solidUnit toNativeMassFor value,
                                        startDate = record.startTime.toNSDate(),
                                        endDate = record.endTime.toNSDate(),
                                        metadata = mapOf(HKMetadataKeyFoodType to record.mealType.name)
                                    )
                                )
                            }
                            record.riboflavin?.let { value ->
                                add(
                                    HKQuantitySample.quantitySampleWithType(
                                        quantityType = ObjectType.Food.Riboflavin,
                                        quantity = record.solidUnit toNativeMassFor value,
                                        startDate = record.startTime.toNSDate(),
                                        endDate = record.endTime.toNSDate(),
                                        metadata = mapOf(HKMetadataKeyFoodType to record.mealType.name)
                                    )
                                )
                            }
                            record.saturatedFat?.let { value ->
                                add(
                                    HKQuantitySample.quantitySampleWithType(
                                        quantityType = ObjectType.Food.FatSaturated,
                                        quantity = record.solidUnit toNativeMassFor value,
                                        startDate = record.startTime.toNSDate(),
                                        endDate = record.endTime.toNSDate(),
                                        metadata = mapOf(HKMetadataKeyFoodType to record.mealType.name)
                                    )
                                )
                            }
                            record.selenium?.let { value ->
                                add(
                                    HKQuantitySample.quantitySampleWithType(
                                        quantityType = ObjectType.Food.Selenium,
                                        quantity = record.solidUnit toNativeMassFor value,
                                        startDate = record.startTime.toNSDate(),
                                        endDate = record.endTime.toNSDate(),
                                        metadata = mapOf(HKMetadataKeyFoodType to record.mealType.name)
                                    )
                                )
                            }
                            record.sodium?.let { value ->
                                add(
                                    HKQuantitySample.quantitySampleWithType(
                                        quantityType = ObjectType.Food.Sodium,
                                        quantity = record.solidUnit toNativeMassFor value,
                                        startDate = record.startTime.toNSDate(),
                                        endDate = record.endTime.toNSDate(),
                                        metadata = mapOf(HKMetadataKeyFoodType to record.mealType.name)
                                    )
                                )
                            }
                            record.sugar?.let { value ->
                                add(
                                    HKQuantitySample.quantitySampleWithType(
                                        quantityType = ObjectType.Food.Sugar,
                                        quantity = record.solidUnit toNativeMassFor value,
                                        startDate = record.startTime.toNSDate(),
                                        endDate = record.endTime.toNSDate(),
                                        metadata = mapOf(HKMetadataKeyFoodType to record.mealType.name)
                                    )
                                )
                            }
                            record.thiamin?.let { value ->
                                add(
                                    HKQuantitySample.quantitySampleWithType(
                                        quantityType = ObjectType.Food.Thiamin,
                                        quantity = record.solidUnit toNativeMassFor value,
                                        startDate = record.startTime.toNSDate(),
                                        endDate = record.endTime.toNSDate(),
                                        metadata = mapOf(HKMetadataKeyFoodType to record.mealType.name)
                                    )
                                )
                            }
                            record.totalCarbohydrate?.let { value ->
                                add(
                                    HKQuantitySample.quantitySampleWithType(
                                        quantityType = ObjectType.Food.Carbohydrates,
                                        quantity = record.solidUnit toNativeMassFor value,
                                        startDate = record.startTime.toNSDate(),
                                        endDate = record.endTime.toNSDate(),
                                        metadata = mapOf(HKMetadataKeyFoodType to record.mealType.name)
                                    )
                                )
                            }
                            record.totalFat?.let { value ->
                                add(
                                    HKQuantitySample.quantitySampleWithType(
                                        quantityType = ObjectType.Food.FatTotal,
                                        quantity = record.solidUnit toNativeMassFor value,
                                        startDate = record.startTime.toNSDate(),
                                        endDate = record.endTime.toNSDate(),
                                        metadata = mapOf(HKMetadataKeyFoodType to record.mealType.name)
                                    )
                                )
                            }
                            record.vitaminA?.let { value ->
                                add(
                                    HKQuantitySample.quantitySampleWithType(
                                        quantityType = ObjectType.Food.VitaminA,
                                        quantity = record.solidUnit toNativeMassFor value,
                                        startDate = record.startTime.toNSDate(),
                                        endDate = record.endTime.toNSDate(),
                                        metadata = mapOf(HKMetadataKeyFoodType to record.mealType.name)
                                    )
                                )
                            }
                            record.vitaminB12?.let { value ->
                                add(
                                    HKQuantitySample.quantitySampleWithType(
                                        quantityType = ObjectType.Food.VitaminB12,
                                        quantity = record.solidUnit toNativeMassFor value,
                                        startDate = record.startTime.toNSDate(),
                                        endDate = record.endTime.toNSDate(),
                                        metadata = mapOf(HKMetadataKeyFoodType to record.mealType.name)
                                    )
                                )
                            }
                            record.vitaminB6?.let { value ->
                                add(
                                    HKQuantitySample.quantitySampleWithType(
                                        quantityType = ObjectType.Food.VitaminB6,
                                        quantity = record.solidUnit toNativeMassFor value,
                                        startDate = record.startTime.toNSDate(),
                                        endDate = record.endTime.toNSDate(),
                                        metadata = mapOf(HKMetadataKeyFoodType to record.mealType.name)
                                    )
                                )
                            }
                            record.vitaminC?.let { value ->
                                add(
                                    HKQuantitySample.quantitySampleWithType(
                                        quantityType = ObjectType.Food.VitaminC,
                                        quantity = record.solidUnit toNativeMassFor value,
                                        startDate = record.startTime.toNSDate(),
                                        endDate = record.endTime.toNSDate(),
                                        metadata = mapOf(HKMetadataKeyFoodType to record.mealType.name)
                                    )
                                )
                            }
                            record.vitaminD?.let { value ->
                                add(
                                    HKQuantitySample.quantitySampleWithType(
                                        quantityType = ObjectType.Food.VitaminD,
                                        quantity = record.solidUnit toNativeMassFor value,
                                        startDate = record.startTime.toNSDate(),
                                        endDate = record.endTime.toNSDate(),
                                        metadata = mapOf(HKMetadataKeyFoodType to record.mealType.name)
                                    )
                                )
                            }
                            record.vitaminE?.let { value ->
                                add(
                                    HKQuantitySample.quantitySampleWithType(
                                        quantityType = ObjectType.Food.VitaminE,
                                        quantity = record.solidUnit toNativeMassFor value,
                                        startDate = record.startTime.toNSDate(),
                                        endDate = record.endTime.toNSDate(),
                                        metadata = mapOf(HKMetadataKeyFoodType to record.mealType.name)
                                    )
                                )
                            }
                            record.vitaminK?.let { value ->
                                add(
                                    HKQuantitySample.quantitySampleWithType(
                                        quantityType = ObjectType.Food.VitaminK,
                                        quantity = record.solidUnit toNativeMassFor value,
                                        startDate = record.startTime.toNSDate(),
                                        endDate = record.endTime.toNSDate(),
                                        metadata = mapOf(HKMetadataKeyFoodType to record.mealType.name)
                                    )
                                )
                            }
                            record.zinc?.let { value ->
                                add(
                                    HKQuantitySample.quantitySampleWithType(
                                        quantityType = ObjectType.Food.Zinc,
                                        quantity = record.solidUnit toNativeMassFor value,
                                        startDate = record.startTime.toNSDate(),
                                        endDate = record.endTime.toNSDate(),
                                        metadata = mapOf(HKMetadataKeyFoodType to record.mealType.name)
                                    )
                                )
                            }
                        }
                    )

                    is KHRecord.OvulationTest -> samples.add(
                        HKCategorySample.categorySampleWithType(
                            type = ObjectType.Category.OvulationTest,
                            value = record.result.toNativeOvulationResult(),
                            startDate = record.time.toNSDate(),
                            endDate = record.time.toNSDate(),
                        ),
                    )

                    is KHRecord.OxygenSaturation -> samples.add(
                        HKQuantitySample.quantitySampleWithType(
                            quantityType = ObjectType.Quantity.OxygenSaturation,
                            quantity = HKQuantity.quantityWithUnit(
                                unit = AppleUnits.percent,
                                doubleValue = record.percentage
                            ),
                            startDate = record.time.toNSDate(),
                            endDate = record.time.toNSDate(),
                        )
                    )

                    is KHRecord.Power -> samples.addAll(
                        record.samples.map { sample ->
                            HKQuantitySample.quantitySampleWithType(
                                quantityType = ObjectType.Quantity.Power,
                                quantity = sample.unit toNativePowerFor sample.value,
                                startDate = sample.time.toNSDate(),
                                endDate = sample.time.toNSDate(),
                            )
                        }
                    )

                    is KHRecord.RespiratoryRate -> samples.add(
                        HKQuantitySample.quantitySampleWithType(
                            quantityType = ObjectType.Quantity.RespiratoryRate,
                            quantity = HKQuantity.quantityWithUnit(
                                unit = AppleUnits.beatsPerMinute,
                                doubleValue = record.rate,
                            ),
                            startDate = record.time.toNSDate(),
                            endDate = record.time.toNSDate(),
                        )
                    )

                    is KHRecord.RestingHeartRate -> samples.add(
                        HKQuantitySample.quantitySampleWithType(
                            quantityType = ObjectType.Quantity.RestingHeartRate,
                            quantity = HKQuantity.quantityWithUnit(
                                unit = AppleUnits.beatsPerMinute,
                                doubleValue = record.beatsPerMinute.toDouble(),
                            ),
                            startDate = record.time.toNSDate(),
                            endDate = record.time.toNSDate(),
                        )
                    )

                    is KHRecord.RunningSpeed -> samples.addAll(
                        record.samples.map { sample ->
                            HKQuantitySample.quantitySampleWithType(
                                quantityType = ObjectType.Quantity.RunningSpeed,
                                quantity = sample.unit toNativeVelocityFor sample.value,
                                startDate = sample.time.toNSDate(),
                                endDate = sample.time.toNSDate(),
                            )
                        }
                    )

                    is KHRecord.SexualActivity -> samples.add(
                        HKCategorySample.categorySampleWithType(
                            type = ObjectType.Category.SexualActivity,
                            value = HKCategoryValueNotApplicable,
                            startDate = record.time.toNSDate(),
                            endDate = record.time.toNSDate(),
                            metadata = mapOf(
                                HKMetadataKeySexualActivityProtectionUsed to record.didUseProtection
                            )
                        )
                    )

                    is KHRecord.SleepSession -> samples.addAll(
                        record.samples.map { sample ->
                            HKCategorySample.categorySampleWithType(
                                type = ObjectType.Category.SleepSession,
                                value = sample.stage.toNativeSleepStage(),
                                startDate = sample.startTime.toNSDate(),
                                endDate = sample.endTime.toNSDate(),
                            )
                        }
                    )

                    is KHRecord.Speed -> Unit

                    is KHRecord.StepCount -> samples.add(
                        HKQuantitySample.quantitySampleWithType(
                            quantityType = ObjectType.Quantity.StepCount,
                            quantity = HKQuantity.quantityWithUnit(
                                unit = AppleUnits.count,
                                doubleValue = record.count.toDouble()
                            ),
                            startDate = record.startTime.toNSDate(),
                            endDate = record.endTime.toNSDate(),
                        )
                    )

                    is KHRecord.Vo2Max -> samples.add(
                        HKQuantitySample.quantitySampleWithType(
                            quantityType = ObjectType.Quantity.Vo2Max,
                            quantity = HKQuantity.quantityWithUnit(
                                unit = AppleUnits.vo2Max,
                                doubleValue = record.vo2MillilitersPerMinuteKilogram
                            ),
                            startDate = record.time.toNSDate(),
                            endDate = record.time.toNSDate(),
                        )
                    )

                    is KHRecord.Weight -> samples.add(
                        HKQuantitySample.quantitySampleWithType(
                            quantityType = ObjectType.Quantity.Weight,
                            quantity = record.unit toNativeMassFor record.value,
                            startDate = record.time.toNSDate(),
                            endDate = record.time.toNSDate(),
                        )
                    )

                    is KHRecord.WheelChairPushes -> samples.add(
                        HKQuantitySample.quantitySampleWithType(
                            quantityType = ObjectType.Quantity.WheelChairPushes,
                            quantity = HKQuantity.quantityWithUnit(
                                unit = AppleUnits.count,
                                doubleValue = record.count.toDouble(),
                            ),
                            startDate = record.startTime.toNSDate(),
                            endDate = record.endTime.toNSDate(),
                        )
                    )
                }
            }

            store.saveObjects(samples) { success, error ->
                when {
                    error != null -> {
                        val exception = error.toException()
                        val parsedException = when {
                            exception.message?.contains(HKNotAuthorizedMessage) == true ->
                                NoWriteAccessException()

                            else -> exception
                        }
                        logError(parsedException, methodName = "writeRecords [NS]")
                        continuation.resume(KHWriteResponse.Failed(parsedException))
                    }

                    success -> continuation.resume(KHWriteResponse.Success)

                    else -> continuation.resume(
                        value = KHWriteResponse.Failed(throwable = NoWriteAccessException())
                    )
                }
            }
        } catch (t: Throwable) {
            logError(t, methodName = "writeRecords")
            continuation.resume(KHWriteResponse.Failed(t))
        }
    }

    @OptIn(UnsafeNumber::class)
    actual suspend fun readRecords(request: KHReadRequest): List<KHRecord> {
        return try {
            val quantitySamplesList = buildList {
                with(request) {
                    when (this) {
                        is KHReadRequest.ActiveCaloriesBurned ->
                            addAll(quantitySamplesFor(ObjectType.Quantity.ActiveCaloriesBurned))

                        is KHReadRequest.BasalMetabolicRate ->
                            addAll(quantitySamplesFor(ObjectType.Quantity.BasalMetabolicRate))

                        is KHReadRequest.BloodGlucose ->
                            addAll(quantitySamplesFor(ObjectType.Quantity.BloodGlucose))

                        is KHReadRequest.BloodPressure -> addAll(
                            quantitySamplesFor(
                                ObjectType.Quantity.BloodPressureSystolic,
                                ObjectType.Quantity.BloodPressureDiastolic
                            )
                        )

                        is KHReadRequest.BodyFat ->
                            addAll(quantitySamplesFor(ObjectType.Quantity.BodyFat))

                        is KHReadRequest.BodyTemperature ->
                            addAll(quantitySamplesFor(ObjectType.Quantity.BodyTemperature))

                        is KHReadRequest.BodyWaterMass -> Unit

                        is KHReadRequest.BoneMass -> Unit

                        is KHReadRequest.CervicalMucus ->
                            addAll(quantitySamplesFor(ObjectType.Category.CervicalMucus))

                        is KHReadRequest.CyclingPedalingCadence -> Unit

                        is KHReadRequest.CyclingSpeed ->
                            addAll(quantitySamplesFor(ObjectType.Quantity.CyclingSpeed))

                        is KHReadRequest.Distance ->
                            addAll(quantitySamplesFor(ObjectType.Quantity.Distance))

                        is KHReadRequest.ElevationGained -> Unit

                        is KHReadRequest.FloorsClimbed ->
                            addAll(quantitySamplesFor(ObjectType.Quantity.FloorsClimbed))

                        is KHReadRequest.HeartRate ->
                            addAll(quantitySamplesFor(ObjectType.Quantity.HeartRate))

                        is KHReadRequest.HeartRateVariability ->
                            addAll(quantitySamplesFor(ObjectType.Quantity.HeartRateVariability))

                        is KHReadRequest.Height ->
                            addAll(quantitySamplesFor(ObjectType.Quantity.Height))

                        is KHReadRequest.Hydration ->
                            addAll(quantitySamplesFor(ObjectType.Quantity.Hydration))

                        is KHReadRequest.IntermenstrualBleeding ->
                            addAll(quantitySamplesFor(ObjectType.Category.IntermenstrualBleeding))

                        is KHReadRequest.LeanBodyMass ->
                            addAll(quantitySamplesFor(ObjectType.Quantity.LeanBodyMass))

                        is KHReadRequest.MenstruationFlow ->
                            addAll(quantitySamplesFor(ObjectType.Category.MenstruationFlow))

                        is KHReadRequest.MenstruationPeriod -> Unit

                        is KHReadRequest.Nutrition -> addAll(
                            quantitySamplesFor(
                                ObjectType.Food.Biotin,
                                ObjectType.Food.Caffeine,
                                ObjectType.Food.Calcium,
                                ObjectType.Food.EnergyConsumed,
                                ObjectType.Food.Chloride,
                                ObjectType.Food.Cholesterol,
                                ObjectType.Food.Chromium,
                                ObjectType.Food.Copper,
                                ObjectType.Food.Fiber,
                                ObjectType.Food.Folate,
                                ObjectType.Food.Iodine,
                                ObjectType.Food.Iron,
                                ObjectType.Food.Magnesium,
                                ObjectType.Food.Manganese,
                                ObjectType.Food.Molybdenum,
                                ObjectType.Food.FatMonounsaturated,
                                ObjectType.Food.Niacin,
                                ObjectType.Food.PantothenicAcid,
                                ObjectType.Food.Phosphorus,
                                ObjectType.Food.FatPolyunsaturated,
                                ObjectType.Food.Potassium,
                                ObjectType.Food.Protein,
                                ObjectType.Food.Riboflavin,
                                ObjectType.Food.FatSaturated,
                                ObjectType.Food.Selenium,
                                ObjectType.Food.Sodium,
                                ObjectType.Food.Sugar,
                                ObjectType.Food.Thiamin,
                                ObjectType.Food.Carbohydrates,
                                ObjectType.Food.FatTotal,
                                ObjectType.Food.VitaminA,
                                ObjectType.Food.VitaminB12,
                                ObjectType.Food.VitaminB6,
                                ObjectType.Food.VitaminC,
                                ObjectType.Food.VitaminD,
                                ObjectType.Food.VitaminE,
                                ObjectType.Food.VitaminK,
                                ObjectType.Food.Zinc,
                            )
                        )

                        is KHReadRequest.OvulationTest ->
                            addAll(quantitySamplesFor(ObjectType.Category.OvulationTest))

                        is KHReadRequest.OxygenSaturation ->
                            addAll(quantitySamplesFor(ObjectType.Quantity.OxygenSaturation))

                        is KHReadRequest.Power ->
                            addAll(quantitySamplesFor(ObjectType.Quantity.Power))

                        is KHReadRequest.RespiratoryRate ->
                            addAll(quantitySamplesFor(ObjectType.Quantity.RespiratoryRate))

                        is KHReadRequest.RestingHeartRate ->
                            addAll(quantitySamplesFor(ObjectType.Quantity.RestingHeartRate))

                        is KHReadRequest.RunningSpeed ->
                            addAll(quantitySamplesFor(ObjectType.Quantity.RunningSpeed))

                        is KHReadRequest.SexualActivity ->
                            addAll(quantitySamplesFor(ObjectType.Category.SexualActivity))

                        is KHReadRequest.SleepSession ->
                            addAll(quantitySamplesFor(ObjectType.Category.SleepSession))

                        is KHReadRequest.Speed -> Unit

                        is KHReadRequest.StepCount ->
                            addAll(quantitySamplesFor(ObjectType.Quantity.StepCount))

                        is KHReadRequest.Vo2Max ->
                            addAll(quantitySamplesFor(ObjectType.Quantity.Vo2Max))

                        is KHReadRequest.Weight ->
                            addAll(quantitySamplesFor(ObjectType.Quantity.Weight))

                        is KHReadRequest.WheelChairPushes ->
                            addAll(quantitySamplesFor(ObjectType.Quantity.WheelChairPushes))
                    }
                }
            }.toTypedArray()

            mergeHKSamples(*quantitySamplesList).mapNotNull { hkSamples ->
                when (request) {
                    is KHReadRequest.ActiveCaloriesBurned -> {
                        val sample = hkSamples.first() as HKQuantitySample
                        KHRecord.ActiveCaloriesBurned(
                            unit = request.unit,
                            value = sample.quantity toDoubleValueFor request.unit,
                            startTime = sample.startDate.toKotlinInstant(),
                            endTime = sample.endDate.toKotlinInstant(),
                        )
                    }


                    is KHReadRequest.BasalMetabolicRate -> {
                        val sample = hkSamples.first() as HKQuantitySample
                        KHRecord.BasalMetabolicRate(
                            unit = request.unit,
                            value = sample.quantity toDoubleValueFor request.unit.right,
                            time = sample.endDate.toKotlinInstant(),
                        )
                    }

                    is KHReadRequest.BloodGlucose -> {
                        val sample = hkSamples.first() as HKQuantitySample
                        KHRecord.BloodGlucose(
                            unit = request.unit,
                            value = sample.quantity toDoubleValueFor request.unit,
                            time = sample.endDate.toKotlinInstant(),
                        )
                    }

                    is KHReadRequest.BloodPressure -> {
                        val samples = hkSamples.filterIsInstance<HKQuantitySample>()
                        KHRecord.BloodPressure(
                            unit = request.unit,
                            systolicValue = samples[0].quantity toDoubleValueFor request.unit,
                            diastolicValue = samples[1].quantity toDoubleValueFor request.unit,
                            time = samples.last().endDate.toKotlinInstant(),
                        )
                    }

                    is KHReadRequest.BodyFat -> {
                        val sample = hkSamples.first() as HKQuantitySample
                        KHRecord.BodyFat(
                            percentage = sample.quantity.doubleValueForUnit(AppleUnits.percent),
                            time = sample.endDate.toKotlinInstant(),
                        )
                    }

                    is KHReadRequest.BodyTemperature -> {
                        val sample = hkSamples.first() as HKQuantitySample
                        KHRecord.BodyTemperature(
                            unit = request.unit,
                            value = sample.quantity toDoubleValueFor request.unit,
                            time = sample.endDate.toKotlinInstant(),
                        )
                    }

                    is KHReadRequest.BodyWaterMass -> null

                    is KHReadRequest.BoneMass -> null

                    is KHReadRequest.CervicalMucus -> {
                        val sample = hkSamples.first() as HKCategorySample
                        KHRecord.CervicalMucus(
                            appearance = sample.value.toKHCervicalMucusAppearance(),
                            time = sample.endDate.toKotlinInstant(),
                        )
                    }

                    is KHReadRequest.CyclingPedalingCadence -> null

                    is KHReadRequest.CyclingSpeed -> KHRecord.CyclingSpeed(
                        samples = hkSamples.filterNotNull().map { hkSample ->
                            val sample = hkSample as HKQuantitySample
                            KHSpeedSample(
                                unit = request.unit,
                                value = sample.quantity toDoubleValueFor request.unit,
                                time = sample.endDate.toKotlinInstant(),
                            )
                        }
                    )

                    is KHReadRequest.Distance -> {
                        val sample = hkSamples.first() as HKQuantitySample
                        KHRecord.Distance(
                            unit = request.unit,
                            value = sample.quantity toDoubleValueFor request.unit,
                            startTime = sample.startDate.toKotlinInstant(),
                            endTime = sample.endDate.toKotlinInstant(),
                        )
                    }

                    is KHReadRequest.ElevationGained -> null

                    is KHReadRequest.FloorsClimbed -> {
                        val sample = hkSamples.first() as HKQuantitySample
                        KHRecord.FloorsClimbed(
                            floors = sample.quantity.doubleValueForUnit(AppleUnits.count),
                            startTime = sample.startDate.toKotlinInstant(),
                            endTime = sample.endDate.toKotlinInstant(),
                        )
                    }

                    is KHReadRequest.HeartRate -> KHRecord.HeartRate(
                        samples = hkSamples.filterIsInstance<HKQuantitySample>().map { sample ->
                            KHHeartRateSample(
                                beatsPerMinute = sample.quantity
                                    .doubleValueForUnit(AppleUnits.beatsPerMinute)
                                    .toLong(),
                                time = sample.endDate.toKotlinInstant(),
                            )
                        }
                    )

                    is KHReadRequest.HeartRateVariability -> {
                        val sample = hkSamples.first() as HKQuantitySample
                        KHRecord.HeartRateVariability(
                            heartRateVariabilityMillis = sample.quantity.doubleValueForUnit(
                                AppleUnits.millisecond
                            ),
                            time = sample.endDate.toKotlinInstant(),
                        )
                    }

                    is KHReadRequest.Height -> {
                        val sample = hkSamples.first() as HKQuantitySample
                        KHRecord.Height(
                            unit = request.unit,
                            value = sample.quantity toDoubleValueFor request.unit,
                            time = sample.endDate.toKotlinInstant(),
                        )
                    }

                    is KHReadRequest.Hydration -> {
                        val sample = hkSamples.first() as HKQuantitySample
                        KHRecord.Hydration(
                            unit = request.unit,
                            value = sample.quantity toDoubleValueFor request.unit,
                            startTime = sample.startDate.toKotlinInstant(),
                            endTime = sample.endDate.toKotlinInstant(),
                        )
                    }

                    is KHReadRequest.IntermenstrualBleeding -> {
                        val sample = hkSamples.first() as HKQuantitySample
                        KHRecord.IntermenstrualBleeding(time = sample.endDate.toKotlinInstant())
                    }

                    is KHReadRequest.LeanBodyMass -> {
                        val sample = hkSamples.first() as HKQuantitySample
                        KHRecord.LeanBodyMass(
                            unit = request.unit,
                            value = sample.quantity toDoubleValueFor request.unit,
                            time = sample.endDate.toKotlinInstant(),
                        )
                    }

                    is KHReadRequest.MenstruationFlow -> {
                        val sample = hkSamples.first() as HKCategorySample
                        KHRecord.MenstruationFlow(
                            type = sample.value.toKHMenstruationFlowType(),
                            time = sample.endDate.toKotlinInstant(),
                        )
                    }

                    is KHReadRequest.MenstruationPeriod -> null

                    is KHReadRequest.Nutrition -> {
                        val quantitySamples = hkSamples.filterIsInstance<HKQuantitySample>()
                        KHRecord.Nutrition(
                            name = null,
                            startTime = request.startTime,
                            endTime = request.endTime,
                            solidUnit = request.solidUnit,
                            energyUnit = request.energyUnit,
                            mealType = KHMealType.valueOf(
                                quantitySamples
                                    .first { it.metadata?.containsKey(HKMetadataKeyFoodType) == true }
                                    .metadata
                                    ?.getValue(HKMetadataKeyFoodType) as String?
                                    ?: KHMealType.Unknown.name
                            ),
                            biotin = quantitySamples
                                .elementAtOrNull(0)
                                ?.quantity?.toDoubleValueFor(request.solidUnit),
                            caffeine = quantitySamples
                                .elementAtOrNull(1)
                                ?.quantity?.toDoubleValueFor(request.solidUnit),
                            calcium = quantitySamples
                                .elementAtOrNull(2)
                                ?.quantity?.toDoubleValueFor(request.solidUnit),
                            chloride = quantitySamples
                                .elementAtOrNull(3)
                                ?.quantity?.toDoubleValueFor(request.solidUnit),
                            cholesterol = quantitySamples
                                .elementAtOrNull(4)
                                ?.quantity?.toDoubleValueFor(request.solidUnit),
                            chromium = quantitySamples
                                .elementAtOrNull(5)
                                ?.quantity?.toDoubleValueFor(request.solidUnit),
                            copper = quantitySamples
                                .elementAtOrNull(6)
                                ?.quantity?.toDoubleValueFor(request.solidUnit),
                            dietaryFiber = quantitySamples
                                .elementAtOrNull(7)
                                ?.quantity?.toDoubleValueFor(request.solidUnit),
                            energy = quantitySamples
                                .elementAtOrNull(8)
                                ?.quantity?.toDoubleValueFor(request.energyUnit),
                            folicAcid = quantitySamples
                                .elementAtOrNull(9)
                                ?.quantity?.toDoubleValueFor(request.solidUnit),
                            iodine = quantitySamples
                                .elementAtOrNull(10)
                                ?.quantity?.toDoubleValueFor(request.solidUnit),
                            iron = quantitySamples
                                .elementAtOrNull(11)
                                ?.quantity?.toDoubleValueFor(request.solidUnit),
                            magnesium = quantitySamples
                                .elementAtOrNull(12)
                                ?.quantity?.toDoubleValueFor(request.solidUnit),
                            manganese = quantitySamples
                                .elementAtOrNull(13)
                                ?.quantity?.toDoubleValueFor(request.solidUnit),
                            molybdenum = quantitySamples
                                .elementAtOrNull(14)
                                ?.quantity?.toDoubleValueFor(request.solidUnit),
                            monounsaturatedFat = quantitySamples
                                .elementAtOrNull(15)
                                ?.quantity?.toDoubleValueFor(request.solidUnit),
                            niacin = quantitySamples
                                .elementAtOrNull(16)
                                ?.quantity?.toDoubleValueFor(request.solidUnit),
                            pantothenicAcid = quantitySamples
                                .elementAtOrNull(17)
                                ?.quantity?.toDoubleValueFor(request.solidUnit),
                            phosphorus = quantitySamples
                                .elementAtOrNull(18)
                                ?.quantity?.toDoubleValueFor(request.solidUnit),
                            polyunsaturatedFat = quantitySamples
                                .elementAtOrNull(19)
                                ?.quantity?.toDoubleValueFor(request.solidUnit),
                            potassium = quantitySamples
                                .elementAtOrNull(20)
                                ?.quantity?.toDoubleValueFor(request.solidUnit),
                            protein = quantitySamples
                                .elementAtOrNull(21)
                                ?.quantity?.toDoubleValueFor(request.solidUnit),
                            riboflavin = quantitySamples
                                .elementAtOrNull(22)
                                ?.quantity?.toDoubleValueFor(request.solidUnit),
                            saturatedFat = quantitySamples
                                .elementAtOrNull(23)
                                ?.quantity?.toDoubleValueFor(request.solidUnit),
                            selenium = quantitySamples
                                .elementAtOrNull(24)
                                ?.quantity?.toDoubleValueFor(request.solidUnit),
                            sodium = quantitySamples
                                .elementAtOrNull(25)
                                ?.quantity?.toDoubleValueFor(request.solidUnit),
                            sugar = quantitySamples
                                .elementAtOrNull(26)
                                ?.quantity?.toDoubleValueFor(request.solidUnit),
                            thiamin = quantitySamples
                                .elementAtOrNull(27)
                                ?.quantity?.toDoubleValueFor(request.solidUnit),
                            totalCarbohydrate = quantitySamples
                                .elementAtOrNull(28)
                                ?.quantity?.toDoubleValueFor(request.solidUnit),
                            totalFat = quantitySamples
                                .elementAtOrNull(29)
                                ?.quantity?.toDoubleValueFor(request.solidUnit),
                            vitaminA = quantitySamples
                                .elementAtOrNull(30)
                                ?.quantity?.toDoubleValueFor(request.solidUnit),
                            vitaminB12 = quantitySamples
                                .elementAtOrNull(31)
                                ?.quantity?.toDoubleValueFor(request.solidUnit),
                            vitaminB6 = quantitySamples
                                .elementAtOrNull(32)
                                ?.quantity?.toDoubleValueFor(request.solidUnit),
                            vitaminC = quantitySamples
                                .elementAtOrNull(33)
                                ?.quantity?.toDoubleValueFor(request.solidUnit),
                            vitaminD = quantitySamples
                                .elementAtOrNull(34)
                                ?.quantity?.toDoubleValueFor(request.solidUnit),
                            vitaminE = quantitySamples
                                .elementAtOrNull(35)
                                ?.quantity?.toDoubleValueFor(request.solidUnit),
                            vitaminK = quantitySamples
                                .elementAtOrNull(36)
                                ?.quantity?.toDoubleValueFor(request.solidUnit),
                            zinc = quantitySamples
                                .elementAtOrNull(37)
                                ?.quantity?.toDoubleValueFor(request.solidUnit),
                        )
                    }

                    is KHReadRequest.OvulationTest -> {
                        val sample = hkSamples.first() as HKCategorySample
                        KHRecord.OvulationTest(
                            result = sample.value.toKHOvulationTestResult(),
                            time = sample.endDate.toKotlinInstant(),
                        )
                    }

                    is KHReadRequest.OxygenSaturation -> {
                        val sample = hkSamples.first() as HKQuantitySample
                        KHRecord.OxygenSaturation(
                            percentage = sample.quantity.doubleValueForUnit(AppleUnits.percent),
                            time = sample.endDate.toKotlinInstant(),
                        )
                    }

                    is KHReadRequest.Power -> KHRecord.Power(
                        samples = hkSamples.filterNotNull().map { hkSample ->
                            val sample = hkSample as HKQuantitySample
                            KHPowerSample(
                                unit = request.unit,
                                value = sample.quantity toDoubleValueFor request.unit,
                                time = sample.endDate.toKotlinInstant(),
                            )
                        }
                    )

                    is KHReadRequest.RespiratoryRate -> {
                        val sample = hkSamples.first() as HKQuantitySample
                        KHRecord.RespiratoryRate(
                            rate = sample.quantity.doubleValueForUnit(AppleUnits.beatsPerMinute),
                            time = sample.endDate.toKotlinInstant(),
                        )
                    }

                    is KHReadRequest.RestingHeartRate -> {
                        val sample = hkSamples.first() as HKQuantitySample
                        KHRecord.RestingHeartRate(
                            beatsPerMinute = sample.quantity
                                .doubleValueForUnit(AppleUnits.beatsPerMinute)
                                .toLong(),
                            time = sample.endDate.toKotlinInstant(),
                        )
                    }

                    is KHReadRequest.RunningSpeed -> KHRecord.RunningSpeed(
                        samples = hkSamples.filterNotNull().map { hkSample ->
                            val sample = hkSample as HKQuantitySample
                            KHSpeedSample(
                                unit = request.unit,
                                value = sample.quantity toDoubleValueFor request.unit,
                                time = sample.endDate.toKotlinInstant(),
                            )
                        }
                    )

                    is KHReadRequest.SexualActivity -> {
                        val sample = hkSamples.first() as HKCategorySample
                        KHRecord.SexualActivity(
                            didUseProtection = sample.metadata?.getValue(
                                HKMetadataKeySexualActivityProtectionUsed
                            ) == true,
                            time = sample.endDate.toKotlinInstant(),
                        )
                    }

                    is KHReadRequest.SleepSession -> KHRecord.SleepSession(
                        samples = hkSamples.filterNotNull().map { hkSample ->
                            val sample = hkSample as HKCategorySample
                            KHSleepStageSample(
                                stage = sample.value.toKHSleepStage(),
                                startTime = sample.startDate.toKotlinInstant(),
                                endTime = sample.endDate.toKotlinInstant(),
                            )
                        }
                    )

                    is KHReadRequest.Speed -> null

                    is KHReadRequest.StepCount -> {
                        val sample = hkSamples.first() as HKQuantitySample
                        KHRecord.StepCount(
                            count = sample.quantity.doubleValueForUnit(AppleUnits.count)
                                .toLong(),
                            startTime = sample.startDate.toKotlinInstant(),
                            endTime = sample.endDate.toKotlinInstant(),
                        )
                    }

                    is KHReadRequest.Vo2Max -> {
                        val sample = hkSamples.first() as HKQuantitySample
                        KHRecord.Vo2Max(
                            vo2MillilitersPerMinuteKilogram = sample.quantity.doubleValueForUnit(
                                AppleUnits.vo2Max
                            ),
                            time = sample.endDate.toKotlinInstant(),
                        )
                    }

                    is KHReadRequest.Weight -> {
                        val sample = hkSamples.first() as HKQuantitySample
                        KHRecord.Weight(
                            unit = request.unit,
                            value = sample.quantity toDoubleValueFor request.unit,
                            time = sample.endDate.toKotlinInstant(),
                        )
                    }

                    is KHReadRequest.WheelChairPushes -> {
                        val sample = hkSamples.first() as HKQuantitySample
                        KHRecord.WheelChairPushes(
                            count = sample
                                .quantity
                                .doubleValueForUnit(AppleUnits.count)
                                .toLong(),
                            startTime = sample.startDate.toKotlinInstant(),
                            endTime = sample.endDate.toKotlinInstant(),
                        )
                    }
                }
            }
        } catch (t: Throwable) {
            logError(throwable = t, methodName = "readRecords")
            emptyList()
        }
    }

    @OptIn(UnsafeNumber::class)
    private suspend fun KHReadRequest.quantitySamplesFor(
        vararg hkObjectTypes: HKSampleType
    ): List<List<HKSample>?> {
        val predicate = HKQuery.predicateForSamplesWithStartDate(
            startDate = startDateTime.toNSDate(),
            endDate = endDateTime.toNSDate(),
            options = HKQueryOptionStrictStartDate
        )
        val limit = HKObjectQueryNoLimit
        val sortDescriptors = listOf(
            NSSortDescriptor.sortDescriptorWithKey(
                HKSampleSortIdentifierStartDate,
                ascending = false
            )
        )

        return hkObjectTypes.map { type ->
            suspendCoroutine { continuation ->
                store.executeQuery(
                    HKSampleQuery(
                        sampleType = type,
                        predicate = predicate,
                        limit = limit,
                        sortDescriptors = sortDescriptors,
                    ) { _, data, error ->
                        error.logToConsole(type)
                        continuation.resume(data?.filterIsInstance<HKQuantitySample>())
                    }
                )
            }
        }
    }

    @OptIn(UnsafeNumber::class)
    private fun NSError?.logToConsole(type: HKSampleType) {
        when {
            this?.code == HKErrorAuthorizationNotDetermined -> logError(
                methodName = "readRecords [NS]",
                message = "Read permission not granted for data type `$type`",
            )

            this != null -> logError(
                throwable = this.toException(),
                methodName = "readRecords [NS]"
            )
        }
    }
}

internal fun NSError.toException() = Exception(this.localizedDescription)
internal const val HKNotAuthorizedMessage = "Authorization is not determined"
