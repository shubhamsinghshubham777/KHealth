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

import kotlin.time.Instant

/**
 * Represents the kind of [KHRecord] the app wants to read.
 */
sealed class KHReadRequest<T : KHRecord>(internal val startDateTime: Instant, internal val endDateTime: Instant) {
    /**
     * Depicts that the app wants to read records of type [KHRecord.ActiveCaloriesBurned] from
     * the health store.
     *
     * @param unit The measurement scale that the returned value should be converted to
     * @param startTime Filters the returned records list to at least start from this time instant
     * @param endTime Filters the returned records list to at most end at this time instant
     */
    data class ActiveCaloriesBurned(
        val unit: KHUnit.Energy,
        val startTime: Instant,
        val endTime: Instant
    ) : KHReadRequest<KHRecord.ActiveCaloriesBurned>(startDateTime = startTime, endDateTime = endTime)

    /**
     * Depicts that the app wants to read records of type [KHRecord.BasalMetabolicRate] from the
     * health store.
     *
     * @param unit The measurement scale that the returned value should be converted to
     * @param startTime Filters the returned records list to at least start from this time instant
     * @param endTime Filters the returned records list to at most end at this time instant
     */
    data class BasalMetabolicRate(
        val unit: KHUnit.Energy,
        val startTime: Instant,
        val endTime: Instant
    ) : KHReadRequest<KHRecord.BasalMetabolicRate>(startDateTime = startTime, endDateTime = endTime)

    /**
     * Depicts that the app wants to read records of type [KHRecord.BloodGlucose] from the
     * health store.
     *
     * @param unit The measurement scale that the returned value should be converted to
     * @param startTime Filters the returned records list to at least start from this time instant
     * @param endTime Filters the returned records list to at most end at this time instant
     */
    data class BloodGlucose(
        val unit: KHUnit.BloodGlucose,
        val startTime: Instant,
        val endTime: Instant
    ) : KHReadRequest<KHRecord.BloodGlucose>(startDateTime = startTime, endDateTime = endTime)

    /**
     * Depicts that the app wants to read records of type [KHRecord.BloodPressure] from the
     * health store.
     *
     * @param unit The measurement scale that the returned value should be converted to
     * @param startTime Filters the returned records list to at least start from this time instant
     * @param endTime Filters the returned records list to at most end at this time instant
     */
    data class BloodPressure(
        val unit: KHUnit.Pressure,
        val startTime: Instant,
        val endTime: Instant
    ) : KHReadRequest<KHRecord.BloodPressure>(startDateTime = startTime, endDateTime = endTime)

    /**
     * Depicts that the app wants to read records of type [KHRecord.BodyFat] from the health
     * store.
     *
     * @param startTime Filters the returned records list to at least start from this time instant
     * @param endTime Filters the returned records list to at most end at this time instant
     */
    data class BodyFat(
        val startTime: Instant,
        val endTime: Instant
    ) : KHReadRequest<KHRecord.BodyFat>(startDateTime = startTime, endDateTime = endTime)

    /**
     * Depicts that the app wants to read records of type [KHRecord.BodyTemperature] from the
     * health store.
     *
     * @param unit The measurement scale that the returned value should be converted to
     * @param startTime Filters the returned records list to at least start from this time instant
     * @param endTime Filters the returned records list to at most end at this time instant
     */
    data class BodyTemperature(
        val unit: KHUnit.Temperature,
        val startTime: Instant,
        val endTime: Instant
    ) : KHReadRequest<KHRecord.BodyTemperature>(startDateTime = startTime, endDateTime = endTime)

    /**
     * Depicts that the app wants to read records of type [KHRecord.BodyWaterMass] from the
     * health store.
     *
     * @param unit The measurement scale that the returned value should be converted to
     * @param startTime Filters the returned records list to at least start from this time instant
     * @param endTime Filters the returned records list to at most end at this time instant
     */
    data class BodyWaterMass(
        val unit: KHUnit.Mass,
        val startTime: Instant,
        val endTime: Instant
    ) : KHReadRequest<KHRecord.BodyWaterMass>(startDateTime = startTime, endDateTime = endTime)

    /**
     * Depicts that the app wants to read records of type [KHRecord.BoneMass] from the health
     * store.
     *
     * @param unit The measurement scale that the returned value should be converted to
     * @param startTime Filters the returned records list to at least start from this time instant
     * @param endTime Filters the returned records list to at most end at this time instant
     */
    data class BoneMass(
        val unit: KHUnit.Mass,
        val startTime: Instant,
        val endTime: Instant
    ) : KHReadRequest<KHRecord.BoneMass>(startDateTime = startTime, endDateTime = endTime)

