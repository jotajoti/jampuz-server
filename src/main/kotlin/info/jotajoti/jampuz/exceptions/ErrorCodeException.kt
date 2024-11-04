package info.jotajoti.jampuz.exceptions

import org.springframework.graphql.execution.*

abstract class ErrorCodeException(message: String, val errorCode: ErrorCode, val errorType: ErrorType) :
    Exception(message)
