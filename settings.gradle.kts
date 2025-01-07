pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        // Agregar el repositorio JitPack
        maven { url = uri("https://jitpack.io") }
    }
}

rootProject.name = "GSTI"
include(":app")
