# Contributing to ShadowCheckMobile

Welcome to ShadowCheckMobile! This guide will help you understand our codebase architecture, development patterns, and contribution workflow.

## Important Context: Dual Architecture

**ShadowCheckMobile contains two architectural patterns:**

1. **Legacy Architecture** - Original code from APK recovery, now cleaned up and functional
   - Direct ViewModel-to-Database access
   - Manual singleton database instances
   - Monolithic ViewModels
   - Located primarily in older packages

2. **Modern Clean Architecture** - New development standard (USE THIS)
   - Hilt dependency injection
   - Repository pattern with domain/data/presentation layers
   - Single-responsibility use cases
   - Feature-specific ViewModels
   - All new code follows this pattern

**For all new development, use the Modern Clean Architecture.** If you need to modify legacy code, consider refactoring it to the new pattern if feasible.

---

## Code Standards

### Dependency Injection (Non-Negotiable)

**All dependencies must be injected via Hilt.** Never use manual singletons or service locators.

❌ **DON'T:**
```kotlin
val database = ShadowCheckDatabase.getInstance(context)
val dao = database.wifiNetworkDao()
```

✅ **DO:**
```kotlin
@HiltViewModel
class WifiViewModel @Inject constructor(
    private val getAllWifiNetworks: GetAllWifiNetworksUseCase
) : ViewModel()
```

### Clean Architecture Layers

Our architecture is organized into three distinct layers:

**1. Domain Layer** (Business Logic - No Android Dependencies)
   - **Repository Interfaces**: Contracts defining data operations
   - **Use Cases**: Single-responsibility business operations
   - **Domain Models**: Pure Kotlin data classes representing business entities
   - Most stable layer - changes rarely

**2. Data Layer** (Data Access & External APIs)
   - **Repository Implementations**: Concrete data access logic
   - **DAOs**: Room database access
   - **API Services**: Retrofit interfaces
   - **DTOs**: Data transfer objects for network responses
   - **Database Entities**: Room @Entity classes
   - Contains all Android-specific data access (Room, Retrofit)

**3. Presentation Layer** (UI & State Management)
   - **ViewModels**: UI state management with StateFlow
   - **Composable Screens**: UI components (read-only, minimal logic)
   - **UI State Classes**: Sealed classes for screen states
   - Depends only on domain layer (uses use cases)

### Data Flow Rules

**Repositories:**
- Return `Flow<T>` for read operations (reactive, continuous updates)
- Use `suspend` functions for write operations (one-time coroutine operations)
- Always run on `Dispatchers.IO` using `.flowOn()` or `withContext()`

```kotlin
class WifiNetworkRepositoryImpl @Inject constructor(
    private val dao: WifiNetworkDao,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) : WifiNetworkRepository {

    // Read: Return Flow for reactive updates
    override fun getAllNetworks(): Flow<List<WifiNetwork>> {
        return dao.getAllNetworks()
            .map { entities -> entities.map { it.toDomainModel() } }
            .flowOn(dispatcher)
    }

    // Write: Suspend function for one-time operation
    override suspend fun insertNetwork(network: WifiNetwork): Long {
        return withContext(dispatcher) {
            dao.insert(network.toEntity())
        }
    }
}
```

**Use Cases:**
- Contain business logic (filtering, sorting, validation, transformation)
- Orchestrate repository calls
- Keep ViewModels thin and focused on UI state

```kotlin
@Singleton
class GetAllWifiNetworksUseCase @Inject constructor(
    private val repository: WifiNetworkRepository
) {
    operator fun invoke(): Flow<List<WifiNetwork>> {
        return repository.getAllNetworks()
            .map { networks -> networks.filter { it.ssid.isNotBlank() } }
            .map { networks -> networks.sortedByDescending { it.timestamp } }
    }
}
```

**ViewModels:**
- Orchestrate use cases
- Manage UI state with `StateFlow` or `MutableStateFlow`
- Never access DAOs directly
- Use `viewModelScope` for coroutines

```kotlin
@HiltViewModel
class WifiViewModel @Inject constructor(
    private val getAllWifiNetworks: GetAllWifiNetworksUseCase
) : ViewModel() {

    private val _networks = MutableStateFlow<List<WifiNetwork>>(emptyList())
    val networks: StateFlow<List<WifiNetwork>> = _networks.asStateFlow()

    init {
        getAllWifiNetworks()
            .onEach { _networks.value = it }
            .launchIn(viewModelScope)
    }
}
```

**All Async Operations:**
- Use Kotlin coroutines with `viewModelScope` or `lifecycleScope`
- Never use `.get()` on LiveData or block threads
- Prefer Flow over callbacks

### Code Style

**Formatting:**
- Max line length: 120 characters
- Indentation: 4 spaces (Kotlin, Java, XML)
- Follow `.editorconfig` settings

**Naming:**
- Use meaningful variable names
- Avoid single letters except loop counters (`i`, `j`, `k`)
- No decompilation artifacts (`var1`, `var2`) - rename immediately

**Immutability:**
- Prefer `val` over `var`
- Use `data class` for immutable data structures
- Use `copy()` for modifications

```kotlin
// ✅ Good
val network = WifiNetwork(ssid = "Home", bssid = "AA:BB:CC:DD:EE:FF")
val updated = network.copy(rssi = -50)

// ❌ Bad
var network = WifiNetwork(ssid = "Home", bssid = "AA:BB:CC:DD:EE:FF")
network.rssi = -50  // Mutation
```

### API Key Management

**Never hardcode API keys.** Store in `EncryptedSharedPreferences`, retrieve at call site.

❌ **DON'T:**
```kotlin
@Provides
fun provideApiKey(): String = "sk_live_abc123"  // NEVER!
```

✅ **DO:**
```kotlin
// Store securely
val encryptedPrefs = EncryptedSharedPreferences.create(...)
encryptedPrefs.edit().putString("wigle_api_key", apiKey).apply()

// Retrieve at call site
val apiKey = encryptedPrefs.getString("wigle_api_key", null) ?: return
repository.syncWithWiGLE(apiKey)
```

---

## Before Submitting a Pull Request

### 1. Code Quality

- [ ] **Run detekt:** `./gradlew detekt`
  - All issues resolved
  - No warnings suppressed without justification
  - Max complexity thresholds respected

