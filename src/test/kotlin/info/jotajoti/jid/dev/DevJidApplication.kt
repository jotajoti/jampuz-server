package info.jotajoti.jid.dev

import org.slf4j.*
import org.springframework.boot.*
import org.springframework.boot.context.properties.*
import org.springframework.boot.devtools.restart.*
import org.springframework.boot.test.context.*
import org.springframework.boot.testcontainers.service.connection.*
import org.springframework.context.annotation.*
import org.testcontainers.containers.*
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
        .applicationContext
        .getBean(MySQLContainer::class.java)
        .also {
            LoggerFactory
                .getLogger(DevJidApplication::class.java)
                .info("MySQL started on port ${it.firstMappedPort}")
        }

}
