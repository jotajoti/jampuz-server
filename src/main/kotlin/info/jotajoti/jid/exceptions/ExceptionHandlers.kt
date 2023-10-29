package info.jotajoti.jid.exceptions

import graphql.GraphQLError
import graphql.execution.ResultPath.parse
import info.jotajoti.jid.jidcode.CodeAlreadyRegisteredForParticipantException
import info.jotajoti.jid.location.CannotAddSelfToLocation
import info.jotajoti.jid.location.CannotRemoveSelfFromLocation
import info.jotajoti.jid.location.LocationNotFoundException
import info.jotajoti.jid.participant.AdminNotInLocationException
import jakarta.validation.ConstraintViolationException
import org.springframework.graphql.data.method.annotation.GraphQlExceptionHandler
import org.springframework.graphql.execution.ErrorType.*
import org.springframework.web.bind.annotation.ControllerAdvice

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
