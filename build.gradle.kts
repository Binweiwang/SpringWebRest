plugins {
    java
    id("org.springframework.boot") version "3.1.5"
    id("io.spring.dependency-management") version "1.1.3"
    jacoco
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // Dependencias de Spring Web for HTML Apps y Rest
    implementation("org.springframework.boot:spring-boot-starter-web")
    // Spring Data JPA par SQL
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    // Cache
    implementation("org.springframework.boot:spring-boot-starter-cache")
    // Validación
    implementation("org.springframework.boot:spring-boot-starter-validation")
    // Websocket
    implementation("org.springframework.boot:spring-boot-starter-websocket")
    // Spring Data JPA para MongoDB
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    // Spring Data JPA para seguridad
    implementation("org.springframework.boot:spring-boot-starter-security")
    // Test Spring Security
    testImplementation("org.springframework.security:spring-security-test")
    // Lombok
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    // H2 Database
    runtimeOnly("com.h2database:h2")
    // Para usar con jackson el controlador las fechas: LocalDate, LocalDateTime, etc
    // Lo podemos usar en el test o en el controlador, si hiciese falta, por eso está aquí
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    // Para pasar a XML los responses, negocacion de contenido
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml")
    // Dependencias para Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    // https://mvnrepository.com/artifact/io.swagger.core.v3/swagger-annotations
    implementation("io.swagger.core.v3:swagger-annotations:2.2.18")
    //
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    // thymeleaf
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    // Bootstrap
    implementation("org.webjars:bootstrap:4.6.2")
    // JWT (Json Web Token)
    implementation("com.auth0:java-jwt:4.4.0")
    // Swagger
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.2.0")
}

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport) // report is always generated after tests run
}
tasks.jacocoTestReport {
    dependsOn(tasks.test) // tests are required to run before generating the report
}

tasks.withType<Test> {
    useJUnitPlatform()
}
