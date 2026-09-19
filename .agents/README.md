# KHealth Agent Context & Architecture Manual

This document provides AI agents and automated tools with high-fidelity context, architectural conventions, and engineering guidelines for the **KHealth** codebase.

---

## 1. Project Overview

- **Repository**: KHealth (`io.github.shubhamsinghshubham777:khealth`)
- **Language & Framework**: Kotlin Multiplatform (Kotlin 2.0+), Jetpack Compose, SwiftUI.
- **Targets**:
  - Android (`androidMain`): Integrates with Android **Health Connect** (`androidx.health.connect:connect-client`).
  - Apple (`appleMain`): Integrates with Apple **HealthKit** across `iosX64`, `iosArm64`, `iosSimulatorArm64`, `watchosX64`, `watchosArm64`, and `watchosSimulatorArm64`.
  - Common (`commonMain`): Provides unified, pure Kotlin interfaces, domain entities, permissions, read requests, and extension methods.

---

## 2. Directory Layout

```
KHealth/
├── .agents/                               # Agent instructions, context, and rule definitions
│   ├── README.md                          # Primary context & architecture documentation (this file)
│   ├── init.md                            # Entrypoint descriptor for agent tools
│   └── rules/
│       └── architecture.md                # Antigravity progressive rules for code generation
├── khealth/                               # Core published multiplatform library
│   └── src/
│       ├── commonMain/kotlin/com/khealth/ # Multiplatform public API contracts
│       │   ├── KHealth.kt                 # Interface KHealth and expect fun KHealth(): KHealth
│       │   ├── KHRecord.kt                # Sealed hierarchy of all health record data classes
│       │   ├── KHReadRequest.kt           # Generic sealed hierarchy: KHReadRequest<T : KHRecord>
│       │   ├── KHPermission.kt            # Sealed hierarchy of permissions with read/write booleans
│       │   ├── KHUnit.kt                  # Strongly-typed units (Energy, Length, Mass, etc.)
│       │   ├── KHWriteResponse.kt         # Success, SomeFailed, Failed results
│       │   ├── KHReadResponse.kt          # Generic and typed read responses
│       │   ├── KHExceptions.kt            # HealthKit / Health Connect domain exceptions
│       │   └── Extensions.kt              # Permission helpers: isGranted, allGranted, hasPermission
│       ├── androidMain/kotlin/com/khealth/# Health Connect platform implementation
│       │   ├── KHealth.android.kt         # Android KHealth actual implementation
│       │   ├── KHealthInitProvider.kt     # ContentProvider auto-initialization of application context
│       │   ├── KHealthContext.kt          # Thread-safe global Context holder
│       │   ├── AndroidPermissionMapper.kt # Health Connect permission string mapping
│       │   ├── AndroidRecordReader.kt     # Health Connect record -> KHRecord reading logic
│       │   ├── AndroidRecordWriter.kt     # KHRecord -> Health Connect record writing logic
│       │   └── Extensions.android.kt      # Unit math and mapper delegations
│       ├── appleMain/kotlin/com/khealth/  # HealthKit platform implementation (iOS + watchOS)
│       │   ├── KHealth.apple.kt           # Apple KHealth actual implementation (HKHealthStore)
│       │   ├── ApplePermissionMapper.kt   # HKObjectType mapping and status determination
│       │   ├── AppleRecordReader.kt       # HKSample querying and conversion to KHRecord
│       │   ├── AppleRecordWriter.kt       # KHRecord -> HKSample conversion logic
│       │   └── Extensions.apple.kt        # HKUnit conversions and sample merging helpers
│       ├── androidHostTest/               # Unit tests for Android record conversions & models
│       └── iosTest/                       # Unit tests for iOS / Apple platform logic
└── sampleApps/                            # Multiplatform sample applications
    ├── shared/                            # Shared KMP module (calls KHealth directly in commonMain)
    ├── android/                           # Native Android Jetpack Compose app
    └── apple/                             # Native Apple Xcode project (SwiftUI iOS & watchOS apps)
```

---

## 3. Core Architectural Contracts & Invariants

When modifying or generating code for KHealth, you **MUST** uphold these architectural rules:

### A. Zero-Boilerplate Instantiation
- `commonMain` exposes `interface KHealth` and `expect fun KHealth(): KHealth`.
- Consumers can call `val kHealth = KHealth()` anywhere in `commonMain` (ViewModels, repositories, Koin/DI modules).
- **Never** require consumers to pass an Android `Context` in common code.

### B. Android Auto-Initialization
- `khealth/src/androidMain/AndroidManifest.xml` registers `KHealthInitProvider` (`ContentProvider`).
- On app startup, `KHealthInitProvider` populates `KHealthContext.applicationContext`.
- If an activity is available or needed for permission prompt dialogs, consumers can call `kHealth.bindActivity(activity)` or use `KHealth(activity)`.
- `kHealth.initialise()` is deprecated and non-operational. Do not add mandatory setup steps.

### C. Type-Safe Generic Reads
- `KHReadRequest<T : KHRecord>` is generic over the specific record type.
- Every read request subclass specifies its type argument:
  ```kotlin
  data class HeartRate(...) : KHReadRequest<KHRecord.HeartRate>
  data class StepCount(...) : KHReadRequest<KHRecord.StepCount>
  ```
