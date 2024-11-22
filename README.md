<h1 align="center">🏥 KHealth 🏥</h1>

<p align="center">
    <img src="assets/logo.svg" width="200"  alt=""/>
</p>

<p align="center">
    <img src="http://img.shields.io/badge/-android-6EDB8D.svg?style=flat" />
    <img src="http://img.shields.io/badge/-ios-CDCDCD.svg?style=flat" />
    <img src="http://img.shields.io/badge/-watchos-C0C0C0.svg?style=flat" />
</p>

**KHealth** (_short for Kotlin Health_) is a simple Kotlin Multiplatform wrapper over
Android's [Health Connect](https://developer.android.com/health-and-fitness/guides/health-connect)
and Apple's [HealthKit](https://developer.apple.com/documentation/healthkit) APIs. It provides a
simple and effective way to consume these native APIs in a Kotlin/Compose Multiplatform environment.

## Demo

> [!NOTE]  
> You can find the following app in the `sample*` directories (e.g. `sampleAndroidApp` and
`sampleAppleApps`)

https://github.com/user-attachments/assets/ef0ddd40-ce10-4143-9711-806d0687bc5b

https://github.com/user-attachments/assets/2dfa8a48-412c-4dc5-8ccd-6ce5149eccca

https://github.com/user-attachments/assets/8e6f609b-8f17-4370-8662-42ac0dad3bc0

## 🚀 Getting Started

