package info.jotajoti.jid.dev

import com.github.javafaker.Faker
import info.jotajoti.jid.admin.Admin
import info.jotajoti.jid.admin.AdminRepository
import info.jotajoti.jid.jidcode.FoundJidCode
import info.jotajoti.jid.jidcode.FoundJidCodeRepository
import info.jotajoti.jid.jidcode.JidCode
import info.jotajoti.jid.location.Location
import info.jotajoti.jid.location.LocationRepository
import info.jotajoti.jid.participant.Participant
import info.jotajoti.jid.participant.ParticipantRepository
import info.jotajoti.jid.random
import info.jotajoti.jid.security.JwtService
import info.jotajoti.jid.security.PinCode
import info.jotajoti.jid.security.SecurityService
import info.jotajoti.jid.security.Subject
import info.jotajoti.jid.security.SubjectType.ADMIN
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.ok
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate
import java.util.*
import kotlin.random.Random

@RestController
class SampleDataController(
    private val sampleProperties: SampleProperties,
    private val adminRepository: AdminRepository,
    private val locationRepository: LocationRepository,
    private val participantRepository: ParticipantRepository,
    private val foundJidCodeRepository: FoundJidCodeRepository,
    private val jwtService: JwtService,
    private val jdbcTemplate: JdbcTemplate,
    private val securityService: SecurityService,
) {

    @PostMapping("/sampledata")
    fun createSampleData(
        @RequestParam deleteExisting: Boolean = false,
        @RequestParam locale: Locale?
    ): ResponseEntity<*> {
        if (deleteExisting) {
            jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS=0")
            jdbcTemplate.execute("TRUNCATE TABLE found_jid_code")
            jdbcTemplate.execute("TRUNCATE TABLE participant")
            jdbcTemplate.execute("TRUNCATE TABLE location_owner")
            jdbcTemplate.execute("TRUNCATE TABLE location")
            jdbcTemplate.execute("TRUNCATE TABLE admin")
            jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS=1")
        }

        val faker = Faker.instance(locale ?: Locale.getDefault())

        val users = createSampleUsers(faker)
        val locations = createSampleLocations(faker, users)
        val participants = createSampleParticipants(faker, locations)
        val jidCodes = createSampleJidCodes()
        createSampleFoundJidCodes(participants, jidCodes)

        return ok().body(
            mapOf(
                "authKeys" to users.map {
                    mapOf(it.id to jwtService.createToken(Subject(ADMIN, it.id!!)))
                },
                "locationOwners" to locations.map { location ->
                    mapOf(location.id to location.owners.map { user -> user.id })
                },
                "sampleAuthKey" to locations
                    .first()
                    .let { location ->
                        location
                            .owners
                            .first()
                            .let { owner ->
                                jwtService.createToken(Subject(ADMIN, owner.id!!))
                            }
                    }
            )
        )
    }

    private fun createSampleUsers(faker: Faker) = createSamples(sampleProperties.users) {
        val name = faker.name()
        val firstName = name.firstName()
        val lastName = name.lastName()
        val username = createUsername(firstName, lastName)
        val admin = Admin(
            id = null,
            name = "$firstName $lastName",
            email = "${username}@example.${faker.internet().domainSuffix()}",
            passwordHash = securityService.hashPassword("${username}123"),
            locations = emptyList(),
        )
        adminRepository.save(admin)
    }

    private fun createUsername(firstName: String, lastName: String) =
        "${firstName.usernamify()}.${lastName.usernamify()}"

    private fun String.usernamify() =
        replace("[' ]".toRegex(), "").lowercase(Locale.getDefault())

    private fun createSampleLocations(faker: Faker, admins: List<Admin>): List<Location> {
        val locations = createSamples(sampleProperties.locations) {
            val owners = admins.random(3)

            Location(
                id = null,
                name = faker.address().cityName(),
                code = JidCode.random(),
                year = LocalDate.now().year,
                owners = owners,
                participants = emptyList(),
            )
        }
        return locationRepository.saveAll(locations)
    }

    private fun createSampleParticipants(faker: Faker, locations: List<Location>) = locations.flatMap { location ->
        val participants = createUnique(sampleProperties.participantsPerLocation) {
            "${faker.name().firstName()} ${faker.name().lastName()}"
        }.map { name ->
            Participant(
                name = name,
                pinCode = PinCode.random(),
                location = location,
                foundJidCodes = emptyList(),
            )
        }
        participantRepository.saveAll(participants)
    }

    private fun createSampleJidCodes() = createSamples(sampleProperties.jidCodes) { JidCode.random() }

    private fun createSampleFoundJidCodes(participants: List<Participant>, jidCodes: List<JidCode>) =
        participants.flatMap { participant ->
            val jidCodesForParticipant =
                jidCodes.random(Random.nextInt(sampleProperties.maxFoundJidCodesPerParticipant))
            val foundJidCodes = jidCodesForParticipant
                .map {
                    FoundJidCode(
                        participant = participant,
                        code = it,
                    )
                }
            foundJidCodeRepository.saveAll(foundJidCodes)
        }

    private fun <T> createSamples(count: Int, action: (Int) -> T): List<T> {
        val samples = mutableListOf<T>()
        repeat(count) {
            samples += action(it)
        }
        return samples
    }

    private fun <T> createUnique(count: Int, action: () -> T): List<T> {
        val result = mutableSetOf<T>()
        while (result.size < count) {
            result += action()
        }
        return result.toList()
    }

    private fun <T> List<T>.random(count: Int): List<T> {
        val sanitizedCount = minOf(count, size)
        val foundItems = mutableSetOf<T>()
        while (foundItems.size < sanitizedCount) {
            foundItems.add(random())
        }
        return foundItems.toList()
    }

}
