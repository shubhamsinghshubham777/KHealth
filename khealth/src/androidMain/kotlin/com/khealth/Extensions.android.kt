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

import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.records.MealType
import androidx.health.connect.client.records.Record
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
import androidx.health.connect.client.units.pounds
import androidx.health.connect.client.units.watts
import kotlin.reflect.KClass

// Delegation to specialized mappers
internal fun KHPermission.toPermissions(): Set<Pair<String, KHPermissionType>> =
    AndroidPermissionMapper.toPermissions(this)

internal fun Array<out KHPermission>.toPermissionsWithStatuses(
    grantedPermissions: Set<String>
): List<KHPermission> = AndroidPermissionMapper.toPermissionsWithStatuses(this, grantedPermissions)

internal fun KHReadRequest<*>.toRecordClass(): KClass<out Record>? =
    AndroidRecordReader.toRecordClass(this)

internal fun KHPermission.toRecordClass(): KClass<out Record>? =
    AndroidPermissionMapper.toRecordClass(this)

internal fun KHRecord.toHCRecord(): Record? =
    AndroidRecordWriter.toHCRecord(this)

internal fun Record.toKHRecordOrNull(request: KHReadRequest<*>): KHRecord? =
    AndroidRecordReader.toKHRecordOrNull(this, request)

// Unit Converters
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

internal infix fun KHUnit.Energy.toNativePowerFor(value: Double): Power = when (this) {
    KHUnit.Energy.KiloCalorie -> value.kilocaloriesPerDay
    KHUnit.Energy.Calorie -> (value / 1000.0).kilocaloriesPerDay
    KHUnit.Energy.KiloJoule -> (value / 4.184).kilocaloriesPerDay
    KHUnit.Energy.Joule -> (value / 4184.0).kilocaloriesPerDay
}

internal infix fun Power.toDoubleValueFor(energy: KHUnit.Energy): Double = when (energy) {
    KHUnit.Energy.KiloCalorie -> this.inKilocaloriesPerDay
    KHUnit.Energy.Calorie -> this.inKilocaloriesPerDay * 1000.0
    KHUnit.Energy.KiloJoule -> this.inKilocaloriesPerDay * 4.184
    KHUnit.Energy.Joule -> this.inKilocaloriesPerDay * 4184.0
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

internal fun KHExerciseType.toNativeExerciseTypeOrNull(): Int? = when (this) {
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
