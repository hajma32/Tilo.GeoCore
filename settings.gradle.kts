rootProject.name = "Tilo.GeoCore"

pluginManagement {
    plugins {
        kotlin("multiplatform") version "2.3.0"
    }

    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
    }
}

include(":spatial-index")
project(":spatial-index").projectDir = file("spatial-index")