- [ ] **Run lint:** Android Studio → Analyze → Inspect Code
  - No Android lint errors
  - No unused imports
  - No deprecated API usage

- [ ] **Code compiles:** `./gradlew clean build`
  - No compilation errors
  - No Hilt code generation failures
  - All KAPT processors succeed

### 2. Testing

- [ ] **Unit tests written**
  - New use cases have tests (100% coverage goal)
  - New repository methods have tests
  - ViewModels have state transition tests

- [ ] **All tests pass:** `./gradlew test`
  - No flaky tests
  - No ignored tests without justification
  - Test coverage >80% for new logic

- [ ] **Mocks properly configured**
  - No real database access in unit tests
  - No real API calls in unit tests
  - Use MockK for all external dependencies

### 3. Device Testing

- [ ] **Tested on real device** (emulator insufficient for hardware features)
  - WiFi scanning requires physical device
  - Bluetooth/BLE requires physical device
  - Location tracking tested with GPS

- [ ] **No crashes on API 26** (minSdk)
  - Test on oldest supported Android version
  - No API compatibility issues

- [ ] **Permissions work correctly**
  - All required permissions requested
  - Graceful handling of denied permissions
  - No crashes due to missing permissions

- [ ] **Foreground service works** (if applicable)
  - Service starts correctly
  - Notification appears
  - Service survives app backgrounding

### 4. Documentation

- [ ] **KDoc comments added**
  - Public use cases documented
  - Public repository methods documented
  - Complex logic explained

- [ ] **README updated** (if user-facing changes)
  - New features documented
  - Screenshots added for UI changes

- [ ] **CLAUDE.md updated** (if architectural changes)
  - New patterns documented
  - Examples updated

### 5. Git Hygiene

- [ ] **Clear commit messages**
  - Format: `type(scope): description`
  - Examples: `feat(wifi): add WPA3 detection`, `fix(bluetooth): resolve scan crash`

- [ ] **No debug artifacts**
  - No `Log.d()` statements in production code
  - No `TODO` or `FIXME` comments
  - No commented-out code blocks

- [ ] **No secrets committed**
  - No API keys in code
  - No `.env` files
  - `local.properties` not committed

---

## File Structure & Architecture Patterns

### Domain Layer (Business Logic)

**Location:** `app/src/main/kotlin/com/shadowcheck/mobile/domain/`

#### Repository Interfaces

Define contracts that the data layer implements:

```kotlin
// domain/repository/WifiNetworkRepository.kt
package com.shadowcheck.mobile.domain.repository

import com.shadowcheck.mobile.domain.model.WifiNetwork
import kotlinx.coroutines.flow.Flow

interface WifiNetworkRepository {
    fun getAllNetworks(): Flow<List<WifiNetwork>>
    fun getNetworkById(ssid: String): Flow<WifiNetwork?>
    fun getNetworksByBssid(bssid: String): Flow<List<WifiNetwork>>
    suspend fun insertNetwork(network: WifiNetwork): Long
    suspend fun updateNetwork(network: WifiNetwork)
    suspend fun deleteNetwork(ssid: String)
    fun searchNetworks(query: String): Flow<List<WifiNetwork>>
    suspend fun syncWithWiGLE(apiKey: String): Int
}
```

#### Use Cases

Encapsulate single business operations:

```kotlin
// domain/usecase/GetAllWifiNetworksUseCase.kt
package com.shadowcheck.mobile.domain.usecase

import com.shadowcheck.mobile.domain.model.WifiNetwork
import com.shadowcheck.mobile.domain.repository.WifiNetworkRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Use case to get all Wi-Fi networks, filtering out those with empty SSIDs
 * and sorting them by the last time they were seen.
 *
 * Business rules:
 * - Hidden networks (blank SSID) are filtered out
 * - Networks sorted by timestamp descending (most recent first)
 */
@Singleton
class GetAllWifiNetworksUseCase @Inject constructor(
    private val repository: WifiNetworkRepository
) {
    operator fun invoke(): Flow<List<WifiNetwork>> {
        return repository.getAllNetworks()
            .map { networks -> networks.filter { it.ssid.isNotBlank() } }
            .map { networks -> networks.sortedByDescending { it.timestamp } }
    }
}
```

#### Domain Models

Pure Kotlin data classes:

```kotlin
// domain/model/WifiNetwork.kt
package com.shadowcheck.mobile.domain.model

/**
 * Domain model representing a Wi-Fi network.
 * Contains no Android-specific types - pure business data.
 */
data class WifiNetwork(
    val ssid: String,
    val bssid: String,
    val capabilities: String,
    val frequency: Int,
    val level: Int,
    val timestamp: Long
)
```

### Data Layer (Storage & API)

**Location:** `app/src/main/kotlin/com/shadowcheck/mobile/data/`

#### Repository Implementations

Concrete data access logic:

