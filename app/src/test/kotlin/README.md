# Test Suite

This directory contains unit tests for the ShadowCheckMobile application.

## Structure

```
test/kotlin/com/shadowcheck/mobile/
├── TestUtils.kt                              # Common test utilities and factory methods
├── domain/
│   └── usecase/                              # Use case tests
│       ├── GetAllWifiNetworksUseCaseTest.kt
│       ├── SearchWifiNetworksUseCaseTest.kt
│       ├── GetAllBluetoothDevicesUseCaseTest.kt
│       └── GetAllCellularTowersUseCaseTest.kt
├── presentation/
│   └── viewmodel/                            # ViewModel tests
│       ├── WifiViewModelTest.kt
│       ├── BluetoothViewModelTest.kt
│       └── CellularViewModelTest.kt
└── data/
    └── repository/                           # Repository tests
        └── WifiNetworkRepositoryImplTest.kt
```

## Running Tests

### Run all tests
```bash
./gradlew test
```

### Run tests with logging
```bash
./gradlew test --info
```

### Run specific test class
```bash
./gradlew test --tests com.shadowcheck.mobile.domain.usecase.GetAllWifiNetworksUseCaseTest
```

### Run tests with coverage
```bash
./gradlew testDebugUnitTest
```

## Testing Guidelines

### 1. Use MockK for Mocking
All tests use MockK for mocking dependencies:

```kotlin
@MockK
private lateinit var repository: WifiNetworkRepository

@InjectMockKs
private lateinit var useCase: GetAllWifiNetworksUseCase
```

### 2. Use Test Utilities
The `TestUtils` object provides factory methods for creating test data:

```kotlin
val network = TestUtils.createTestWifiNetwork(ssid = "MyNetwork")
val networks = TestUtils.createTestWifiNetworks(count = 5)
```

### 3. Use Coroutine Test Dispatcher
For testing coroutines and ViewModels:

```kotlin
private val testDispatcher = StandardTestDispatcher()

@Before
fun setup() {
    Dispatchers.setMain(testDispatcher)
}

@After
fun tearDown() {
    Dispatchers.resetMain()
}

@Test
fun `test something`() = runTest {
    // Test code
    advanceUntilIdle() // Wait for coroutines to complete
}
```

### 4. Test Naming Convention
Use backticks for descriptive test names:

```kotlin
@Test
fun `invoke should return all networks filtered and sorted by timestamp`() = runTest {
    // Test implementation
}
```

### 5. Test Structure (Given-When-Then)
```kotlin
@Test
fun `test description`() = runTest {
    // Given - Setup test data and expectations
    val testData = createTestData()
    every { dependency.method() } returns expectedResult

    // When - Execute the action being tested
    val result = systemUnderTest.execute()

    // Then - Assert the results
    assertEquals(expectedValue, result)
    verify { dependency.method() }
}
```

## Test Coverage Goals

- **Use Cases**: 100% - All business logic should be tested
- **ViewModels**: 90%+ - All state changes and user interactions
- **Repositories**: 80%+ - Data transformation and API calls

## Dependencies

- **JUnit 4**: Test framework
- **MockK**: Mocking library for Kotlin
- **Kotlinx Coroutines Test**: Testing utilities for coroutines
- **Truth** (optional): Fluent assertions

## Common Test Patterns

### Testing Flow Emissions
```kotlin
@Test
fun `flow should emit values`() = runTest {
    // Given
    val expected = listOf(item1, item2)
    every { repository.getData() } returns flowOf(expected)

    // When
    val result = useCase().first()

    // Then
    assertEquals(expected, result)
}
```

### Testing StateFlow in ViewModels
```kotlin
@Test
fun `state should update`() = runTest {
    // Given
    val expectedState = ExpectedState()
    every { useCase() } returns flowOf(expectedState)

    // When
    viewModel.updateState()
    advanceUntilIdle()

    // Then
    assertEquals(expectedState, viewModel.state.value)
}
```

### Testing Error Handling
```kotlin
@Test
fun `should handle errors gracefully`() = runTest {
    // Given
    val exception = RuntimeException("Test error")
    coEvery { repository.getData() } throws exception

    // When
    viewModel.loadData()
    advanceUntilIdle()

    // Then
    assertTrue(viewModel.error.value != null)
    assertEquals("Test error", viewModel.error.value)
}
```

## Troubleshooting

### Tests Not Running
- Ensure JDK 17 is installed and configured
- Run `./gradlew clean test`
- Check that test dependencies are properly configured in `build.gradle.kts`

### Coroutine Tests Hanging
- Make sure to call `advanceUntilIdle()` after triggering coroutines
- Verify `Dispatchers.setMain(testDispatcher)` is called in `@Before`
- Check that `Dispatchers.resetMain()` is called in `@After`

### MockK Issues
- Ensure `@get:Rule val mockkRule = MockKRule(this)` is present
- Verify all mocked dependencies are annotated with `@MockK`
- Use `every { }` for regular functions and `coEvery { }` for suspend functions
