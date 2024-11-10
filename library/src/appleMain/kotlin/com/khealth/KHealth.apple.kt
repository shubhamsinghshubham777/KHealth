package com.khealth

import kotlinx.cinterop.UnsafeNumber
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import platform.HealthKit.HKAuthorizationStatusNotDetermined
import platform.HealthKit.HKAuthorizationStatusSharingAuthorized
import platform.HealthKit.HKAuthorizationStatusSharingDenied
import platform.HealthKit.HKCategoryTypeIdentifierCervicalMucusQuality
import platform.HealthKit.HKCategoryTypeIdentifierIntermenstrualBleeding
import platform.HealthKit.HKCategoryTypeIdentifierMenstrualFlow
import platform.HealthKit.HKCategoryTypeIdentifierOvulationTestResult
import platform.HealthKit.HKCategoryTypeIdentifierSexualActivity
import platform.HealthKit.HKCategoryTypeIdentifierSleepAnalysis
import platform.HealthKit.HKHealthStore
import platform.HealthKit.HKObjectType
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
import platform.HealthKit.HKQuantityTypeIdentifierDietaryWater
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
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

actual class KHealth {
    private val store = HKHealthStore()

    actual val isHealthStoreAvailable: Boolean
        get() {
            return try {
                HKHealthStore.isHealthDataAvailable()
            } catch (t: Throwable) {
                false
            }
        }

    actual fun initialise() = Unit

    @OptIn(UnsafeNumber::class)
    actual suspend fun checkPermissions(
        vararg permissions: KHPermission
    ): Set<KHPermissionWithStatus> {
        try {
            val permissionsWithStatuses = permissions.mapNotNull { permission ->
                val type = permission.toHKObjectType()

                if (type == null) {
                    println("Type for $permission not found!")
                    return@mapNotNull null
                }

                KHPermissionWithStatus(
                    permission = permission,
                    // HealthKit does not provide status for READ permissions for privacy concerns
                    readStatus = KHPermissionStatus.NotDetermined,
                    writeStatus = when (store.authorizationStatusForType(type)) {
                        HKAuthorizationStatusSharingAuthorized -> KHPermissionStatus.Granted
                        HKAuthorizationStatusSharingDenied -> KHPermissionStatus.Denied
                        HKAuthorizationStatusNotDetermined -> KHPermissionStatus.NotDetermined
                        else -> throw Exception("Unknown authorization status!")
                    },
                )
            }

            return permissionsWithStatuses.toSet()
        } catch (t: Throwable) {
            logError(t)
            return emptySet()
        }
    }

    actual suspend fun requestPermissions(
        vararg permissions: KHPermission
    ): Set<KHPermissionWithStatus> = suspendCoroutine { continuation ->
        try {
            val coroutineScope = CoroutineScope(continuation.context)
            store.requestAuthorizationToShareTypes(
                typesToShare = getTypes(from = permissions, where = { it.write }),
                readTypes = getTypes(from = permissions, where = { it.read }),
                completion = { _, error ->
                    if (error != null) {
                        continuation.resumeWithException(Exception(error.localizedDescription))
                    } else {
                        coroutineScope.launch {
                            continuation.resume(checkPermissions(*permissions))
                        }
                    }
                }
            )
        } catch (t: Throwable) {
            logError(t)
            continuation.resume(emptySet())
        }
    }
}

private fun getTypes(from: Array<out KHPermission>, where: (KHPermission) -> Boolean) =
    from.filter(where).mapNotNull { it.toHKObjectType() }.toSet()

private fun KHPermission.toHKObjectType(): HKObjectType? {
    return when (this.dataType) {
        KHDataType.ActiveCaloriesBurned ->
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierActiveEnergyBurned)

        KHDataType.BasalMetabolicRate ->
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierBasalEnergyBurned)

        KHDataType.BloodGlucose ->
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierBloodGlucose)

        KHDataType.BloodPressureSystolic ->
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierBloodPressureSystolic)

        KHDataType.BloodPressureDiastolic ->
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierBloodPressureDiastolic)

        KHDataType.BodyFat ->
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierBodyFatPercentage)

        KHDataType.BodyTemperature ->
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierBodyTemperature)

        KHDataType.BodyWaterMass ->
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierBodyMass)

        KHDataType.BoneMass -> null

        KHDataType.CervicalMucus ->
            HKObjectType.categoryTypeForIdentifier(HKCategoryTypeIdentifierCervicalMucusQuality)

        KHDataType.CyclingPedalingCadence -> null

        KHDataType.Distance ->
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierDistanceWalkingRunning)

        KHDataType.ElevationGained -> null

        // TODO: Verify this
        KHDataType.ExerciseSession -> HKObjectType.workoutType()

        KHDataType.FloorsClimbed ->
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierFlightsClimbed)

        KHDataType.HeartRate ->
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierHeartRate)

        KHDataType.HeartRateVariability ->
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierHeartRateVariabilitySDNN)

        KHDataType.Height ->
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierHeight)

        KHDataType.Hydration ->
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierDietaryWater)

        KHDataType.IntermenstrualBleeding ->
            HKObjectType.categoryTypeForIdentifier(HKCategoryTypeIdentifierIntermenstrualBleeding)

        KHDataType.Menstruation -> null

        KHDataType.LeanBodyMass ->
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierLeanBodyMass)

        KHDataType.MenstruationFlow ->
            HKObjectType.categoryTypeForIdentifier(HKCategoryTypeIdentifierMenstrualFlow)

        KHDataType.OvulationTest ->
            HKObjectType.categoryTypeForIdentifier(HKCategoryTypeIdentifierOvulationTestResult)

        KHDataType.OxygenSaturation ->
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierOxygenSaturation)

        KHDataType.Power ->
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierCyclingPower)

        KHDataType.RespiratoryRate ->
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierRespiratoryRate)

        KHDataType.RestingHeartRate ->
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierRestingHeartRate)

        KHDataType.SexualActivity ->
            HKObjectType.categoryTypeForIdentifier(HKCategoryTypeIdentifierSexualActivity)

        KHDataType.SleepSession ->
            HKObjectType.categoryTypeForIdentifier(HKCategoryTypeIdentifierSleepAnalysis)

        KHDataType.RunningSpeed ->
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierRunningSpeed)

        KHDataType.CyclingSpeed ->
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierCyclingSpeed)

        KHDataType.StepCount ->
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierStepCount)

        KHDataType.Vo2Max ->
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierVO2Max)

        KHDataType.Weight ->
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierBodyMass)

        KHDataType.WheelChairPushes ->
            HKObjectType.quantityTypeForIdentifier(HKQuantityTypeIdentifierPushCount)
    }
}
