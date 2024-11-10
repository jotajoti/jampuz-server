package info.jotajoti.jampuz.dev

import com.github.javafaker.*
import info.jotajoti.jampuz.*
import info.jotajoti.jampuz.admin.*
import info.jotajoti.jampuz.event.*
import info.jotajoti.jampuz.jidcode.*
import info.jotajoti.jampuz.location.*
import info.jotajoti.jampuz.participant.*
import info.jotajoti.jampuz.security.*
import info.jotajoti.jampuz.security.SubjectType.*
import info.jotajoti.jampuz.util.*
import org.springframework.http.*
import org.springframework.http.ResponseEntity.*
import org.springframework.jdbc.core.*
import org.springframework.web.bind.annotation.*
import java.time.*
import java.util.*
import kotlin.collections.toList
import kotlin.random.Random

@RestController
class SampleDataController(
    private val sampleProperties: SampleProperties,
    private val adminRepository: AdminRepository,
    private val locationRepository: LocationRepository,
    private val eventRepository: EventRepository,
    private val participantRepository: ParticipantRepository,
    private val foundJidCodeRepository: FoundJidCodeRepository,
    private val jwtService: JwtService,
    private val jdbcTemplate: JdbcTemplate,
    private val securityService: SecurityService,
) {

    @GetMapping("/sampledata")
    fun createSampleData(
        @RequestParam deleteExisting: Boolean = false,
        @RequestParam locale: Locale?
    ): ResponseEntity<*> {
        if (deleteExisting) {
            jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS=0")
            jdbcTemplate.execute("TRUNCATE TABLE found_jid_code")
            jdbcTemplate.execute("TRUNCATE TABLE participant")
            jdbcTemplate.execute("TRUNCATE TABLE event")
            jdbcTemplate.execute("TRUNCATE TABLE location_owner")
            jdbcTemplate.execute("TRUNCATE TABLE location")
            jdbcTemplate.execute("TRUNCATE TABLE admin")
            jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS=1")
        }

        val faker = Faker.instance(locale ?: Locale.getDefault())

        val staticAdmin = createStaticAdmin()
        val admins = mutableListOf(staticAdmin) + createSampleAdmins(faker)
        val locations = createSampleLocations(faker, admins)
        val events = createSampleEvents(locations)
        val participants = createSampleParticipants(faker, events)
        val jidCodes = createSampleJidCodes()
        createSampleFoundJidCodes(participants, jidCodes)

        return ok().body(
            mapOf(
                "eventCodes" to events.map {
                    it.code.code
                }.toSet(),
                "firstAdmin" to mapOf(
                    "email" to admins.first().email,
                    "password" to "john.doe123",
                    "token" to jwtService.createToken(Subject(ADMIN, admins.first().id!!))
                ),
            )
        )
    }

    private fun createStaticAdmin() =
        adminRepository.save(
            Admin(
                id = null,
                name = "John Doe",
                email = "john.doe@example.com",
                passwordHash = securityService.hashPassword("john.doe123"),
                locations = emptyList(),
            )
        )

    private fun createSampleAdmins(faker: Faker) = createSamples(sampleProperties.users) {
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
        adminRepository.save(admin).toList()
    }

    private fun createUsername(firstName: String, lastName: String) =
        "${firstName.usernamify()}.${lastName.usernamify()}"

    private fun String.usernamify() =
        replace("[' ]".toRegex(), "")
            .lowercase(Locale.getDefault())
            .replace("æ", "ae")
            .replace("ø", "oe")
            .replace("å", "aa")

    private fun createSampleLocations(faker: Faker, admins: List<Admin>): List<Location> {
        val locations = createSamples(sampleProperties.locations) {
            val owners = admins.random(2).toMutableSet()
            owners += admins[0]

            val locationName = faker.address().cityName()

            Location(
                id = null,
                name = locationName,
                owners = owners.toList(),
            ).toList()
        }
        return locationRepository.saveAll(locations)
    }

    private fun createSampleEvents(locations: List<Location>): List<Event> {
        val events = locations.flatMap { location ->

            createSamples(sampleProperties.yearsPerLocation) { yearOffset ->
                Event(
                    id = null,
                    location = location,
                    code = JidCode.random(),
                    year = LocalDate.now().minusYears(yearOffset.toLong()).year,
                    active = yearOffset == 0, // Only most recent is active
                    participants = emptyList(),
                ).toList()
            }
        }
        return eventRepository.saveAll(events)
    }

    private fun createSampleParticipants(faker: Faker, events: List<Event>) = events.flatMap { event ->
        val participants = createUnique(sampleProperties.randomNumberOfParticipants()) {
            "${faker.name().firstName()} ${faker.name().lastName()}"
        }.map { name ->
            Participant(
                name = name,
                pinCode = PinCode.random(),
                event = event,
                foundJidCodes = emptyList(),
            )
        }
        participantRepository.saveAll(participants)
    }

    private fun createSampleJidCodes() = createSamples(sampleProperties.jidCodes) { JidCode.random().toList() }

    private fun createSampleFoundJidCodes(participants: List<Participant>, jidCodes: List<JidCode>) =
        participants.flatMap { participant ->
            val jidCodesForParticipant =
                jidCodes.random(Random.nextInt(sampleProperties.randomNumberOfFoundJidCodes()))
            java.util.Random().nextGaussian()
            val foundJidCodes = jidCodesForParticipant
                .map {
                    FoundJidCode(
                        participant = participant,
                        code = it,
                    )
                }
            foundJidCodeRepository.saveAll(foundJidCodes)
        }

    private fun <T> createSamples(count: Int, action: (Int) -> List<T>): List<T> {
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
