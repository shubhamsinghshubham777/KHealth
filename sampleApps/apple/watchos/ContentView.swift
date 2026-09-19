import SwiftUI
import sampleShared

class WatchDashboardViewModel: ObservableObject {
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
    
    func logWorkout() {
        manager.logWorkout(caloriesKcal: 150.0, distanceMeters: 1200.0)
    }
}

struct ContentView: View {
    @StateObject private var viewModel = WatchDashboardViewModel()
    
    var body: some View {
        ScrollView {
            VStack(spacing: 12) {
                // Header / Permissions
                if !viewModel.state.isPermissionGranted {
                    Button(action: {
                        viewModel.requestPermissions()
                    }) {
                        Label("Grant Access", systemImage: "lock.shield")
                            .font(.caption2)
                    }
                    .tint(.orange)
                }
                
                // Status banner if any
                if let status = viewModel.state.statusMessage {
                    Text(status)
                        .font(.caption2)
                        .foregroundColor(.green)
                        .multilineTextAlignment(.center)
                }
                
                // Steps Card
                VStack(spacing: 6) {
                    HStack {
                        Image(systemName: "figure.walk")
                            .foregroundColor(.blue)
                        Text("Steps")
                            .font(.caption)
                            .foregroundColor(.secondary)
                        Spacer()
                    }
                    
                    HStack(alignment: .firstTextBaseline) {
                        Text("\(viewModel.state.stepsToday)")
                            .font(.system(size: 24, weight: .bold, design: .rounded))
                        Text("/ 10k")
                            .font(.caption2)
                            .foregroundColor(.secondary)
                        Spacer()
                    }
                    
                    ProgressView(value: Double(viewModel.state.stepProgress))
                        .tint(.blue)
                    
                    Button("+500 Steps") {
                        viewModel.logSteps(500)
                    }
                    .font(.caption2)
                    .tint(.blue)
                }
                .padding(10)
                .background(Color.white.opacity(0.1))
                .cornerRadius(12)
                
                // Heart Rate Card
                VStack(spacing: 6) {
                    HStack {
                        Image(systemName: "heart.fill")
                            .foregroundColor(.red)
                        Text("Heart Rate")
                            .font(.caption)
                            .foregroundColor(.secondary)
                        Spacer()
                    }
                    
                    HStack {
                        if let bpm = viewModel.state.latestHeartRateBpm {
                            Text("\(Int(truncating: bpm)) BPM")
                                .font(.system(size: 22, weight: .bold, design: .rounded))
                                .foregroundColor(.red)
                        } else {
                            Text("-- BPM")
                                .font(.system(size: 22, weight: .bold, design: .rounded))
                                .foregroundColor(.secondary)
                        }
                        Spacer()
                    }
                    
                    Button("Record 72 BPM") {
                        viewModel.logHeartRate(72.0)
                    }
                    .font(.caption2)
                    .tint(.red)
                }
                .padding(10)
                .background(Color.white.opacity(0.1))
                .cornerRadius(12)
                
                // Hydration Card
                VStack(spacing: 6) {
                    HStack {
                        Image(systemName: "drop.fill")
                            .foregroundColor(.cyan)
                        Text("Water")
                            .font(.caption)
                            .foregroundColor(.secondary)
                        Spacer()
                    }
                    
                    HStack {
                        Text("\(viewModel.state.hydrationMl) mL")
                            .font(.system(size: 22, weight: .bold, design: .rounded))
                            .foregroundColor(.cyan)
                        Spacer()
                    }
                    
                    Button("+250 mL") {
                        viewModel.logHydration(0.25)
                    }
                    .font(.caption2)
                    .tint(.cyan)
                }
                .padding(10)
                .background(Color.white.opacity(0.1))
                .cornerRadius(12)
                
                // Quick Workout
                Button(action: {
                    viewModel.logWorkout()
                }) {
                    Label("Log Workout", systemImage: "flame.fill")
                        .font(.caption)
                }
                .tint(.green)
                
                // Refresh
                Button(action: {
                    viewModel.refresh()
                }) {
                    Label("Refresh", systemImage: "arrow.clockwise")
                        .font(.caption2)
                }
            }
            .padding(.horizontal, 4)
        }
        .navigationTitle("KHealth")
    }
}

#Preview {
    ContentView()
}
