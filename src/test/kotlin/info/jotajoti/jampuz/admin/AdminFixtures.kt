package info.jotajoti.jampuz.admin

fun admin(
    id: AdminId = AdminId.randomUUID(),
    name: String = "Admin $id",
    email: String = "${name.slugify()}@example.com",
    password: String = name.slugify(),
) = Admin(
    id = id,
    name = name,
    email = email,
    passwordHash = "hash-$password",
)

private fun String.slugify() = replace(" ", "-").lowercase()
