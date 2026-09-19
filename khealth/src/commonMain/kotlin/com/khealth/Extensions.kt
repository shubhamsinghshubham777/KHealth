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

/**
 * Checks whether this granted permission satisfies all requested capabilities in [requested].
 */
fun KHPermission.satisfies(requested: KHPermission): Boolean {
    if (this::class != requested::class) return false

    return when (this) {
        is KHPermission.BloodPressure -> {
            val req = requested as KHPermission.BloodPressure
            (!req.readSystolic || this.readSystolic) &&
                    (!req.writeSystolic || this.writeSystolic) &&
                    (!req.readDiastolic || this.readDiastolic) &&
                    (!req.writeDiastolic || this.writeDiastolic)
        }

        is KHPermission.Nutrition -> {
            val req = requested as KHPermission.Nutrition
            (!req.readBiotin || this.readBiotin) &&
                    (!req.writeBiotin || this.writeBiotin) &&
                    (!req.readCaffeine || this.readCaffeine) &&
                    (!req.writeCaffeine || this.writeCaffeine) &&
                    (!req.readCalcium || this.readCalcium) &&
                    (!req.writeCalcium || this.writeCalcium) &&
                    (!req.readChloride || this.readChloride) &&
                    (!req.writeChloride || this.writeChloride) &&
                    (!req.readCholesterol || this.readCholesterol) &&
                    (!req.writeCholesterol || this.writeCholesterol) &&
                    (!req.readChromium || this.readChromium) &&
                    (!req.writeChromium || this.writeChromium) &&
                    (!req.readCopper || this.readCopper) &&
                    (!req.writeCopper || this.writeCopper) &&
                    (!req.readDietaryFiber || this.readDietaryFiber) &&
                    (!req.writeDietaryFiber || this.writeDietaryFiber) &&
                    (!req.readEnergy || this.readEnergy) &&
                    (!req.writeEnergy || this.writeEnergy) &&
                    (!req.readFolicAcid || this.readFolicAcid) &&
                    (!req.writeFolicAcid || this.writeFolicAcid) &&
                    (!req.readIodine || this.readIodine) &&
                    (!req.writeIodine || this.writeIodine) &&
                    (!req.readIron || this.readIron) &&
                    (!req.writeIron || this.writeIron) &&
                    (!req.readMagnesium || this.readMagnesium) &&
                    (!req.writeMagnesium || this.writeMagnesium) &&
                    (!req.readManganese || this.readManganese) &&
                    (!req.writeManganese || this.writeManganese) &&
                    (!req.readMolybdenum || this.readMolybdenum) &&
                    (!req.writeMolybdenum || this.writeMolybdenum) &&
                    (!req.readMonounsaturatedFat || this.readMonounsaturatedFat) &&
                    (!req.writeMonounsaturatedFat || this.writeMonounsaturatedFat) &&
                    (!req.readNiacin || this.readNiacin) &&
                    (!req.writeNiacin || this.writeNiacin) &&
                    (!req.readPantothenicAcid || this.readPantothenicAcid) &&
                    (!req.writePantothenicAcid || this.writePantothenicAcid) &&
                    (!req.readPhosphorus || this.readPhosphorus) &&
                    (!req.writePhosphorus || this.writePhosphorus) &&
                    (!req.readPolyunsaturatedFat || this.readPolyunsaturatedFat) &&
                    (!req.writePolyunsaturatedFat || this.writePolyunsaturatedFat) &&
                    (!req.readPotassium || this.readPotassium) &&
                    (!req.writePotassium || this.writePotassium) &&
                    (!req.readProtein || this.readProtein) &&
                    (!req.writeProtein || this.writeProtein) &&
                    (!req.readRiboflavin || this.readRiboflavin) &&
                    (!req.writeRiboflavin || this.writeRiboflavin) &&
                    (!req.readSaturatedFat || this.readSaturatedFat) &&
                    (!req.writeSaturatedFat || this.writeSaturatedFat) &&
                    (!req.readSelenium || this.readSelenium) &&
                    (!req.writeSelenium || this.writeSelenium) &&
                    (!req.readSodium || this.readSodium) &&
                    (!req.writeSodium || this.writeSodium) &&
                    (!req.readSugar || this.readSugar) &&
                    (!req.writeSugar || this.writeSugar) &&
                    (!req.readThiamin || this.readThiamin) &&
                    (!req.writeThiamin || this.writeThiamin) &&
                    (!req.readTotalCarbohydrate || this.readTotalCarbohydrate) &&
                    (!req.writeTotalCarbohydrate || this.writeTotalCarbohydrate) &&
                    (!req.readTotalFat || this.readTotalFat) &&
                    (!req.writeTotalFat || this.writeTotalFat) &&
                    (!req.readVitaminA || this.readVitaminA) &&
                    (!req.writeVitaminA || this.writeVitaminA) &&
                    (!req.readVitaminB12 || this.readVitaminB12) &&
                    (!req.writeVitaminB12 || this.writeVitaminB12) &&
                    (!req.readVitaminB6 || this.readVitaminB6) &&
                    (!req.writeVitaminB6 || this.writeVitaminB6) &&
                    (!req.readVitaminC || this.readVitaminC) &&
                    (!req.writeVitaminC || this.writeVitaminC) &&
                    (!req.readVitaminD || this.readVitaminD) &&
                    (!req.writeVitaminD || this.writeVitaminD) &&
                    (!req.readVitaminE || this.readVitaminE) &&
                    (!req.writeVitaminE || this.writeVitaminE) &&
                    (!req.readVitaminK || this.readVitaminK) &&
                    (!req.writeVitaminK || this.writeVitaminK) &&
                    (!req.readZinc || this.readZinc) &&
                    (!req.writeZinc || this.writeZinc)
        }

        is KHPermission.ActiveCaloriesBurned -> {
            val req = requested as KHPermission.ActiveCaloriesBurned
            (!req.read || this.read) && (!req.write || this.write)
        }

        is KHPermission.BasalMetabolicRate -> {
            val req = requested as KHPermission.BasalMetabolicRate
            (!req.read || this.read) && (!req.write || this.write)
        }

        is KHPermission.BloodGlucose -> {
            val req = requested as KHPermission.BloodGlucose
            (!req.read || this.read) && (!req.write || this.write)
        }

        is KHPermission.BodyFat -> {
            val req = requested as KHPermission.BodyFat
            (!req.read || this.read) && (!req.write || this.write)
        }

        is KHPermission.BodyTemperature -> {
            val req = requested as KHPermission.BodyTemperature
            (!req.read || this.read) && (!req.write || this.write)
        }

        is KHPermission.BodyWaterMass -> {
            val req = requested as KHPermission.BodyWaterMass
            (!req.read || this.read) && (!req.write || this.write)
        }

        is KHPermission.BoneMass -> {
            val req = requested as KHPermission.BoneMass
            (!req.read || this.read) && (!req.write || this.write)
        }

        is KHPermission.CervicalMucus -> {
            val req = requested as KHPermission.CervicalMucus
            (!req.read || this.read) && (!req.write || this.write)
        }

        is KHPermission.CyclingPedalingCadence -> {
            val req = requested as KHPermission.CyclingPedalingCadence
            (!req.read || this.read) && (!req.write || this.write)
        }

        is KHPermission.CyclingSpeed -> {
            val req = requested as KHPermission.CyclingSpeed
            (!req.read || this.read) && (!req.write || this.write)
        }

        is KHPermission.Distance -> {
            val req = requested as KHPermission.Distance
            (!req.read || this.read) && (!req.write || this.write)
        }

        is KHPermission.ElevationGained -> {
            val req = requested as KHPermission.ElevationGained
            (!req.read || this.read) && (!req.write || this.write)
        }

        is KHPermission.Exercise -> {
            val req = requested as KHPermission.Exercise
            (!req.read || this.read) && (!req.write || this.write)
        }

        is KHPermission.FloorsClimbed -> {
            val req = requested as KHPermission.FloorsClimbed
            (!req.read || this.read) && (!req.write || this.write)
        }

        is KHPermission.HeartRate -> {
            val req = requested as KHPermission.HeartRate
            (!req.read || this.read) && (!req.write || this.write)
        }

        is KHPermission.HeartRateVariability -> {
            val req = requested as KHPermission.HeartRateVariability
            (!req.read || this.read) && (!req.write || this.write)
        }

        is KHPermission.Height -> {
            val req = requested as KHPermission.Height
            (!req.read || this.read) && (!req.write || this.write)
        }

        is KHPermission.Hydration -> {
            val req = requested as KHPermission.Hydration
            (!req.read || this.read) && (!req.write || this.write)
        }

        is KHPermission.IntermenstrualBleeding -> {
            val req = requested as KHPermission.IntermenstrualBleeding
            (!req.read || this.read) && (!req.write || this.write)
        }

        is KHPermission.LeanBodyMass -> {
            val req = requested as KHPermission.LeanBodyMass
            (!req.read || this.read) && (!req.write || this.write)
        }

        is KHPermission.MenstruationFlow -> {
            val req = requested as KHPermission.MenstruationFlow
            (!req.read || this.read) && (!req.write || this.write)
        }

        is KHPermission.MenstruationPeriod -> {
            val req = requested as KHPermission.MenstruationPeriod
            (!req.read || this.read) && (!req.write || this.write)
        }

        is KHPermission.OvulationTest -> {
            val req = requested as KHPermission.OvulationTest
            (!req.read || this.read) && (!req.write || this.write)
        }

        is KHPermission.OxygenSaturation -> {
            val req = requested as KHPermission.OxygenSaturation
            (!req.read || this.read) && (!req.write || this.write)
        }

        is KHPermission.Power -> {
            val req = requested as KHPermission.Power
            (!req.read || this.read) && (!req.write || this.write)
        }

        is KHPermission.RespiratoryRate -> {
            val req = requested as KHPermission.RespiratoryRate
            (!req.read || this.read) && (!req.write || this.write)
        }

        is KHPermission.RestingHeartRate -> {
            val req = requested as KHPermission.RestingHeartRate
            (!req.read || this.read) && (!req.write || this.write)
        }

        is KHPermission.RunningSpeed -> {
            val req = requested as KHPermission.RunningSpeed
            (!req.read || this.read) && (!req.write || this.write)
        }

        is KHPermission.SexualActivity -> {
            val req = requested as KHPermission.SexualActivity
            (!req.read || this.read) && (!req.write || this.write)
        }

        is KHPermission.SleepSession -> {
            val req = requested as KHPermission.SleepSession
            (!req.read || this.read) && (!req.write || this.write)
        }

        is KHPermission.Speed -> {
            val req = requested as KHPermission.Speed
            (!req.read || this.read) && (!req.write || this.write)
        }

        is KHPermission.StepCount -> {
            val req = requested as KHPermission.StepCount
            (!req.read || this.read) && (!req.write || this.write)
        }

        is KHPermission.Vo2Max -> {
            val req = requested as KHPermission.Vo2Max
            (!req.read || this.read) && (!req.write || this.write)
        }

        is KHPermission.Weight -> {
            val req = requested as KHPermission.Weight
            (!req.read || this.read) && (!req.write || this.write)
        }

        is KHPermission.WheelChairPushes -> {
            val req = requested as KHPermission.WheelChairPushes
            (!req.read || this.read) && (!req.write || this.write)
        }
    }
}

/**
 * Checks whether the given [permission] is granted in this [Set] of permissions.
 *
 * Example:
 * ```kotlin
 * val permissions = kHealth.checkPermissions(KHPermission.HeartRate(read = true))
 * if (permissions.isGranted(KHPermission.HeartRate(read = true))) {
 *     // Heart rate read permission is granted!
 * }
 * ```
 */
fun Set<KHPermission>.isGranted(permission: KHPermission): Boolean {
    val matched = firstOrNull { it::class == permission::class } ?: return false
    return matched.satisfies(permission)
}

/**
 * Checks whether all requested permissions in [permissions] are granted in this set.
 */
fun Set<KHPermission>.allGranted(vararg permissions: KHPermission): Boolean =
    permissions.all { isGranted(it) }

/**
 * Checks directly on [KHealth] if a specific permission is granted.
 */
suspend fun KHealth.hasPermission(permission: KHPermission): Boolean =
    checkPermissions(permission).isGranted(permission)

/**
 * Checks directly on [KHealth] if all specified permissions are granted.
 */
suspend fun KHealth.hasPermissions(vararg permissions: KHPermission): Boolean {
    val results = checkPermissions(*permissions)
    return permissions.all { results.isGranted(it) }
}
