package info.jotajoti.jampuz.dev

import org.slf4j.LoggerFactory
import org.springframework.boot.SpringApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.devtools.restart.RestartScope
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.annotation.Bean
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.MySQLContainer
import info.jotajoti.jampuz.main as springMain

@TestConfiguration
@EnableConfigurationProperties(SampleProperties::class)
class DevJampuzApplication {

    @Bean
    @RestartScope
    @ServiceConnection
    fun mySQLContainer() =
        MySQLContainer("mysql:8")

    @Bean
    @RestartScope
    @ServiceConnection("openzipkin/zipkin")
    fun zipkinContainer() =
        GenericContainer("openzipkin/zipkin").apply {
            addExposedPorts(9411)
        }

}

fun main(args: Array<String>) {
    SpringApplication
        .from(::springMain)
        .with(DevJampuzApplication::class.java)
        .run(*args)
        .applicationContext
        .also { context ->
            context
                .getBean(MySQLContainer::class.java)
                .also {
                    LoggerFactory
                        .getLogger(DevJampuzApplication::class.java)
                        .info("MySQL started on port ${it.firstMappedPort}")
                }

            context
                .getBean("zipkinContainer", GenericContainer::class.java)
                .also {
                    LoggerFactory
                        .getLogger(DevJampuzApplication::class.java)
                        .info("Zipkin started on port ${it.firstMappedPort}: http://localhost:${it.firstMappedPort}/")
                }
        }

}
