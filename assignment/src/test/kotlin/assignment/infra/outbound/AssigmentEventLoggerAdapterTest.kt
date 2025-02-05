package assignment.infra.outbound

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
import shared.AssignmentAttemptFailed
import shared.TicketAssigned
import shared.TicketQueued
import java.util.UUID

@Tag("integration")
@SpringBootTest(classes = [AssigmentEventLoggerAdapter::class, TestConfig::class])
class AssigmentEventLoggerAdapterTest {

    @Autowired
    private lateinit var eventPublisher: ApplicationEventPublisher

    @Autowired
    private lateinit var logger: Logger

    @Test
    fun `should write a log when a assignment attempt failed`() {
        val event = AssignmentAttemptFailed(UUID.randomUUID(), AssignmentAttemptFailed.Reason.NO_AGENTS_AVAILABLE)

        eventPublisher.publishEvent(event)

        verify { logger.warn("event:'AssignmentAttemptFailed', ticket-id:'${event.ticketId}', reason:'${event.reason}'") }
    }

    @Test
    fun `should write a log when a ticket is assigned`() {
        val event = TicketAssigned(UUID.randomUUID(), UUID.randomUUID())

        eventPublisher.publishEvent(event)

        verify { logger.info("event:'TicketAssigned', ticket-id:'${event.ticketId}'") }
    }

    @Test
    fun `should write a log when a ticket is queued`() {
        val event = TicketQueued(UUID.randomUUID())

        eventPublisher.publishEvent(event)

        verify { logger.info("event:'TicketQueued', ticket-id:'${event.ticketId}'") }
    }

}

class TestConfig {
    @Bean
    fun logger(): Logger = spyk(NOPLogger.NOP_LOGGER)
}