- `KHealth.readRecords(request: KHReadRequest<T>): List<T>` returns `List<T>` directly.
- **Never** revert to untyped lists or force consumers to cast `record as KHRecord.HeartRate`.

### D. Separation of Concerns (Single Responsibility Principle)
- Do **NOT** inflate `KHealth.apple.kt` or `KHealth.android.kt` with mapping or conversion logic.
- Keep mapping, reading, and writing in their dedicated mappers:
  - Permission checks: `ApplePermissionMapper` / `AndroidPermissionMapper`
  - Record querying: `AppleRecordReader` / `AndroidRecordReader`
  - Record writing: `AppleRecordWriter` / `AndroidRecordWriter`
  - Units & helpers: `Extensions.apple.kt` / `Extensions.android.kt`

### E. Normalized Units & No `KHEither`
- Basal Metabolic Rate (BMR) is standardized to `KHUnit.Energy` (kcal/day).
- Android Health Connect automatically converts between `KHUnit.Energy` and Power (Joules/sec / kcal/day).
- `KHEither` is permanently deleted. Use standard sealed hierarchies or Kotlin primitives.

---

## 4. How to Add a New Health Data Type

To support a new health data type across both platforms, follow these exact steps:

1. **Unit Definition (if new)**:
   - Add units in `khealth/src/commonMain/kotlin/com/khealth/KHUnit.kt`.
2. **Domain Record Model**:
   - Add a `data class` under `sealed interface KHRecord` in `khealth/src/commonMain/kotlin/com/khealth/KHRecord.kt`.
3. **Read Request Model**:
   - Add a `data class` under `sealed class KHReadRequest<T : KHRecord>` in `khealth/src/commonMain/kotlin/com/khealth/KHReadRequest.kt` specifying `: KHReadRequest<KHRecord.YourType>`.
4. **Permission Model**:
   - Add a `data class` under `sealed class KHPermission` in `khealth/src/commonMain/kotlin/com/khealth/KHPermission.kt`.
5. **Android Health Connect Integration**:
   - Add permission string mapping in `AndroidPermissionMapper.kt`.
   - Add write conversion to Health Connect `Record` in `AndroidRecordWriter.kt`.
   - Add read parsing from Health Connect `Record` to `KHRecord` in `AndroidRecordReader.kt`.
6. **Apple HealthKit Integration**:
   - Map to `HKQuantityType` or `HKCategoryType` in `ApplePermissionMapper.kt`.
   - Add write conversion to `HKSample` in `AppleRecordWriter.kt`.
   - Add read parsing from `HKSample` to `KHRecord` in `AppleRecordReader.kt`.
7. **Testing**:
    - Add conversion unit tests in `khealth/src/androidHostTest/kotlin/com/khealth/`.
    - Add multiplatform unit tests in `khealth/src/iosTest/kotlin/com/khealth/`.
8. **Documentation**:
    - Update the Supported Data Types table in `README.md`.

---

## 5. Verification Commands

Always verify changes using the following Gradle and Xcode commands before completing tasks:

| Scope | Command | Expected Result |
| :--- | :--- | :--- |
| **Android Compilation** | `./gradlew :khealth:compileAndroidMain` | Build Successful |
| **Android Unit Tests** | `./gradlew :khealth:testAndroidHostTest` | Tests Pass |
| **iOS Core Compilation** | `./gradlew :khealth:compileKotlinIosSimulatorArm64 :khealth:compileKotlinIosX64` | Build Successful |
| **watchOS Core Compilation** | `./gradlew :khealth:compileKotlinWatchosSimulatorArm64` | Build Successful |
| **iOS Simulator Tests** | `./gradlew :khealth:iosSimulatorArm64Test` | Tests Pass |
| **Shared Sample Framework** | `./gradlew :sampleApps:shared:linkDebugFrameworkIosSimulatorArm64` | Build Successful |
| **Android Sample App** | `./gradlew :sampleApps:android:assembleDebug` | Build Successful |
| **Apple iOS App (Xcode)** | `xcodebuild -project sampleApps/apple/sampleAppleApps.xcodeproj -scheme ios -destination "generic/platform=iOS Simulator"` | Build Succeeded |
| **Apple watchOS App (Xcode)** | `xcodebuild -project sampleApps/apple/sampleAppleApps.xcodeproj -scheme watchos -destination "generic/platform=watchOS Simulator"` | Build Succeeded |

---

## 6. Common Pitfalls & Guardrails

- **Apple Category Samples**: When querying category samples (e.g. cervical mucus, sleep stages, menstrual flow), remember they are instances of `HKCategorySample`. Never filter them out with `filterNot { it is HKCategorySample }`.
- **Blood Glucose Units**: In HealthKit, `1 mmol/L = 18.0182 mg/dL`. Double check that conversions are not inverted.
- **Activity Context Leaks**: Android `KHealthContext` holds only the `applicationContext`. Activities bound via `bindActivity` or `KHealth(activity)` should use `WeakReference` to avoid memory leaks.
- **Sample Apps Imports**: The shared module package is `com.khealth.sample`. Swift calls use `KHealthSampleKt`.
