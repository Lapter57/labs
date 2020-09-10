plugins {
    java
    id("org.springframework.boot") version "2.3.3.RELEASE"
    id("io.spring.dependency-management") version "1.0.9.RELEASE"
}

group = "ru.spbstu.shakhmin"
version = "1.0-SNAPSHOT"

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_11
}

repositories {
    jcenter()
}

tasks {
    test {
        maxHeapSize = "256m"
        useJUnitPlatform()
    }
}

dependencies {
    // Annotations for better code documentation
    implementation("com.intellij:annotations:12.0")

    // Bouncycastle
    implementation("org.bouncycastle:bcpkix-jdk15on:1.66")

    // Apache commons-cli
    implementation("commons-cli:commons-cli:1.4")

    // Spring Boot
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // JUnit Jupiter test framework
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }

    val lombok = "org.projectlombok:lombok:1.18.12"
    compileOnly(lombok)
    annotationProcessor(lombok)
    testCompileOnly(lombok)
    testAnnotationProcessor(lombok)
}
