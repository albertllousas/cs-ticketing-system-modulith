package prioritization.infra.inbound

import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Bean
import prioritization.app.PrioritiseTicketService
import prioritization.domain.PrioritizationStrategy
import shared.TicketCreated
import java.util.UUID


@Tag("integration")
@SpringBootTest(classes = [InMemoryEventsConsumerAdapter::class, TestConfig::class])
class InMemoryEventsConsumerAdapterTest {

    @Autowired
    private lateinit var eventPublisher: ApplicationEventPublisher

    @Autowired
    private lateinit var prioritiseTicket: PrioritiseTicketService

    @Test
    fun `should prioritise a ticket when a new one is created`() {
        val ticketCreated = TicketCreated(UUID.randomUUID(), "title", "description", UUID.randomUUID(), "")

        eventPublisher.publishEvent(ticketCreated)

        verify { prioritiseTicket(ticketCreated.id, PrioritizationStrategy.RULE_BASED) }
    }
}

class TestConfig {
    @Bean
    fun prioritiseTicketService() = mockk<PrioritiseTicketService>(relaxed = true)

    @Bean
    fun prioritizationStrategy() = PrioritizationStrategy.RULE_BASED
}
