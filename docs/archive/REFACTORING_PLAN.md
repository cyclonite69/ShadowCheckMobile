# Refactoring to Clean Architecture

## Target Structure

```
com.shadowcheck.mobile/
├── data/                    # Data layer (KEEP AS-IS)
│   ├── local/
│   │   ├── Entities.kt
│   │   ├── Daos.kt
│   │   └── ShadowCheckDatabase.kt
│   └── repository/          # NEW
│       ├── NetworkRepository.kt
│       ├── ScannerRepository.kt
│       └── WigleRepository.kt
│
├── domain/                  # Business logic (NEW)
│   ├── model/              # Domain models
│   │   ├── Network.kt
│   │   ├── Device.kt
│   │   └── Threat.kt
│   ├── usecase/            # Use cases
│   │   ├── StartScanningUseCase.kt
│   │   ├── DetectThreatsUseCase.kt
│   │   └── ExportDataUseCase.kt
│   └── repository/         # Repository interfaces
│       └── INetworkRepository.kt
│
├── presentation/           # UI layer (MOVE HERE)
│   ├── MainActivity.kt
│   ├── viewmodel/
│   │   ├── MainViewModel.kt
│   │   ├── ScannerViewModel.kt
│   │   └── StatsViewModel.kt
│   ├── screens/
│   │   ├── home/
│   │   ├── scanner/
│   │   ├── stats/
│   │   ├── maps/
│   │   └── settings/
│   ├── components/
│   │   ├── NetworkList.kt
│   │   ├── FilterPanel.kt
│   │   └── StatCard.kt
│   └── theme/
│       ├── Theme.kt
│       ├── Color.kt
│       └── Type.kt
│
├── network/                # API layer (MOVE HERE)
│   ├── WigleApi.kt
│   ├── WigleApiService.kt
│   └── dto/
│       ├── WigleNetwork.kt
│       └── WigleStats.kt
│
├── service/                # Android services (MOVE HERE)
│   └── ScannerService.kt
│
└── utils/                  # Utilities (MOVE HERE)
    ├── ExportUtils.kt
    ├── DeduplicationUtil.kt
    ├── SecureApiKeyManager.kt
    └── Extensions.kt
```

## File Reorganization

### 1. Move to `presentation/`
- MainActivity.kt
- All UI screens
- All UI components
- All ViewModels (create them)
- Theme files

### 2. Move to `network/`
- WigleApi.kt
- WigleApiService.kt
- WigleNetwork.kt
- WigleStats.kt
- WigleTransaction.kt
- WigleUploadResponse.kt
- All Wigle-related DTOs

### 3. Move to `service/`
- ScannerService.kt

### 4. Move to `utils/`
- ExportUtils.kt
- DeduplicationUtil.kt
- SecureApiKeyManager.kt
- AnimationsKt.kt
- GlassmorphicComponentsKt.kt

### 5. Move to `domain/model/`
- ThreatDetection.kt
- ThreatType.kt
- ThreatSeverity.kt
- RadioType.kt
- UnifiedSighting.kt

### 6. Rename Files (Remove "Kt" suffix)
- AnimationsKt.kt → Animations.kt
- ARNetworkViewKt.kt → ARNetworkView.kt
- ChannelGraphKt.kt → ChannelGraph.kt
- GlassmorphicComponentsKt.kt → GlassmorphicComponents.kt
- UnifiedDetailScreenKt.kt → UnifiedDetailScreen.kt

### 7. Create New Files

#### MainViewModel.kt
```kotlin
package com.shadowcheck.mobile.presentation.viewmodel

class MainViewModel(
    private val repository: NetworkRepository
) : ViewModel() {
    // State management
}
```

#### NetworkRepository.kt
```kotlin
package com.shadowcheck.mobile.data.repository

class NetworkRepository(
    private val database: ShadowCheckDatabase
) {
    fun getWifiNetworks() = database.wifiNetworkDao().getAllFlow()
    // etc
}
```

## Refactoring Steps

### Phase 1: Create Structure (5 min)
```bash
# Already done - folders created
```

### Phase 2: Move Files (10 min)
```bash
# Move presentation files
mv MainActivity.kt presentation/
mv ui/* presentation/

# Move network files
mv Wigle*.kt network/

# Move utils
mv *Utils.kt utils/
mv SecureApiKeyManager.kt utils/

# Move service
mv scanner/ScannerService.kt service/
```

### Phase 3: Rename Files (5 min)
```bash
# Remove Kt suffix
for file in *Kt.kt; do
    mv "$file" "${file/Kt.kt/.kt}"
done
```

### Phase 4: Update Imports (15 min)
- Update package declarations
- Update import statements
- Fix references

### Phase 5: Create ViewModels (30 min)
- Extract state from MainActivity
- Create MainViewModel
- Create ScannerViewModel
- Create StatsViewModel

### Phase 6: Create Repositories (20 min)
- Create NetworkRepository
- Create ScannerRepository
- Create WigleRepository

### Phase 7: Create Use Cases (20 min)
- StartScanningUseCase
- DetectThreatsUseCase
- ExportDataUseCase

## Benefits

✅ **Separation of Concerns** - Each layer has clear responsibility
✅ **Testability** - Easy to unit test each layer
✅ **Maintainability** - Easy to find and modify code
✅ **Scalability** - Easy to add new features
✅ **Clean Architecture** - Industry best practices

## Time Estimate

- **Automated moves**: 15 minutes
- **Manual refactoring**: 1-2 hours
- **Testing**: 30 minutes
- **Total**: 2-3 hours

## Start Refactoring?

Ready to begin? I can:
1. **Automate file moves** (quick)
2. **Create ViewModels** (medium)
3. **Create Repositories** (medium)
4. **Update all imports** (tedious but necessary)

Should I start?
