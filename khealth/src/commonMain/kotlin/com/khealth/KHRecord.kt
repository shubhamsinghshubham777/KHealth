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

import kotlinx.datetime.Instant

/**
 * Represents a readable or writable entity.
 */
sealed class KHRecord {
    /**
     * Captures the estimated active energy burned by the user, excluding basal metabolic rate
     * (BMR). Each record represents the total energy burned over a time interval, so both the
     * start and end times should be set.
     *
     * @param unit Measurement scale of this record
     * @param value The value of this record
     * @param startTime The start instant of the interval over which the value this record was
     * captured
     * @param endTime The end instant of the interval over which the value this record was captured
     */
    data class ActiveCaloriesBurned(
        val unit: KHUnit.Energy,
        val value: Double,
        val startTime: Instant,
        val endTime: Instant
    ) : KHRecord()

    /**
     * Captures the BMR of a user. Each record represents the energy a user would burn if at rest
     * all day, based on their height and weight.
     *
     * @param unit Measurement scale of this record. It is a disjoint union (named [KHEither]
     * in this project) because Android and Apple use different units to represent this record but
     * at the time of accessing its value in runtime, only one of these units will be available
     * (not null) based on the current platform.
     * @param value The value of this record
     * @param time Time instant of the interval over which the value this record was captured
     */
    data class BasalMetabolicRate(
        val unit: KHEither<KHUnit.Power, KHUnit.Energy>,
        val value: Double,
        val time: Instant
    ) : KHRecord()

    /**
     * Captures the concentration of glucose in the blood. Each record represents a single
     * instantaneous blood glucose reading.
     *
     * @param unit Measurement scale of this record
     * @param value The value of this record
     * @param time Time instant of the interval over which the value this record was captured
     */
    data class BloodGlucose(
        val unit: KHUnit.BloodGlucose,
        val value: Double,
        val time: Instant
    ) : KHRecord()

    /**
     * Captures the blood pressure of a user. Each record represents a single instantaneous blood
     * pressure reading.
     *
     * @param unit Measurement scale of this record
     * @param systolicValue Represents the top number of a BP reading and refers to the amount of
     * pressure experienced by the arteries while the heart is beating
     * @param diastolicValue Represents the bottom number of a BP reading and refers to the amount
     * of pressure in the arteries while the heart is resting in between heartbeats
     * @param time Time instant of the interval over which the value this record was captured
     */
    data class BloodPressure(
        val unit: KHUnit.Pressure,
        val systolicValue: Double,
        val diastolicValue: Double,
        val time: Instant
    ) : KHRecord()

    /**
     * Captures the body fat percentage of a user. Each record represents a person's total body fat
     * as a percentage of their total body mass.
     *
     * @param percentage A double value representing the person's total body fat
     * @param time Time instant of the interval over which the value this record was captured
     */
    data class BodyFat(
        val percentage: Double,
        val time: Instant
    ) : KHRecord()

    /**
     * Captures the body temperature of a user. Each record represents a single instantaneous body
     * temperature measurement.
     *
     * @param unit Measurement scale of this record
     * @param value The value of this record
     * @param time Time instant of the interval over which the value this record was captured
     */
    data class BodyTemperature(
        val unit: KHUnit.Temperature,
        val value: Double,
        val time: Instant
    ) : KHRecord()

    /**
     * Body water mass is the total amount of water in a person's body, typically comprising about
     * 60% of their total body weight.
     *
     * > **Note -** This record is available on `Android` only. It will be ignored on `Apple`.
     * @param unit Measurement scale of this record
     * @param value The value of this record
     * @param time Time instant of the interval over which the value this record was captured
     */
    data class BodyWaterMass(
        val unit: KHUnit.Mass,
        val value: Double,
        val time: Instant
    ) : KHRecord()

    /**
     * Bone mass is the total weight of bones in the body, reflecting the amount of bone tissue and
     * its density.
     *
     * > **Note -** This record is available on `Android` only. It will be ignored on `Apple`.
     * @param unit Measurement scale of this record
     * @param value The value of this record
     * @param time Time instant of the interval over which the value this record was captured
     */
    data class BoneMass(
        val unit: KHUnit.Mass,
        val value: Double,
        val time: Instant
    ) : KHRecord()

    /**
     * Cervical mucus is a fluid produced by the cervix that changes in consistency throughout the
     * menstrual cycle, aiding in fertility and sperm transport.
     *
     * @param appearance Represents one of the types of cervical mucus
     * @param time Time instant of the interval over which the value this record was captured
     */
    data class CervicalMucus(
        val appearance: KHCervicalMucusAppearance,
        val time: Instant
    ) : KHRecord()

