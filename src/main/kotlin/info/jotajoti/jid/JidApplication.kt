package info.jotajoti.jid

import graphql.scalars.*
import info.jotajoti.jid.jidcode.*
import info.jotajoti.jid.location.*
import org.springframework.aot.hint.annotation.*
import org.springframework.boot.*
import org.springframework.boot.autoconfigure.*
import org.springframework.context.annotation.*
import org.springframework.graphql.execution.*


@SpringBootApplication
class JidApplication


@Configuration
class GraphQlConfig {

    @Bean
    fun runtimeWiringConfigurer() =
        RuntimeWiringConfigurer { wiringBuilder ->
            wiringBuilder.scalar(ExtendedScalars.Date)
        }
}

@Configuration
@RegisterReflectionForBinding(
    AssociatedCode::class,
    UniqueCodeAndYearValidator::class,
    ValidJidCodeValidator::class,
)
class NativeConfig

@Configuration
@PropertySource("classpath:version.properties")
class VersionConfig

fun main(args: Array<String>) {
    runApplication<JidApplication>(*args)
}
