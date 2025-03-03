package assignment.acceptance

import assignment.acceptance.BaseAcceptanceTest.Initializer
import assignment.domain.AIBasedTicketAssigner
import assignment.fixtures.TestModuleApp
import assignment.fixtures.ai.FakeChatClient
import assignment.infra.outbound.SpringAITicketAssignerAdapter
import fixtures.TestEventListener
import fixtures.containers.Postgres
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.restassured.RestAssured
import io.restassured.parsing.Parser.JSON
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.core.io.ClassPathResource
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_CLASS
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import shared.AgentDto
import shared.CustomerDto
import shared.ExposedInboundPorts
import shared.TicketDto
import shared.TicketPriorityDto
import java.util.Locale
import java.util.UUID

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = RANDOM_PORT)
@ContextConfiguration(
    initializers = [Initializer::class],
    classes = [TestModuleApp::class, OtherModuleExposedDependencies::class, AIDependencies::class, TestEventListenerConfig::class]
)
@DirtiesContext(classMode = AFTER_CLASS)
abstract class BaseAcceptanceTest {

    init {
        RestAssured.defaultParser = JSON
    }

    @Autowired
    protected lateinit var testEventListener: TestEventListener

    @Autowired
    protected lateinit var eventPublisher: shared.EventPublisher

    @Autowired
    protected lateinit var fakeChatClient: FakeChatClient

    @Autowired
    protected lateinit var findTicket: ExposedInboundPorts.FindTicket

    @Autowired
    protected lateinit var findTicketPriority: ExposedInboundPorts.FindTicketPriority

    @Autowired
    protected lateinit var findCustomer: ExposedInboundPorts.FindCustomer

    @Autowired
    protected lateinit var findAvailableAgents: ExposedInboundPorts.FindAvailableAgents

    @LocalServerPort
    protected val servicePort: Int = 0

    companion object {
        val postgres: Postgres = Postgres(
            flywayLocationSchemasList = listOf(Pair("db/migration/assignment", "assignment")),
        )
    }

    private val defaultTicketDto = TicketDto(
        id = UUID.randomUUID(),
        title = "title",
        description = "description",
        status = TicketDto.Status.OPEN,
        type = TicketDto.Type.ISSUE,
        customerId = UUID.randomUUID(),
    )

    private val defaultCustomerDto = CustomerDto(
        id = defaultTicketDto.customerId,
        fullName = "John Doe",
        email = "john.doe@gmail.com",
        preferredLang = Locale.ENGLISH,
        CustomerDto.Tier.PREMIUM
    )

    private val defaultTicketPriority = TicketPriorityDto(
        id = UUID.randomUUID(),
        ticketId = defaultTicketDto.id,
        priority = TicketPriorityDto.Priority.HIGH
    )

    private val defaultAgentDto = AgentDto(
        id = UUID.randomUUID(),
        email = "jane.doe@comany.com",
        fullName = "Jane Doe",
        skills = listOf("skill1", "skill2"),
        languages = listOf(Locale.ENGLISH),
        status = AgentDto.Status.ONLINE
    )

    @BeforeEach
    fun setUp() {
        postgres.container.start()
        testEventListener.clear()
        every { findTicket(any()) } returns defaultTicketDto
        every { findCustomer(any()) } returns defaultCustomerDto
        every { findTicketPriority(any()) } returns defaultTicketPriority
        every { findAvailableAgents() } returns listOf(defaultAgentDto)
    }

    @AfterEach
    fun tearDown() {
        postgres.container.stop()
        clearAllMocks()
    }

    class Initializer : ApplicationContextInitializer<ConfigurableApplicationContext> {

        override fun initialize(configurableApplicationContext: ConfigurableApplicationContext) {
            TestPropertyValues.of(
                "spring.datasource.url=" + postgres.container.jdbcUrl,
                "spring.datasource.password=" + postgres.container.password,
                "spring.datasource.username=" + postgres.container.username,
                "spring.flyway.url=" + postgres.container.jdbcUrl,
                "spring.flyway.password=" + postgres.container.password,
                "spring.flyway.user=" + postgres.container.username,
            ).applyTo(configurableApplicationContext.environment)
        }
    }
}

@TestConfiguration
class AIDependencies {

    @Bean
    fun fakeChatClient() = FakeChatClient("")

    @Bean
    fun aiBasedTicketAssigner(client: FakeChatClient): AIBasedTicketAssigner = SpringAITicketAssignerAdapter(
        ticketAssignerChatClient = client,
        promptResource = ClassPathResource("assignment_prompt.txt")
    )
}

@TestConfiguration
class OtherModuleExposedDependencies {

    @Bean
    fun findTicket() = mockk<ExposedInboundPorts.FindTicket>()

    @Bean
    fun findAssignments() = mockk<ExposedInboundPorts.FindAssignments>()

    @Bean
    fun findAvailableAgents() = mockk<ExposedInboundPorts.FindAvailableAgents>()

    @Bean
    fun findTicketPriority() = mockk<ExposedInboundPorts.FindTicketPriority>()

    @Bean
    fun findCustomer() = mockk<ExposedInboundPorts.FindCustomer>()
}

@TestConfiguration
class TestEventListenerConfig {
    @Bean
    fun testEventListener() = TestEventListener()
}
