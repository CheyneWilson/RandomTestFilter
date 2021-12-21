# JUnit 5 - Random Test Filter

This filter enables a randomized subset of tests to be executed. The filter is only takes effect when a limit is 
configured via a system property doesn't interfere with test configurations. This filter requires JUnit 5.

I was working on a project that had thousands of tests that took hours to run. Optimizing overall test times is a much
bigger job, it's been death by a thousand cuts and so there is a lot of mending to do. There was a practice of running a 
randomized subset of tests as a quicker smoke test. That inspired the creation of this Filter.

## Quickstart

1. Include the library in your source. E.g. in your `build.gradle.kts` add the following:
```kotlin
repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testImplementation("nz.cheyne.junit.RandomTestFilter")
    
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")
}
```
2. Register the filter. Create the file `org.junit.platform.launcher.PostDiscoveryFilter` with the following contents.
```
nz.cheyne.junit.RandomTestFilter
```
3. Set the system property `nz.cheyne.junit.test.limit` to limit the number of tests to run. This can be passed in via
the command line or configured as a specific task. Note, if this is not set, the filter will not take effect. An example
of how to configure this in your `build.gradle.kts` is shown below:
```kotlin
tasks.create<Test>("randomTests") {
    group = "verification"
    systemProperty("nz.cheyne.junit.test.limit", 2)
    include("**/Dummy**")
}
```
Alternatively, the system property can be passed on the commandline with `-Dnz.cheyne.junit.test.limit=2`.

## Randomizing the test execution order

The execution order of tests can be changed using standard JUnit5 system properties. An example of configuring them in 
a gradle tasks is shown below:
```kotlin
tasks.create<Test>("randomTests") {
    group = "verification"
    systemProperty("nz.cheyne.junit.test.limit", 2)
    include("**/Dummy**")
    // Randomize the order that individual tests are run
    systemProperty("junit.jupiter.testmethod.order.default", "org.junit.jupiter.api.MethodOrderer\$Random")
    // Randomize the order that test classes are run.
    systemProperty("junit.jupiter.testclass.order.default", "org.junit.jupiter.api.ClassOrderer\$Random")
 
}
```

## Contributing

This is a pretty simple project (single class). 

### Testing

A small set of unit tests that check the filtering can executed with the standard `test` tasks. The `randomTests` test
needs to be manually run to verify the functionality of the filter.
![](documentation/images/twoTestsAtRandom.png)
