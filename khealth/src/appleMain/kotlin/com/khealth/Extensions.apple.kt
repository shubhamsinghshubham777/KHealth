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
import platform.HealthKit.HKCategoryTypeIdentifierCervicalMucusQuality
import platform.HealthKit.HKCategoryTypeIdentifierIntermenstrualBleeding
import platform.HealthKit.HKCategoryTypeIdentifierMenstrualFlow
import platform.HealthKit.HKCategoryTypeIdentifierOvulationTestResult
import platform.HealthKit.HKCategoryTypeIdentifierSexualActivity
import platform.HealthKit.HKCategoryTypeIdentifierSleepAnalysis
import platform.HealthKit.HKCategoryValueCervicalMucusQuality
import platform.HealthKit.HKCategoryValueCervicalMucusQualityCreamy
import platform.HealthKit.HKCategoryValueCervicalMucusQualityDry
import platform.HealthKit.HKCategoryValueCervicalMucusQualityEggWhite
import platform.HealthKit.HKCategoryValueCervicalMucusQualitySticky
import platform.HealthKit.HKCategoryValueCervicalMucusQualityWatery
import platform.HealthKit.HKCategoryValueMenstrualFlowHeavy
import platform.HealthKit.HKCategoryValueMenstrualFlowLight
import platform.HealthKit.HKCategoryValueMenstrualFlowMedium
import platform.HealthKit.HKCategoryValueMenstrualFlowUnspecified
import platform.HealthKit.HKCategoryValueOvulationTestResultEstrogenSurge
import platform.HealthKit.HKCategoryValueOvulationTestResultIndeterminate
import platform.HealthKit.HKCategoryValueOvulationTestResultNegative
import platform.HealthKit.HKCategoryValueOvulationTestResultPositive
import platform.HealthKit.HKCategoryValueSleepAnalysisAsleepCore
import platform.HealthKit.HKCategoryValueSleepAnalysisAsleepDeep
import platform.HealthKit.HKCategoryValueSleepAnalysisAsleepREM
import platform.HealthKit.HKCategoryValueSleepAnalysisAsleepUnspecified
import platform.HealthKit.HKCategoryValueSleepAnalysisAwake
import platform.HealthKit.HKMetricPrefixDeci
import platform.HealthKit.HKMetricPrefixKilo
import platform.HealthKit.HKMetricPrefixMilli
import platform.HealthKit.HKObjectType
import platform.HealthKit.HKQuantity
import platform.HealthKit.HKQuantityType
import platform.HealthKit.HKQuantityTypeIdentifierActiveEnergyBurned
import platform.HealthKit.HKQuantityTypeIdentifierBasalEnergyBurned
import platform.HealthKit.HKQuantityTypeIdentifierBloodGlucose
import platform.HealthKit.HKQuantityTypeIdentifierBloodPressureDiastolic
import platform.HealthKit.HKQuantityTypeIdentifierBloodPressureSystolic
import platform.HealthKit.HKQuantityTypeIdentifierBodyFatPercentage
import platform.HealthKit.HKQuantityTypeIdentifierBodyMass
import platform.HealthKit.HKQuantityTypeIdentifierBodyTemperature
import platform.HealthKit.HKQuantityTypeIdentifierCyclingPower
import platform.HealthKit.HKQuantityTypeIdentifierCyclingSpeed
import platform.HealthKit.HKQuantityTypeIdentifierDietaryBiotin
import platform.HealthKit.HKQuantityTypeIdentifierDietaryCaffeine
import platform.HealthKit.HKQuantityTypeIdentifierDietaryCalcium
import platform.HealthKit.HKQuantityTypeIdentifierDietaryCarbohydrates
import platform.HealthKit.HKQuantityTypeIdentifierDietaryChloride
import platform.HealthKit.HKQuantityTypeIdentifierDietaryCholesterol
import platform.HealthKit.HKQuantityTypeIdentifierDietaryChromium
import platform.HealthKit.HKQuantityTypeIdentifierDietaryCopper
import platform.HealthKit.HKQuantityTypeIdentifierDietaryEnergyConsumed
import platform.HealthKit.HKQuantityTypeIdentifierDietaryFatMonounsaturated
import platform.HealthKit.HKQuantityTypeIdentifierDietaryFatPolyunsaturated
import platform.HealthKit.HKQuantityTypeIdentifierDietaryFatSaturated
import platform.HealthKit.HKQuantityTypeIdentifierDietaryFatTotal
import platform.HealthKit.HKQuantityTypeIdentifierDietaryFiber
import platform.HealthKit.HKQuantityTypeIdentifierDietaryFolate
import platform.HealthKit.HKQuantityTypeIdentifierDietaryIodine
import platform.HealthKit.HKQuantityTypeIdentifierDietaryIron
import platform.HealthKit.HKQuantityTypeIdentifierDietaryMagnesium
import platform.HealthKit.HKQuantityTypeIdentifierDietaryManganese
import platform.HealthKit.HKQuantityTypeIdentifierDietaryMolybdenum
import platform.HealthKit.HKQuantityTypeIdentifierDietaryNiacin
import platform.HealthKit.HKQuantityTypeIdentifierDietaryPantothenicAcid
import platform.HealthKit.HKQuantityTypeIdentifierDietaryPhosphorus
import platform.HealthKit.HKQuantityTypeIdentifierDietaryPotassium
import platform.HealthKit.HKQuantityTypeIdentifierDietaryProtein
import platform.HealthKit.HKQuantityTypeIdentifierDietaryRiboflavin
import platform.HealthKit.HKQuantityTypeIdentifierDietarySelenium
import platform.HealthKit.HKQuantityTypeIdentifierDietarySodium
import platform.HealthKit.HKQuantityTypeIdentifierDietarySugar
import platform.HealthKit.HKQuantityTypeIdentifierDietaryThiamin
import platform.HealthKit.HKQuantityTypeIdentifierDietaryVitaminA
import platform.HealthKit.HKQuantityTypeIdentifierDietaryVitaminB12
import platform.HealthKit.HKQuantityTypeIdentifierDietaryVitaminB6
import platform.HealthKit.HKQuantityTypeIdentifierDietaryVitaminC
import platform.HealthKit.HKQuantityTypeIdentifierDietaryVitaminD
import platform.HealthKit.HKQuantityTypeIdentifierDietaryVitaminE
import platform.HealthKit.HKQuantityTypeIdentifierDietaryVitaminK
import platform.HealthKit.HKQuantityTypeIdentifierDietaryWater
import platform.HealthKit.HKQuantityTypeIdentifierDietaryZinc
import platform.HealthKit.HKQuantityTypeIdentifierDistanceWalkingRunning
import platform.HealthKit.HKQuantityTypeIdentifierFlightsClimbed
import platform.HealthKit.HKQuantityTypeIdentifierHeartRate
import platform.HealthKit.HKQuantityTypeIdentifierHeartRateVariabilitySDNN
import platform.HealthKit.HKQuantityTypeIdentifierHeight
import platform.HealthKit.HKQuantityTypeIdentifierLeanBodyMass
import platform.HealthKit.HKQuantityTypeIdentifierOxygenSaturation
import platform.HealthKit.HKQuantityTypeIdentifierPushCount
import platform.HealthKit.HKQuantityTypeIdentifierRespiratoryRate
import platform.HealthKit.HKQuantityTypeIdentifierRestingHeartRate
import platform.HealthKit.HKQuantityTypeIdentifierRunningSpeed
import platform.HealthKit.HKQuantityTypeIdentifierStepCount
import platform.HealthKit.HKQuantityTypeIdentifierVO2Max
import platform.HealthKit.HKSample
import platform.HealthKit.HKUnit
import platform.HealthKit.HKUnitMolarMassBloodGlucose
import platform.HealthKit.HKWorkoutActivityType
import platform.HealthKit.HKWorkoutActivityTypeAmericanFootball
import platform.HealthKit.HKWorkoutActivityTypeArchery
import platform.HealthKit.HKWorkoutActivityTypeAustralianFootball
import platform.HealthKit.HKWorkoutActivityTypeBadminton
import platform.HealthKit.HKWorkoutActivityTypeBarre
import platform.HealthKit.HKWorkoutActivityTypeBaseball
import platform.HealthKit.HKWorkoutActivityTypeBasketball
import platform.HealthKit.HKWorkoutActivityTypeBowling
import platform.HealthKit.HKWorkoutActivityTypeBoxing
import platform.HealthKit.HKWorkoutActivityTypeCardioDance
import platform.HealthKit.HKWorkoutActivityTypeClimbing
import platform.HealthKit.HKWorkoutActivityTypeCooldown
import platform.HealthKit.HKWorkoutActivityTypeCoreTraining
import platform.HealthKit.HKWorkoutActivityTypeCricket
import platform.HealthKit.HKWorkoutActivityTypeCrossCountrySkiing
import platform.HealthKit.HKWorkoutActivityTypeCrossTraining
import platform.HealthKit.HKWorkoutActivityTypeCurling
import platform.HealthKit.HKWorkoutActivityTypeCycling
import platform.HealthKit.HKWorkoutActivityTypeDance
import platform.HealthKit.HKWorkoutActivityTypeDanceInspiredTraining
import platform.HealthKit.HKWorkoutActivityTypeDiscSports
import platform.HealthKit.HKWorkoutActivityTypeDownhillSkiing
import platform.HealthKit.HKWorkoutActivityTypeElliptical
import platform.HealthKit.HKWorkoutActivityTypeEquestrianSports
import platform.HealthKit.HKWorkoutActivityTypeFencing
import platform.HealthKit.HKWorkoutActivityTypeFishing
import platform.HealthKit.HKWorkoutActivityTypeFitnessGaming
import platform.HealthKit.HKWorkoutActivityTypeFlexibility
import platform.HealthKit.HKWorkoutActivityTypeFunctionalStrengthTraining
import platform.HealthKit.HKWorkoutActivityTypeGolf
import platform.HealthKit.HKWorkoutActivityTypeGymnastics
import platform.HealthKit.HKWorkoutActivityTypeHandCycling
import platform.HealthKit.HKWorkoutActivityTypeHandball
import platform.HealthKit.HKWorkoutActivityTypeHighIntensityIntervalTraining
import platform.HealthKit.HKWorkoutActivityTypeHiking
import platform.HealthKit.HKWorkoutActivityTypeHockey
import platform.HealthKit.HKWorkoutActivityTypeHunting
import platform.HealthKit.HKWorkoutActivityTypeJumpRope
import platform.HealthKit.HKWorkoutActivityTypeKickboxing
import platform.HealthKit.HKWorkoutActivityTypeLacrosse
import platform.HealthKit.HKWorkoutActivityTypeMartialArts
import platform.HealthKit.HKWorkoutActivityTypeMindAndBody
import platform.HealthKit.HKWorkoutActivityTypeMixedCardio
import platform.HealthKit.HKWorkoutActivityTypeMixedMetabolicCardioTraining
import platform.HealthKit.HKWorkoutActivityTypeOther
import platform.HealthKit.HKWorkoutActivityTypePaddleSports
import platform.HealthKit.HKWorkoutActivityTypePickleball
import platform.HealthKit.HKWorkoutActivityTypePilates
import platform.HealthKit.HKWorkoutActivityTypePlay
import platform.HealthKit.HKWorkoutActivityTypePreparationAndRecovery
import platform.HealthKit.HKWorkoutActivityTypeRacquetball
import platform.HealthKit.HKWorkoutActivityTypeRowing
import platform.HealthKit.HKWorkoutActivityTypeRugby
import platform.HealthKit.HKWorkoutActivityTypeRunning
import platform.HealthKit.HKWorkoutActivityTypeSailing
import platform.HealthKit.HKWorkoutActivityTypeSkatingSports
import platform.HealthKit.HKWorkoutActivityTypeSnowSports
import platform.HealthKit.HKWorkoutActivityTypeSnowboarding
import platform.HealthKit.HKWorkoutActivityTypeSoccer
import platform.HealthKit.HKWorkoutActivityTypeSocialDance
import platform.HealthKit.HKWorkoutActivityTypeSoftball
import platform.HealthKit.HKWorkoutActivityTypeSquash
import platform.HealthKit.HKWorkoutActivityTypeStairClimbing
import platform.HealthKit.HKWorkoutActivityTypeStairs
import platform.HealthKit.HKWorkoutActivityTypeStepTraining
import platform.HealthKit.HKWorkoutActivityTypeSurfingSports
import platform.HealthKit.HKWorkoutActivityTypeSwimBikeRun
import platform.HealthKit.HKWorkoutActivityTypeSwimming
import platform.HealthKit.HKWorkoutActivityTypeTableTennis
import platform.HealthKit.HKWorkoutActivityTypeTaiChi
import platform.HealthKit.HKWorkoutActivityTypeTennis
import platform.HealthKit.HKWorkoutActivityTypeTrackAndField
import platform.HealthKit.HKWorkoutActivityTypeTraditionalStrengthTraining
import platform.HealthKit.HKWorkoutActivityTypeTransition
import platform.HealthKit.HKWorkoutActivityTypeUnderwaterDiving
import platform.HealthKit.HKWorkoutActivityTypeVolleyball
import platform.HealthKit.HKWorkoutActivityTypeWalking
import platform.HealthKit.HKWorkoutActivityTypeWaterFitness
import platform.HealthKit.HKWorkoutActivityTypeWaterPolo
import platform.HealthKit.HKWorkoutActivityTypeWaterSports
import platform.HealthKit.HKWorkoutActivityTypeWheelchairRunPace
import platform.HealthKit.HKWorkoutActivityTypeWheelchairWalkPace
import platform.HealthKit.HKWorkoutActivityTypeWrestling
import platform.HealthKit.HKWorkoutActivityTypeYoga
import platform.HealthKit.countUnit
import platform.HealthKit.dayUnit
import platform.HealthKit.degreeCelsiusUnit
import platform.HealthKit.degreeFahrenheitUnit
import platform.HealthKit.fluidOunceUSUnit
import platform.HealthKit.gramUnit
import platform.HealthKit.gramUnitWithMetricPrefix
import platform.HealthKit.hourUnit
import platform.HealthKit.inchUnit
import platform.HealthKit.jouleUnit
import platform.HealthKit.jouleUnitWithMetricPrefix
import platform.HealthKit.kilocalorieUnit
import platform.HealthKit.largeCalorieUnit
import platform.HealthKit.literUnit
import platform.HealthKit.literUnitWithMetricPrefix
import platform.HealthKit.meterUnit
import platform.HealthKit.meterUnitWithMetricPrefix
import platform.HealthKit.mileUnit
import platform.HealthKit.millimeterOfMercuryUnit
import platform.HealthKit.minuteUnit
import platform.HealthKit.moleUnitWithMetricPrefix
import platform.HealthKit.ounceUnit
import platform.HealthKit.percentUnit
import platform.HealthKit.poundUnit
import platform.HealthKit.secondUnit
import platform.HealthKit.secondUnitWithMetricPrefix
import platform.HealthKit.smallCalorieUnit
import platform.HealthKit.unitDividedByUnit
import platform.HealthKit.wattUnit
import platform.darwin.NSInteger

