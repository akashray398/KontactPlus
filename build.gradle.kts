// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.ksp) apply false
    
    // Add JVM and Ktor plugins to root for version management
    id("org.jetbrains.kotlin.jvm") version libs.versions.kotlin.get() apply false
    id("io.ktor.plugin") version libs.versions.ktor.get() apply false
}