```kotlin
// data/repository/WifiNetworkRepositoryImpl.kt
package com.shadowcheck.mobile.data.repository

import android.util.Log
import com.shadowcheck.mobile.data.database.dao.WifiNetworkDao
import com.shadowcheck.mobile.data.database.model.toDomainModel
import com.shadowcheck.mobile.data.database.model.toEntity
import com.shadowcheck.mobile.data.remote.WiGLEApiService
import com.shadowcheck.mobile.data.remote.dto.toEntity
import com.shadowcheck.mobile.di.IoDispatcher
import com.shadowcheck.mobile.domain.model.WifiNetwork
import com.shadowcheck.mobile.domain.repository.WifiNetworkRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WifiNetworkRepositoryImpl @Inject constructor(
    private val wifiDao: WifiNetworkDao,
    private val wigleService: WiGLEApiService,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) : WifiNetworkRepository {

    override fun getAllNetworks(): Flow<List<WifiNetwork>> {
        return wifiDao.getAllNetworks()
            .map { entities -> entities.map { it.toDomainModel() } }
            .distinctUntilChanged()
            .flowOn(dispatcher)
    }

    override fun getNetworkById(ssid: String): Flow<WifiNetwork?> {
        return wifiDao.getNetworkBySsid(ssid)
            .map { it?.toDomainModel() }
            .flowOn(dispatcher)
    }

    override fun getNetworksByBssid(bssid: String): Flow<List<WifiNetwork>> {
        return wifiDao.getNetworksByBssid(bssid)
            .map { entities -> entities.map { it.toDomainModel() } }
            .flowOn(dispatcher)
    }

    override suspend fun insertNetwork(network: WifiNetwork): Long {
        return withContext(dispatcher) {
            wifiDao.insertNetwork(network.toEntity())
        }
    }

    override suspend fun updateNetwork(network: WifiNetwork) {
        withContext(dispatcher) {
            wifiDao.updateNetwork(network.toEntity())
        }
    }

    override suspend fun deleteNetwork(ssid: String) {
        withContext(dispatcher) {
            wifiDao.deleteNetwork(ssid)
        }
    }

    override fun searchNetworks(query: String): Flow<List<WifiNetwork>> {
        val formattedQuery = "%${query.replace(' ', '%')}%"
        return wifiDao.searchNetworks(formattedQuery)
            .map { entities -> entities.map { it.toDomainModel() } }
            .flowOn(dispatcher)
    }

    override suspend fun syncWithWiGLE(apiKey: String): Int {
        return withContext(dispatcher) {
            try {
                val response = wigleService.searchNetworks(apiKey = "Basic $apiKey")
                if (response.isSuccessful) {
                    response.body()?.results?.let { dtos ->
                        val entities = dtos.map { it.toEntity() }
                        wifiDao.insertAll(entities)
                        return@withContext entities.size
                    }
                } else {
                    Log.e("WifiRepo", "WiGLE API Error: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("WifiRepo", "WiGLE sync failed", e)
                throw e
            }
            return@withContext 0
        }
    }
}
```

#### DAOs (Room Database Access)

```kotlin
// data/database/dao/WifiNetworkDao.kt
package com.shadowcheck.mobile.data.database.dao

import androidx.room.*
import com.shadowcheck.mobile.data.database.model.WifiNetworkEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WifiNetworkDao {

    @Query("SELECT * FROM wifi_networks ORDER BY timestamp DESC")
    fun getAllNetworks(): Flow<List<WifiNetworkEntity>>

    @Query("SELECT * FROM wifi_networks WHERE ssid = :ssid LIMIT 1")
    fun getNetworkBySsid(ssid: String): Flow<WifiNetworkEntity?>

    @Query("SELECT * FROM wifi_networks WHERE bssid = :bssid")
    fun getNetworksByBssid(bssid: String): Flow<List<WifiNetworkEntity>>

    @Query("SELECT * FROM wifi_networks WHERE ssid LIKE :query OR bssid LIKE :query")
    fun searchNetworks(query: String): Flow<List<WifiNetworkEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNetwork(network: WifiNetworkEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(networks: List<WifiNetworkEntity>)

    @Update
    suspend fun updateNetwork(network: WifiNetworkEntity)

    @Query("DELETE FROM wifi_networks WHERE ssid = :ssid")
    suspend fun deleteNetwork(ssid: String)
}
```

#### Database Entities

```kotlin
// data/database/model/WifiNetworkEntity.kt
package com.shadowcheck.mobile.data.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.shadowcheck.mobile.domain.model.WifiNetwork

@Entity(tableName = "wifi_networks")
data class WifiNetworkEntity(
    @PrimaryKey val ssid: String,
    val bssid: String,
    val capabilities: String,
    val frequency: Int,
    val level: Int,
    val timestamp: Long
)

// Mapper functions
fun WifiNetworkEntity.toDomainModel(): WifiNetwork {
    return WifiNetwork(
        ssid = ssid,
        bssid = bssid,
        capabilities = capabilities,
        frequency = frequency,
        level = level,
        timestamp = timestamp
    )
}

fun WifiNetwork.toEntity(): WifiNetworkEntity {
    return WifiNetworkEntity(
        ssid = ssid,
        bssid = bssid,
        capabilities = capabilities,
        frequency = frequency,
        level = level,
        timestamp = timestamp
    )
}
```

### Presentation Layer (UI)

**Location:** `app/src/main/kotlin/com/shadowcheck/mobile/presentation/`

#### ViewModels

UI state management:

```kotlin
// presentation/viewmodel/WifiViewModel.kt
package com.shadowcheck.mobile.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shadowcheck.mobile.domain.model.WifiNetwork
import com.shadowcheck.mobile.domain.usecase.GetAllWifiNetworksUseCase
import com.shadowcheck.mobile.domain.usecase.SearchWifiNetworksUseCase
import com.shadowcheck.mobile.domain.usecase.SyncWiGLEUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WifiViewModel @Inject constructor(
    private val getAllWifiNetworks: GetAllWifiNetworksUseCase,
    private val searchWifiNetworks: SearchWifiNetworksUseCase,
    private val syncWiGLE: SyncWiGLEUseCase
) : ViewModel() {

    private val _networks = MutableStateFlow<List<WifiNetwork>>(emptyList())
    val networks: StateFlow<List<WifiNetwork>> = _networks.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadNetworks()
    }

    fun loadNetworks() {
        getAllWifiNetworks()
            .onEach { _networks.value = it }
            .launchIn(viewModelScope)
    }

    fun search(query: String) {
        searchWifiNetworks(query)
            .onEach { _networks.value = it }
            .launchIn(viewModelScope)
    }

    fun syncData(apiKey: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                syncWiGLE(apiKey)
                loadNetworks()
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}
```

#### Compose Screens

UI components (read-only, minimal logic):

```kotlin
// presentation/ui/screens/WifiListScreen.kt
package com.shadowcheck.mobile.presentation.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun WifiListScreen(
    viewModel: WifiViewModel = hiltViewModel()
) {
    val networks by viewModel.networks.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        if (isLoading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }

        error?.let { errorMessage ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
            ) {
                Text(
                    text = errorMessage,
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(networks, key = { it.ssid }) { network ->
                WifiNetworkCard(network = network)
            }
        }
    }
}

@Composable
fun WifiNetworkCard(network: WifiNetwork) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = network.ssid,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = network.bssid,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Signal: ${network.level} dBm",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = network.capabilities,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
```

### Dependency Injection Modules

**Location:** `app/src/main/kotlin/com/shadowcheck/mobile/di/`

