# Espresso Framework - Structure Guide

## 📁 Complete Project Structure

```
EspressoFramework/
│
├── 📄 Configuration Files (Root Level)
│   ├── build.gradle.kts              # Root build configuration
│   ├── settings.gradle.kts           # Project settings & plugin management
│   ├── gradle.properties             # Gradle properties & environment config
│   └── .gitignore                    # Git ignore rules
│
├── 📁 gradle/
│   └── wrapper/                      # Gradle wrapper files
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties
│
├── 📁 app/
│   ├── build.gradle.kts              # App-level build configuration
│   ├── proguard-rules.pro            # ProGuard rules
│   │
│   ├── 📁 src/
│   │   │
│   │   ├── 📁 main/                  # Main app source (minimal)
│   │   │   ├── AndroidManifest.xml   # App manifest
│   │   │   ├── java/com/espresso/framework/
│   │   │   │   └── MainActivity.java # Placeholder activity
│   │   │   └── res/
│   │   │       └── values/
│   │   │           └── strings.xml
│   │   │
│   │   └── 📁 androidTest/           # Test framework (main content)
│   │       │
│   │       ├── 📁 java/com/espresso/framework/
│   │       │   │
│   │       │   ├── 📁 base/          # Core framework classes
│   │       │   │   ├── BasePage.java         # Base page object with Espresso actions
│   │       │   │   ├── FluentPage.java       # Fluent API implementation
│   │       │   │   └── BaseTest.java         # Base test class with lifecycle
│   │       │   │
│   │       │   ├── 📁 config/        # Configuration management
│   │       │   │   └── ConfigReader.java     # Multi-environment config reader
│   │       │   │
│   │       │   ├── 📁 pages/         # Page Objects (examples)
│   │       │   │   └── LoginPage.java        # Example login page
│   │       │   │
│   │       │   ├── 📁 components/    # Reusable UI components
│   │       │   │   ├── AlertDialogComponent.java
│   │       │   │   └── LoadingSpinnerComponent.java
│   │       │   │
│   │       │   ├── 📁 utils/         # Utility classes
│   │       │   │   ├── ScreenshotUtils.java      # Screenshot capture
│   │       │   │   ├── JsonReader.java           # JSON data reader
│   │       │   │   ├── ExcelReader.java          # Excel data reader
│   │       │   │   ├── RetryUtils.java           # Retry logic
│   │       │   │   ├── IdlingResourceManager.java # Async handling
│   │       │   │   └── GestureUtils.java         # Advanced gestures
│   │       │   │
│   │       │   ├── 📁 listeners/     # Test event listeners
│   │       │   │   ├── TestLifecycleListener.java # Test lifecycle hooks
│   │       │   │   └── AllureListener.java        # Allure reporting
│   │       │   │
│   │       │   ├── 📁 rules/         # JUnit rules
│   │       │   │   └── RetryRule.java            # Automatic retry rule
│   │       │   │
│   │       │   ├── 📁 enums/         # Enumerations
│   │       │   │   └── SwipeDirection.java       # Swipe directions
│   │       │   │
│   │       │   └── 📁 exceptions/    # Custom exceptions
│   │       │       ├── EspressoFrameworkException.java (base)
│   │       │       ├── ViewNotFoundException.java
│   │       │       ├── TestTimeoutException.java
│   │       │       ├── ConfigurationException.java
│   │       │       ├── DataReadException.java
│   │       │       └── ScreenshotException.java
│   │       │
│   │       ├── 📁 java/com/espresso/tests/
│   │       │   └── LoginTest.java    # Example test class
│   │       │
│   │       └── 📁 assets/            # Test resources
│   │           ├── 📁 config/        # Environment configurations
│   │           │   ├── config.properties         # Default config
│   │           │   ├── config-dev.properties     # Development
│   │           │   ├── config-qa.properties      # QA
│   │           │   ├── config-sbx.properties     # Sandbox
│   │           │   └── config-prod.properties    # Production
│   │           │
│   │           ├── 📁 testdata/      # Test data files
│   │           │   └── testdata.json
│   │           │
│   │           ├── logback.xml       # Logging configuration
│   │           └── allure.properties # Allure configuration
│
└── 📁 Documentation/
    ├── README.md                     # Main documentation
    ├── 1_FRAMEWORK_STRUCTURE.md      # This file
    ├── 2_FRAMEWORK_FEATURES.md       # Features guide
    ├── 3_WRITING_TESTS.md            # Test writing guide
    ├── FINAL_STATUS_REPORT.md        # Project status
    └── SSL_FIX_INSTRUCTIONS.md       # SSL troubleshooting
```