@OptIn(UnsafeNumber::class)
internal object AppleUnits {
    val millimolesPerLiter = HKUnit
        .moleUnitWithMetricPrefix(HKMetricPrefixMilli, HKUnitMolarMassBloodGlucose)
        .unitDividedByUnit(HKUnit.literUnit())

    val milligramsPerDeciliter = HKUnit
        .gramUnitWithMetricPrefix(HKMetricPrefixMilli)
        .unitDividedByUnit(HKUnit.literUnitWithMetricPrefix(HKMetricPrefixDeci))

    val calorie = HKUnit.smallCalorieUnit()
    val joule = HKUnit.jouleUnit()
    val kilocalorie = HKUnit.largeCalorieUnit()
    val kilojoule = HKUnit.jouleUnitWithMetricPrefix(HKMetricPrefixKilo)
    val inch = HKUnit.inchUnit()
    val meter = HKUnit.meterUnit()
    val mile = HKUnit.mileUnit()
    val gram = HKUnit.gramUnit()
    val ounce = HKUnit.ounceUnit()
    val pound = HKUnit.poundUnit()

    val kilometersPerHour = HKUnit
        .meterUnitWithMetricPrefix(HKMetricPrefixKilo)
        .unitDividedByUnit(HKUnit.hourUnit())

