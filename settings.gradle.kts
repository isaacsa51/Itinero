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
include(":designsystem-lib")
include(":core:data")
include(":core:domain")
include(":di")
include(":feature:home")
include(":feature:onboard")
include(":core:navigation")
include(":feature:auth")
include(":feature:expenses")
include(":feature:chat")
include(":feature:itinerary")
include(":feature:settings")
include(":feature:itinerary:data")
include(":feature:itinerary:domain")
include(":feature:expenses:data")
include(":feature:expenses:domain")
include(":core:settings")
include(":feature:chat:data")
include(":feature:chat:domain")
