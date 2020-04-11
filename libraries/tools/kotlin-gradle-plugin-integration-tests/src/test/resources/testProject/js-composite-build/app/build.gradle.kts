plugins {
    kotlin("js") version "<pluginMarkerVersion>"
}

repositories {
    jcenter()
    mavenLocal()
    mavenCentral()
}

kotlin.js {
    binaries.executable()
    nodejs()
}

dependencies {
    implementation(kotlin("stdlib-js"))
    implementation("com.example:lib2")
}