![Maven Central Version](https://img.shields.io/maven-central/v/io.github.shubhamsinghshubham777/khealth?label=Stable)

Add the following to your shared module's `build.gradle.kts`:

```kotlin
implementation("io.github.shubhamsinghshubham777:khealth:0.0.1")
```

or add it to your version catalog:

```toml
[versions]
khealth = "0.0.1"

[libraries]
khealth = { module = "io.github.shubhamsinghshubham777:khealth", version.ref = "khealth" }

[plugins]
```

and use it in your `build.gradle.kts`:

```kotlin
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.khealth)
        }
    }
}
```

## ⚙️ Usage

1. (Android only) Add the following code in your `AndroidManifest.xml`:
   ```xml
   <uses-permission android:name="..." />
   
   <!-- Check if Health Connect is installed -->
   <queries>
        <package android:name="com.google.android.apps.healthdata" />
   </queries>
   
   <application ...>
        <!-- For supported versions through Android 13, create an activity to show the rationale
        of Health Connect permissions once users click the privacy policy link. -->
        <activity
            android:name=".PermissionsRationaleActivity"
            android:exported="true">
            <intent-filter>
                <action android:name="androidx.health.ACTION_SHOW_PERMISSIONS_RATIONALE" />
            </intent-filter>
        </activity>
   
        <!-- For versions starting Android 14, create an activity alias to show the rationale
             of Health Connect permissions once users click the privacy policy link. -->
        <activity-alias
            android:name="ViewPermissionUsageActivity"
            android:exported="true"
            android:permission="android.permission.START_VIEW_PERMISSION_USAGE"
            android:targetActivity=".PermissionsRationaleActivity">
            <intent-filter>
                <action android:name="android.intent.action.VIEW_PERMISSION_USAGE" />
                <category android:name="android.intent.category.HEALTH_PERMISSIONS" />
            </intent-filter>
        </activity-alias>
   </application>
   ```

2. (Android only) Add the dependencies you require to use in `AndroidManifest.xml`

   | Type                    | Permissions                                                                                                        |
   |-------------------------|--------------------------------------------------------------------------------------------------------------------|
   | ACTIVE_CALORIES_BURNED  | android.permission.health.READ_ACTIVE_CALORIES_BURNED<br/>android.permission.health.WRITE_ACTIVE_CALORIES_BURNED   |
   | BASAL_METABOLIC_RATE    | android.permission.health.READ_BASAL_METABOLIC_RATE<br/>android.permission.health.WRITE_BASAL_METABOLIC_RATE       |
   | BLOOD_GLUCOSE           | android.permission.health.READ_BLOOD_GLUCOSE<br/>android.permission.health.WRITE_BLOOD_GLUCOSE                     |
   | BLOOD_PRESSURE          | android.permission.health.READ_BLOOD_PRESSURE<br/>android.permission.health.WRITE_BLOOD_PRESSURE                   |
   | BODY_FAT                | android.permission.health.READ_BODY_FAT<br/>android.permission.health.WRITE_BODY_FAT                               |
   | BODY_TEMPERATURE        | android.permission.health.READ_BODY_TEMPERATURE<br/>android.permission.health.WRITE_BODY_TEMPERATURE               |
   | BODY_WATER_MASS         | android.permission.health.READ_BODY_WATER_MASS<br/>android.permission.health.WRITE_BODY_WATER_MASS                 |
   | BONE_MASS               | android.permission.health.READ_BONE_MASS<br/>android.permission.health.WRITE_BONE_MASS                             |
   | CERVICAL_MUCUS          | android.permission.health.READ_CERVICAL_MUCUS<br/>android.permission.health.WRITE_CERVICAL_MUCUS                   |
   | DISTANCE                | android.permission.health.READ_DISTANCE<br/>android.permission.health.WRITE_DISTANCE                               |
   | ELEVATION_GAINED        | android.permission.health.READ_ELEVATION_GAINED<br/>android.permission.health.WRITE_ELEVATION_GAINED               |
   | EXERCISE                | android.permission.health.READ_EXERCISE<br/>android.permission.health.WRITE_EXERCISE                               |
   | FLOORS_CLIMBED          | android.permission.health.READ_FLOORS_CLIMBED<br/>android.permission.health.WRITE_FLOORS_CLIMBED                   |
   | HEART_RATE              | android.permission.health.READ_HEART_RATE<br/>android.permission.health.WRITE_HEART_RATE                           |
   | HEART_RATE_VARIABILITY  | android.permission.health.READ_HEART_RATE_VARIABILITY<br/>android.permission.health.WRITE_HEART_RATE_VARIABILITY   |
   | HEIGHT                  | android.permission.health.READ_HEIGHT<br/>android.permission.health.WRITE_HEIGHT                                   |
   | HYDRATION               | android.permission.health.READ_HYDRATION<br/>android.permission.health.WRITE_HYDRATION                             |
   | INTERMENSTRUAL_BLEEDING | android.permission.health.READ_INTERMENSTRUAL_BLEEDING<br/>android.permission.health.WRITE_INTERMENSTRUAL_BLEEDING |
   | LEAN_BODY_MASS          | android.permission.health.READ_LEAN_BODY_MASS<br/>android.permission.health.WRITE_LEAN_BODY_MASS                   |
   | MENSTRUATION            | android.permission.health.READ_MENSTRUATION<br/>android.permission.health.WRITE_MENSTRUATION                       |
   | MENSTRUATION            | android.permission.health.READ_MENSTRUATION<br/>android.permission.health.WRITE_MENSTRUATION                       |
   | NUTRITION               | android.permission.health.READ_NUTRITION<br/>android.permission.health.WRITE_NUTRITION                             |
   | OVULATION_TEST          | android.permission.health.READ_OVULATION_TEST<br/>android.permission.health.WRITE_OVULATION_TEST                   |
   | OXYGEN_SATURATION       | android.permission.health.READ_OXYGEN_SATURATION<br/>android.permission.health.WRITE_OXYGEN_SATURATION             |
   | POWER                   | android.permission.health.READ_POWER<br/>android.permission.health.WRITE_POWER                                     |
   | RESPIRATORY_RATE        | android.permission.health.READ_RESPIRATORY_RATE<br/>android.permission.health.WRITE_RESPIRATORY_RATE               |
   | RESTING_HEART_RATE      | android.permission.health.READ_RESTING_HEART_RATE<br/>android.permission.health.WRITE_RESTING_HEART_RATE           |
   | SEXUAL_ACTIVITY         | android.permission.health.READ_SEXUAL_ACTIVITY<br/>android.permission.health.WRITE_SEXUAL_ACTIVITY                 |
   | SLEEP                   | android.permission.health.READ_SLEEP<br/>android.permission.health.WRITE_SLEEP                                     |
   | SPEED                   | android.permission.health.READ_SPEED<br/>android.permission.health.WRITE_SPEED                                     |
   | STEPS                   | android.permission.health.READ_STEPS<br/>android.permission.health.WRITE_STEPS                                     |
   | VO2_MAX                 | android.permission.health.READ_VO2_MAX<br/>android.permission.health.WRITE_VO2_MAX                                 |
   | WEIGHT                  | android.permission.health.READ_WEIGHT<br/>android.permission.health.WRITE_WEIGHT                                   |
   | WHEELCHAIR_PUSHES       | android.permission.health.READ_WHEELCHAIR_PUSHES<br/>android.permission.health.WRITE_WHEELCHAIR_PUSHES             |

3. Instantiate

    ```kotlin
    // On Apple (iOS, watchOS)
    val kHealth = KHealth()
    
    // On Android (inside a ComponentActivity)
    class MainActivity : ComponentActivity() {
      private val kHealth = KHealth(this)
    
      // Rest of your code
    }
    ```

4. Initialise (only required on Android)

    ```kotlin
    // Inside a `ComponentActivity`
    override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      // Initialise (only on Android, on Apple, this function is no-op)
      kHealth.initialise()
    }
    ```

5. Check Permission Status

   ```kotlin
    val permissionResponse: Set<KHPermission> = kHealth.checkPermissions(
        KHPermission.ActiveCaloriesBurned(read = true, write = true),
        KHPermission.HeartRate(read = true, write = false),
        // Add as many requests as you want
    )
   ```

6. Request Permissions

   ```kotlin
   // Same syntax as `checkPermissions`
    val permissionResponse: Set<KHPermission> = kHealth.requestPermissions(
        KHPermission.ActiveCaloriesBurned(read = true, write = true),
        KHPermission.HeartRate(read = true, write = false),
        // Add as many requests as you want
    )
   ```

7. Check if permission was granted

    ```kotlin
    val caloriesPermResponse = permissionResponse.first { response ->
        response is KHPermission.ActiveCaloriesBurned
    }.writeStatus

    val wasWritePermissionGranted = caloriesPermResponse.write == true 
    ```

8. Write records

   ```kotlin
   if (wasWritePermissionGranted) {
        val insertResponse: KHWriteResponse = kHealth.writeRecords(
            KHRecord.ActiveCaloriesBurned(
                unit = KHUnit.Energy.KiloCalorie,
                value = 3.4,
                startTime = Clock.System.now().minus(10.minutes),
                endTime = Clock.System.now(),
            ),
            KHRecord.HeartRate(
                samples = listOf(
                    KHHeartRateSample(
                        beatsPerMinute = 126,
                        time = Clock.System.now().minus(10.minutes)
                    )
                ),
            ),
            // Add as many records as you want
        )

        when (insertResponse) {
           is KHWriteResponse.Failed -> {
             println("Records insertion failed ❌ Reason: ${insertResponse.throwable}")
           }

           KHWriteResponse.SomeFailed -> println("Some records were not inserted ⚠️")

           KHWriteResponse.Success -> println("Records inserted ✅")
        }
   }
   ```

9. Read records
   ```kotlin
    val heartRateRecords = kHealth.readRecords(
        KHReadRequest.HeartRate(
            startTime = Clock.System.now().minus(1.days),
            endTime = Clock.System.now()
        )
    )
    println("Heart Rate records: $heartRateRecords")
   ```

## Supported Data Types (based on platforms)

KHealth supports reading and writing the following data types on the following platforms:

| Type                   | Android | Apple (iOS & watchOS) |
|------------------------|---------|-----------------------|
| ActiveCaloriesBurned   | ✅       | ✅                     |
| BasalMetabolicRate     | ✅       | ✅                     |
| BloodGlucose           | ✅       | ✅                     |
| BloodPressure          | ✅       | ✅                     |
| BodyFat                | ✅       | ✅                     |
| BodyTemperature        | ✅       | ✅                     |
| BodyWaterMass          | ✅       | ❌                     |
| BoneMass               | ✅       | ❌                     |
| CervicalMucus          | ✅       | ✅                     |
| CyclingPedalingCadence | ✅       | ❌                     |
| Distance               | ✅       | ✅                     |
| ElevationGained        | ✅       | ❌                     |
| FloorsClimbed          | ✅       | ✅                     |
| HeartRate              | ✅       | ✅                     |
| HeartRateVariability   | ✅       | ✅                     |
| Height                 | ✅       | ✅                     |
| Hydration              | ✅       | ✅                     |
| IntermenstrualBleeding | ✅       | ✅                     |
| LeanBodyMass           | ✅       | ✅                     |
| MenstruationPeriod     | ✅       | ❌                     |
| MenstruationFlow       | ✅       | ✅                     |
| OvulationTest          | ✅       | ✅                     |
| OxygenSaturation       | ✅       | ✅                     |
| Power                  | ✅       | ✅                     |
| RespiratoryRate        | ✅       | ✅                     |
| RestingHeartRate       | ✅       | ✅                     |
| SexualActivity         | ✅       | ✅                     |
| SleepSession           | ✅       | ✅                     |
| Speed                  | ✅       | ❌                     |
| RunningSpeed           | ❌       | ✅                     |
| CyclingSpeed           | ❌       | ✅                     |
| StepCount              | ✅       | ✅                     |
| Vo2Max                 | ✅       | ✅                     |
| Weight                 | ✅       | ✅                     |
| WheelChairPushes       | ✅       | ✅                     |

> [!NOTE]
> The unsupported data types will simply be ignored by all platforms.

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## 📄 License

This library is licensed under the Apache 2.0 License. See the [LICENSE](LICENSE) file for details.
