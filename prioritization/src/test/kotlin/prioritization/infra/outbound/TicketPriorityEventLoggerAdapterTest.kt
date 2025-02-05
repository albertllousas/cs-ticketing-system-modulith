package prioritization.infra.outbound

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
import shared.TicketPrioritized
import java.util.UUID

@Tag("integration")
@SpringBootTest(classes = [TicketPriorityEventLoggerAdapter::class, TestConfig::class])
class TicketPriorityEventLoggerAdapterTest {

    @Autowired
    private lateinit var eventPublisher: ApplicationEventPublisher

    @Autowired
    private lateinit var logger: Logger

    @Test
    fun `should write a log when a new ticket is prioritized`() {
        val event = TicketPrioritized(UUID.randomUUID(), UUID.randomUUID(), "HIGH")

        eventPublisher.publishEvent(event)

        verify { logger.info("event:'TicketPrioritized', ticket-id:'${event.id}', priority:'${event.priority}'") }
    }
}

class TestConfig {
    @Bean
    fun logger(): Logger = spyk(NOPLogger.NOP_LOGGER)
}
