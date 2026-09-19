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
import kotlinx.datetime.toKotlinInstant
import kotlinx.datetime.toNSDate
import platform.Foundation.NSError
import platform.Foundation.NSSortDescriptor
import platform.HealthKit.HKCategorySample
import platform.HealthKit.HKErrorAuthorizationNotDetermined
import platform.HealthKit.HKHealthStore
import platform.HealthKit.HKMetadataKeyFoodType
import platform.HealthKit.HKMetadataKeySexualActivityProtectionUsed
import platform.HealthKit.HKObjectQueryNoLimit
import platform.HealthKit.HKQuantitySample
import platform.HealthKit.HKQuery
import platform.HealthKit.HKQueryOptionStrictStartDate
import platform.HealthKit.HKSample
import platform.HealthKit.HKSampleQuery
import platform.HealthKit.HKSampleSortIdentifierStartDate
import platform.HealthKit.HKSampleType
import platform.HealthKit.HKWorkout
import platform.HealthKit.predicateForSamplesWithStartDate
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

internal object AppleRecordReader {

    fun sampleTypesFor(request: KHReadRequest<*>): List<HKSampleType> = when (request) {
        is KHReadRequest.ActiveCaloriesBurned -> listOf(ObjectType.Quantity.ActiveCaloriesBurned)
        is KHReadRequest.BasalMetabolicRate -> listOf(ObjectType.Quantity.BasalMetabolicRate)
        is KHReadRequest.BloodGlucose -> listOf(ObjectType.Quantity.BloodGlucose)
        is KHReadRequest.BloodPressure -> listOf(
            ObjectType.Quantity.BloodPressureSystolic,
            ObjectType.Quantity.BloodPressureDiastolic
        )
        is KHReadRequest.BodyFat -> listOf(ObjectType.Quantity.BodyFat)
        is KHReadRequest.BodyTemperature -> listOf(ObjectType.Quantity.BodyTemperature)
        is KHReadRequest.BodyWaterMass,
        is KHReadRequest.BoneMass,
        is KHReadRequest.CyclingPedalingCadence,
        is KHReadRequest.ElevationGained,
        is KHReadRequest.MenstruationPeriod,
        is KHReadRequest.Speed -> emptyList()

        is KHReadRequest.CervicalMucus -> listOf(ObjectType.Category.CervicalMucus)
        is KHReadRequest.CyclingSpeed -> listOf(ObjectType.Quantity.CyclingSpeed)
        is KHReadRequest.Distance -> listOf(ObjectType.Quantity.Distance)
        is KHReadRequest.Exercise -> listOf(ObjectType.Exercise)
        is KHReadRequest.FloorsClimbed -> listOf(ObjectType.Quantity.FloorsClimbed)
        is KHReadRequest.HeartRate -> listOf(ObjectType.Quantity.HeartRate)
        is KHReadRequest.HeartRateVariability -> listOf(ObjectType.Quantity.HeartRateVariability)
        is KHReadRequest.Height -> listOf(ObjectType.Quantity.Height)
        is KHReadRequest.Hydration -> listOf(ObjectType.Quantity.Hydration)
        is KHReadRequest.IntermenstrualBleeding -> listOf(ObjectType.Category.IntermenstrualBleeding)
        is KHReadRequest.LeanBodyMass -> listOf(ObjectType.Quantity.LeanBodyMass)
        is KHReadRequest.MenstruationFlow -> listOf(ObjectType.Category.MenstruationFlow)
        is KHReadRequest.Nutrition -> listOf(
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
        is KHReadRequest.OvulationTest -> listOf(ObjectType.Category.OvulationTest)
        is KHReadRequest.OxygenSaturation -> listOf(ObjectType.Quantity.OxygenSaturation)
        is KHReadRequest.Power -> listOf(ObjectType.Quantity.Power)
        is KHReadRequest.RespiratoryRate -> listOf(ObjectType.Quantity.RespiratoryRate)
        is KHReadRequest.RestingHeartRate -> listOf(ObjectType.Quantity.RestingHeartRate)
        is KHReadRequest.RunningSpeed -> listOf(ObjectType.Quantity.RunningSpeed)
        is KHReadRequest.SexualActivity -> listOf(ObjectType.Category.SexualActivity)
        is KHReadRequest.SleepSession -> listOf(ObjectType.Category.SleepSession)
        is KHReadRequest.StepCount -> listOf(ObjectType.Quantity.StepCount)
        is KHReadRequest.Vo2Max -> listOf(ObjectType.Quantity.Vo2Max)
        is KHReadRequest.Weight -> listOf(ObjectType.Quantity.Weight)
        is KHReadRequest.WheelChairPushes -> listOf(ObjectType.Quantity.WheelChairPushes)
    }

    @OptIn(UnsafeNumber::class)
    suspend fun querySamples(
        store: HKHealthStore,
        request: KHReadRequest<*>,
        sampleTypes: List<HKSampleType>
    ): List<List<HKSample>?> {
        val predicate = HKQuery.predicateForSamplesWithStartDate(
            startDate = request.startDateTime.toNSDate(),
            endDate = request.endDateTime.toNSDate(),
            options = HKQueryOptionStrictStartDate
        )
        val limit = HKObjectQueryNoLimit
        val sortDescriptors = listOf(
            NSSortDescriptor.sortDescriptorWithKey(
                HKSampleSortIdentifierStartDate,
                ascending = false
            )
        )

        return sampleTypes.map { type ->
            suspendCoroutine { continuation ->
                store.executeQuery(
                    HKSampleQuery(
                        sampleType = type,
                        predicate = predicate,
                        limit = limit,
                        sortDescriptors = sortDescriptors,
                    ) { _, data, error ->
                        error.logToConsole(type)
                        continuation.resume(data?.filterIsInstance<HKSample>())
                    }
                )
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    @OptIn(UnsafeNumber::class)
    fun <T : KHRecord> mapSamplesToRecord(request: KHReadRequest<T>, hkSamples: List<HKSample?>): T? {
        val record: KHRecord? = when (request) {
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
                    value = sample.quantity toDoubleValueFor request.unit,
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

            is KHReadRequest.Exercise -> {
                val sample = hkSamples.first() as HKWorkout
                sample.workoutActivityType.toKHExerciseTypeOrNull()?.let { exerciseType ->
                    KHRecord.Exercise(
                        startTime = sample.startDate.toKotlinInstant(),
                        endTime = sample.endDate.toKotlinInstant(),
                        type = exerciseType,
                    )
                }
            }

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
                            .firstOrNull { it.metadata?.containsKey(HKMetadataKeyFoodType) == true }
                            ?.metadata
                            ?.getValue(HKMetadataKeyFoodType) as? String
                            ?: KHMealType.Unknown.name
                    ),
                    biotin = quantitySamples.elementAtOrNull(0)?.quantity?.toDoubleValueFor(request.solidUnit),
                    caffeine = quantitySamples.elementAtOrNull(1)?.quantity?.toDoubleValueFor(request.solidUnit),
                    calcium = quantitySamples.elementAtOrNull(2)?.quantity?.toDoubleValueFor(request.solidUnit),
                    energy = quantitySamples.elementAtOrNull(3)?.quantity?.toDoubleValueFor(request.energyUnit),
                    chloride = quantitySamples.elementAtOrNull(4)?.quantity?.toDoubleValueFor(request.solidUnit),
                    cholesterol = quantitySamples.elementAtOrNull(5)?.quantity?.toDoubleValueFor(request.solidUnit),
                    chromium = quantitySamples.elementAtOrNull(6)?.quantity?.toDoubleValueFor(request.solidUnit),
                    copper = quantitySamples.elementAtOrNull(7)?.quantity?.toDoubleValueFor(request.solidUnit),
                    dietaryFiber = quantitySamples.elementAtOrNull(8)?.quantity?.toDoubleValueFor(request.solidUnit),
                    folicAcid = quantitySamples.elementAtOrNull(9)?.quantity?.toDoubleValueFor(request.solidUnit),
                    iodine = quantitySamples.elementAtOrNull(10)?.quantity?.toDoubleValueFor(request.solidUnit),
                    iron = quantitySamples.elementAtOrNull(11)?.quantity?.toDoubleValueFor(request.solidUnit),
                    magnesium = quantitySamples.elementAtOrNull(12)?.quantity?.toDoubleValueFor(request.solidUnit),
                    manganese = quantitySamples.elementAtOrNull(13)?.quantity?.toDoubleValueFor(request.solidUnit),
                    molybdenum = quantitySamples.elementAtOrNull(14)?.quantity?.toDoubleValueFor(request.solidUnit),
                    monounsaturatedFat = quantitySamples.elementAtOrNull(15)?.quantity?.toDoubleValueFor(request.solidUnit),
                    niacin = quantitySamples.elementAtOrNull(16)?.quantity?.toDoubleValueFor(request.solidUnit),
                    pantothenicAcid = quantitySamples.elementAtOrNull(17)?.quantity?.toDoubleValueFor(request.solidUnit),
                    phosphorus = quantitySamples.elementAtOrNull(18)?.quantity?.toDoubleValueFor(request.solidUnit),
                    polyunsaturatedFat = quantitySamples.elementAtOrNull(19)?.quantity?.toDoubleValueFor(request.solidUnit),
                    potassium = quantitySamples.elementAtOrNull(20)?.quantity?.toDoubleValueFor(request.solidUnit),
                    protein = quantitySamples.elementAtOrNull(21)?.quantity?.toDoubleValueFor(request.solidUnit),
                    riboflavin = quantitySamples.elementAtOrNull(22)?.quantity?.toDoubleValueFor(request.solidUnit),
                    saturatedFat = quantitySamples.elementAtOrNull(23)?.quantity?.toDoubleValueFor(request.solidUnit),
                    selenium = quantitySamples.elementAtOrNull(24)?.quantity?.toDoubleValueFor(request.solidUnit),
                    sodium = quantitySamples.elementAtOrNull(25)?.quantity?.toDoubleValueFor(request.solidUnit),
                    sugar = quantitySamples.elementAtOrNull(26)?.quantity?.toDoubleValueFor(request.solidUnit),
                    thiamin = quantitySamples.elementAtOrNull(27)?.quantity?.toDoubleValueFor(request.solidUnit),
                    totalCarbohydrate = quantitySamples.elementAtOrNull(28)?.quantity?.toDoubleValueFor(request.solidUnit),
                    totalFat = quantitySamples.elementAtOrNull(29)?.quantity?.toDoubleValueFor(request.solidUnit),
                    vitaminA = quantitySamples.elementAtOrNull(30)?.quantity?.toDoubleValueFor(request.solidUnit),
                    vitaminB12 = quantitySamples.elementAtOrNull(31)?.quantity?.toDoubleValueFor(request.solidUnit),
                    vitaminB6 = quantitySamples.elementAtOrNull(32)?.quantity?.toDoubleValueFor(request.solidUnit),
                    vitaminC = quantitySamples.elementAtOrNull(33)?.quantity?.toDoubleValueFor(request.solidUnit),
                    vitaminD = quantitySamples.elementAtOrNull(34)?.quantity?.toDoubleValueFor(request.solidUnit),
                    vitaminE = quantitySamples.elementAtOrNull(35)?.quantity?.toDoubleValueFor(request.solidUnit),
                    vitaminK = quantitySamples.elementAtOrNull(36)?.quantity?.toDoubleValueFor(request.solidUnit),
                    zinc = quantitySamples.elementAtOrNull(37)?.quantity?.toDoubleValueFor(request.solidUnit),
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
        return record as? T
    }

    suspend fun <T : KHRecord> readRecords(
        store: HKHealthStore,
        request: KHReadRequest<T>
    ): List<T> {
        val sampleTypes = sampleTypesFor(request)
        if (sampleTypes.isEmpty()) return emptyList()

        val rawSampleLists = querySamples(store, request, sampleTypes)
        val mergedSamples = mergeHKSamples(*rawSampleLists.toTypedArray())
        return mergedSamples.mapNotNull { samplesGroup ->
            mapSamplesToRecord(request, samplesGroup)
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
