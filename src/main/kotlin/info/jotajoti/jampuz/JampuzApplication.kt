package info.jotajoti.jampuz

import graphql.scalars.*
import info.jotajoti.jampuz.event.*
import info.jotajoti.jampuz.jidcode.*
import info.jotajoti.jampuz.security.*
import org.springframework.aot.hint.annotation.*
import org.springframework.boot.*
import org.springframework.boot.autoconfigure.*
import org.springframework.boot.context.properties.*
import org.springframework.context.annotation.*
import org.springframework.graphql.execution.*


@SpringBootApplication
@EnableConfigurationProperties(JwtProperties::class)
class JampuzApplication


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
    runApplication<JampuzApplication>(*args)
}