    /**
     * Depicts that the app wants to read records of type [KHRecord.CervicalMucus] from the
     * health store.
     *
     * @param startTime Filters the returned records list to at least start from this time instant
     * @param endTime Filters the returned records list to at most end at this time instant
     */
    data class CervicalMucus(
        val startTime: Instant,
        val endTime: Instant
    ) : KHReadRequest<KHRecord.CervicalMucus>(startDateTime = startTime, endDateTime = endTime)

    /**
     * Depicts that the app wants to read records of type [KHRecord.CyclingPedalingCadence] from
     * the health store.
     *
     * @param startTime Filters the returned records list to at least start from this time instant
     * @param endTime Filters the returned records list to at most end at this time instant
     */
    data class CyclingPedalingCadence(
        val startTime: Instant,
        val endTime: Instant
    ) : KHReadRequest<KHRecord.CyclingPedalingCadence>(startDateTime = startTime, endDateTime = endTime)

    /**
     * Depicts that the app wants to read records of type [KHRecord.CyclingSpeed] from the
     * health store.
     *
     * @param unit The measurement scale that the returned value should be converted to
     * @param startTime Filters the returned records list to at least start from this time instant
     * @param endTime Filters the returned records list to at most end at this time instant
     */
    data class CyclingSpeed(
        val unit: KHUnit.Velocity,
        val startTime: Instant,
        val endTime: Instant
    ) : KHReadRequest<KHRecord.CyclingSpeed>(startDateTime = startTime, endDateTime = endTime)

    /**
     * Depicts that the app wants to read records of type [KHRecord.Distance] from the health
     * store.
     *
     * @param unit The measurement scale that the returned value should be converted to
     * @param startTime Filters the returned records list to at least start from this time instant
     * @param endTime Filters the returned records list to at most end at this time instant
     */
    data class Distance(
        val unit: KHUnit.Length,
        val startTime: Instant,
        val endTime: Instant
    ) : KHReadRequest<KHRecord.Distance>(startDateTime = startTime, endDateTime = endTime)

    /**
     * Depicts that the app wants to read records of type [KHRecord.ElevationGained] from the
     * health store.
     *
     * @param unit The measurement scale that the returned value should be converted to
     * @param startTime Filters the returned records list to at least start from this time instant
     * @param endTime Filters the returned records list to at most end at this time instant
     */
    data class ElevationGained(
        val unit: KHUnit.Length,
        val startTime: Instant,
        val endTime: Instant
    ) : KHReadRequest<KHRecord.ElevationGained>(startDateTime = startTime, endDateTime = endTime)

    /**
     * Depicts that the app wants to read records of type [KHRecord.Exercise] from the
     * health store.
     *
     * @param startTime Filters the returned records list to at least start from this time instant
     * @param endTime Filters the returned records list to at most end at this time instant
     */
    data class Exercise(
        val startTime: Instant,
        val endTime: Instant,
    ) : KHReadRequest<KHRecord.Exercise>(startDateTime = startTime, endDateTime = endTime)

    /**
     * Depicts that the app wants to read records of type [KHRecord.FloorsClimbed] from the
     * health store.
     *
     * @param startTime Filters the returned records list to at least start from this time instant
     * @param endTime Filters the returned records list to at most end at this time instant
     */
    data class FloorsClimbed(
        val startTime: Instant,
        val endTime: Instant
    ) : KHReadRequest<KHRecord.FloorsClimbed>(startDateTime = startTime, endDateTime = endTime)

    /**
     * Depicts that the app wants to read records of type [KHRecord.HeartRate] from the health
     * store.
     *
     * @param startTime Filters the returned records list to at least start from this time instant
     * @param endTime Filters the returned records list to at most end at this time instant
     */
    data class HeartRate(
        val startTime: Instant,
        val endTime: Instant
    ) : KHReadRequest<KHRecord.HeartRate>(startDateTime = startTime, endDateTime = endTime)

    /**
     * Depicts that the app wants to read records of type [KHRecord.HeartRateVariability] from
     * the health store.
     *
     * @param startTime Filters the returned records list to at least start from this time instant
     * @param endTime Filters the returned records list to at most end at this time instant
     */
    data class HeartRateVariability(
        val startTime: Instant,
        val endTime: Instant
    ) : KHReadRequest<KHRecord.HeartRateVariability>(startDateTime = startTime, endDateTime = endTime)

    /**
     * Depicts that the app wants to read records of type [KHRecord.Height] from the health
     * store.
     *
     * @param unit The measurement scale that the returned value should be converted to
     * @param startTime Filters the returned records list to at least start from this time instant
     * @param endTime Filters the returned records list to at most end at this time instant
     */
    data class Height(
        val unit: KHUnit.Length,
        val startTime: Instant,
        val endTime: Instant
    ) : KHReadRequest<KHRecord.Height>(startDateTime = startTime, endDateTime = endTime)