    /**
     * In cycling, cadence is a measure of rotational speed of the crank, expressed in revolutions
     * per minute. In other words, it is the pedalling rate at which a cyclist is turning the
     * pedals.
     *
     * > **Note -** This record is available on `Android` only. It will be ignored on `Apple`.
     *
     * @param samples The list of captured cadence values along with their time instances within
     * this object's [startTime] and [endTime] range
     * @param startTime The start instant of the interval over which the value this record was
     * captured
     * @param endTime The end instant of the interval over which the value this record was captured
     */
    data class CyclingPedalingCadence(
        val samples: List<KHCyclingPedalingCadenceSample>,
        val startTime: Instant,
        val endTime: Instant
    ) : KHRecord()

    /**
     * Cycling speed is the rate at which a cyclist travels, usually measured in kilometers per hour
     * (km/h) or miles per hour (mph).
     *
     * > **Note -** This record is available on `Apple` only. It will be ignored on `Android`.
     *
     * @param samples The list of captured speed values along with their unit and time instances
     */
    data class CyclingSpeed(
        val samples: List<KHSpeedSample>,
    ) : KHRecord()

    /**
     * Captures distance travelled by the user since the last reading. The total distance over an
     * interval can be calculated by adding together all the values during the interval. The start
     * time of each record should represent the start of the interval in which the distance was
     * covered.
     *
     * If break downs are preferred in scenario of a long workout, consider writing multiple
     * distance records. The start time of each record should be equal to or greater than the end
     * time of the previous record.
     *
     * @param unit Measurement scale of this record
     * @param value The value of this record
     * @param startTime The start instant of the interval over which the value this record was
     * captured
     * @param endTime The end instant of the interval over which the value this record was captured
     */
    data class Distance(
        val unit: KHUnit.Length,
        val value: Double,
        val startTime: Instant,
        val endTime: Instant
    ) : KHRecord()

    /**
     * Captures the elevation gained by the user since the last reading.
     *
     * > **Note -** This record is available on `Android` only. It will be ignored on `Apple`.
     *
     * @param unit Measurement scale of this record
     * @param value The value of this record
     * @param startTime The start instant of the interval over which the value this record was
     * captured
     * @param endTime The end instant of the interval over which the value this record was captured
     */
    data class ElevationGained(
        val unit: KHUnit.Length,
        val value: Double,
        val startTime: Instant,
        val endTime: Instant
    ) : KHRecord()

    /**
     * Captures any exercise a user does. This can be common fitness exercise like running or
     * different sports.
     *
     * Each record needs a start time and end time. Records don't need to be back-to-back or
     * directly after each other, there can be gaps in between.
     *
     * @param startTime The start instant of the interval over which the value this record was
     * captured
     * @param endTime The end instant of the interval over which the value this record was captured
     * @param type The type of exercise that the user is doing
     */
    data class Exercise(
        val type: KHExerciseType,
        val startTime: Instant,
        val endTime: Instant,
    ) : KHRecord()

    /**
     * Captures the number of floors climbed by the user since the last reading.
     *
     * @param floors Count of the floors climbed
     * @param startTime The start instant of the interval over which the value this record was
     * captured
     * @param endTime The end instant of the interval over which the value this record was captured
     */
    data class FloorsClimbed(
        val floors: Double,
        val startTime: Instant,
        val endTime: Instant
    ) : KHRecord()

    /**
     * Captures the user's heart rate. Each record represents a series of measurements.
     *
     * @param samples The list of captured heart rate values along with their time instances
     */
    data class HeartRate(
        val samples: List<KHHeartRateSample>
    ) : KHRecord()

    /**
     * Captures user's heart rate variability (HRV) as measured by the root mean square of
     * successive differences `RMSSD` on Android and Standard Deviation of NN intervals `SDNN` on
     * Apple between normal heartbeats.
     *
     * @param heartRateVariabilityMillis Variation in time between consecutive heartbeats
     * @param time Time instant of the interval over which the value this record was captured
     */
    data class HeartRateVariability(
        val heartRateVariabilityMillis: Double,
        val time: Instant
    ) : KHRecord()

    /**
     * Captures the user's height.
     *
     * @param unit Measurement scale of this record
     * @param value The value of this record
     * @param time Time instant of the interval over which the value this record was captured
     */
    data class Height(
        val unit: KHUnit.Length,
        val value: Double,
        val time: Instant
    ) : KHRecord()

