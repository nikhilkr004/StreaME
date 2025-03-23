pluginManagement {
    repositories {
       google()
        mavenCentral()
        gradlePluginPortal()

        maven (url = "https://jitpack.io")



    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()

        maven (url = "https://jitpack.io")
        maven ( url = "https://storage.zego.im/maven" )
    }
}

rootProject.name = "Visionary"
include(":app")
 