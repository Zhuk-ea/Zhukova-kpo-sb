plugins {
    id("java")
}

allprojects {
    group = "com.hse"
    version = "1.0.0"

    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "java")

    java {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    dependencies {
        implementation("org.springframework.boot:spring-boot-starter:3.2.0")
        testImplementation("org.springframework.boot:spring-boot-starter-test:3.2.0")
    }
}