    /**
     * Depicts that the app wants to read records of type [KHRecord.Hydration] from the health
     * store.
     *
     * @param unit The measurement scale that the returned value should be converted to
     * @param startTime Filters the returned records list to at least start from this time instant
     * @param endTime Filters the returned records list to at most end at this time instant
     */
    data class Hydration(
        val unit: KHUnit.Volume,
        val startTime: Instant,
        val endTime: Instant
    ) : KHReadRequest<KHRecord.Hydration>(startDateTime = startTime, endDateTime = endTime)

    /**
     * Depicts that the app wants to read records of type [KHRecord.IntermenstrualBleeding] from
     * the health store.
     *
     * @param startTime Filters the returned records list to at least start from this time instant
     * @param endTime Filters the returned records list to at most end at this time instant
     */
    data class IntermenstrualBleeding(
        val startTime: Instant,
        val endTime: Instant
    ) : KHReadRequest<KHRecord.IntermenstrualBleeding>(startDateTime = startTime, endDateTime = endTime)

    /**
     * Depicts that the app wants to read records of type [KHRecord.LeanBodyMass] from the health
     * store.
     *
     * @param unit The measurement scale that the returned value should be converted to
     * @param startTime Filters the returned records list to at least start from this time instant
     * @param endTime Filters the returned records list to at most end at this time instant
     */
    data class LeanBodyMass(
        val unit: KHUnit.Mass,
        val startTime: Instant,
        val endTime: Instant
    ) : KHReadRequest<KHRecord.LeanBodyMass>(startDateTime = startTime, endDateTime = endTime)

    /**
     * Depicts that the app wants to read records of type [KHRecord.MenstruationPeriod] from the
     * health store.
     *
     * @param startTime Filters the returned records list to at least start from this time instant
     * @param endTime Filters the returned records list to at most end at this time instant
     */
    data class MenstruationPeriod(
        val startTime: Instant,
        val endTime: Instant
    ) : KHReadRequest<KHRecord.MenstruationPeriod>(startDateTime = startTime, endDateTime = endTime)

    /**
     * Depicts that the app wants to read records of type [KHRecord.MenstruationFlow] from the
     * health store.
     *
     * @param startTime Filters the returned records list to at least start from this time instant
     * @param endTime Filters the returned records list to at most end at this time instant
     */
    data class MenstruationFlow(
        val startTime: Instant,
        val endTime: Instant,
    ) : KHReadRequest<KHRecord.MenstruationFlow>(startDateTime = startTime, endDateTime = endTime)

    data class Nutrition(
        val solidUnit: KHUnit.Mass = KHUnit.Mass.Gram,
        val energyUnit: KHUnit.Energy = KHUnit.Energy.KiloCalorie,
        val startTime: Instant,
        val endTime: Instant
    ) : KHReadRequest<KHRecord.Nutrition>(startDateTime = startTime, endDateTime = endTime)

    /**
     * Depicts that the app wants to read records of type [KHRecord.OvulationTest] from the
     * health store.
     *
     * @param startTime Filters the returned records list to at least start from this time instant
     * @param endTime Filters the returned records list to at most end at this time instant
     */
    data class OvulationTest(
        val startTime: Instant,
        val endTime: Instant
    ) : KHReadRequest<KHRecord.OvulationTest>(startDateTime = startTime, endDateTime = endTime)

    /**
     * Depicts that the app wants to read records of type [KHRecord.OxygenSaturation] from the
     * health store.
     *
     * @param startTime Filters the returned records list to at least start from this time instant
     * @param endTime Filters the returned records list to at most end at this time instant
     */
    data class OxygenSaturation(
        val startTime: Instant,
        val endTime: Instant
    ) : KHReadRequest<KHRecord.OxygenSaturation>(startDateTime = startTime, endDateTime = endTime)

    /**
     * Depicts that the app wants to read records of type [KHRecord.Power] from the health
     * store.
     *
     * @param unit The measurement scale that the returned value should be converted to
     * @param startTime Filters the returned records list to at least start from this time instant
     * @param endTime Filters the returned records list to at most end at this time instant
     */
    data class Power(
        val unit: KHUnit.Power,
        val startTime: Instant,
        val endTime: Instant,
    ) : KHReadRequest<KHRecord.Power>(startDateTime = startTime, endDateTime = endTime)

