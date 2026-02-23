# Multi-Module Architecture

## Module Structure

```
ShadowCheckMobile/
├── app/                    (Application module)
│   ├── UI (Compose screens)
│   ├── ViewModels
│   └── DI configuration
│
├── domain/                 (Business logic module)
│   ├── Use cases
│   ├── Repository interfaces
│   └── Business rules
│
├── data/                   (Data access module)
│   ├── Repository implementations
│   ├── Room database
│   ├── Network API
│   └── Data sources
│
└── core/                   (Shared module)
    ├── Domain models
    ├── Utilities
    └── Common interfaces
```

## Module Dependencies

```
app → domain → core
app → data → domain → core
```

## Benefits

✅ **Enforced Boundaries** - Modules can't access internals
✅ **Parallel Builds** - Faster compilation
✅ **Feature Isolation** - Independent development
✅ **Reusability** - Share modules across projects
✅ **Clear Architecture** - Explicit dependencies

## Module Responsibilities

### :core
- Domain models (WifiNetwork, BluetoothDevice, etc.)
- Common utilities
- No Android dependencies

### :domain
- Use cases (business logic)
- Repository interfaces
- Platform-independent

### :data
- Repository implementations
- Room database
- Retrofit API
- Data caching

### :app
- UI (Compose screens)
- ViewModels
- Dependency injection
- Android framework

## Build

```bash
./gradlew :core:build
./gradlew :domain:build
./gradlew :data:build
./gradlew :app:assembleDebug
```