#### Database Module

```kotlin
// di/DatabaseModule.kt
package com.shadowcheck.mobile.di

import android.content.Context
import androidx.room.Room
import com.shadowcheck.mobile.data.database.AppDatabase
import com.shadowcheck.mobile.data.database.dao.WifiNetworkDao
import com.shadowcheck.mobile.data.database.dao.BluetoothDeviceDao
import com.shadowcheck.mobile.data.database.dao.CellularTowerDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "shadowcheck.db"
        ).build()
    }

    @Provides
    fun provideWifiNetworkDao(database: AppDatabase): WifiNetworkDao {
        return database.wifiNetworkDao()
    }

    @Provides
    fun provideBluetoothDeviceDao(database: AppDatabase): BluetoothDeviceDao {
        return database.bluetoothDeviceDao()
    }

    @Provides
    fun provideCellularTowerDao(database: AppDatabase): CellularTowerDao {
        return database.cellularTowerDao()
    }
}
```

#### Repository Module

```kotlin
// di/RepositoryModule.kt
package com.shadowcheck.mobile.di

import com.shadowcheck.mobile.data.repository.WifiNetworkRepositoryImpl
import com.shadowcheck.mobile.data.repository.BluetoothDeviceRepositoryImpl
import com.shadowcheck.mobile.data.repository.CellularTowerRepositoryImpl
import com.shadowcheck.mobile.domain.repository.WifiNetworkRepository
import com.shadowcheck.mobile.domain.repository.BluetoothDeviceRepository
import com.shadowcheck.mobile.domain.repository.CellularTowerRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindWifiNetworkRepository(
        impl: WifiNetworkRepositoryImpl
    ): WifiNetworkRepository

    @Binds
    @Singleton
    abstract fun bindBluetoothDeviceRepository(
        impl: BluetoothDeviceRepositoryImpl
    ): BluetoothDeviceRepository

    @Binds
    @Singleton
    abstract fun bindCellularTowerRepository(
        impl: CellularTowerRepositoryImpl
    ): CellularTowerRepository
}
```

#### Network Module

```kotlin
// di/NetworkModule.kt
package com.shadowcheck.mobile.di

import com.shadowcheck.mobile.data.remote.WiGLEApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.wigle.net/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideWiGLEApiService(retrofit: Retrofit): WiGLEApiService {
        return retrofit.create(WiGLEApiService::class.java)
    }
}
```

#### Dispatchers Module

```kotlin
// di/DispatchersModule.kt
package com.shadowcheck.mobile.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class IoDispatcher

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class MainDispatcher

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DefaultDispatcher

@Module
@InstallIn(SingletonComponent::class)
object DispatchersModule {

    @Provides
    @IoDispatcher
    fun providesIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    @MainDispatcher
    fun providesMainDispatcher(): CoroutineDispatcher = Dispatchers.Main

    @Provides
    @DefaultDispatcher
    fun providesDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default
}
```

---

## Testing Strategy

### Unit Test Repositories

Test data access logic without hitting real database:

```kotlin
// test/data/repository/WifiNetworkRepositoryImplTest.kt
package com.shadowcheck.mobile.data.repository

import com.shadowcheck.mobile.data.database.dao.WifiNetworkDao
import com.shadowcheck.mobile.data.database.model.WifiNetworkEntity
import com.shadowcheck.mobile.data.remote.WiGLEApiService
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class WifiNetworkRepositoryImplTest {

    private lateinit var wifiDao: WifiNetworkDao
    private lateinit var wigleService: WiGLEApiService
    private lateinit var repository: WifiNetworkRepositoryImpl
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        wifiDao = mockk()
        wigleService = mockk()
        repository = WifiNetworkRepositoryImpl(wifiDao, wigleService, testDispatcher)
    }

    @Test
    fun getAllNetworks_returnsNetworksFromDao() = runTest(testDispatcher) {
        // Given
        val entities = listOf(
            WifiNetworkEntity("TestSSID", "AA:BB:CC:DD:EE:FF", "WPA2", 2412, -70, 1000L)
        )
        every { wifiDao.getAllNetworks() } returns flowOf(entities)

        // When
        val result = repository.getAllNetworks().first()

        // Then
        assertEquals(1, result.size)
        assertEquals("TestSSID", result[0].ssid)
        verify { wifiDao.getAllNetworks() }
    }

    @Test
    fun insertNetwork_insertsIntoDao() = runTest(testDispatcher) {
        // Given
        val network = WifiNetwork("TestSSID", "AA:BB:CC:DD:EE:FF", "WPA2", 2412, -70, 1000L)
        coEvery { wifiDao.insertNetwork(any()) } returns 1L

        // When
        val result = repository.insertNetwork(network)

        // Then
        assertEquals(1L, result)
        coVerify { wifiDao.insertNetwork(any()) }
    }

    @Test
    fun searchNetworks_filtersCorrectly() = runTest(testDispatcher) {
        // Given
        val entities = listOf(
            WifiNetworkEntity("CoffeeShop", "AA:BB:CC:DD:EE:01", "WPA2", 2412, -70, 1000L),
            WifiNetworkEntity("Home", "AA:BB:CC:DD:EE:02", "WPA3", 2417, -50, 2000L)
        )
        every { wifiDao.searchNetworks("%Coffee%") } returns flowOf(
            entities.filter { it.ssid.contains("Coffee") }
        )

        // When
        val result = repository.searchNetworks("Coffee").first()

        // Then
        assertEquals(1, result.size)
        assertEquals("CoffeeShop", result[0].ssid)
    }
}
```

### Unit Test Use Cases

Test business logic independently:

