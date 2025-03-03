package agentcenter.infra.outbound

import agentcenter.fixtures.TestBuilders
import io.mockk.spyk
import io.mockk.verify
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.slf4j.Logger
import org.slf4j.helpers.NOPLogger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Bean
import shared.AgentConnected
import shared.AgentCreated
import shared.AgentDisconnected
import java.util.UUID

@Tag("integration")
@SpringBootTest(classes = [AgentEventLoggerAdapter::class, TestConfig::class])
class AgentEventLoggerAdapterTest {

    @Autowired
    private lateinit var eventPublisher: ApplicationEventPublisher

    @Autowired
    private lateinit var logger: Logger

    @Test
    fun `should write a log when an agent connects`() {
        val event = AgentConnected(UUID.randomUUID())

        eventPublisher.publishEvent(event)

        verify { logger.info("event:'AgentConnected', agent-id:'${event.id}'") }
    }

    @Test
    fun `should write a log when an agent disconnects`() {
        val event = AgentDisconnected(UUID.randomUUID())

        eventPublisher.publishEvent(event)

        verify { logger.info("event:'AgentDisconnected', agent-id:'${event.id}'") }
    }

    @Test
    fun `should write a log when agent is created`() {
        val agent = TestBuilders.buildAgent()
        val event = AgentCreated(agent.id, agent.email, agent.fullName, agent.skills, agent.languages)

        eventPublisher.publishEvent(event)

        verify { logger.info("event:'AgentCreated', agent-id:'${event.id}'") }
    }
}

class TestConfig {
    @Bean
    fun logger(): Logger = spyk(NOPLogger.NOP_LOGGER)
}