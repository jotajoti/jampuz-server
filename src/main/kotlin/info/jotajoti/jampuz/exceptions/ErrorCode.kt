package info.jotajoti.jampuz.exceptions

enum class ErrorCode(val code: Int) {
    PARTICIPANT_NAME_NOT_AVAILABLE(1),
    CODE_ALREADY_REGISTERED_FOR_PARTICIPANT(2),
    CANNOT_ADD_SELF_TO_LOCATION(3),
    CANNOT_REMOVE_SELF_FROM_LOCATION(4),
    ADMIN_NOT_IN_LOCATION(5),
    LOCATION_NOT_FOUND(6),
}