```kotlin
// test/domain/usecase/GetAllWifiNetworksUseCaseTest.kt
package com.shadowcheck.mobile.domain.usecase

import com.shadowcheck.mobile.domain.model.WifiNetwork
import com.shadowcheck.mobile.domain.repository.WifiNetworkRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class GetAllWifiNetworksUseCaseTest {

    private lateinit var repository: WifiNetworkRepository
    private lateinit var useCase: GetAllWifiNetworksUseCase

    @Before
    fun setup() {
        repository = mockk()
        useCase = GetAllWifiNetworksUseCase(repository)
    }

    @Test
    fun invoke_filtersBlankSsids() = runTest {
        // Given
        val networks = listOf(
            WifiNetwork("ValidNetwork", "AA:BB:CC:DD:EE:01", "WPA2", 2412, -70, 1000L),
            WifiNetwork("", "AA:BB:CC:DD:EE:02", "WPA3", 2417, -50, 2000L),
            WifiNetwork("AnotherValid", "AA:BB:CC:DD:EE:03", "WPA2", 2422, -60, 3000L)
        )
        every { repository.getAllNetworks() } returns flowOf(networks)

        // When
        val result = useCase().first()

        // Then
        assertEquals(2, result.size)
        assertTrue(result.all { it.ssid.isNotBlank() })
    }

    @Test
    fun invoke_sortsByTimestampDescending() = runTest {
        // Given
        val networks = listOf(
            WifiNetwork("Network1", "AA:BB:CC:DD:EE:01", "WPA2", 2412, -70, 1000L),
            WifiNetwork("Network2", "AA:BB:CC:DD:EE:02", "WPA3", 2417, -50, 3000L),
            WifiNetwork("Network3", "AA:BB:CC:DD:EE:03", "WPA2", 2422, -60, 2000L)
        )
        every { repository.getAllNetworks() } returns flowOf(networks)

        // When
        val result = useCase().first()

        // Then
        assertEquals(3000L, result[0].timestamp)
        assertEquals(2000L, result[1].timestamp)
        assertEquals(1000L, result[2].timestamp)
    }

    @Test
    fun invoke_returnsEmptyListWhenNoNetworks() = runTest {
        // Given
        every { repository.getAllNetworks() } returns flowOf(emptyList())

        // When
        val result = useCase().first()

        // Then
        assertTrue(result.isEmpty())
    }
}
```

### Unit Test ViewModels

Test state management with coroutines:

```kotlin
// test/presentation/viewmodel/WifiViewModelTest.kt
package com.shadowcheck.mobile.presentation.viewmodel

import com.shadowcheck.mobile.domain.model.WifiNetwork
import com.shadowcheck.mobile.domain.usecase.GetAllWifiNetworksUseCase
import com.shadowcheck.mobile.domain.usecase.SearchWifiNetworksUseCase
import com.shadowcheck.mobile.domain.usecase.SyncWiGLEUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class WifiViewModelTest {

    private lateinit var getAllWifiNetworks: GetAllWifiNetworksUseCase
    private lateinit var searchWifiNetworks: SearchWifiNetworksUseCase
    private lateinit var syncWiGLE: SyncWiGLEUseCase
    private lateinit var viewModel: WifiViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getAllWifiNetworks = mockk()
        searchWifiNetworks = mockk()
        syncWiGLE = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun init_loadsNetworks() = runTest(testDispatcher) {
        // Given
        val networks = listOf(
            WifiNetwork("Network1", "AA:BB:CC:DD:EE:01", "WPA2", 2412, -70, 1000L)
        )
        every { getAllWifiNetworks() } returns flowOf(networks)

        // When
        viewModel = WifiViewModel(getAllWifiNetworks, searchWifiNetworks, syncWiGLE)
        advanceUntilIdle()

        // Then
        assertEquals(1, viewModel.networks.value.size)
        assertEquals("Network1", viewModel.networks.value[0].ssid)
    }

    @Test
    fun search_updatesNetworks() = runTest(testDispatcher) {
        // Given
        val initialNetworks = listOf(
            WifiNetwork("Network1", "AA:BB:CC:DD:EE:01", "WPA2", 2412, -70, 1000L)
        )
        val searchResults = listOf(
            WifiNetwork("SearchResult", "AA:BB:CC:DD:EE:02", "WPA3", 2417, -50, 2000L)
        )
        every { getAllWifiNetworks() } returns flowOf(initialNetworks)
        every { searchWifiNetworks("test") } returns flowOf(searchResults)

        viewModel = WifiViewModel(getAllWifiNetworks, searchWifiNetworks, syncWiGLE)
        advanceUntilIdle()

        // When
        viewModel.search("test")
        advanceUntilIdle()

        // Then
        assertEquals(1, viewModel.networks.value.size)
        assertEquals("SearchResult", viewModel.networks.value[0].ssid)
    }

    @Test
    fun syncData_setsLoadingAndCallsUseCase() = runTest(testDispatcher) {
        // Given
        every { getAllWifiNetworks() } returns flowOf(emptyList())
        coEvery { syncWiGLE("test-api-key") } returns Unit

        viewModel = WifiViewModel(getAllWifiNetworks, searchWifiNetworks, syncWiGLE)
        advanceUntilIdle()

        // When
        viewModel.syncData("test-api-key")

        // During sync
        assertTrue(viewModel.isLoading.value)

        advanceUntilIdle()

        // Then
        assertFalse(viewModel.isLoading.value)
        coVerify { syncWiGLE("test-api-key") }
    }

    @Test
    fun syncData_handlesErrors() = runTest(testDispatcher) {
        // Given
        every { getAllWifiNetworks() } returns flowOf(emptyList())
        coEvery { syncWiGLE("test-api-key") } throws Exception("Network error")

        viewModel = WifiViewModel(getAllWifiNetworks, searchWifiNetworks, syncWiGLE)
        advanceUntilIdle()

        // When
        viewModel.syncData("test-api-key")
        advanceUntilIdle()

        // Then
        assertFalse(viewModel.isLoading.value)
        assertEquals("Network error", viewModel.error.value)
    }

    @Test
    fun clearError_resetsErrorState() = runTest(testDispatcher) {
        // Given
        every { getAllWifiNetworks() } returns flowOf(emptyList())
        coEvery { syncWiGLE("test-api-key") } throws Exception("Error")

        viewModel = WifiViewModel(getAllWifiNetworks, searchWifiNetworks, syncWiGLE)
        viewModel.syncData("test-api-key")
        advanceUntilIdle()

        // When
        viewModel.clearError()

        // Then
        assertEquals(null, viewModel.error.value)
    }
}
```

### Testing Guidelines

**Core Principles:**

