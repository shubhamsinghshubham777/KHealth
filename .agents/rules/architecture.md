---
description: Core architecture rules and engineering invariants for KHealth Kotlin Multiplatform library.
trigger: always_on
---

# KHealth Architectural Invariants & Rules

When modifying, generating, or refactoring code in this repository, you must adhere to the following invariants:

1. **Common Multiplatform API**:
   - `commonMain` exposes `interface KHealth` with `expect fun KHealth(): KHealth`.
   - Never introduce platform types (`android.content.Context`, `HKHealthStore`, etc.) into `commonMain`.
   - Never require passing `Context` in common multiplatform classes.

2. **Android Context Management**:
   - The application context is auto-initialized on Android via `KHealthInitProvider` (`ContentProvider`) into `KHealthContext`.
   - The zero-arg constructor `KHealth()` relies on `KHealthContext.applicationContext`.
   - Any Activity references (e.g. for `requestPermissions` contract launching) must use `WeakReference<ComponentActivity>` to prevent memory leaks.

3. **Type-Safe Generic Requests**:
   - Every read request extends `KHReadRequest<T : KHRecord>`.
   - `KHealth.readRecords(request: KHReadRequest<T>): List<T>` must return `List<T>` directly without requiring the caller to cast.

4. **Code Organization & Modularity**:
   - Platform implementations in `androidMain` and `appleMain` must remain lean coordinators.
   - Separate concerns into:
     - `*PermissionMapper` for mapping permission objects and checking authorization.
     - `*RecordReader` for querying platform stores and parsing records.
     - `*RecordWriter` for constructing platform records and writing to the store.
     - `Extensions.*.kt` for unit conversions and mathematical helpers.

5. **Units & Data Models**:
   - Basal Metabolic Rate (BMR) uses `KHUnit.Energy` (kcal/day).
   - Never reintroduce `KHEither`.

6. **Sample Apps Separation**:
   - `sampleApps/shared`: Shared KMP logic and repository layers.
   - `sampleApps/android`: Android Jetpack Compose UI.
   - `sampleApps/apple`: SwiftUI iOS and watchOS UIs.
