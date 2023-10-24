package info.jotajoti.jid.exceptions

import graphql.GraphQLError
import graphql.execution.ResultPath
import jakarta.validation.ConstraintViolationException
import org.springframework.graphql.data.method.annotation.GraphQlExceptionHandler
import org.springframework.graphql.execution.ErrorType
import org.springframework.web.bind.annotation.ControllerAdvice

@ControllerAdvice
class ExceptionHandlers {

    @GraphQlExceptionHandler
    fun handle(constraintViolationException: ConstraintViolationException): GraphQLError =
        GraphQLError
            .newError()
            .errorType(ErrorType.BAD_REQUEST)
            .path(ResultPath.parse(constraintViolationException.message?.toPath()))
            .message(constraintViolationException.message?.substringAfter(": ")?.substringBefore(", "))
            .build()

    private fun String.toPath() =
        "/${substringBefore(":").replace(".", "/")}"

}