1. **No Real Database** - Use MockK for DAOs. Never connect to actual SQLite in unit tests.
2. **No Real API** - Mock Retrofit services. Never make actual HTTP calls.
3. **Use `runTest {}`** - Properly handles coroutines and Flow collection.
4. **One Behavior Per Test** - Each test verifies one specific behavior.
5. **Mock External Dependencies** - Retrofit, Firebase, location services—all mocked.
6. **No UI Tests in Unit Tests** - Compose UI tests belong in `androidTest/`, not `test/`.

**Flow Testing Pattern:**

```kotlin
// Collect first emission
val result = repository.getData().first()

// Collect all emissions
val results = repository.getData().take(3).toList()

// Test with delays
repository.getData()
    .onEach { delay(100) }
    .test {
        assertEquals(expected1, awaitItem())
        assertEquals(expected2, awaitItem())
        awaitComplete()
    }
```

**Coroutine Testing Pattern:**

```kotlin
@Before
fun setup() {
    Dispatchers.setMain(testDispatcher)
}

@After
fun tearDown() {
    Dispatchers.resetMain()
}

@Test
fun testSuspendFunction() = runTest(testDispatcher) {
    // Trigger coroutine
    viewModel.performAction()

    // Advance virtual time
    advanceUntilIdle()

    // Assert results
    assertEquals(expected, viewModel.state.value)
}
```

---

## Common Development Tasks

### Task 1: Adding a New Entity (e.g., CellularTower)

#### Step 1: Create Database Entity

```kotlin
// data/database/model/CellularTowerEntity.kt
package com.shadowcheck.mobile.data.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.shadowcheck.mobile.domain.model.CellularTower

@Entity(tableName = "cellular_towers")
data class CellularTowerEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val cellId: Int,
    val mcc: Int,
    val mnc: Int,
    val lac: Int,
    val latitude: Double,
    val longitude: Double,
    val signalStrength: Int,
    val timestamp: Long
)

// Mapper functions
fun CellularTowerEntity.toDomainModel(): CellularTower {
    return CellularTower(
        id = id,
        cellId = cellId,
        mcc = mcc,
        mnc = mnc,
        lac = lac,
        latitude = latitude,
        longitude = longitude,
        signalStrength = signalStrength,
        timestamp = timestamp
    )
}

fun CellularTower.toEntity(): CellularTowerEntity {
    return CellularTowerEntity(
        id = id,
        cellId = cellId,
        mcc = mcc,
        mnc = mnc,
        lac = lac,
        latitude = latitude,
        longitude = longitude,
        signalStrength = signalStrength,
        timestamp = timestamp
    )
}
```

#### Step 2: Create DAO

```kotlin
// data/database/dao/CellularTowerDao.kt
package com.shadowcheck.mobile.data.database.dao

import androidx.room.*
import com.shadowcheck.mobile.data.database.model.CellularTowerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CellularTowerDao {

    @Query("SELECT * FROM cellular_towers ORDER BY timestamp DESC")
    fun getAllTowers(): Flow<List<CellularTowerEntity>>

    @Query("SELECT * FROM cellular_towers WHERE cellId = :cellId LIMIT 1")
    fun getTowerById(cellId: Int): Flow<CellularTowerEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTower(tower: CellularTowerEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(towers: List<CellularTowerEntity>)

    @Update
    suspend fun updateTower(tower: CellularTowerEntity)

    @Delete
    suspend fun deleteTower(tower: CellularTowerEntity)
}
```

#### Step 3: Update Database Class

```kotlin
// data/database/AppDatabase.kt
package com.shadowcheck.mobile.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.shadowcheck.mobile.data.database.dao.CellularTowerDao
import com.shadowcheck.mobile.data.database.dao.WifiNetworkDao
import com.shadowcheck.mobile.data.database.dao.BluetoothDeviceDao
import com.shadowcheck.mobile.data.database.model.CellularTowerEntity
import com.shadowcheck.mobile.data.database.model.WifiNetworkEntity
import com.shadowcheck.mobile.data.database.model.BluetoothDeviceEntity

@Database(
    entities = [
        WifiNetworkEntity::class,
        BluetoothDeviceEntity::class,
        CellularTowerEntity::class  // Add new entity
    ],
    version = 2,  // Increment version
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun wifiNetworkDao(): WifiNetworkDao
    abstract fun bluetoothDeviceDao(): BluetoothDeviceDao
    abstract fun cellularTowerDao(): CellularTowerDao  // Add DAO
}
```

#### Step 4: Create Domain Model

```kotlin
// domain/model/CellularTower.kt
package com.shadowcheck.mobile.domain.model

data class CellularTower(
    val id: Long = 0,
    val cellId: Int,
    val mcc: Int,
    val mnc: Int,
    val lac: Int,
    val latitude: Double,
    val longitude: Double,
    val signalStrength: Int,
    val timestamp: Long
)
```

#### Step 5: Create Repository Interface

```kotlin
// domain/repository/CellularTowerRepository.kt
package com.shadowcheck.mobile.domain.repository

import com.shadowcheck.mobile.domain.model.CellularTower
import kotlinx.coroutines.flow.Flow

interface CellularTowerRepository {
    fun getAllTowers(): Flow<List<CellularTower>>
    fun getTowerById(cellId: Int): Flow<CellularTower?>
    suspend fun insertTower(tower: CellularTower): Long
    suspend fun updateTower(tower: CellularTower)
    suspend fun deleteTower(tower: CellularTower)
}
```

#### Step 6: Implement Repository

```kotlin
// data/repository/CellularTowerRepositoryImpl.kt
package com.shadowcheck.mobile.data.repository

import com.shadowcheck.mobile.data.database.dao.CellularTowerDao
import com.shadowcheck.mobile.data.database.model.toDomainModel
import com.shadowcheck.mobile.data.database.model.toEntity
import com.shadowcheck.mobile.di.IoDispatcher
import com.shadowcheck.mobile.domain.model.CellularTower
import com.shadowcheck.mobile.domain.repository.CellularTowerRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CellularTowerRepositoryImpl @Inject constructor(
    private val cellularTowerDao: CellularTowerDao,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) : CellularTowerRepository {

    override fun getAllTowers(): Flow<List<CellularTower>> {
        return cellularTowerDao.getAllTowers()
            .map { entities -> entities.map { it.toDomainModel() } }
            .flowOn(dispatcher)
    }

    override fun getTowerById(cellId: Int): Flow<CellularTower?> {
        return cellularTowerDao.getTowerById(cellId)
            .map { it?.toDomainModel() }
            .flowOn(dispatcher)
    }

    override suspend fun insertTower(tower: CellularTower): Long {
        return withContext(dispatcher) {
            cellularTowerDao.insertTower(tower.toEntity())
        }
    }

    override suspend fun updateTower(tower: CellularTower) {
        withContext(dispatcher) {
            cellularTowerDao.updateTower(tower.toEntity())
        }
    }

    override suspend fun deleteTower(tower: CellularTower) {
        withContext(dispatcher) {
            cellularTowerDao.deleteTower(tower.toEntity())
        }
    }
}
```

