package info.jotajoti.jid

import graphql.scalars.ExtendedScalars
import info.jotajoti.jid.jidcode.AssociatedCode
import info.jotajoti.jid.jidcode.ValidJidCodeValidator
import info.jotajoti.jid.location.UniqueCodeAndYearValidator
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource
import org.springframework.graphql.execution.RuntimeWiringConfigurer


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
