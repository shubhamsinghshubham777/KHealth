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
import kotlinx.datetime.toNSDate
import platform.HealthKit.HKCategorySample
import platform.HealthKit.HKCategoryValueNotApplicable
import platform.HealthKit.HKMetadataKeyFoodType
import platform.HealthKit.HKMetadataKeyMenstrualCycleStart
import platform.HealthKit.HKMetadataKeySexualActivityProtectionUsed
import platform.HealthKit.HKQuantity
import platform.HealthKit.HKQuantitySample
import platform.HealthKit.HKSample
import platform.HealthKit.HKWorkout

internal object AppleRecordWriter {
    @OptIn(UnsafeNumber::class)
    fun toSamples(record: KHRecord): List<HKSample> = buildList {
        when (record) {
            is KHRecord.ActiveCaloriesBurned -> add(
                HKQuantitySample.quantitySampleWithType(
                    quantityType = ObjectType.Quantity.ActiveCaloriesBurned,
                    quantity = record.unit toNativeEnergyFor record.value,
                    startDate = record.startTime.toNSDate(),
                    endDate = record.endTime.toNSDate(),
                )
            )

            is KHRecord.BasalMetabolicRate -> add(
                HKQuantitySample.quantitySampleWithType(
                    quantityType = ObjectType.Quantity.BasalMetabolicRate,
                    quantity = record.unit toNativeEnergyFor record.value,
                    startDate = record.time.toNSDate(),
                    endDate = record.time.toNSDate(),
                )
            )

            is KHRecord.BloodGlucose -> add(
                HKQuantitySample.quantitySampleWithType(
                    quantityType = ObjectType.Quantity.BloodGlucose,
                    quantity = record.unit toNativeBloodGlucoseFor record.value,
                    startDate = record.time.toNSDate(),
                    endDate = record.time.toNSDate(),
                )
            )

            is KHRecord.BloodPressure -> {
                add(
                    HKQuantitySample.quantitySampleWithType(
                        quantityType = ObjectType.Quantity.BloodPressureSystolic,
                        quantity = record.unit toNativePressureFor record.systolicValue,
                        startDate = record.time.toNSDate(),
                        endDate = record.time.toNSDate(),
                    )
                )
                add(
                    HKQuantitySample.quantitySampleWithType(
                        quantityType = ObjectType.Quantity.BloodPressureDiastolic,
                        quantity = record.unit toNativePressureFor record.diastolicValue,
                        startDate = record.time.toNSDate(),
                        endDate = record.time.toNSDate(),
                    )
                )
            }

            is KHRecord.BodyFat -> add(
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

            is KHRecord.BodyTemperature -> add(
                HKQuantitySample.quantitySampleWithType(
                    quantityType = ObjectType.Quantity.BodyTemperature,
                    quantity = record.unit toNativeTemperatureFor record.value,
                    startDate = record.time.toNSDate(),
                    endDate = record.time.toNSDate(),
                )
            )

            is KHRecord.BodyWaterMass -> Unit
            is KHRecord.BoneMass -> Unit

            is KHRecord.CervicalMucus -> add(
                HKCategorySample.categorySampleWithType(
                    type = ObjectType.Category.CervicalMucus,
                    value = record.appearance.toNativeCervicalMucusQuality(),
                    startDate = record.time.toNSDate(),
                    endDate = record.time.toNSDate(),
                )
            )

            is KHRecord.CyclingPedalingCadence -> Unit

            is KHRecord.CyclingSpeed -> addAll(
                record.samples.map { sample ->
                    HKQuantitySample.quantitySampleWithType(
                        quantityType = ObjectType.Quantity.CyclingSpeed,
                        quantity = sample.unit toNativeVelocityFor sample.value,
                        startDate = sample.time.toNSDate(),
                        endDate = sample.time.toNSDate(),
                    )
                }
            )

            is KHRecord.Distance -> add(
                HKQuantitySample.quantitySampleWithType(
                    quantityType = ObjectType.Quantity.Distance,
                    quantity = record.unit toNativeLengthFor record.value,
                    startDate = record.startTime.toNSDate(),
                    endDate = record.endTime.toNSDate(),
                )
            )

            is KHRecord.ElevationGained -> Unit

            is KHRecord.Exercise -> record.type
                .toNativeExerciseTypeOrNull()
                ?.let { exerciseType ->
                    add(
                        HKWorkout.workoutWithActivityType(
                            workoutActivityType = exerciseType,
                            startDate = record.startTime.toNSDate(),
                            endDate = record.endTime.toNSDate(),
                        )
                    )
                }

            is KHRecord.FloorsClimbed -> add(
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

            is KHRecord.HeartRate -> addAll(
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

            is KHRecord.HeartRateVariability -> add(
                HKQuantitySample.quantitySampleWithType(
                    quantityType = ObjectType.Quantity.HeartRateVariability,
                    quantity = HKQuantity.quantityWithUnit(
                        unit = AppleUnits.millisecond,
                        doubleValue = record.heartRateVariabilityMillis
                    ),
                    startDate = record.time.toNSDate(),
                    endDate = record.time.toNSDate(),
                )
            )

            is KHRecord.Height -> add(
                HKQuantitySample.quantitySampleWithType(
                    quantityType = ObjectType.Quantity.Height,
                    quantity = record.unit toNativeLengthFor record.value,
                    startDate = record.time.toNSDate(),
                    endDate = record.time.toNSDate(),
                )
            )

            is KHRecord.Hydration -> add(
                HKQuantitySample.quantitySampleWithType(
                    quantityType = ObjectType.Quantity.Hydration,
                    quantity = record.unit toNativeVolumeFor record.value,
                    startDate = record.startTime.toNSDate(),
                    endDate = record.endTime.toNSDate(),
                )
            )

            is KHRecord.IntermenstrualBleeding -> add(
                HKCategorySample.categorySampleWithType(
                    type = ObjectType.Category.IntermenstrualBleeding,
                    value = HKCategoryValueNotApplicable,
                    startDate = record.time.toNSDate(),
                    endDate = record.time.toNSDate(),
                )
            )

            is KHRecord.LeanBodyMass -> add(
                HKQuantitySample.quantitySampleWithType(
                    quantityType = ObjectType.Quantity.LeanBodyMass,
                    quantity = record.unit toNativeMassFor record.value,
                    startDate = record.time.toNSDate(),
                    endDate = record.time.toNSDate(),
                )
            )

            is KHRecord.MenstruationFlow -> add(
                HKCategorySample.categorySampleWithType(
                    type = ObjectType.Category.MenstruationFlow,
                    value = record.type.toNativeMenstrualFlow(),
                    startDate = record.time.toNSDate(),
                    endDate = record.time.toNSDate(),
                    metadata = mapOf(HKMetadataKeyMenstrualCycleStart to record.isStartOfCycle)
                )
            )

            is KHRecord.MenstruationPeriod -> Unit

            is KHRecord.Nutrition -> {
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

            is KHRecord.OvulationTest -> add(
                HKCategorySample.categorySampleWithType(
                    type = ObjectType.Category.OvulationTest,
                    value = record.result.toNativeOvulationResult(),
                    startDate = record.time.toNSDate(),
                    endDate = record.time.toNSDate(),
                )
            )

            is KHRecord.OxygenSaturation -> add(
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

            is KHRecord.Power -> addAll(
                record.samples.map { sample ->
                    HKQuantitySample.quantitySampleWithType(
                        quantityType = ObjectType.Quantity.Power,
                        quantity = sample.unit toNativePowerFor sample.value,
                        startDate = sample.time.toNSDate(),
                        endDate = sample.time.toNSDate(),
                    )
                }
            )

            is KHRecord.RespiratoryRate -> add(
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

            is KHRecord.RestingHeartRate -> add(
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

            is KHRecord.RunningSpeed -> addAll(
                record.samples.map { sample ->
                    HKQuantitySample.quantitySampleWithType(
                        quantityType = ObjectType.Quantity.RunningSpeed,
                        quantity = sample.unit toNativeVelocityFor sample.value,
                        startDate = sample.time.toNSDate(),
                        endDate = sample.time.toNSDate(),
                    )
                }
            )

            is KHRecord.SexualActivity -> add(
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

            is KHRecord.SleepSession -> addAll(
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

            is KHRecord.StepCount -> add(
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

            is KHRecord.Vo2Max -> add(
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

            is KHRecord.Weight -> add(
                HKQuantitySample.quantitySampleWithType(
                    quantityType = ObjectType.Quantity.Weight,
                    quantity = record.unit toNativeMassFor record.value,
                    startDate = record.time.toNSDate(),
                    endDate = record.time.toNSDate(),
                )
            )

            is KHRecord.WheelChairPushes -> add(
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
}