    val metersPerSecond = HKUnit.meterUnit().unitDividedByUnit(HKUnit.secondUnit())
    val milesPerHour = HKUnit.mileUnit().unitDividedByUnit(HKUnit.hourUnit())
    val millimeterOfMercury = HKUnit.millimeterOfMercuryUnit()
    val celsius = HKUnit.degreeCelsiusUnit()
    val fahrenheit = HKUnit.degreeFahrenheitUnit()
    val beatsPerMinute = HKUnit.countUnit().unitDividedByUnit(HKUnit.minuteUnit())
    val millisecond = HKUnit.secondUnitWithMetricPrefix(HKMetricPrefixMilli)
    val fluidOunceUS = HKUnit.fluidOunceUSUnit()
    val liter = HKUnit.literUnit()
    val percent = HKUnit.percentUnit()
    val kilocaloriePerDay = HKUnit.kilocalorieUnit().unitDividedByUnit(HKUnit.dayUnit())
    val watt = HKUnit.wattUnit()
    val count = HKUnit.countUnit()
    val vo2Max = HKUnit.unitFromString("ml/kg*min")
}

internal infix fun KHUnit.BloodGlucose.toNativeBloodGlucoseFor(value: Double): HKQuantity {
    return when (this) {
        is KHUnit.BloodGlucose.MillimolesPerLiter -> HKQuantity.quantityWithUnit(
            unit = AppleUnits.millimolesPerLiter,
            doubleValue = value
        )

        is KHUnit.BloodGlucose.MilligramsPerDeciliter -> HKQuantity.quantityWithUnit(
            unit = AppleUnits.milligramsPerDeciliter,
            doubleValue = value
        )
    }
}

internal infix fun HKQuantity.toDoubleValueFor(glucose: KHUnit.BloodGlucose): Double {
    return when (glucose) {
        KHUnit.BloodGlucose.MilligramsPerDeciliter -> doubleValueForUnit(AppleUnits.millimolesPerLiter)
        KHUnit.BloodGlucose.MillimolesPerLiter -> doubleValueForUnit(AppleUnits.milligramsPerDeciliter)
    }
}

internal infix fun KHUnit.Energy.toNativeEnergyFor(value: Double): HKQuantity = when (this) {
    is KHUnit.Energy.Calorie ->
        HKQuantity.quantityWithUnit(unit = AppleUnits.calorie, doubleValue = value)

    is KHUnit.Energy.Joule ->
        HKQuantity.quantityWithUnit(unit = AppleUnits.joule, doubleValue = value)

    is KHUnit.Energy.KiloCalorie ->
        HKQuantity.quantityWithUnit(unit = AppleUnits.kilocalorie, doubleValue = value)

    is KHUnit.Energy.KiloJoule ->
        HKQuantity.quantityWithUnit(unit = AppleUnits.kilojoule, doubleValue = value)
}

internal infix fun HKQuantity.toDoubleValueFor(energy: KHUnit.Energy): Double = when (energy) {
    KHUnit.Energy.Calorie -> this.doubleValueForUnit(AppleUnits.calorie)
    KHUnit.Energy.Joule -> this.doubleValueForUnit(AppleUnits.joule)
    KHUnit.Energy.KiloCalorie -> this.doubleValueForUnit(AppleUnits.kilocalorie)
    KHUnit.Energy.KiloJoule -> this.doubleValueForUnit(AppleUnits.kilojoule)
}