---

## 🏗️ Architecture Layers

### Layer 1: Base Classes (Foundation)
**Location**: `app/src/androidTest/java/com/espresso/framework/base/`

```
BasePage.java (353 lines)
├── Core Espresso Actions
│   ├── click()
│   ├── type()
│   ├── getText()
│   ├── isDisplayed()
│   └── verifyText()
├── Wait Mechanisms
│   ├── waitForView()
│   └── waitForViewToDisappear()
├── Gestures
│   ├── swipe()
│   ├── longPress()
│   └── scrollToView()
└── Utilities
    └── closeKeyboard()

FluentPage.java (190 lines)
├── Extends BasePage
├── Fluent API Methods
│   ├── clickFluent()
│   ├── typeFluent()
│   ├── verifyTextFluent()
│   └── replaceTextFluent()
├── Navigation
│   └── navigateTo()
├── Conditional Actions
│   └── doIf()
└── Generic Type Support
    └── <T extends FluentPage>

BaseTest.java (185 lines)
├── JUnit Rules
│   ├── TestName
│   ├── RuleChain (Retry + Lifecycle + Allure)
│   └── ActivityScenarioRule (abstract)
├── Lifecycle Methods
│   ├── setUp() - before each test
│   ├── tearDown() - after each test
│   ├── onSetUp() - custom setup
│   └── onTearDown() - custom teardown
├── Test Utilities
│   ├── disableAnimations()
│   ├── getCurrentTestName()
│   └── logging helpers
└── Resource Cleanup
    └── IdlingResource cleanup
```

### Layer 2: Page Objects (UI Representation)
**Location**: `app/src/androidTest/java/com/espresso/framework/pages/`

```
LoginPage.java (example)
├── Extends FluentPage
├── Element Locators (private)
│   ├── usernameField
│   ├── passwordField
│   └── loginButton
├── Actions (public)
│   ├── typeUsername()
│   ├── typePassword()
│   ├── clickLogin()
│   └── login() - composite action
└── Verifications
    ├── verifyPageLoaded()
    ├── verifySuccessDisplayed()
    └── verifyErrorDisplayed()

YourPage.java (template)
├── Element locators
├── Page actions
└── Verification methods
```

### Layer 3: Test Classes (Test Logic)
**Location**: `app/src/androidTest/java/com/espresso/tests/`

```
LoginTest.java (example)
├── Extends BaseTest
├── ActivityScenarioRule
├── Test Setup
│   └── @Before setupTest()
├── Test Methods
│   ├── @Test testValidLogin()
│   ├── @Test testInvalidLogin()
│   ├── @Test testEmptyCredentials()
│   └── more tests...
└── Allure Annotations
    ├── @Epic
    ├── @Feature
    ├── @Story
    └── @Severity

YourTest.java (template)
├── Test data
├── Page object instances
└── Test methods
```

### Layer 4: Utilities (Supporting Services)
**Location**: `app/src/androidTest/java/com/espresso/framework/utils/`

```
ScreenshotUtils.java
├── captureScreenshot()
├── captureScreenshotOnFailure()
├── saveScreenshot()
└── attachToAllure()

JsonReader.java
├── readJson() - returns Map
├── readJson(Class<T>) - type-safe
├── getJsonValue()
├── getNestedValue()
└── readJsonArray()

ExcelReader.java
├── readExcel() - returns List<Map>
├── getCellValue()
├── getRowCount()
├── getColumnCount()
└── getTestData() - for data providers

RetryUtils.java
├── retry() - basic retry
├── retryWithExponentialBackoff()
├── retryUntilSuccess()
└── retryOnException()

IdlingResourceManager.java
├── register()
├── unregister()
├── unregisterAll()
├── SimpleCountingIdlingResource
├── TimeBasedIdlingResource
└── ConditionIdlingResource

GestureUtils.java
├── Swipes: swipeUp/Down/Left/Right()
├── Taps: tap(), doubleTap()
├── Press: longPress()
├── drag()
└── scrollToView()
```

### Layer 5: Components (Reusable UI Elements)
**Location**: `app/src/androidTest/java/com/espresso/framework/components/`

```
AlertDialogComponent.java
├── clickPositiveButton()
├── clickNegativeButton()
├── clickNeutralButton()
├── getTitle()
├── getMessage()
├── verifyDisplayed()
└── waitForDialog()

LoadingSpinnerComponent.java
├── waitForSpinnerToAppear()
├── waitForSpinnerToDisappear()
├── isDisplayed()
└── waitForLoadingComplete()
```

### Layer 6: Configuration (Environment Management)
**Location**: `app/src/androidTest/java/com/espresso/framework/config/`

