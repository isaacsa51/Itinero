pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
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

rootProject.name = "Itinero"
include(":app")
include(":designsystem")
include(":core:data")
include(":core:domain")
include(":di")
include(":feature:home")
include(":feature:onboard")
include(":core:navigation")
include(":feature:auth")
include(":feature:expenses")
include(":feature:chat")
