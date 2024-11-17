package info.jotajoti.jampuz.location

import info.jotajoti.jampuz.admin.*

fun location(
    id: LocationId = LocationId.randomUUID(),
    name: String = "Location $id",
    owners: List<Admin> = emptyList(),
) = Location(
    id = id,
    name = name,
    owners = owners,
)
