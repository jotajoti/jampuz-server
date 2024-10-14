package info.jotajoti.jampuz.test

import info.jotajoti.jampuz.*
import info.jotajoti.jampuz.admin.*
import info.jotajoti.jampuz.dev.*
import info.jotajoti.jampuz.event.*
import info.jotajoti.jampuz.jidcode.*
import info.jotajoti.jampuz.location.*
import info.jotajoti.jampuz.participant.*
import info.jotajoti.jampuz.security.*
import info.jotajoti.jampuz.security.SubjectType.*
import org.intellij.lang.annotations.*
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.*
import org.springframework.boot.context.properties.*
import org.springframework.boot.test.autoconfigure.graphql.tester.*
import org.springframework.boot.test.context.*
import org.springframework.boot.testcontainers.service.connection.*
import org.springframework.graphql.test.tester.*
import org.springframework.test.context.jdbc.*
import org.testcontainers.containers.*
import java.time.*

@AutoConfigureHttpGraphQlTester
@SpringBootTest
@EnableConfigurationProperties(
    SampleProperties::class,
)
abstract class GraphQLIntegrationTests {

    companion object {
        @ServiceConnection
        val mySQLContainer =
            MySQLContainer("mysql:8")

        init {
            mySQLContainer.start()
        }
    }

    @Autowired
    lateinit var graphQlTester: WebGraphQlTester

    @Autowired
    lateinit var adminRepository: AdminRepository

    @Autowired
    lateinit var locationRepository: LocationRepository

    @Autowired
    lateinit var eventRepository: EventRepository

    @Autowired
    lateinit var participantRepository: ParticipantRepository

    @Autowired
    lateinit var securityService: SecurityService

    @Autowired
    lateinit var jwtService: JwtService

    val testAdmins = mutableListOf<Admin>()
    lateinit var testLocation: Location
    lateinit var testEvent: Event
    val testParticipants = mutableListOf<Participant>()

    @BeforeEach
    @Sql("/db/reset.sql")
    fun createTestData() {
        testAdmins.clear()

        repeat(5) {
            testAdmins += adminRepository.save(
                Admin(
                    name = "Admin $it",
                    email = "admin$it@example.com",
                    passwordHash = securityService.hashPassword("admin$it"),
                )
            )
        }

        testLocation = locationRepository.save(
            Location(
                name = "Location 1",
                owners = listOf(testAdmins[0], testAdmins[1]),
            )
        )

        testEvent = eventRepository.save(
            Event(
                location = testLocation,
                code = JidCode.random(),
                year = LocalDate.now().year,
            )
        )

        repeat(5) {
            testParticipants += participantRepository.save(
                Participant(
                    name = "Participant $it",
                    pinCode = PinCode.random(),
                    event = testEvent,
                )
            )
        }
    }

    fun executeAnonymousQuery(
        @Language("GraphQL") graphQlString: String,
        vararg variables: Pair<String, Any>
    ) =
        graphQlTester
            .document(graphQlString)
            .addVariables(*variables)
            .execute()

    fun executeAnonymousQueryName(
        graphQlDocument: String,
        vararg variables: Pair<String, Any>
    ) =
        graphQlTester
            .documentName(graphQlDocument)
            .addVariables(*variables)
            .execute()

    fun executeAdminQuery(
        @Language("GraphQL") graphQlString: String,
        adminId: AdminId = testAdmins[0].id!!,
        vararg variables: Pair<String, Any>
    ) =
        prepareAdminTester(adminId)
            .document(graphQlString)
            .addVariables(*variables)
            .execute()

    fun executeAdminQueryName(
        graphQlDocument: String,
        adminId: AdminId = testAdmins[0].id!!,
        vararg variables: Pair<String, Any>
    ) =
        prepareAdminTester(adminId)
            .documentName(graphQlDocument)
            .addVariables(*variables)
            .execute()

    fun executeParticipantQuery(
        @Language("GraphQL") graphQlString: String,
        participantId: ParticipantId = testParticipants[0].id!!,
        vararg variables: Pair<String, Any>
    ) =
        prepareParticipantTester(participantId)
            .document(graphQlString)
            .addVariables(*variables)
            .execute()

    fun executeParticipantQueryName(
        graphQlDocument: String,
        participantId: ParticipantId = testParticipants[0].id!!,
        vararg variables: Pair<String, Any>
    ) =
        prepareParticipantTester(participantId)
            .documentName(graphQlDocument)
            .addVariables(*variables)
            .execute()

    private fun prepareAdminTester(adminId: AdminId) =
        graphQlTester
            .mutate()
            .header("Authorization", "Bearer ${jwtService.createToken(Subject(ADMIN, adminId))}")
            .build()

    private fun prepareParticipantTester(participantId: ParticipantId) =
        graphQlTester
            .mutate()
            .header("Authorization", "Bearer ${jwtService.createToken(Subject(PARTICIPANT, participantId))}")
            .build()

    private fun <T : GraphQlTester.Request<T>?> GraphQlTester.Request<T>.addVariables(vararg variables: Pair<String, Any>) =
        apply {
            variables.forEach { (name, value) ->
                variable(name, value)
            }
        }
}