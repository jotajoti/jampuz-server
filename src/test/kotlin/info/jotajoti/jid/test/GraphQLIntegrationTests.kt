package info.jotajoti.jid.test

import info.jotajoti.jid.admin.Admin
import info.jotajoti.jid.admin.AdminRepository
import info.jotajoti.jid.dev.SampleProperties
import info.jotajoti.jid.participant.ParticipantId
import info.jotajoti.jid.security.JwtService
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
    lateinit var securityService: SecurityService

    @Autowired
    lateinit var jwtService: JwtService

    val testAdmins = mutableListOf<Admin>()

    @BeforeEach
    @Sql("/db/reset.sql")
    fun createTestData() {
        testAdmins.clear()

        repeat(5) {
            testAdmins += adminRepository.save(
                Admin(
                    name = "Admin $it",
                    email = "admin$it@example.com",
                    passwordHash = securityService.hashPassword("admin$it")
                )
            )
        }
    }

    fun executeQuery(@Language("GraphQL") graphQlString: String) =
        graphQlTester.executeQuery(graphQlString)

    fun executeAdminQuery(@Language("GraphQL") graphQlString: String) =
        graphQlTester
            .mutate()
            .header("Authorization", "Bearer ${jwtService.createToken(Subject(ADMIN, testAdmins[0].id!!))}")
            .build()
            .executeQuery(graphQlString)

    fun executeParticipantQuery(@Language("GraphQL") graphQlString: String, participantId: ParticipantId) =
        graphQlTester
            .mutate()
            .header("Authorization", "Bearer ${jwtService.createToken(Subject(PARTICIPANT, participantId))}")
            .build()
            .executeQuery(graphQlString)

    private fun GraphQlTester.executeQuery(@Language("GraphQL") graphQlString: String) =
        document(graphQlString)
            .execute()
}