```
ConfigReader.java
├── Environment Detection
│   ├── getCurrentEnvironment()
│   └── Reads: System Property > Env Var > Gradle
├── Property Getters
│   ├── getProperty()
│   ├── getPropertyAsInt()
│   ├── getPropertyAsLong()
│   └── getPropertyAsBoolean()
└── Framework Settings
    ├── getAppPackage()
    ├── getDefaultTimeout()
    ├── getRetryCount()
    ├── captureScreenshotOnFailure()
    └── disableAnimations()
```

### Layer 7: Exception Hierarchy
**Location**: `app/src/androidTest/java/com/espresso/framework/exceptions/`

```
EspressoFrameworkException (base)
├── ViewNotFoundException
│   └── Used when view not found
├── TestTimeoutException
│   └── Used when wait times out
├── ConfigurationException
│   └── Used for config errors
├── DataReadException
│   └── Used for data file errors
└── ScreenshotException
    └── Used for screenshot errors
```

### Layer 8: Test Rules
**Location**: `app/src/androidTest/java/com/espresso/framework/rules/`

```
RetryRule.java
├── Implements TestRule
├── Wraps test execution
├── Retries on failure
├── Configurable retry count
└── Logs each attempt
```

### Layer 9: Test Listeners
**Location**: `app/src/androidTest/java/com/espresso/framework/listeners/`

```
TestLifecycleListener.java
├── Extends TestWatcher
├── Hooks:
│   ├── starting()
│   ├── succeeded()
│   ├── failed()
│   └── finished()
└── Screenshot capture on failure

AllureListener.java
├── Extends TestWatcher
├── Allure Integration:
│   ├── Add parameters
│   ├── Attach screenshots
│   ├── Add failure info
│   └── Track test lifecycle
```

---

## 🔗 Component Dependencies

```
Test Class (LoginTest)
    │
    ├── extends BaseTest
    │       │
    │       ├── uses RetryRule
    │       ├── uses TestLifecycleListener
    │       ├── uses AllureListener
    │       └── uses ConfigReader
    │
    ├── uses Page Object (LoginPage)
    │       │
    │       ├── extends FluentPage
    │       │       │
    │       │       └── extends BasePage
    │       │               │
    │       │               ├── uses ViewMatchers
    │       │               ├── uses ViewActions
    │       │               ├── uses GestureUtils
    │       │               └── throws Custom Exceptions
    │       │
    │       └── uses Components
    │               ├── AlertDialogComponent
    │               └── LoadingSpinnerComponent
    │
    ├── uses Test Data
    │       ├── JsonReader
    │       └── ExcelReader
    │
    └── uses Utilities
            ├── ScreenshotUtils
            ├── RetryUtils
            └── IdlingResourceManager
```

---

## 📊 File Size & Complexity

| File | Lines | Complexity | Purpose |
|------|-------|------------|---------|
| **Base Classes** |
| BasePage.java | 353 | High | Core Espresso wrapper |
| FluentPage.java | 190 | Medium | Fluent API layer |
| BaseTest.java | 185 | Medium | Test base class |
| **Utilities** |
| ScreenshotUtils.java | 159 | Medium | Screenshot mgmt |
| ConfigReader.java | 236 | Medium | Config management |
| JsonReader.java | 157 | Low | JSON reading |
| ExcelReader.java | 261 | Medium | Excel reading |
| RetryUtils.java | 234 | Medium | Retry logic |
| IdlingResourceManager.java | 243 | High | Async handling |
| GestureUtils.java | 382 | Medium | Gestures |
| **Components** |
| AlertDialogComponent.java | 269 | Medium | Dialog handling |
| LoadingSpinnerComponent.java | 208 | Medium | Spinner handling |
| **Listeners** |
| TestLifecycleListener.java | 94 | Low | Test hooks |
| AllureListener.java | 82 | Low | Allure integration |
| **Rules** |
| RetryRule.java | 62 | Low | Retry rule |
| **Exceptions** |
| All exception classes | ~30 each | Low | Error handling |
| **Page Objects** |
| LoginPage.java | 187 | Medium | Example page |
| **Tests** |
| LoginTest.java | 197 | Medium | Example tests |

**Total Framework Size**: ~4,000 lines of production code

---

## 🎯 Key Design Patterns

### 1. Page Object Model (POM)
- **Location**: `pages/` directory
- **Pattern**: Each screen = one page class
- **Benefit**: UI changes isolated to page objects

### 2. Fluent Interface
- **Implementation**: `FluentPage.java`
- **Pattern**: Method chaining
- **Benefit**: Readable, natural test code

