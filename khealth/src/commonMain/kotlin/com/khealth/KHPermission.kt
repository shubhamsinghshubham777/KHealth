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
 * Depicts an instance of the app's request to the operating system to provide it with the access to
 * read and/or write a particular kind of data from/to the health store.
 */
sealed class KHPermission {
    data class ActiveCaloriesBurned(
        val read: Boolean = false,
        val write: Boolean = false
    ) : KHPermission()

    data class BasalMetabolicRate(
        val read: Boolean = false,
        val write: Boolean = false
    ) : KHPermission()

    data class BloodGlucose(
        val read: Boolean = false,
        val write: Boolean = false
    ) : KHPermission()

    data class BloodPressure(
        val readSystolic: Boolean = false,
        val writeSystolic: Boolean = false,
        val readDiastolic: Boolean = false,
        val writeDiastolic: Boolean = false
    ) : KHPermission()

    data class BodyFat(
        val read: Boolean = false,
        val write: Boolean = false
    ) : KHPermission()

    data class BodyTemperature(
        val read: Boolean = false,
        val write: Boolean = false
    ) : KHPermission()

    data class BodyWaterMass(
        val read: Boolean = false,
        val write: Boolean = false
    ) : KHPermission()

    data class BoneMass(
        val read: Boolean = false,
        val write: Boolean = false
    ) : KHPermission()

    data class CervicalMucus(
        val read: Boolean = false,
        val write: Boolean = false
    ) : KHPermission()

    data class CyclingPedalingCadence(
        val read: Boolean = false,
        val write: Boolean = false
    ) : KHPermission()

    data class CyclingSpeed(
        val read: Boolean = false,
        val write: Boolean = false
    ) : KHPermission()

    data class Distance(
        val read: Boolean = false,
        val write: Boolean = false
    ) : KHPermission()

    data class ElevationGained(
        val read: Boolean = false,
        val write: Boolean = false
    ) : KHPermission()

    data class Exercise(
        val read: Boolean = false,
        val write: Boolean = false,
    ) : KHPermission()

    data class FloorsClimbed(
        val read: Boolean = false,
        val write: Boolean = false
    ) : KHPermission()

    data class HeartRate(
        val read: Boolean = false,
        val write: Boolean = false
    ) : KHPermission()

    data class HeartRateVariability(
        val read: Boolean = false,
        val write: Boolean = false
    ) : KHPermission()

    data class Height(
        val read: Boolean = false,
        val write: Boolean = false
    ) : KHPermission()

    data class Hydration(
        val read: Boolean = false,
        val write: Boolean = false
    ) : KHPermission()

    data class IntermenstrualBleeding(
        val read: Boolean = false,
        val write: Boolean = false
    ) : KHPermission()

    data class LeanBodyMass(
        val read: Boolean = false,
        val write: Boolean = false
    ) : KHPermission()

    data class MenstruationPeriod(
        val read: Boolean = false,
        val write: Boolean = false
    ) : KHPermission()

    data class MenstruationFlow(
        val read: Boolean = false,
        val write: Boolean = false
    ) : KHPermission()

