package tickets.infra.outbound

import io.mockk.spyk
import io.mockk.verify
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.slf4j.Logger
import org.slf4j.helpers.NOPLogger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Bean
import shared.TicketCreated
import java.util.UUID

@Tag("integration")
@SpringBootTest(classes = [TicketEventLoggerAdapter::class, TestConfig::class])
class TicketEventLoggerAdapterTest {

    @Autowired
    private lateinit var eventPublisher: ApplicationEventPublisher

    @Autowired
    private lateinit var logger: Logger

    @Test
    fun `should write a log when a new ticket is created`() {
        val ticketCreated = TicketCreated(UUID.randomUUID(), "title", "description", UUID.randomUUID(), "")

        eventPublisher.publishEvent(ticketCreated)

        verify { logger.info("event:'TicketCreated', ticket-id:'${ticketCreated.id}'") }
    }
}

class TestConfig {
    @Bean
    fun logger(): Logger = spyk(NOPLogger.NOP_LOGGER)
}
