import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

apply(plugin = "org.springframework.boot")

plugins {
	application
}
application {
	mainClass.set("io.ktor.server.netty.EngineMain")
}

group = "com.gemu"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17
val ktorVersion = "2.0.1"
val logbackVersion = "1.2.3"

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

	implementation("io.ktor:ktor-server-netty:$ktorVersion")
	implementation("io.ktor:ktor-server-websockets:$ktorVersion")
	implementation("io.ktor:ktor-server-auth:$ktorVersion")
	implementation("io.ktor:ktor-server-auth-jwt:$ktorVersion")

	implementation("com.nimbusds:nimbus-jose-jwt:9.23")

	// Logging
	implementation("ch.qos.logback:logback-classic:$logbackVersion")

	// Test
	testImplementation("io.ktor:ktor-server-test-host:$ktorVersion")
	testImplementation("org.jetbrains.kotlin:kotlin-test")

	implementation("io.lettuce:lettuce-core:6.2.0.RELEASE")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "17"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