internal infix fun KHUnit.Length.toNativeLengthFor(value: Double): HKQuantity = when (this) {
    is KHUnit.Length.Inch -> HKQuantity.quantityWithUnit(
        unit = AppleUnits.inch,
        doubleValue = value
    )

    is KHUnit.Length.Meter -> HKQuantity.quantityWithUnit(
        unit = AppleUnits.meter,
        doubleValue = value
    )

    is KHUnit.Length.Mile -> HKQuantity.quantityWithUnit(
        unit = AppleUnits.mile,
        doubleValue = value
    )
}

internal infix fun HKQuantity.toDoubleValueFor(length: KHUnit.Length): Double = when (length) {
    KHUnit.Length.Inch -> doubleValueForUnit(AppleUnits.inch)
    KHUnit.Length.Meter -> doubleValueForUnit(AppleUnits.meter)
    KHUnit.Length.Mile -> doubleValueForUnit(AppleUnits.mile)
}

internal infix fun KHUnit.Mass.toNativeMassFor(value: Double): HKQuantity = when (this) {
    is KHUnit.Mass.Gram -> HKQuantity.quantityWithUnit(unit = AppleUnits.gram, doubleValue = value)
    is KHUnit.Mass.Ounce -> HKQuantity.quantityWithUnit(
        unit = AppleUnits.ounce,
        doubleValue = value
    )

    is KHUnit.Mass.Pound -> HKQuantity.quantityWithUnit(
        unit = AppleUnits.pound,
        doubleValue = value
    )
}

internal infix fun HKQuantity.toDoubleValueFor(mass: KHUnit.Mass): Double = when (mass) {
    KHUnit.Mass.Gram -> doubleValueForUnit(AppleUnits.gram)
    KHUnit.Mass.Ounce -> doubleValueForUnit(AppleUnits.ounce)
    KHUnit.Mass.Pound -> doubleValueForUnit(AppleUnits.pound)
}

internal infix fun KHUnit.Power.toNativePowerFor(value: Double): HKQuantity = when (this) {
    is KHUnit.Power.KilocaloriePerDay -> HKQuantity.quantityWithUnit(
        unit = AppleUnits.kilocaloriePerDay,
        doubleValue = value,
    )

    is KHUnit.Power.Watt -> HKQuantity.quantityWithUnit(
        unit = AppleUnits.watt,
        doubleValue = value,
    )
}

internal infix fun HKQuantity.toDoubleValueFor(power: KHUnit.Power): Double = when (power) {
    KHUnit.Power.KilocaloriePerDay -> doubleValueForUnit(AppleUnits.kilocaloriePerDay)
    KHUnit.Power.Watt -> doubleValueForUnit(AppleUnits.watt)
}

internal infix fun KHUnit.Temperature.toNativeTemperatureFor(value: Double): HKQuantity {
    return when (this) {
        is KHUnit.Temperature.Celsius ->
            HKQuantity.quantityWithUnit(unit = AppleUnits.celsius, doubleValue = value)

        is KHUnit.Temperature.Fahrenheit ->
            HKQuantity.quantityWithUnit(unit = AppleUnits.fahrenheit, doubleValue = value)
    }
}

internal infix fun HKQuantity.toDoubleValueFor(temperature: KHUnit.Temperature): Double {
    return when (temperature) {
        KHUnit.Temperature.Celsius -> doubleValueForUnit(AppleUnits.celsius)
        KHUnit.Temperature.Fahrenheit -> doubleValueForUnit(AppleUnits.fahrenheit)
    }
}

internal infix fun KHUnit.Velocity.toNativeVelocityFor(value: Double): HKQuantity = when (this) {
    is KHUnit.Velocity.KilometersPerHour ->
        HKQuantity.quantityWithUnit(unit = AppleUnits.kilometersPerHour, doubleValue = value)

    is KHUnit.Velocity.MetersPerSecond ->
        HKQuantity.quantityWithUnit(unit = AppleUnits.metersPerSecond, doubleValue = value)

    is KHUnit.Velocity.MilesPerHour ->
        HKQuantity.quantityWithUnit(unit = AppleUnits.milesPerHour, doubleValue = value)
}

internal infix fun HKQuantity.toDoubleValueFor(velocity: KHUnit.Velocity): Double {
    return when (velocity) {
        KHUnit.Velocity.KilometersPerHour -> doubleValueForUnit(AppleUnits.kilometersPerHour)
        KHUnit.Velocity.MetersPerSecond -> doubleValueForUnit(AppleUnits.metersPerSecond)
        KHUnit.Velocity.MilesPerHour -> doubleValueForUnit(AppleUnits.milesPerHour)
    }
}

internal infix fun KHUnit.Pressure.toNativePressureFor(value: Double): HKQuantity = when (this) {
    is KHUnit.Pressure.MillimeterOfMercury ->
        HKQuantity.quantityWithUnit(unit = AppleUnits.millimeterOfMercury, doubleValue = value)
}

internal infix fun HKQuantity.toDoubleValueFor(pressure: KHUnit.Pressure): Double {
    return when (pressure) {
        KHUnit.Pressure.MillimeterOfMercury -> doubleValueForUnit(AppleUnits.millimeterOfMercury)
    }
}

internal infix fun KHUnit.Volume.toNativeVolumeFor(value: Double): HKQuantity = when (this) {
    is KHUnit.Volume.FluidOunceUS -> HKQuantity.quantityWithUnit(
        unit = AppleUnits.fluidOunceUS,
        doubleValue = value,
    )

    is KHUnit.Volume.Liter -> HKQuantity.quantityWithUnit(
        unit = AppleUnits.liter,
        doubleValue = value,
    )
}

internal infix fun HKQuantity.toDoubleValueFor(volume: KHUnit.Volume): Double {
    return when (volume) {
        KHUnit.Volume.FluidOunceUS -> doubleValueForUnit(AppleUnits.fluidOunceUS)
        KHUnit.Volume.Liter -> doubleValueForUnit(AppleUnits.liter)
    }
}

