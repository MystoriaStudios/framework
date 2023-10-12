# Getting Started

How setup the Framework Paper API with a gradle project.

`build.gradle.kts`

```kts
// For a guide on how to add this to your gradle settings check out (TODO)
val artifactory_contextUrl: String by project
val artifactory_release: String by project
val artifactory_user: String by project
val artifactory_password: String by project

repositories {
    maven {
        name = "Jungle" // This is subject to change
        url = URI("${artifactory_contextUrl}/${artifactory_release}")
        credentials {
            username = artifactory_user
            password = artifactory_password
        }
    }
}

dependencies {
    compileOnly("net.revive.framework:independent-api:VERSION") // Framework API
    compileOnly("net.revive.framework:paper-api:VERSION") // Paper Specific API
}
```