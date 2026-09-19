import SwiftUI
import sampleShared

class DashboardViewModel: ObservableObject {
    @Published var state: HealthDashboardState = HealthDashboardState(
        isHealthStoreAvailable: true,
        isLoading: false,
        isPermissionGranted: false,
        stepsToday: 0,
        stepGoal: 10000,
        latestHeartRateBpm: nil,
        activeCaloriesKcal: 0.0,
        hydrationLiters: 0.0,
        distanceMeters: 0.0,
        lastSyncTime: nil,
        statusMessage: nil,
        errorMessage: nil,
        recentLogs: []
    )
    
    private let manager = HealthDashboardManager()
    private var cancelObservation: (() -> Void)?
    
    init() {
        cancelObservation = manager.observeState { [weak self] newState in
            DispatchQueue.main.async {
                self?.state = newState
            }
        }
    }
    
    deinit {
        cancelObservation?()
    }
    
    func refresh() {
        manager.refreshDashboardData()
    }
    
    func requestPermissions() {
        manager.requestPermissions()
    }
    
    func logSteps(_ count: Int64) {
        manager.logSteps(count: count)
    }
    
    func logHydration(_ liters: Double) {
        manager.logHydration(liters: liters)
    }
    
    func logHeartRate(_ bpm: Double) {
        manager.logHeartRate(bpm: bpm)
    }
    
    func logWorkout(calories: Double, distanceMeters: Double) {
        manager.logWorkout(caloriesKcal: calories, distanceMeters: distanceMeters)
    }
}