    /**
     * Depicts that the app wants to read records of type [KHRecord.RespiratoryRate] from the
     * health store.
     *
     * @param startTime Filters the returned records list to at least start from this time instant
     * @param endTime Filters the returned records list to at most end at this time instant
     */
    data class RespiratoryRate(
        val startTime: Instant,
        val endTime: Instant,
    ) : KHReadRequest<KHRecord.RespiratoryRate>(startDateTime = startTime, endDateTime = endTime)

    /**
     * Depicts that the app wants to read records of type [KHRecord.RestingHeartRate] from the
     * health store.
     *
     * @param startTime Filters the returned records list to at least start from this time instant
     * @param endTime Filters the returned records list to at most end at this time instant
     */
    data class RestingHeartRate(
        val startTime: Instant,
        val endTime: Instant
    ) : KHReadRequest<KHRecord.RestingHeartRate>(startDateTime = startTime, endDateTime = endTime)

    /**
     * Depicts that the app wants to read records of type [KHRecord.RunningSpeed] from the
     * health store.
     *
     * @param unit The measurement scale that the returned value should be converted to
     * @param startTime Filters the returned records list to at least start from this time instant
     * @param endTime Filters the returned records list to at most end at this time instant
     */
    data class RunningSpeed(
        val unit: KHUnit.Velocity,
        val startTime: Instant,
        val endTime: Instant
    ) : KHReadRequest<KHRecord.RunningSpeed>(startDateTime = startTime, endDateTime = endTime)

    /**
     * Depicts that the app wants to read records of type [KHRecord.SexualActivity] from the
     * health store.
     *
     * @param startTime Filters the returned records list to at least start from this time instant
     * @param endTime Filters the returned records list to at most end at this time instant
     */
    data class SexualActivity(
        val startTime: Instant,
        val endTime: Instant
    ) : KHReadRequest<KHRecord.SexualActivity>(startDateTime = startTime, endDateTime = endTime)

    /**
     * Depicts that the app wants to read records of type [KHRecord.SleepSession] from the
     * health store.
     *
     * @param startTime Filters the returned records list to at least start from this time instant
     * @param endTime Filters the returned records list to at most end at this time instant
     */
    data class SleepSession(
        val startTime: Instant,
        val endTime: Instant
    ) : KHReadRequest<KHRecord.SleepSession>(startDateTime = startTime, endDateTime = endTime)

    /**
     * Depicts that the app wants to read records of type [KHRecord.Speed] from the health
     * store.
     *
     * @param unit The measurement scale that the returned value should be converted to
     * @param startTime Filters the returned records list to at least start from this time instant
     * @param endTime Filters the returned records list to at most end at this time instant
     */
    data class Speed(
        val unit: KHUnit.Velocity,
        val startTime: Instant,
        val endTime: Instant
    ) : KHReadRequest<KHRecord.Speed>(startDateTime = startTime, endDateTime = endTime)

    /**
     * Depicts that the app wants to read records of type [KHRecord.StepCount] from the
     * health store.
     *
     * @param startTime Filters the returned records list to at least start from this time instant
     * @param endTime Filters the returned records list to at most end at this time instant
     */
    data class StepCount(
        val startTime: Instant,
        val endTime: Instant
    ) : KHReadRequest<KHRecord.StepCount>(startDateTime = startTime, endDateTime = endTime)

    /**
     * Depicts that the app wants to read records of type [KHRecord.Vo2Max] from the health
     * store.
     *
     * @param startTime Filters the returned records list to at least start from this time instant
     * @param endTime Filters the returned records list to at most end at this time instant
     */
    data class Vo2Max(
        val startTime: Instant,
        val endTime: Instant
    ) : KHReadRequest<KHRecord.Vo2Max>(startDateTime = startTime, endDateTime = endTime)

    /**
     * Depicts that the app wants to read records of type [KHRecord.Weight] from the health
     * store.
     *
     * @param unit The measurement scale that the returned value should be converted to
     * @param startTime Filters the returned records list to at least start from this time instant
     * @param endTime Filters the returned records list to at most end at this time instant
     */
    data class Weight(
        val unit: KHUnit.Mass,
        val startTime: Instant,
        val endTime: Instant
    ) : KHReadRequest<KHRecord.Weight>(startDateTime = startTime, endDateTime = endTime)

    /**
     * Depicts that the app wants to read records of type [KHRecord.WheelChairPushes] from the
     * health store.
     *
     * @param startTime Filters the returned records list to at least start from this time instant
     * @param endTime Filters the returned records list to at most end at this time instant
     */
    data class WheelChairPushes(
        val startTime: Instant,
        val endTime: Instant,
    ) : KHReadRequest<KHRecord.WheelChairPushes>(startDateTime = startTime, endDateTime = endTime)
}
