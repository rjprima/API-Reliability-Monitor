plugins {
    application
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.xerial:sqlite-jdbc:3.45.2.0")
}

sourceSets {
    main {
        resources.setSrcDirs(listOf("data", "src/main/resources"))
    }
}

application {
    mainClass = "core"
}