struct ContentView: View {
    @StateObject private var viewModel = DashboardViewModel()
    
    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(spacing: 16) {
                    if !viewModel.state.isHealthStoreAvailable {
                        HStack(spacing: 12) {
                            Text("⚠️")
                                .font(.title2)
                            VStack(alignment: .leading, spacing: 4) {
                                Text("HealthKit Unavailable")
                                    .font(.headline)
                                    .foregroundColor(.red)
                                Text("HealthKit is not supported on this device.")
                                    .font(.subheadline)
                                    .foregroundColor(.secondary)
                            }
                            Spacer()
                        }
                        .padding()
                        .background(Color.red.opacity(0.1))
                        .cornerRadius(12)
                    }
                    
                    // Permissions Card
                    VStack(alignment: .leading, spacing: 10) {
                        HStack {
                            Text(viewModel.state.isPermissionGranted ? "✅" : "🔒")
                                .font(.title3)
                            VStack(alignment: .leading) {
                                Text(viewModel.state.isPermissionGranted ? "Permissions Active" : "Permissions Needed")
                                    .font(.headline)
                                Text(viewModel.state.isPermissionGranted ? "HealthKit read & write enabled" : "Grant access to read and record health data")
                                    .font(.caption)
                                    .foregroundColor(.secondary)
                            }
                            Spacer()
                            if !viewModel.state.isPermissionGranted {
                                Button("Grant") {
                                    viewModel.requestPermissions()
                                }
                                .buttonStyle(.borderedProminent)
                                .controlSize(.small)
                            }
                        }
                    }
                    .padding()
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .background(Color(.secondarySystemBackground))
                    .cornerRadius(12)
                    
                    // Steps Card
                    VStack(alignment: .leading, spacing: 12) {
                        HStack {
                            Text("👟 Steps Today")
                                .font(.headline)
                                .foregroundColor(.primary)
                            Spacer()
                        }
                        
                        HStack(alignment: .firstTextBaseline, spacing: 6) {
                            Text("\(viewModel.state.stepsToday)")
                                .font(.system(size: 34, weight: .bold, design: .rounded))
                            Text("/ \(viewModel.state.stepGoal) steps")
                                .font(.subheadline)
                                .foregroundColor(.secondary)
                        }
                        
                        ProgressView(value: Double(viewModel.state.stepProgress))
                            .tint(.blue)
                        
                        HStack(spacing: 10) {
                            Button("+500 Steps") {
                                viewModel.logSteps(500)
                            }
                            .buttonStyle(.bordered)
                            .controlSize(.small)
                            
                            Button("+1,000 Steps") {
                                viewModel.logSteps(1000)
                            }
                            .buttonStyle(.bordered)
                            .controlSize(.small)
                        }
                    }
                    .padding()
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .background(Color(.secondarySystemBackground))
                    .cornerRadius(12)
                    
                    // Heart Rate Card
                    VStack(alignment: .leading, spacing: 12) {
                        HStack {
                            Text("❤️ Heart Rate")
                                .font(.headline)
                            Spacer()
                        }
                        
                        if let bpm = viewModel.state.latestHeartRateBpm {
                            Text("\(Int(truncating: bpm)) BPM")
                                .font(.system(size: 30, weight: .bold, design: .rounded))
                                .foregroundColor(.red)
                        } else {
                            Text("-- BPM")
                                .font(.system(size: 30, weight: .bold, design: .rounded))
                                .foregroundColor(.secondary)
                        }
                        
                        Button("Record 72 BPM") {
                            viewModel.logHeartRate(72.0)
                        }
                        .buttonStyle(.bordered)
                        .controlSize(.small)
                    }
                    .padding()
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .background(Color(.secondarySystemBackground))
                    .cornerRadius(12)
                    
                    // Hydration Card
                    VStack(alignment: .leading, spacing: 12) {
                        HStack {
                            Text("💧 Hydration")
                                .font(.headline)
                            Spacer()
                        }
                        
                        Text("\(viewModel.state.hydrationMl) mL")
                            .font(.system(size: 30, weight: .bold, design: .rounded))
                            .foregroundColor(.cyan)
                        
                        HStack(spacing: 10) {
                            Button("+250 mL (Glass)") {
                                viewModel.logHydration(0.25)
                            }
                            .buttonStyle(.bordered)
                            .controlSize(.small)
                            
                            Button("+500 mL (Bottle)") {
                                viewModel.logHydration(0.50)
                            }
                            .buttonStyle(.bordered)
                            .controlSize(.small)
                        }
                    }
                    .padding()
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .background(Color(.secondarySystemBackground))
                    .cornerRadius(12)
                    
                    // Activity & Workout Card
                    VStack(alignment: .leading, spacing: 12) {
                        Text("🔥 Activity & Workout")
                            .font(.headline)
                        
                        HStack {
                            VStack(alignment: .leading) {
                                Text("Active Burn")
                                    .font(.caption)
                                    .foregroundColor(.secondary)
                                Text("\(Int(viewModel.state.activeCaloriesKcal)) kcal")
                                    .font(.title3)
                                    .fontWeight(.bold)
                            }
                            Spacer()
                            VStack(alignment: .leading) {
                                Text("Distance")
                                    .font(.caption)
                                    .foregroundColor(.secondary)
                                Text(String(format: "%.1f km", viewModel.state.distanceKm))
                                    .font(.title3)
                                    .fontWeight(.bold)
                            }
                            Spacer()
                        }
                        
                        Button(action: {
                            viewModel.logWorkout(calories: 150.0, distanceMeters: 1200.0)
                        }) {
                            Text("Log Workout (150 kcal, 1.2 km)")
                                .frame(maxWidth: .infinity)
                        }
                        .buttonStyle(.borderedProminent)
                        .controlSize(.regular)
                    }
                    .padding()
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .background(Color(.secondarySystemBackground))
                    .cornerRadius(12)
                    
                    // Recent Actions List
                    if !viewModel.state.recentLogs.isEmpty {
                        VStack(alignment: .leading, spacing: 8) {
                            Text("Recent Activity")
                                .font(.headline)
                            
                            ForEach(viewModel.state.recentLogs.prefix(5), id: \.self) { log in
                                HStack {
                                    Text("•")
                                    Text(log)
                                        .font(.subheadline)
                                }
                                .foregroundColor(.secondary)
                            }
                        }
                        .padding()
                        .frame(maxWidth: .infinity, alignment: .leading)
                        .background(Color(.secondarySystemBackground))
                        .cornerRadius(12)
                    }
                }
                .padding()
            }
            .navigationTitle("KHealth Dashboard")
            .toolbar {
                ToolbarItem(placement: .navigationBarTrailing) {
                    if viewModel.state.isLoading {
                        ProgressView()
                    } else {
                        Button("Refresh") {
                            viewModel.refresh()
                        }
                    }
                }
            }
        }
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