#### Step 7: Update RepositoryModule

```kotlin
// di/RepositoryModule.kt
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    // ... existing bindings ...

    @Binds
    @Singleton
    abstract fun bindCellularTowerRepository(
        impl: CellularTowerRepositoryImpl
    ): CellularTowerRepository
}
```

#### Step 8: Create Use Case

```kotlin
// domain/usecase/GetAllCellularTowersUseCase.kt
package com.shadowcheck.mobile.domain.usecase

import com.shadowcheck.mobile.domain.model.CellularTower
import com.shadowcheck.mobile.domain.repository.CellularTowerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetAllCellularTowersUseCase @Inject constructor(
    private val repository: CellularTowerRepository
) {
    operator fun invoke(): Flow<List<CellularTower>> {
        return repository.getAllTowers()
            .map { towers -> towers.sortedByDescending { it.timestamp } }
    }
}
```

#### Step 9: Create ViewModel

```kotlin
// presentation/viewmodel/CellularViewModel.kt
package com.shadowcheck.mobile.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shadowcheck.mobile.domain.model.CellularTower
import com.shadowcheck.mobile.domain.usecase.GetAllCellularTowersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class CellularViewModel @Inject constructor(
    private val getAllCellularTowers: GetAllCellularTowersUseCase
) : ViewModel() {

    private val _towers = MutableStateFlow<List<CellularTower>>(emptyList())
    val towers: StateFlow<List<CellularTower>> = _towers.asStateFlow()

    init {
        loadTowers()
    }

    fun loadTowers() {
        getAllCellularTowers()
            .onEach { _towers.value = it }
            .launchIn(viewModelScope)
    }
}
```

#### Step 10: Update DatabaseModule

```kotlin
// di/DatabaseModule.kt
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    // ... existing providers ...

    @Provides
    fun provideCellularTowerDao(database: AppDatabase): CellularTowerDao {
        return database.cellularTowerDao()
    }
}
```

---

### Task 2: Adding an API Call (e.g., Fetch WiGLE Data)

#### Step 1: Define API Service Interface

```kotlin
// data/remote/WiGLEApiService.kt
package com.shadowcheck.mobile.data.remote

import com.shadowcheck.mobile.data.remote.dto.WigleWifiSearchResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface WiGLEApiService {

    @GET("/api/v3/network/search")
    suspend fun searchNetworks(
        @Query("onlymine") onlyMine: Boolean = false,
        @Query("freenet") freeNet: Boolean = false,
        @Query("paynet") payNet: Boolean = false,
        @Header("Authorization") apiKey: String
    ): Response<WigleWifiSearchResponse>
}
```

#### Step 2: Create DTOs

```kotlin
// data/remote/dto/WigleWifiSearchResponse.kt
package com.shadowcheck.mobile.data.remote.dto

import com.google.gson.annotations.SerializedName

data class WigleWifiSearchResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("totalResults") val totalResults: Int,
    @SerializedName("results") val results: List<WigleNetworkDto>
)

data class WigleNetworkDto(
    @SerializedName("ssid") val ssid: String,
    @SerializedName("netid") val bssid: String,
    @SerializedName("channel") val channel: Int,
    @SerializedName("encryption") val encryption: String,
    @SerializedName("lastupdt") val lastUpdate: String,
    @SerializedName("trilat") val latitude: Double,
    @SerializedName("trilong") val longitude: Double
)
```

#### Step 3: Create Mapper Extension

```kotlin
// data/remote/dto/WigleMappers.kt
package com.shadowcheck.mobile.data.remote.dto

import com.shadowcheck.mobile.data.database.model.WifiNetworkEntity

fun WigleNetworkDto.toEntity(): WifiNetworkEntity {
    return WifiNetworkEntity(
        ssid = ssid,
        bssid = bssid,
        capabilities = encryption,
        frequency = channelToFrequency(channel),
        level = -70,  // WiGLE doesn't provide signal strength
        timestamp = System.currentTimeMillis()
    )
}

private fun channelToFrequency(channel: Int): Int {
    return when (channel) {
        in 1..13 -> 2407 + (channel * 5)
        14 -> 2484
        else -> 5000 + (channel * 5)
    }
}
```

#### Step 4: Add Method to Repository

```kotlin
// data/repository/WifiNetworkRepositoryImpl.kt
@Singleton
class WifiNetworkRepositoryImpl @Inject constructor(
    private val wifiDao: WifiNetworkDao,
    private val wigleService: WiGLEApiService,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) : WifiNetworkRepository {

    // ... existing methods ...

    override suspend fun syncWithWiGLE(apiKey: String): Int {
        return withContext(dispatcher) {
            try {
                val response = wigleService.searchNetworks(apiKey = "Basic $apiKey")
                if (response.isSuccessful) {
                    response.body()?.results?.let { dtos ->
                        val entities = dtos.map { it.toEntity() }
                        wifiDao.insertAll(entities)
                        return@withContext entities.size
                    }
                } else {
                    Log.e("WifiRepo", "WiGLE API Error: ${response.code()}")
                    throw Exception("WiGLE API returned error: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("WifiRepo", "WiGLE sync failed", e)
                throw e
            }
            return@withContext 0
        }
    }
}
```

#### Step 5: Create Use Case

```kotlin
// domain/usecase/SyncWiGLEUseCase.kt
package com.shadowcheck.mobile.domain.usecase

import com.shadowcheck.mobile.domain.repository.WifiNetworkRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncWiGLEUseCase @Inject constructor(
    private val repository: WifiNetworkRepository
) {
    suspend operator fun invoke(apiKey: String): Int {
        return repository.syncWithWiGLE(apiKey)
    }
}
```

