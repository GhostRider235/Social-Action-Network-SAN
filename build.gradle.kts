plugins {
	java
	id("org.springframework.boot") version "3.3.11"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "com.proyect"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
	// Repositorio necesario para Spring AI
	maven { url = uri("https://repo.spring.io/milestone") }
}

val springAiVersion = "1.0.0"

dependencies {
	// Web y Vistas
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-thymeleaf")

	// Persistencia y Validación
	implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
	implementation("org.springframework.boot:spring-boot-starter-validation")

	// Seguridad y Encriptación
	implementation("org.springframework.boot:spring-boot-starter-security")


	// Spring AI (OpenAI)
	implementation("org.springframework.ai:spring-ai-starter-model-openai")

	// Utilidades y Aspectos
	implementation("org.springframework.retry:spring-retry")
	implementation("org.springframework:spring-aspects")

	// Lombok (Configuración necesaria en Gradle)
	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")

	// Desarrollo
	developmentOnly("org.springframework.boot:spring-boot-devtools")

	// Testing
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

dependencyManagement {
	imports {
		mavenBom("org.springframework.ai:spring-ai-bom:$springAiVersion")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}