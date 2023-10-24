package info.jotajoti.jid.dev

import org.springframework.boot.SpringApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.devtools.restart.RestartScope
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.annotation.Bean
import org.testcontainers.containers.MySQLContainer
import info.jotajoti.jid.main as springMain

@TestConfiguration
@EnableConfigurationProperties(SampleProperties::class)
class DevJidApplication {

    @Bean
    @RestartScope
    @ServiceConnection
    fun mySQLContainer() =
        MySQLContainer("mysql:8")
}

fun main(args: Array<String>) {
    SpringApplication
        .from(::springMain)
        .with(DevJidApplication::class.java)
        .run(*args)
}