    /**
     * Captures how much water a user drank in a single drink.
     *
     * @param unit Measurement scale of this record
     * @param value The value of this record
     * @param startTime The start instant of the interval over which the value this record was
     * captured
     * @param endTime The end instant of the interval over which the value this record was captured
     */
    data class Hydration(
        val unit: KHUnit.Volume,
        val value: Double,
        val startTime: Instant,
        val endTime: Instant
    ) : KHRecord()

    /**
     * Captures an instance of user's intermenstrual bleeding, also known as spotting.
     *
     * @param time Time instant of the interval over which the value this record was captured
     */
    data class IntermenstrualBleeding(
        val time: Instant
    ) : KHRecord()

    /**
     * Captures the user's lean body mass. Each record represents a single instantaneous
     * measurement.
     *
     * @param unit Measurement scale of this record
     * @param value The value of this record
     * @param time Time instant of the interval over which the value this record was captured
     */
    data class LeanBodyMass(
        val unit: KHUnit.Mass,
        val value: Double,
        val time: Instant
    ) : KHRecord()

    /**
     * Captures user's menstruation periods.
     *
     * > **Note -** This record is available on `Android` only. It will be ignored on `Apple`.
     * @param startTime The start instant of the interval over which the value this record was
     * captured
     * @param endTime The end instant of the interval over which the value this record was captured
     */
    data class MenstruationPeriod(
        val startTime: Instant,
        val endTime: Instant
    ) : KHRecord()

    /**
     * Captures a description of how heavy a user's menstrual flow was (light, medium, or heavy).
     * Each record represents a description of how heavy the user's menstrual bleeding was.
     *
     * @param type The pattern or characteristics of menstrual bleeding, such as light, moderate,
     * or heavy flow
     * @param time Time instant of the interval over which the value this record was captured
     * @param isStartOfCycle Metadata that describes whether this event is the start of the
     * menstruation cycle (applied on `Apple` platforms only)
     */
    data class MenstruationFlow(
        val type: KHMenstruationFlowType,
        val time: Instant,
        val isStartOfCycle: Boolean = false,
    ) : KHRecord()

