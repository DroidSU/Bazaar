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

rootProject.name = "Bazaar"
include(":app")
include(":core:designsystem")
include(":core:authentication")
include(":core:model")
include(":core:common")
include(":core:database")
include(":core:data")
include(":feature:dashboard")
include(":feature:products")
include(":feature:transactions")
