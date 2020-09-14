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

    // Spring Boot
    implementation("org.springframework.boot:spring-boot-starter-web")

    // Spring Cloud
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-zuul:2.2.5.RELEASE")

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