@OptIn(UnsafeNumber::class)
internal fun KHCervicalMucusAppearance.toNativeCervicalMucusQuality(): HKCategoryValueCervicalMucusQuality {
    return when (this) {
        KHCervicalMucusAppearance.Creamy -> HKCategoryValueCervicalMucusQualityCreamy
        KHCervicalMucusAppearance.Dry -> HKCategoryValueCervicalMucusQualityDry
        KHCervicalMucusAppearance.EggWhite -> HKCategoryValueCervicalMucusQualityEggWhite
        KHCervicalMucusAppearance.Sticky -> HKCategoryValueCervicalMucusQualitySticky
        KHCervicalMucusAppearance.Watery -> HKCategoryValueCervicalMucusQualityWatery
    }
}

@OptIn(UnsafeNumber::class)
internal fun NSInteger.toKHCervicalMucusAppearance(): KHCervicalMucusAppearance = when (this) {
    HKCategoryValueCervicalMucusQualityCreamy -> KHCervicalMucusAppearance.Creamy
    HKCategoryValueCervicalMucusQualityDry -> KHCervicalMucusAppearance.Dry
    HKCategoryValueCervicalMucusQualityEggWhite -> KHCervicalMucusAppearance.EggWhite
    HKCategoryValueCervicalMucusQualitySticky -> KHCervicalMucusAppearance.Sticky
    HKCategoryValueCervicalMucusQualityWatery -> KHCervicalMucusAppearance.Watery
    else -> throw IllegalStateException("Unknown Cervical Mucus Appearance!")
}

@OptIn(UnsafeNumber::class)
internal fun KHMenstruationFlowType.toNativeMenstrualFlow(): NSInteger {
    return when (this) {
        KHMenstruationFlowType.Unknown -> HKCategoryValueMenstrualFlowUnspecified
        KHMenstruationFlowType.Light -> HKCategoryValueMenstrualFlowLight
        KHMenstruationFlowType.Medium -> HKCategoryValueMenstrualFlowMedium
        KHMenstruationFlowType.Heavy -> HKCategoryValueMenstrualFlowHeavy
    }
}

@OptIn(UnsafeNumber::class)
internal fun NSInteger.toKHMenstruationFlowType(): KHMenstruationFlowType = when (this) {
    HKCategoryValueMenstrualFlowUnspecified -> KHMenstruationFlowType.Unknown
    HKCategoryValueMenstrualFlowLight -> KHMenstruationFlowType.Light
    HKCategoryValueMenstrualFlowMedium -> KHMenstruationFlowType.Medium
    HKCategoryValueMenstrualFlowHeavy -> KHMenstruationFlowType.Heavy
    else -> throw IllegalStateException("Unknown Menstrual Flow type!")
}

@OptIn(UnsafeNumber::class)
internal fun KHOvulationTestResult.toNativeOvulationResult(): NSInteger {
    return when (this) {
        KHOvulationTestResult.High -> HKCategoryValueOvulationTestResultEstrogenSurge
        KHOvulationTestResult.Negative -> HKCategoryValueOvulationTestResultNegative
        KHOvulationTestResult.Positive -> HKCategoryValueOvulationTestResultPositive
        KHOvulationTestResult.Inconclusive -> HKCategoryValueOvulationTestResultIndeterminate
    }
}

@OptIn(UnsafeNumber::class)
internal fun NSInteger.toKHOvulationTestResult(): KHOvulationTestResult = when (this) {
    HKCategoryValueOvulationTestResultEstrogenSurge -> KHOvulationTestResult.High
    HKCategoryValueOvulationTestResultNegative -> KHOvulationTestResult.Negative
    HKCategoryValueOvulationTestResultPositive -> KHOvulationTestResult.Positive
    HKCategoryValueOvulationTestResultIndeterminate -> KHOvulationTestResult.Inconclusive
    else -> throw IllegalStateException("Unknown Ovulation Test result!")
}

@OptIn(UnsafeNumber::class)
internal fun KHSleepStage.toNativeSleepStage(): NSInteger = when (this) {
    KHSleepStage.Awake,
    KHSleepStage.AwakeInBed,
    KHSleepStage.AwakeOutOfBed -> HKCategoryValueSleepAnalysisAwake

    KHSleepStage.Deep -> HKCategoryValueSleepAnalysisAsleepDeep
    KHSleepStage.Light -> HKCategoryValueSleepAnalysisAsleepCore
    KHSleepStage.REM -> HKCategoryValueSleepAnalysisAsleepREM

    KHSleepStage.Sleeping,
    KHSleepStage.Unknown -> HKCategoryValueSleepAnalysisAsleepUnspecified
}

@OptIn(UnsafeNumber::class)
internal fun NSInteger.toKHSleepStage(): KHSleepStage = when (this) {
    HKCategoryValueSleepAnalysisAwake -> KHSleepStage.Awake
    HKCategoryValueSleepAnalysisAsleepDeep -> KHSleepStage.Deep
    HKCategoryValueSleepAnalysisAsleepCore -> KHSleepStage.Light
    HKCategoryValueSleepAnalysisAsleepREM -> KHSleepStage.REM
    HKCategoryValueSleepAnalysisAsleepUnspecified -> KHSleepStage.Sleeping
    else -> throw IllegalStateException("Unknown Sleep stage!")
}

/**
 * Merges multiple lists of quantity samples based on their startDate.
 * Each inner list in the result corresponds to the input list at the same position.
 * If no matching sample is found for a particular date, null is used in that position.
 */
internal fun mergeHKSamples(vararg samples: List<HKSample>?): List<List<HKSample?>> {
    // Convert null lists to empty lists
    val validSamples = samples.map { it ?: emptyList() }

    // If all lists are empty, return an empty result
    if (validSamples.all { it.isEmpty() }) return emptyList()

    // Get all unique dates
    val allDates = validSamples
        .flatten()
        .map { it.startDate }
        .distinct()
        .sortedByDescending { it.toKotlinInstant() }

    // Create maps for efficient lookup
    val sampleMaps = validSamples.map { validSampleList ->
        validSampleList.associateBy(
            keySelector = { it.startDate },
            valueTransform = { it }
        )
    }

    return allDates.map { date ->
        // For each date, create a list of values matching the input order
        sampleMaps.map { sampleMap -> sampleMap[date] }
    }
}

