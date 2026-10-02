plugins {
    id("com.android.application")
    id("io.qameta.allure") version "2.11.2"
}

android {
    namespace = "com.espresso.framework"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.espresso.framework"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Enable test orchestrator for better test isolation
        testOptions {
            execution = "ANDROIDX_TEST_ORCHESTRATOR"
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isDebuggable = true
        }
    }

    // Environment-specific build flavors (like Appium framework)
    flavorDimensions += "environment"
    productFlavors {
        create("dev") {
            dimension = "environment"
            buildConfigField("String", "ENV", "\"dev\"")
        }
        create("qa") {
            dimension = "environment"
            buildConfigField("String", "ENV", "\"qa\"")
        }
        create("sbx") {
            dimension = "environment"
            buildConfigField("String", "ENV", "\"sbx\"")
        }
        create("prod") {
            dimension = "environment"
            buildConfigField("String", "ENV", "\"prod\"")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    testOptions {
        unitTests.isReturnDefaultValues = true
        animationsDisabled = true
    }

    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // AndroidX Core
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // Espresso Core Dependencies
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.test.espresso:espresso-contrib:3.5.1")
    androidTestImplementation("androidx.test.espresso:espresso-intents:3.5.1")
    androidTestImplementation("androidx.test.espresso:espresso-web:3.5.1")
    androidTestImplementation("androidx.test.espresso:espresso-idling-resource:3.5.1")

    // AndroidX Test
    androidTestImplementation("androidx.test:runner:1.5.2")
    androidTestImplementation("androidx.test:rules:1.5.0")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestUtil("androidx.test:orchestrator:1.4.2")

    // UI Automator (for system UI interactions)
    androidTestImplementation("androidx.test.uiautomator:uiautomator:2.3.0")

    // JUnit 4 (Espresso uses JUnit 4)
    androidTestImplementation("junit:junit:4.13.2")

    // Allure Reporting
    androidTestImplementation("io.qameta.allure:allure-java-commons:2.25.0")
    androidTestImplementation("io.qameta.allure:allure-junit4:2.25.0")

    // JSON Parsing (Jackson - same as Appium framework)
    androidTestImplementation("com.fasterxml.jackson.core:jackson-databind:2.16.1")
    androidTestImplementation("com.fasterxml.jackson.core:jackson-core:2.16.1")
    androidTestImplementation("com.fasterxml.jackson.core:jackson-annotations:2.16.1")

    // Excel Reading (Apache POI - same as Appium framework)
    androidTestImplementation("org.apache.poi:poi:5.2.5")
    androidTestImplementation("org.apache.poi:poi-ooxml:5.2.5")

    // Logging (SLF4J + Logback Android)
    androidTestImplementation("org.slf4j:slf4j-api:2.0.11")
    androidTestImplementation("com.github.tony19:logback-android:3.0.0")

    // MockWebServer (for API mocking)
    androidTestImplementation("com.squareup.okhttp3:mockwebserver:4.12.0")

    // Hamcrest Matchers
    androidTestImplementation("org.hamcrest:hamcrest:2.2")

    // Kotlin Standard Library (for build scripts)
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.22")
}

allure {
    version.set("2.25.0")
}

tasks.register("clearAllureResults") {
    doLast {
        delete("build/allure-results")
    }
}

tasks.register("generateAllureReport") {
    dependsOn("connectedAndroidTest")
    doLast {
        exec {
            commandLine("allure", "generate", "build/allure-results", "-o", "build/reports/allure-report", "--clean")
        }
    }
}
