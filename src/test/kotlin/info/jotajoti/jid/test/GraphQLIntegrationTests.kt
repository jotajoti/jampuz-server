package info.jotajoti.jid.test

import info.jotajoti.jid.admin.Admin
import info.jotajoti.jid.admin.AdminId
import info.jotajoti.jid.admin.AdminRepository
import info.jotajoti.jid.dev.SampleProperties
import info.jotajoti.jid.jidcode.JidCode
import info.jotajoti.jid.location.Location
import info.jotajoti.jid.location.LocationRepository
import info.jotajoti.jid.participant.Participant
import info.jotajoti.jid.participant.ParticipantId
import info.jotajoti.jid.participant.ParticipantRepository
import info.jotajoti.jid.random
import info.jotajoti.jid.security.JwtService
import info.jotajoti.jid.security.PinCode
import info.jotajoti.jid.security.SecurityService
import info.jotajoti.jid.security.Subject
import info.jotajoti.jid.security.SubjectType.ADMIN
import info.jotajoti.jid.security.SubjectType.PARTICIPANT
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureHttpGraphQlTester
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.graphql.test.tester.GraphQlTester
import org.springframework.graphql.test.tester.WebGraphQlTester
import org.springframework.test.context.jdbc.Sql
import org.testcontainers.containers.MySQLContainer
import java.time.LocalDate

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
    lateinit var participantRepository: ParticipantRepository

    @Autowired
    lateinit var securityService: SecurityService

    @Autowired
    lateinit var jwtService: JwtService

    val testAdmins = mutableListOf<Admin>()
    lateinit var testLocation: Location
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
                code = JidCode.random(),
                year = LocalDate.now().year,
                owners = listOf(testAdmins[0], testAdmins[1]),
            )
        )

        repeat(5) {
            testParticipants += participantRepository.save(
                Participant(
                    name = "Participant $it",
                    pinCode = PinCode.random(),
                    location = testLocation,
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