@OptIn(UnsafeNumber::class)
internal fun KHExerciseType.toNativeExerciseTypeOrNull(): HKWorkoutActivityType? = when (this) {
    KHExerciseType.AmericanFootball -> HKWorkoutActivityTypeAmericanFootball
    KHExerciseType.Archery -> HKWorkoutActivityTypeArchery
    KHExerciseType.AustralianFootball -> HKWorkoutActivityTypeAustralianFootball
    KHExerciseType.Badminton -> HKWorkoutActivityTypeBadminton
    KHExerciseType.Barre -> HKWorkoutActivityTypeBarre
    KHExerciseType.Baseball -> HKWorkoutActivityTypeBaseball
    KHExerciseType.Basketball -> HKWorkoutActivityTypeBasketball
    KHExerciseType.Bowling -> HKWorkoutActivityTypeBowling
    KHExerciseType.Boxing -> HKWorkoutActivityTypeBoxing
    KHExerciseType.CardioDance -> HKWorkoutActivityTypeCardioDance
    KHExerciseType.Climbing -> HKWorkoutActivityTypeClimbing
    KHExerciseType.Cooldown -> HKWorkoutActivityTypeCooldown
    KHExerciseType.CoreTraining -> HKWorkoutActivityTypeCoreTraining
    KHExerciseType.Cricket -> HKWorkoutActivityTypeCricket
    KHExerciseType.CrossCountrySkiing -> HKWorkoutActivityTypeCrossCountrySkiing
    KHExerciseType.CrossTraining -> HKWorkoutActivityTypeCrossTraining
    KHExerciseType.Curling -> HKWorkoutActivityTypeCurling
    KHExerciseType.Cycling -> HKWorkoutActivityTypeCycling
    KHExerciseType.Dance -> HKWorkoutActivityTypeDance
    KHExerciseType.DanceInspiredTraining -> HKWorkoutActivityTypeDanceInspiredTraining
    KHExerciseType.DiscSports -> HKWorkoutActivityTypeDiscSports
    KHExerciseType.DownhillSkiing -> HKWorkoutActivityTypeDownhillSkiing
    KHExerciseType.Elliptical -> HKWorkoutActivityTypeElliptical
    KHExerciseType.EquestrianSports -> HKWorkoutActivityTypeEquestrianSports
    KHExerciseType.Fencing -> HKWorkoutActivityTypeFencing
    KHExerciseType.Fishing -> HKWorkoutActivityTypeFishing
    KHExerciseType.FitnessGaming -> HKWorkoutActivityTypeFitnessGaming
    KHExerciseType.Flexibility -> HKWorkoutActivityTypeFlexibility
    KHExerciseType.FunctionalStrengthTraining -> HKWorkoutActivityTypeFunctionalStrengthTraining
    KHExerciseType.Golf -> HKWorkoutActivityTypeGolf
    KHExerciseType.Gymnastics -> HKWorkoutActivityTypeGymnastics
    KHExerciseType.HandCycling -> HKWorkoutActivityTypeHandCycling
    KHExerciseType.Handball -> HKWorkoutActivityTypeHandball
    KHExerciseType.HighIntensityIntervalTraining -> HKWorkoutActivityTypeHighIntensityIntervalTraining
    KHExerciseType.Hiking -> HKWorkoutActivityTypeHiking
    KHExerciseType.Hockey -> HKWorkoutActivityTypeHockey
    KHExerciseType.Hunting -> HKWorkoutActivityTypeHunting
    KHExerciseType.JumpRope -> HKWorkoutActivityTypeJumpRope
    KHExerciseType.Kickboxing -> HKWorkoutActivityTypeKickboxing
    KHExerciseType.Lacrosse -> HKWorkoutActivityTypeLacrosse
    KHExerciseType.MartialArts -> HKWorkoutActivityTypeMartialArts
    KHExerciseType.MindAndBody -> HKWorkoutActivityTypeMindAndBody
    KHExerciseType.MixedCardio -> HKWorkoutActivityTypeMixedCardio
    KHExerciseType.MixedMetabolicCardioTraining -> HKWorkoutActivityTypeMixedMetabolicCardioTraining
    KHExerciseType.Other -> HKWorkoutActivityTypeOther
    KHExerciseType.PaddleSports -> HKWorkoutActivityTypePaddleSports
    KHExerciseType.Pickleball -> HKWorkoutActivityTypePickleball
    KHExerciseType.Pilates -> HKWorkoutActivityTypePilates
    KHExerciseType.Play -> HKWorkoutActivityTypePlay
    KHExerciseType.PreparationAndRecovery -> HKWorkoutActivityTypePreparationAndRecovery
    KHExerciseType.Racquetball -> HKWorkoutActivityTypeRacquetball
    KHExerciseType.Rowing -> HKWorkoutActivityTypeRowing
    KHExerciseType.Rugby -> HKWorkoutActivityTypeRugby
    KHExerciseType.Running -> HKWorkoutActivityTypeRunning
    KHExerciseType.Sailing -> HKWorkoutActivityTypeSailing
    KHExerciseType.SkatingSports -> HKWorkoutActivityTypeSkatingSports
    KHExerciseType.SnowSports -> HKWorkoutActivityTypeSnowSports
    KHExerciseType.Snowboarding -> HKWorkoutActivityTypeSnowboarding
    KHExerciseType.Soccer -> HKWorkoutActivityTypeSoccer
    KHExerciseType.SocialDance -> HKWorkoutActivityTypeSocialDance
    KHExerciseType.Softball -> HKWorkoutActivityTypeSoftball
    KHExerciseType.Squash -> HKWorkoutActivityTypeSquash
    KHExerciseType.StairClimbing -> HKWorkoutActivityTypeStairClimbing
    KHExerciseType.Stairs -> HKWorkoutActivityTypeStairs
    KHExerciseType.StepTraining -> HKWorkoutActivityTypeStepTraining
    KHExerciseType.SurfingSports -> HKWorkoutActivityTypeSurfingSports
    KHExerciseType.SwimBikeRun -> HKWorkoutActivityTypeSwimBikeRun
    KHExerciseType.Swimming -> HKWorkoutActivityTypeSwimming
    KHExerciseType.TableTennis -> HKWorkoutActivityTypeTableTennis
    KHExerciseType.TaiChi -> HKWorkoutActivityTypeTaiChi
    KHExerciseType.Tennis -> HKWorkoutActivityTypeTennis
    KHExerciseType.TrackAndField -> HKWorkoutActivityTypeTrackAndField
    KHExerciseType.TraditionalStrengthTraining -> HKWorkoutActivityTypeTraditionalStrengthTraining
    KHExerciseType.Transition -> HKWorkoutActivityTypeTransition
    KHExerciseType.UnderwaterDiving -> HKWorkoutActivityTypeUnderwaterDiving
    KHExerciseType.Volleyball -> HKWorkoutActivityTypeVolleyball
    KHExerciseType.Walking -> HKWorkoutActivityTypeWalking
    KHExerciseType.WaterFitness -> HKWorkoutActivityTypeWaterFitness
    KHExerciseType.WaterPolo -> HKWorkoutActivityTypeWaterPolo
    KHExerciseType.WaterSports -> HKWorkoutActivityTypeWaterSports
    KHExerciseType.WheelchairRunPace -> HKWorkoutActivityTypeWheelchairRunPace
    KHExerciseType.WheelchairWalkPace -> HKWorkoutActivityTypeWheelchairWalkPace
    KHExerciseType.Wrestling -> HKWorkoutActivityTypeWrestling
    KHExerciseType.Yoga -> HKWorkoutActivityTypeYoga
    else -> null
}

