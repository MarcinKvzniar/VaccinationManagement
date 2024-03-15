pluginManagement {
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://repo1.maven.org/maven2")
        }
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "VaccinationManagement"
include(":app")
