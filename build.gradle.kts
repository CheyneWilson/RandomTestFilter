plugins {
   java
}

version "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.junit.platform:junit-platform-launcher:1.8.2")
    implementation("org.slf4j:slf4j-api:1.7.32")
    testImplementation("ch.qos.logback:logback-classic:1.2.9")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")

}

tasks.withType<Test> {
    useJUnitPlatform()

    // Did not class-randomize with junit 5.7.0... it worked with 5.8.2
    testLogging {
        events ("FAILED", "PASSED", "SKIPPED")
    }
}

tasks.create<Test>("randomTests") {
    group = "verification"
    systemProperty("nz.cheyne.junit.test.limit", 2)
    // Randomize the order that individual tests are run
    systemProperty("junit.jupiter.testmethod.order.default", "org.junit.jupiter.api.MethodOrderer\$Random")
    // Randomize the order that test classes are run.
    systemProperty("junit.jupiter.testclass.order.default", "org.junit.jupiter.api.ClassOrderer\$Random")
    include("**/Dummy**")
}