    data class Nutrition(
        val readBiotin: Boolean = false,
        val writeBiotin: Boolean = false,
        val readCaffeine: Boolean = false,
        val writeCaffeine: Boolean = false,
        val readCalcium: Boolean = false,
        val writeCalcium: Boolean = false,
        val readChloride: Boolean = false,
        val writeChloride: Boolean = false,
        val readCholesterol: Boolean = false,
        val writeCholesterol: Boolean = false,
        val readChromium: Boolean = false,
        val writeChromium: Boolean = false,
        val readCopper: Boolean = false,
        val writeCopper: Boolean = false,
        val readDietaryFiber: Boolean = false,
        val writeDietaryFiber: Boolean = false,
        val readEnergy: Boolean = false,
        val writeEnergy: Boolean = false,
        val readFolicAcid: Boolean = false,
        val writeFolicAcid: Boolean = false,
        val readIodine: Boolean = false,
        val writeIodine: Boolean = false,
        val readIron: Boolean = false,
        val writeIron: Boolean = false,
        val readMagnesium: Boolean = false,
        val writeMagnesium: Boolean = false,
        val readManganese: Boolean = false,
        val writeManganese: Boolean = false,
        val readMolybdenum: Boolean = false,
        val writeMolybdenum: Boolean = false,
        val readMonounsaturatedFat: Boolean = false,
        val writeMonounsaturatedFat: Boolean = false,
        val readNiacin: Boolean = false,
        val writeNiacin: Boolean = false,
        val readPantothenicAcid: Boolean = false,
        val writePantothenicAcid: Boolean = false,
        val readPhosphorus: Boolean = false,
        val writePhosphorus: Boolean = false,
        val readPolyunsaturatedFat: Boolean = false,
        val writePolyunsaturatedFat: Boolean = false,
        val readPotassium: Boolean = false,
        val writePotassium: Boolean = false,
        val readProtein: Boolean = false,
        val writeProtein: Boolean = false,
        val readRiboflavin: Boolean = false,
        val writeRiboflavin: Boolean = false,
        val readSaturatedFat: Boolean = false,
        val writeSaturatedFat: Boolean = false,
        val readSelenium: Boolean = false,
        val writeSelenium: Boolean = false,
        val readSodium: Boolean = false,
        val writeSodium: Boolean = false,
        val readSugar: Boolean = false,
        val writeSugar: Boolean = false,
        val readThiamin: Boolean = false,
        val writeThiamin: Boolean = false,
        val readTotalCarbohydrate: Boolean = false,
        val writeTotalCarbohydrate: Boolean = false,
        val readTotalFat: Boolean = false,
        val writeTotalFat: Boolean = false,
        val readVitaminA: Boolean = false,
        val writeVitaminA: Boolean = false,
        val readVitaminB12: Boolean = false,
        val writeVitaminB12: Boolean = false,
        val readVitaminB6: Boolean = false,
        val writeVitaminB6: Boolean = false,
        val readVitaminC: Boolean = false,
        val writeVitaminC: Boolean = false,
        val readVitaminD: Boolean = false,
        val writeVitaminD: Boolean = false,
        val readVitaminE: Boolean = false,
        val writeVitaminE: Boolean = false,
        val readVitaminK: Boolean = false,
        val writeVitaminK: Boolean = false,
        val readZinc: Boolean = false,
        val writeZinc: Boolean = false,
    ) : KHPermission()

    data class OvulationTest(
        val read: Boolean = false,
        val write: Boolean = false
    ) : KHPermission()

    data class OxygenSaturation(
        val read: Boolean = false,
        val write: Boolean = false
    ) : KHPermission()

    data class Power(
        val read: Boolean = false,
        val write: Boolean = false
    ) : KHPermission()

    data class RespiratoryRate(
        val read: Boolean = false,
        val write: Boolean = false
    ) : KHPermission()

    data class RestingHeartRate(
        val read: Boolean = false,
        val write: Boolean = false
    ) : KHPermission()

    data class RunningSpeed(
        val read: Boolean = false,
        val write: Boolean = false
    ) : KHPermission()

    data class SexualActivity(
        val read: Boolean = false,
        val write: Boolean = false
    ) : KHPermission()

    data class SleepSession(
        val read: Boolean = false,
        val write: Boolean = false
    ) : KHPermission()

    data class Speed(
        val read: Boolean = false,
        val write: Boolean = false
    ) : KHPermission()

    data class StepCount(
        val read: Boolean = false,
        val write: Boolean = false
    ) : KHPermission()

    data class Vo2Max(
        val read: Boolean = false,
        val write: Boolean = false
    ) : KHPermission()

    data class Weight(
        val read: Boolean = false,
        val write: Boolean = false
    ) : KHPermission()

    data class WheelChairPushes(
        val read: Boolean = false,
        val write: Boolean = false
    ) : KHPermission()
}
