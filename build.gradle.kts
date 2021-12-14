import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.31"
    id("application")
}

group = "me.ieva"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-test:1.5.31")
    implementation("nz.ac.waikato.cms.weka:weka-stable:3.8.5")
    implementation("org.slf4j:slf4j-log4j12:1.7.32")
    implementation("org.slf4j:slf4j-api:1.7.32")
    implementation("guru.nidi:graphviz-java:0.18.1")
    implementation("org.graalvm.js:js:21.3.0")
}

tasks.test {
    useJUnit()
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "16"
}

application {
    mainClass.set("MainKt")
}
