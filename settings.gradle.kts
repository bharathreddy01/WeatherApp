pluginManagement {
    repositories {
        google() // Required for Android Gradle Plugin
        mavenCentral() // Required for libraries like Hilt
        gradlePluginPortal() // For Gradle plugins
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "WeatherApplication"
include(":app")
