buildscript {
    repositories {
        google() // Required for Android Gradle Plugin
        mavenCentral() // Required for Kotlin and Hilt Plugins
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.6.1") // Android Gradle Plugin
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.10") // Kotlin Plugin
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.48") // Hilt Plugin
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
