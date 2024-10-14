package info.jotajoti.jampuz.exceptions

import graphql.*
import graphql.execution.ResultPath.*
import info.jotajoti.jampuz.jidcode.*
import info.jotajoti.jampuz.location.*
import jakarta.validation.*
import org.springframework.graphql.data.method.annotation.*
import org.springframework.graphql.execution.ErrorType.*
import org.springframework.web.bind.annotation.*

@ControllerAdvice
class ExceptionHandlers {

    @GraphQlExceptionHandler(CodeAlreadyRegisteredForParticipantException::class)
    fun handleBadRequest(exception: Exception): GraphQLError =
        GraphQLError
            .newError()
            .errorType(BAD_REQUEST)
            .message(exception.message)
            .build()

    @GraphQlExceptionHandler(LocationNotFoundException::class)
    fun handleNotFound(exception: Exception): GraphQLError =
        GraphQLError
            .newError()
            .errorType(NOT_FOUND)
            .message(exception.message)
            .build()

    @GraphQlExceptionHandler(
        CannotAddSelfToLocation::class,
        CannotRemoveSelfFromLocation::class,
        AdminNotInLocationException::class
    )
    fun handleForbidden(exception: Exception): GraphQLError =
        GraphQLError
            .newError()
            .errorType(FORBIDDEN)
            .message(exception.message)
            .build()

    @GraphQlExceptionHandler(ConstraintViolationException::class)
    fun handle(constraintViolationException: ConstraintViolationException): GraphQLError =
        GraphQLError
            .newError()
            .errorType(BAD_REQUEST)
            .path(parse(constraintViolationException.message?.toPath()))
            .message(constraintViolationException.message?.substringAfter(": ")?.substringBefore(", "))
            .build()

    private fun String.toPath() =
        "/${substringBefore(":").replace(".", "/")}"

}