### 3. Builder Pattern
- **Location**: `IdlingResourceManager.java`
- **Pattern**: Resource builders
- **Benefit**: Flexible resource creation

### 4. Template Method
- **Location**: `BaseTest.java`
- **Pattern**: `onSetUp()`, `onTearDown()`
- **Benefit**: Customizable lifecycle

### 5. Singleton (quasi)
- **Location**: Utility classes
- **Pattern**: Static methods
- **Benefit**: Shared functionality

### 6. Strategy Pattern
- **Location**: `RetryUtils.java`
- **Pattern**: Different retry strategies
- **Benefit**: Flexible retry logic

### 7. Factory Pattern
- **Location**: `IdlingResourceManager.java`
- **Pattern**: Create different resource types
- **Benefit**: Flexible resource creation

---

## 🔧 Configuration Files Structure

### build.gradle.kts (Root)
```kotlin
buildscript {
    repositories { google(), mavenCentral() }
    dependencies { classpath("com.android.tools.build:gradle") }
}
tasks.register("clean")
```

### app/build.gradle.kts
```kotlin
plugins { android.application, allure }
android {
    compileSdk, minSdk, targetSdk
    buildTypes, productFlavors
    dependencies { espresso, allure, poi, jackson }
}
```

### settings.gradle.kts
```kotlin
pluginManagement { repositories }
dependencyResolutionManagement { repositories }
rootProject.name = "EspressoFramework"
include("app")
```

### gradle.properties
```properties
org.gradle.jvmargs=-Xmx4096m
android.useAndroidX=true
org.gradle.parallel=true
env=dev  # Default environment
```

---

## 📦 Assets Structure

```
assets/
├── config/                    # Environment configurations
│   ├── config.properties      # Default (fallback)
│   ├── config-dev.properties  # Development
│   ├── config-qa.properties   # QA testing
│   ├── config-sbx.properties  # Sandbox
│   └── config-prod.properties # Production
│
├── testdata/                  # Test data
│   ├── testdata.json          # JSON test data
│   ├── users.json             # User data
│   ├── testdata.xlsx          # Excel test data
│   └── [your-data-files]
│
├── logback.xml                # Logging config
│   ├── Console appender
│   ├── File appender
│   ├── Logcat appender
│   └── Rolling file appender
│
└── allure.properties          # Allure config
    ├── Results directory
    └── Link patterns
```

---

## 🎨 Naming Conventions

### Classes
- **Page Objects**: `[Screen]Page.java` (e.g., `LoginPage.java`)
- **Test Classes**: `[Feature]Test.java` (e.g., `LoginTest.java`)
- **Components**: `[Component]Component.java`
- **Utilities**: `[Function]Utils.java`
- **Exceptions**: `[Type]Exception.java`

### Methods
- **Actions**: verb + noun (e.g., `clickLoginButton()`)
- **Verifications**: `verify` + condition (e.g., `verifyPageLoaded()`)
- **Getters**: `get` + property (e.g., `getErrorMessage()`)
- **Waiters**: `waitFor` + condition (e.g., `waitForViewToDisappear()`)

### Variables
- **Locators**: descriptive name (e.g., `usernameField`, `loginButton`)
- **Data**: descriptive name (e.g., `testData`, `validUser`)

### Files
- **Config**: `config-[env].properties`
- **Data**: `[type].json` or `[type].xlsx`
- **Logs**: `test-execution-[date].log`

---

## 📍 Quick Navigation Guide

**Want to...**

**Add a new page?**
→ Create in `pages/`, extend `FluentPage`

**Add a new test?**
→ Create in `tests/`, extend `BaseTest`

**Add test data?**
→ Place in `assets/testdata/`, use `JsonReader` or `ExcelReader`

**Add new utility?**
→ Create in `utils/`, use static methods

**Configure environment?**
→ Edit `assets/config/config-[env].properties`

**Add reusable component?**
→ Create in `components/`, similar to `AlertDialogComponent`

**Handle custom exception?**
→ Create in `exceptions/`, extend `EspressoFrameworkException`

**Add new gesture?**
→ Add to `GestureUtils.java`

**Modify logging?**
→ Edit `assets/logback.xml`

---

## ✅ Structure Verification Checklist

- [ ] All 24 Java files present
- [ ] All directories properly organized
- [ ] Config files in correct locations
- [ ] Test data files accessible
- [ ] Dependencies properly declared
- [ ] No circular dependencies
- [ ] Clear separation of concerns
- [ ] Logical package structure

---

**Next**: See `2_FRAMEWORK_FEATURES.md` for detailed feature documentation
