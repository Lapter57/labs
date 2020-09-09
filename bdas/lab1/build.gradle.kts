plugins {
    java
    application
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

repositories {
    jcenter()
}

dependencies {
    // Annotations for better code documentation
    compile("com.intellij:annotations:12.0")

    // Spring
    compile("org.springframework:spring-context:5.2.8.RELEASE")
    compile("org.springframework:spring-test:5.2.8.RELEASE")

    // JUnit Jupiter test framework
    testCompile("org.junit.jupiter:junit-jupiter-api:5.4.0")
    testRuntime("org.junit.jupiter:junit-jupiter-engine:5.4.0")
}

val run by tasks.getting(JavaExec::class) {
    standardInput = System.`in`
}

tasks {
    test {
        maxHeapSize = "256m"
        useJUnitPlatform()
    }
}

application {
    // And limit Xmx
    applicationDefaultJvmArgs = listOf("-Xmx256m")
}

// Fail on warnings
tasks.withType<JavaCompile> {
    val compilerArgs = options.compilerArgs
    compilerArgs.add("-Werror")
    compilerArgs.add("-Xlint:all")
}