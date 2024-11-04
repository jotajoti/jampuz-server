package info.jotajoti.jampuz.exceptions

import graphql.*
import graphql.execution.ResultPath.*
import jakarta.validation.*
import org.springframework.core.annotation.AnnotationUtils.*
import org.springframework.graphql.data.method.annotation.*
import org.springframework.graphql.execution.ErrorType.*
import org.springframework.web.bind.annotation.*

@ControllerAdvice
class ExceptionHandlers {

    @GraphQlExceptionHandler(ErrorCodeException::class)
    fun handleErrorCodeException(exception: ErrorCodeException): GraphQLError =
        GraphQLError
            .newError()
            .errorType(exception.errorType)
            .message(exception.message)
            .addErrorCodes(exception.errorCode)
            .build()

    @GraphQlExceptionHandler(ConstraintViolationException::class)
    fun handle(constraintViolationException: ConstraintViolationException): GraphQLError =
        GraphQLError
            .newError()
            .errorType(BAD_REQUEST)
            .path(parse(constraintViolationException.message?.toPath()))
            .message(constraintViolationException.message?.substringAfter(": ")?.substringBefore(", "))
            .addErrorCode(constraintViolationException)
            .build()

    private fun GraphQLError.Builder<*>.addErrorCode(constraintViolationException: ConstraintViolationException): GraphQLError.Builder<*> {
        val errorCodes = constraintViolationException
            .constraintViolations
            .filter {
                findAnnotation(
                    it.constraintDescriptor.annotation.annotationClass.java,
                    ExposeErrorCode::class.java
                ) != null
            }
            .map {
                findAnnotation(
                    it.constraintDescriptor.annotation.annotationClass.java,
                    ExposeErrorCode::class.java
                )!!.value
            }

        addErrorCodes(*errorCodes.toTypedArray())

        return this
    }

    private fun GraphQLError.Builder<*>.addErrorCodes(vararg errorCodes: ErrorCode) =
        apply {
            if (errorCodes.isNotEmpty()) {
                extensions(
                    mapOf(
                        "firstErrorCode" to errorCodes[0].code,
                        "errorCodes" to errorCodes.map { it.code },
                    )
                )
            }
        }

    private fun String.toPath() =
        "/${substringBefore(":").replace(".", "/")}"

}
