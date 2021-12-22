plugins {
    java
    signing
    `maven-publish`
}

group = "nz.cheyne.junit"
version = "0.0.1"

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
        events("FAILED", "PASSED", "SKIPPED")
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

java {
    withSourcesJar()
    withJavadocJar()
}

publishing {
    publications {
        create<MavenPublication>("randomTestFilter") {
            from(components["java"])
            pom {
                name.set("Random Test Filter")
                description.set("A filter to run a randomized subset of JUnit tests.")
                url.set("https://cheyne.nz/random-test-filter/")
                licenses {
                    license {
                        name.set("MIT Licence")
                        url.set("https://github.com/CheyneWilson/RandomTestFilter/blob/main/LICENSE")
                    }
                }
                developers {
                    developer {
                        id.set("cheynewilson")
                        name.set("Cheyne Wilson")
                        email.set("dev+random-test-filter@cheyne.nz")
                        url.set("https://cheyne.nz")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/cheynewilson/random-test-filter.git")
                    developerConnection.set("scm:git:ssh://github.com/cheynewilson/random-test-filter.git")
                    url.set("https://github.com/cheynewilson/random-test-filter")
                }
            }
        }
    }
}

val signingKeyId: String? by project
val signingKey: String? by project
val signingPassword: String? by project

signing {
    useInMemoryPgpKeys(signingKeyId, signingKey, signingPassword)
    sign(publishing.publications["randomTestFilter"])
}