    /**
     * Captures what nutrients were consumed as part of a meal or a food item.
     *
     * @param name Name for food or drink, provided by the user.
     * @param startTime The start instant of the interval over which the value this record was
     * captured
     * @param endTime The end instant of the interval over which the value this record was captured
     * @param biotin Biotin in [KHUnit.Mass] unit (Valid range: 0-100 grams)
     * @param caffeine Caffeine in [KHUnit.Mass] unit (Valid range: 0-100 grams)
     * @param calcium Calcium in [KHUnit.Mass] unit (Valid range: 0-100 grams)
     * @param chloride Chloride in [KHUnit.Mass] unit (Valid range: 0-100 grams)
     * @param cholesterol Cholesterol in [KHUnit.Mass] unit (Valid range: 0-100 grams)
     * @param chromium Chromium in [KHUnit.Mass] unit (Valid range: 0-100 grams)
     * @param copper Copper in [KHUnit.Mass] unit (Valid range: 0-100 grams)
     * @param dietaryFiber Dietary fiber in [KHUnit.Mass] unit (Valid range: 0-100000 grams)
     * @param energy Energy in [KHUnit.Energy] unit (Valid range: 0-100000 kcal)
     * @param folicAcid Folic acid in [KHUnit.Mass] unit (Valid range: 0-100 grams)
     * @param iodine Iodine in [KHUnit.Mass] unit (Valid range: 0-100 grams)
     * @param iron Iron in [KHUnit.Mass] unit (Valid range: 0-100 grams)
     * @param magnesium Magnesium in [KHUnit.Mass] unit (Valid range: 0-100 grams)
     * @param manganese Manganese in [KHUnit.Mass] unit (Valid range: 0-100 grams)
     * @param molybdenum Molybdenum in [KHUnit.Mass] unit (Valid range: 0-100 grams)
     * @param monounsaturatedFat Monounsaturated fat in [KHUnit.Mass] unit (Valid range: 0-100000
     * grams)
     * @param niacin Niacin in [KHUnit.Mass] unit (Valid range: 0-100 grams)
     * @param pantothenicAcid Pantothenic acid in [KHUnit.Mass] unit (Valid range: 0-100 grams)
     * @param phosphorus Phosphorus in [KHUnit.Mass] unit (Valid range: 0-100 grams)
     * @param polyunsaturatedFat Polyunsaturated fat in [KHUnit.Mass] unit (Valid range: 0-100000
     * grams)
     * @param potassium Potassium in [KHUnit.Mass] unit (Valid range: 0-100 grams)
     * @param protein Protein in [KHUnit.Mass] unit (Valid range: 0-100000 grams)
     * @param riboflavin Riboflavin in [KHUnit.Mass] unit (Valid range: 0-100 grams)
     * @param saturatedFat Saturated fat in [KHUnit.Mass] unit (Valid range: 0-100000 grams)
     * @param selenium Selenium in [KHUnit.Mass] unit (Valid range: 0-100 grams)
     * @param sodium Sodium in [KHUnit.Mass] unit (Valid range: 0-100 grams)
     * @param sugar Sugar in [KHUnit.Mass] unit (Valid range: 0-100000 grams)
     * @param thiamin Thiamin in [KHUnit.Mass] unit (Valid range: 0-100 grams)
     * @param totalCarbohydrate Total carbohydrate in [KHUnit.Mass] unit (Valid
     * range: 0-100000 grams)
     * @param totalFat Total fat in [KHUnit.Mass] unit (Valid range: 0-100000 grams)
     * @param vitaminA Vitamin A in [KHUnit.Mass] unit (Valid range: 0-100 grams)
     * @param vitaminB12 Vitamin B12 in [KHUnit.Mass] unit (Valid range: 0-100 grams)
     * @param vitaminB6 Vitamin B6 in [KHUnit.Mass] unit (Valid range: 0-100 grams)
     * @param vitaminC Vitamin C in [KHUnit.Mass] unit (Valid range: 0-100 grams)
     * @param vitaminD Vitamin D in [KHUnit.Mass] unit (Valid range: 0-100 grams)
     * @param vitaminE Vitamin E in [KHUnit.Mass] unit (Valid range: 0-100 grams)
     * @param vitaminK Vitamin K in [KHUnit.Mass] unit (Valid range: 0-100 grams)
     * @param zinc Zinc in [KHUnit.Mass] unit (Valid range: 0-100 grams)
     */
    data class Nutrition(
        val name: String? = null,
        val startTime: Instant,
        val endTime: Instant,
        val solidUnit: KHUnit.Mass = KHUnit.Mass.Gram,
        val energyUnit: KHUnit.Energy = KHUnit.Energy.KiloCalorie,
        val mealType: KHMealType,
        val biotin: Double? = null,
        val caffeine: Double? = null,
        val calcium: Double? = null,
        val chloride: Double? = null,
        val cholesterol: Double? = null,
        val chromium: Double? = null,
        val copper: Double? = null,
        val dietaryFiber: Double? = null,
        val energy: Double? = null,
        val folicAcid: Double? = null,
        val iodine: Double? = null,
        val iron: Double? = null,
        val magnesium: Double? = null,
        val manganese: Double? = null,
        val molybdenum: Double? = null,
        val monounsaturatedFat: Double? = null,
        val niacin: Double? = null,
        val pantothenicAcid: Double? = null,
        val phosphorus: Double? = null,
        val polyunsaturatedFat: Double? = null,
        val potassium: Double? = null,
        val protein: Double? = null,
        val riboflavin: Double? = null,
        val saturatedFat: Double? = null,
        val selenium: Double? = null,
        val sodium: Double? = null,
        val sugar: Double? = null,
        val thiamin: Double? = null,
        val totalCarbohydrate: Double? = null,
        val totalFat: Double? = null,
        val vitaminA: Double? = null,
        val vitaminB12: Double? = null,
        val vitaminB6: Double? = null,
        val vitaminC: Double? = null,
        val vitaminD: Double? = null,
        val vitaminE: Double? = null,
        val vitaminK: Double? = null,
        val zinc: Double? = null,
    ) : KHRecord()

    /**
     * Each record represents the result of an ovulation test.
     *
     * @param result Represents one of the types of the ovulation test result (high, negative, etc.)
     * @param time Time instant of the interval over which the value this record was captured
     */
    data class OvulationTest(
        val result: KHOvulationTestResult,
        val time: Instant,
    ) : KHRecord()

    /**
     * Captures the amount of oxygen circulating in the blood, measured as a percentage of
     * oxygen-saturated hemoglobin. Each record represents a single blood oxygen saturation reading
     * at the time of measurement.
     *
     * @param percentage Ranges from 0-100%
     * @param time Time instant of the interval over which the value this record was captured
     */
    data class OxygenSaturation(
        val percentage: Double,
        val time: Instant,
    ) : KHRecord()

