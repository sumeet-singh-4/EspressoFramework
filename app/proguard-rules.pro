# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
-renamesourcefileattribute SourceFile

# Keep test framework classes
-keep class com.espresso.framework.** { *; }

# Keep Espresso classes
-keep class androidx.test.espresso.** { *; }

# Keep JUnit classes
-keep class org.junit.** { *; }
-keep class junit.** { *; }

# Keep Allure classes
-keep class io.qameta.allure.** { *; }

# Keep Jackson JSON processing classes
-keep class com.fasterxml.jackson.** { *; }
-keepclassmembers class * {
    @com.fasterxml.jackson.annotation.* <fields>;
    @com.fasterxml.jackson.annotation.* <methods>;
}

# Keep Apache POI classes for Excel reading
-keep class org.apache.poi.** { *; }

# Keep SLF4J and Logback classes
-keep class org.slf4j.** { *; }
-keep class ch.qos.logback.** { *; }

# Preserve annotations
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes InnerClasses
-keepattributes EnclosingMethod

# Keep native methods
-keepclasseswithmembernames class * {
    native <methods>;
}

# Keep custom exceptions
-keep public class * extends java.lang.Exception

# AndroidX rules
-keep class androidx.** { *; }
-keep interface androidx.** { *; }
