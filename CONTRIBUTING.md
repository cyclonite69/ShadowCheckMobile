# Contributing to ShadowCheckMobile

Thank you for considering contributing to ShadowCheckMobile! This document provides guidelines and instructions for contributing.

## Getting Started

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or later
- JDK 17
- Android SDK 34
- Git

### Setting Up Development Environment

1. **Fork and Clone**
   ```bash
   git clone https://github.com/yourusername/ShadowCheckMobile.git
   cd ShadowCheckMobile
   ```

2. **Configure Local Properties**
   ```bash
   cp local.properties.example local.properties
   # Edit local.properties and add your API keys
   ```

3. **Build the Project**
   ```bash
   ./gradlew clean build
   ```

4. **Run Tests**
   ```bash
   ./gradlew test
   ```

## Development Workflow

### Branch Naming Convention
- `feature/` - New features (e.g., `feature/bluetooth-scanning`)
- `fix/` - Bug fixes (e.g., `fix/wifi-crash`)
- `refactor/` - Code refactoring (e.g., `refactor/viewmodel-cleanup`)
- `test/` - Adding or updating tests (e.g., `test/wifi-repository`)
- `docs/` - Documentation updates (e.g., `docs/api-guide`)

### Commit Message Guidelines

Follow the conventional commits format:

```
<type>(<scope>): <subject>

<body>

<footer>
```

**Types:**
- `feat`: New feature
- `fix`: Bug fix
- `refactor`: Code refactoring
- `test`: Adding or updating tests
- `docs`: Documentation changes
- `style`: Code style changes (formatting, etc.)
- `perf`: Performance improvements
- `chore`: Build process or auxiliary tool changes

**Example:**
```
feat(wifi): Add WPA3 network detection

Implement WPA3 security detection for WiFi networks.
Updates the scanner service to properly identify WPA3
capabilities and display them in the network list.

Closes #42
```

## Code Style Guidelines

### Kotlin Style
- Follow [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)
- Use 4 spaces for indentation
- Max line length: 120 characters
- Use meaningful variable names (avoid decompilation artifacts like `var1`, `var2`)

### Architecture Patterns

**Use Clean Architecture for new code:**

```
domain/
├── model/          # Pure business objects
├── repository/     # Repository interfaces
└── usecase/        # Business logic

data/
├── database/       # Room entities and DAOs
├── repository/     # Repository implementations
└── remote/         # API services and DTOs

presentation/
└── viewmodel/      # Hilt ViewModels

ui/
├── screens/        # Composable screens
└── components/     # Reusable UI components
```

### File Organization

1. **Package by feature, not layer**
   ```
   ✅ Good:
   com.shadowcheck.mobile.wifi/
   ├── data/
   ├── domain/
   └── presentation/

   ❌ Avoid:
   com.shadowcheck.mobile.data/
   com.shadowcheck.mobile.domain/
   com.shadowcheck.mobile.presentation/
   ```

2. **Use descriptive file names**
   - ViewModels: `WifiViewModel.kt`
   - Use Cases: `GetAllWifiNetworksUseCase.kt`
   - Repositories: `WifiNetworkRepository.kt` (interface), `WifiNetworkRepositoryImpl.kt` (implementation)

### Dependency Injection

Always use Hilt for dependency injection:

```kotlin
// ViewModel
@HiltViewModel
class WifiViewModel @Inject constructor(
    private val getAllWifiNetworks: GetAllWifiNetworksUseCase
) : ViewModel()

// Repository
@Singleton
class WifiNetworkRepositoryImpl @Inject constructor(
    private val dao: WifiNetworkDao,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) : WifiNetworkRepository
```

### Testing Requirements

All new code must include tests:

1. **Use Cases**: 100% coverage required
2. **ViewModels**: 90%+ coverage
3. **Repositories**: 80%+ coverage

**Example test:**
```kotlin
@ExperimentalCoroutinesApi
class GetAllWifiNetworksUseCaseTest {
    @get:Rule
    val mockkRule = MockKRule(this)

    @MockK
    private lateinit var repository: WifiNetworkRepository

    @InjectMockKs
    private lateinit var useCase: GetAllWifiNetworksUseCase

    @Test
    fun `invoke should return filtered networks`() = runTest {
        // Given
        val networks = TestUtils.createTestWifiNetworks(5)
        every { repository.getAllNetworks() } returns flowOf(networks)

        // When
        val result = useCase().first()

        // Then
        assertEquals(5, result.size)
    }
}
```

## Pull Request Process

### Before Submitting

1. **Run code quality checks**
   ```bash
   ./gradlew detekt
   ```

2. **Run all tests**
   ```bash
   ./gradlew test
   ```

3. **Format code**
   - Use Android Studio's "Reformat Code" (Ctrl+Alt+L / Cmd+Option+L)
   - Follow `.editorconfig` settings

4. **Update documentation**
   - Update CLAUDE.md if architectural patterns change
   - Add KDoc comments for public APIs
   - Update README.md if user-facing features change

### PR Checklist

- [ ] Code follows style guidelines
- [ ] Tests added for new functionality
- [ ] All tests pass
- [ ] Documentation updated
- [ ] Commit messages follow convention
- [ ] No merge conflicts with main branch
- [ ] PR description explains changes clearly

### PR Description Template

```markdown
## Description
Brief description of changes

## Type of Change
- [ ] Bug fix
- [ ] New feature
- [ ] Refactoring
- [ ] Documentation update

## Testing
- [ ] Unit tests added/updated
- [ ] Manual testing performed on device
- [ ] Edge cases considered

## Screenshots (if UI changes)
Add screenshots here

## Related Issues
Closes #<issue_number>

## Additional Notes
Any additional context
```

## Code Review Process

### Reviewers Will Check

1. **Code Quality**
   - Follows Clean Architecture
   - Uses Hilt DI properly
   - No code duplication
   - Proper error handling

2. **Testing**
   - Adequate test coverage
   - Tests follow Given-When-Then pattern
   - Edge cases covered

3. **Performance**
   - No blocking calls on main thread
   - Efficient database queries
   - Proper use of coroutines and Flow

4. **Security**
   - No hardcoded secrets or API keys
   - Proper data encryption
   - Input validation

### Addressing Review Comments

- Respond to all comments
- Make requested changes in new commits (don't force push)
- Mark conversations as resolved after addressing
- Ask for clarification if feedback is unclear

## Common Pitfalls to Avoid

### ❌ Don't
- Use KSP for Room/Hilt (project uses KAPT)
- Create new files in `com.shadowcheck.mobile.rebuilt` package
- Add features without tests
- Commit API keys or secrets
- Use blocking calls in ViewModels
- Ignore detekt warnings

### ✅ Do
- Use KAPT for annotation processing
- Follow Clean Architecture for new code
- Write tests for all new functionality
- Use `SecureApiKeyManager` for API keys
- Use coroutines and Flow for async operations
- Fix detekt issues before submitting PR

## Getting Help

- **Questions about architecture**: Check CLAUDE.md
- **Build issues**: See docs/BUILD_FIX_GUIDE.md
- **Development workflow**: See docs/DEVELOPMENT.md
- **Feature questions**: Open a GitHub Discussion
- **Bug reports**: Open a GitHub Issue

## Recognition

Contributors will be recognized in:
- README.md contributors section
- Release notes for significant contributions
- Git commit history

Thank you for contributing to ShadowCheckMobile!
