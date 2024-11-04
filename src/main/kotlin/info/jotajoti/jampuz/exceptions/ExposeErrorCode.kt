package info.jotajoti.jampuz.exceptions

import kotlin.annotation.AnnotationRetention.*
import kotlin.annotation.AnnotationTarget.*

@Target(ANNOTATION_CLASS)
@Retention(RUNTIME)
annotation class ExposeErrorCode(
    val value: ErrorCode,
)