#### Step 6: Call from ViewModel

```kotlin
// presentation/viewmodel/WifiViewModel.kt
@HiltViewModel
class WifiViewModel @Inject constructor(
    private val getAllWifiNetworks: GetAllWifiNetworksUseCase,
    private val searchWifiNetworks: SearchWifiNetworksUseCase,
    private val syncWiGLE: SyncWiGLEUseCase
) : ViewModel() {

    // ... existing properties ...

    fun syncData(apiKey: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val count = syncWiGLE(apiKey)
                // Optionally show success message
                Log.d("WifiViewModel", "Synced $count networks from WiGLE")
                loadNetworks()  // Refresh local data
            } catch (e: Exception) {
                _error.value = e.message ?: "Unknown error during sync"
                Log.e("WifiViewModel", "Sync failed", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
}
```

---

### Task 3: Debugging Tips

#### Logging Best Practices

```kotlin
// Use consistent tags
private const val TAG = "WifiRepo"

// Log at appropriate levels
Log.d(TAG, "Synced ${networks.size} networks")  // Debug info
Log.w(TAG, "API key missing, skipping sync")     // Warnings
Log.e(TAG, "Failed to sync", exception)          // Errors

// Use Timber (if available) for automatic tagging
Timber.d("Synced ${networks.size} networks")
Timber.e(exception, "Failed to sync")
```

#### Inspect Flows

```kotlin
getAllWifiNetworksUseCase()
    .onEach { networks ->
        Log.d("Debug", "Flow emitted ${networks.size} networks")
        Log.d("Debug", "First network: ${networks.firstOrNull()}")
    }
    .launchIn(viewModelScope)
```

#### Database Inspection

1. Open Android Studio
2. View → Tool Windows → Database Inspector
3. Connect to running device/emulator
4. Browse tables and run SQL queries

#### Network Inspection

```kotlin
// Add logging interceptor in NetworkModule
val loggingInterceptor = HttpLoggingInterceptor().apply {
    level = HttpLoggingInterceptor.Level.BODY
}

val okHttpClient = OkHttpClient.Builder()
    .addInterceptor(loggingInterceptor)
    .build()
```

Then filter logcat for `okhttp` to see HTTP requests/responses.

#### Coroutine Debugging

```kotlin
// Print current dispatcher
Log.d("Debug", "Running on: ${Thread.currentThread().name}")

// Check if on main thread
if (Looper.myLooper() == Looper.getMainLooper()) {
    Log.w("Debug", "Running on main thread!")
}
```

---

## Troubleshooting

| Problem | Cause | Solution |
|---------|-------|----------|
| **"Hilt cannot find a binding for X"** | Dependency not provided in DI modules | Check `DatabaseModule`, `NetworkModule`, `RepositoryModule` have `@Provides` or `@Binds` for X. Run `./gradlew clean build` to regenerate Hilt code. |
| **"Compilation error: Cannot resolve symbol"** | KSP/KAPT processor not run | Run `./gradlew clean build` to regenerate Hilt/Room code. Check that KAPT is enabled in `build.gradle.kts`. |
| **"Test hangs or never completes"** | Flow not collected or coroutine not launched | Wrap test with `runTest {}` and call `.first()` or `.collect()` on Flow. Use `advanceUntilIdle()` to advance virtual time. |
| **"Mock not working, real database accessed"** | Using `every` instead of `coEvery` for suspend function | Check function signature. Use `every` for non-suspend, `coEvery` for suspend functions. |
| **"Build passes but app crashes at runtime"** | Missing `@Singleton` annotation on repository | Ensure all repository implementations have `@Singleton`. Check Hilt modules are properly configured. |
| **"API key appears in logs"** | Hardcoded or logged during debug | Fetch from `EncryptedSharedPreferences`. Never log sensitive values. Remove all `Log.d()` that print API keys. |
| **"Room database version conflict"** | Database schema changed but version not incremented | Increment `version` in `@Database` annotation. Consider adding migration or using `.fallbackToDestructiveMigration()` for dev builds. |
| **"NetworkOnMainThreadException"** | Blocking network call on main thread | Ensure repository uses `withContext(Dispatchers.IO)` or `.flowOn(Dispatchers.IO)`. Never use `.get()` on Flow. |
| **"Compose recomposition loop"** | State change triggers itself | Check that ViewModel state updates don't re-trigger the same operation. Use `derivedStateOf` for computed values. |
| **"WiFi scanning not working in tests"** | Real hardware required for WiFi scanning | Mock scanner service in tests. Use real device for integration testing, not emulator. |
| **"Hilt ViewModels not injecting"** | Missing `@HiltViewModel` or `@AndroidEntryPoint` | Ensure ViewModel has `@HiltViewModel` and Activity/Fragment has `@AndroidEntryPoint`. Use `hiltViewModel()` in Compose. |
| **"Detekt failures blocking build"** | Code doesn't meet style guidelines | Run `./gradlew detekt` to see issues. Fix or suppress with `@Suppress("RuleName")` with justification. |

---

## Additional Resources

### Project Documentation

- **CLAUDE.md** - Architectural overview and build configuration
- **README.md** - User-facing features and setup instructions
- **docs/DEVELOPMENT.md** - Development workflow and environment setup
- **docs/BUILD_FIX_GUIDE.md** - Build troubleshooting

### External Resources

- [Kotlin Coroutines Guide](https://kotlinlang.org/docs/coroutines-guide.html)
- [Hilt Dependency Injection](https://developer.android.com/training/dependency-injection/hilt-android)
- [Room Database](https://developer.android.com/training/data-storage/room)
- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Retrofit](https://square.github.io/retrofit/)

---

## Questions & Support

- **Architecture questions**: Check CLAUDE.md or ask in GitHub Discussions
- **Build issues**: See docs/BUILD_FIX_GUIDE.md
- **Bug reports**: Open a GitHub Issue using the bug report template
- **Feature requests**: Open a GitHub Issue using the feature request template

---

**Thank you for contributing to ShadowCheckMobile!**

Your contributions help make network security and surveillance detection accessible to everyone.