    /**
     * Captures the power generated by the user, e. g. during cycling or rowing with a power meter.
     * Each record represents a series of measurements.
     *
     * @param samples The list of captured power values along with their time instances
     */
    data class Power(val samples: List<KHPowerSample>) : KHRecord()

    /**
     * Captures the user's respiratory rate. Each record represents a single instantaneous
     * measurement.
     *
     * @param rate Number of breaths taken per minute
     * @param time Time instant of the interval over which the value this record was captured
     */
    data class RespiratoryRate(
        val rate: Double,
        val time: Instant,
    ) : KHRecord()

    /**
     * Captures the user's resting heart rate. Each record represents a single instantaneous
     * measurement. These are typically measured after waking up and before getting out of bed.
     *
     * @param beatsPerMinute Number of heartbeats per minute while at rest
     * @param time Time instant of the interval over which the value this record was captured
     */
    data class RestingHeartRate(
        val beatsPerMinute: Long,
        val time: Instant,
    ) : KHRecord()

    /**
     * Running speed is the rate at which a person moves while running, typically measured in meters
     * per second or kilometers per hour.
     *
     * > **Note -** This record is available on `Apple` only. It will be ignored on `Android`.
     *
     * @param samples The list of captured speed values along with their unit and time instances
     */
    data class RunningSpeed(
        val samples: List<KHSpeedSample>,
    ) : KHRecord()

    /**
     * Captures an occurrence of sexual activity. Each record is a single occurrence.
     *
     * @param didUseProtection Metadata representing whether any kind of protection was used
     * @param time Time instant of the interval over which the value this record was captured
     */
    data class SexualActivity(
        val didUseProtection: Boolean,
        val time: Instant,
    ) : KHRecord()

    /**
     * Captures the user's sleep length and its stages. Each record represents a time interval for
     * a full sleep session.
     *
     * All sleep stage time intervals should fall within the sleep session interval. Time intervals
     * for stages don't need to be continuous but shouldn't overlap.
     *
     * @param samples The list of captured sleep values along with their time and stage instances
     */
    data class SleepSession(
        val samples: List<KHSleepStageSample>
    ) : KHRecord()

    /**
     * Captures the user's speed, e. g. during running or cycling. Each record represents a series
     * of measurements.
     *
     * > **Note -** This record is available on `Android` only. It will be ignored on `Apple`.
     *
     * @param samples The list of captured speed values along with their unit and time instances
     */
    data class Speed(
        val samples: List<KHSpeedSample>,
    ) : KHRecord()

    /**
     * Captures the number of steps taken since the last reading. Each step is only reported once
     * so records shouldn't have overlapping time. The start time of each record should represent
     * the start of the interval in which steps were taken.
     *
     * The start time must be equal to or greater than the end time of the previous record. Adding
     * all of the values together for a period of time calculates the total number of steps during
     * that period.
     *
     * @param count The number of steps taken
     * @param startTime The start instant of the interval over which the value this record was
     * captured
     * @param endTime The end instant of the interval over which the value this record was captured
     */
    data class StepCount(
        val count: Long,
        val startTime: Instant,
        val endTime: Instant
    ) : KHRecord()

    /**
     * VO2 max is the maximum amount of oxygen the body can utilize during intense exercise,
     * indicating cardiovascular fitness and endurance capacity.
     *
     * @param vo2MillilitersPerMinuteKilogram Captured value for VO2Max
     * @param time Time instant of the interval over which the value this record was captured
     */
    data class Vo2Max(
        val vo2MillilitersPerMinuteKilogram: Double,
        val time: Instant,
    ) : KHRecord()

    /**
     * Captures the user's weight.
     *
     * @param unit Measurement scale of this record
     * @param value Weight of the user in the provided [unit]
     * @param time Time instant of the interval over which the value this record was captured
     */
    data class Weight(
        val unit: KHUnit.Mass,
        val value: Double,
        val time: Instant,
    ) : KHRecord()

    /**
     * Captures the number of wheelchair pushes done since the last reading. Each push is only
     * reported once so records shouldn't have overlapping time. The start time of each record
     * should represent the start of the interval in which pushes were made.
     *
     * @param count Number of times a wheel chair was pushed
     * @param startTime The start instant of the interval over which the value this record was
     * captured
     * @param endTime The end instant of the interval over which the value this record was captured
     */
    data class WheelChairPushes(
        val count: Long,
        val startTime: Instant,
        val endTime: Instant,
    ) : KHRecord()
}