@OptIn(UnsafeNumber::class)
internal fun HKWorkoutActivityType.toKHExerciseTypeOrNull(): KHExerciseType? = when (this) {
    HKWorkoutActivityTypeAmericanFootball -> KHExerciseType.AmericanFootball
    HKWorkoutActivityTypeArchery -> KHExerciseType.Archery
    HKWorkoutActivityTypeAustralianFootball -> KHExerciseType.AustralianFootball
    HKWorkoutActivityTypeBadminton -> KHExerciseType.Badminton
    HKWorkoutActivityTypeBarre -> KHExerciseType.Barre
    HKWorkoutActivityTypeBaseball -> KHExerciseType.Baseball
    HKWorkoutActivityTypeBasketball -> KHExerciseType.Basketball
    HKWorkoutActivityTypeBowling -> KHExerciseType.Bowling
    HKWorkoutActivityTypeBoxing -> KHExerciseType.Boxing
    HKWorkoutActivityTypeCardioDance -> KHExerciseType.CardioDance
    HKWorkoutActivityTypeClimbing -> KHExerciseType.Climbing
    HKWorkoutActivityTypeCooldown -> KHExerciseType.Cooldown
    HKWorkoutActivityTypeCoreTraining -> KHExerciseType.CoreTraining
    HKWorkoutActivityTypeCricket -> KHExerciseType.Cricket
    HKWorkoutActivityTypeCrossCountrySkiing -> KHExerciseType.CrossCountrySkiing
    HKWorkoutActivityTypeCrossTraining -> KHExerciseType.CrossTraining
    HKWorkoutActivityTypeCurling -> KHExerciseType.Curling
    HKWorkoutActivityTypeCycling -> KHExerciseType.Cycling
    HKWorkoutActivityTypeDance -> KHExerciseType.Dance
    HKWorkoutActivityTypeDanceInspiredTraining -> KHExerciseType.DanceInspiredTraining
    HKWorkoutActivityTypeDiscSports -> KHExerciseType.DiscSports
    HKWorkoutActivityTypeDownhillSkiing -> KHExerciseType.DownhillSkiing
    HKWorkoutActivityTypeElliptical -> KHExerciseType.Elliptical
    HKWorkoutActivityTypeEquestrianSports -> KHExerciseType.EquestrianSports
    HKWorkoutActivityTypeFencing -> KHExerciseType.Fencing
    HKWorkoutActivityTypeFishing -> KHExerciseType.Fishing
    HKWorkoutActivityTypeFitnessGaming -> KHExerciseType.FitnessGaming
    HKWorkoutActivityTypeFlexibility -> KHExerciseType.Flexibility
    HKWorkoutActivityTypeFunctionalStrengthTraining -> KHExerciseType.FunctionalStrengthTraining
    HKWorkoutActivityTypeGolf -> KHExerciseType.Golf
    HKWorkoutActivityTypeGymnastics -> KHExerciseType.Gymnastics
    HKWorkoutActivityTypeHandCycling -> KHExerciseType.HandCycling
    HKWorkoutActivityTypeHandball -> KHExerciseType.Handball
    HKWorkoutActivityTypeHighIntensityIntervalTraining -> KHExerciseType.HighIntensityIntervalTraining
    HKWorkoutActivityTypeHiking -> KHExerciseType.Hiking
    HKWorkoutActivityTypeHockey -> KHExerciseType.Hockey
    HKWorkoutActivityTypeHunting -> KHExerciseType.Hunting
    HKWorkoutActivityTypeJumpRope -> KHExerciseType.JumpRope
    HKWorkoutActivityTypeKickboxing -> KHExerciseType.Kickboxing
    HKWorkoutActivityTypeLacrosse -> KHExerciseType.Lacrosse
    HKWorkoutActivityTypeMartialArts -> KHExerciseType.MartialArts
    HKWorkoutActivityTypeMindAndBody -> KHExerciseType.MindAndBody
    HKWorkoutActivityTypeMixedCardio -> KHExerciseType.MixedCardio
    HKWorkoutActivityTypeMixedMetabolicCardioTraining -> KHExerciseType.MixedMetabolicCardioTraining
    HKWorkoutActivityTypeOther -> KHExerciseType.Other
    HKWorkoutActivityTypePaddleSports -> KHExerciseType.PaddleSports
    HKWorkoutActivityTypePickleball -> KHExerciseType.Pickleball
    HKWorkoutActivityTypePilates -> KHExerciseType.Pilates
    HKWorkoutActivityTypePlay -> KHExerciseType.Play
    HKWorkoutActivityTypePreparationAndRecovery -> KHExerciseType.PreparationAndRecovery
    HKWorkoutActivityTypeRacquetball -> KHExerciseType.Racquetball
    HKWorkoutActivityTypeRowing -> KHExerciseType.Rowing
    HKWorkoutActivityTypeRugby -> KHExerciseType.Rugby
    HKWorkoutActivityTypeRunning -> KHExerciseType.Running
    HKWorkoutActivityTypeSailing -> KHExerciseType.Sailing
    HKWorkoutActivityTypeSkatingSports -> KHExerciseType.SkatingSports
    HKWorkoutActivityTypeSnowSports -> KHExerciseType.SnowSports
    HKWorkoutActivityTypeSnowboarding -> KHExerciseType.Snowboarding
    HKWorkoutActivityTypeSoccer -> KHExerciseType.Soccer
    HKWorkoutActivityTypeSocialDance -> KHExerciseType.SocialDance
    HKWorkoutActivityTypeSoftball -> KHExerciseType.Softball
    HKWorkoutActivityTypeSquash -> KHExerciseType.Squash
    HKWorkoutActivityTypeStairClimbing -> KHExerciseType.StairClimbing
    HKWorkoutActivityTypeStairs -> KHExerciseType.Stairs
    HKWorkoutActivityTypeStepTraining -> KHExerciseType.StepTraining
    HKWorkoutActivityTypeSurfingSports -> KHExerciseType.SurfingSports
    HKWorkoutActivityTypeSwimBikeRun -> KHExerciseType.SwimBikeRun
    HKWorkoutActivityTypeSwimming -> KHExerciseType.Swimming
    HKWorkoutActivityTypeTableTennis -> KHExerciseType.TableTennis
    HKWorkoutActivityTypeTaiChi -> KHExerciseType.TaiChi
    HKWorkoutActivityTypeTennis -> KHExerciseType.Tennis
    HKWorkoutActivityTypeTrackAndField -> KHExerciseType.TrackAndField
    HKWorkoutActivityTypeTraditionalStrengthTraining -> KHExerciseType.TraditionalStrengthTraining
    HKWorkoutActivityTypeTransition -> KHExerciseType.Transition
    HKWorkoutActivityTypeUnderwaterDiving -> KHExerciseType.UnderwaterDiving
    HKWorkoutActivityTypeVolleyball -> KHExerciseType.Volleyball
    HKWorkoutActivityTypeWalking -> KHExerciseType.Walking
    HKWorkoutActivityTypeWaterFitness -> KHExerciseType.WaterFitness
    HKWorkoutActivityTypeWaterPolo -> KHExerciseType.WaterPolo
    HKWorkoutActivityTypeWaterSports -> KHExerciseType.WaterSports
    HKWorkoutActivityTypeWheelchairRunPace -> KHExerciseType.WheelchairRunPace
    HKWorkoutActivityTypeWheelchairWalkPace -> KHExerciseType.WheelchairWalkPace
    HKWorkoutActivityTypeWrestling -> KHExerciseType.Wrestling
    HKWorkoutActivityTypeYoga -> KHExerciseType.Yoga
    else -> null
}

