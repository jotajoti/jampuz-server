package info.jotajoti.jampuz.security

import info.jotajoti.jampuz.admin.*

fun adminAuthentication(
    admin: Admin = admin()
) = AdminAuthentication(admin)