internal object ObjectType {
    object Category {
        val CervicalMucus =
            HKObjectType.categoryTypeForIdentifier(HKCategoryTypeIdentifierCervicalMucusQuality)!!

        val IntermenstrualBleeding =
            HKObjectType.categoryTypeForIdentifier(HKCategoryTypeIdentifierIntermenstrualBleeding)!!

        val MenstruationFlow =
            HKObjectType.categoryTypeForIdentifier(HKCategoryTypeIdentifierMenstrualFlow)!!

        val SexualActivity =
            HKObjectType.categoryTypeForIdentifier(HKCategoryTypeIdentifierSexualActivity)!!

        val OvulationTest =
            HKObjectType.categoryTypeForIdentifier(HKCategoryTypeIdentifierOvulationTestResult)!!

        val SleepSession =
            HKObjectType.categoryTypeForIdentifier(HKCategoryTypeIdentifierSleepAnalysis)!!
    }

    object Food {
        val Biotin =
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierDietaryBiotin)!!
        val Caffeine =
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierDietaryCaffeine)!!
        val Calcium =
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierDietaryCalcium)!!
        val EnergyConsumed =
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierDietaryEnergyConsumed)!!
        val Chloride =
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierDietaryChloride)!!
        val Cholesterol =
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierDietaryCholesterol)!!
        val Chromium =
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierDietaryChromium)!!
        val Copper =
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierDietaryCopper)!!
        val Fiber =
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierDietaryFiber)!!
        val Folate =
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierDietaryFolate)!!
        val Iodine =
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierDietaryIodine)!!
        val Iron =
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierDietaryIron)!!
        val Magnesium =
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierDietaryMagnesium)!!
        val Manganese =
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierDietaryManganese)!!
        val Molybdenum =
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierDietaryMolybdenum)!!
        val FatMonounsaturated =
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierDietaryFatMonounsaturated)!!
        val Niacin =
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierDietaryNiacin)!!
        val PantothenicAcid =
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierDietaryPantothenicAcid)!!
        val Phosphorus =
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierDietaryPhosphorus)!!
        val FatPolyunsaturated =
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierDietaryFatPolyunsaturated)!!
        val Potassium =
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierDietaryPotassium)!!
        val Protein =
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierDietaryProtein)!!
        val Riboflavin =
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierDietaryRiboflavin)!!
        val FatSaturated =
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierDietaryFatSaturated)!!
        val Selenium =
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierDietarySelenium)!!
        val Sodium =
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierDietarySodium)!!
        val Sugar =
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierDietarySugar)!!
        val Thiamin =
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierDietaryThiamin)!!
        val Carbohydrates =
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierDietaryCarbohydrates)!!
        val FatTotal =
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierDietaryFatTotal)!!
        val VitaminA =
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierDietaryVitaminA)!!
        val VitaminB12 =
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierDietaryVitaminB12)!!
        val VitaminB6 =
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierDietaryVitaminB6)!!
        val VitaminC =
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierDietaryVitaminC)!!
        val VitaminD =
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierDietaryVitaminD)!!
        val VitaminE =
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierDietaryVitaminE)!!
        val VitaminK =
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierDietaryVitaminK)!!
        val Zinc =
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierDietaryZinc)!!
    }

    object Quantity {
        val ActiveCaloriesBurned =
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierActiveEnergyBurned)!!
        val BasalMetabolicRate =
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierBasalEnergyBurned)!!
        val BloodGlucose =
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierBloodGlucose)!!
        val BloodPressureSystolic =
            HKQuantityType.quantityTypeForIdentifier(HKQuantityTypeIdentifierBloodPressureSystolic)!!
        val BloodPressureDiastolic =
            HKQuantityType.quantityTypeForIdentifier(HKQuantityTypeIdentifierBloodPressureDiastolic)!!
        val BodyFat =
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierBodyFatPercentage)!!
        val BodyTemperature =
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierBodyTemperature)!!
        val Distance =
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierDistanceWalkingRunning)!!
        val FloorsClimbed =
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierFlightsClimbed)!!
        val HeartRate =
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierHeartRate)!!
        val HeartRateVariability =
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierHeartRateVariabilitySDNN)!!
        val Height =
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierHeight)!!
        val Hydration =
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierDietaryWater)!!
        val LeanBodyMass =
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierLeanBodyMass)!!
        val OxygenSaturation =
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierOxygenSaturation)!!
        val Power =
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierCyclingPower)!!
        val RespiratoryRate =
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierRespiratoryRate)!!
        val RestingHeartRate =
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierRestingHeartRate)!!
        val RunningSpeed =
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierRunningSpeed)!!
        val CyclingSpeed =
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierCyclingSpeed)!!
        val StepCount =
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierStepCount)!!
        val Vo2Max =
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierVO2Max)!!
        val Weight =
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierBodyMass)!!
        val WheelChairPushes =
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierPushCount)!!
    }

    val Exercise = HKObjectType.workoutType()
}
