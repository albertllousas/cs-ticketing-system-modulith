package assignment.infra.inbound

import assignment.app.AssignTicketService
import assignment.app.QueueTicketService
import assignment.domain.AssignmentStrategy
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Bean
import shared.AssignmentAttemptFailed
import shared.AssignmentAttemptFailed.Reason.NO_SUITABLE_AGENT_AVAILABLE
import shared.TicketCreated
import shared.TicketPrioritized
import java.util.UUID

@Tag("integration")
@SpringBootTest(classes = [InMemoryEventsConsumerAdapter::class, TestConfig::class])
class InMemoryEventsConsumerAdapterTest {

    @Autowired
    private lateinit var eventPublisher: ApplicationEventPublisher

    @Autowired
    private lateinit var assignTicket: AssignTicketService

    @Autowired
    private lateinit var queueTicket: QueueTicketService

    @Test
    fun `should assign a ticket when a new one is prioritized`() {
        val ticketPrioritized = TicketPrioritized(UUID.randomUUID(), UUID.randomUUID(), "HIGH")

        eventPublisher.publishEvent(ticketPrioritized)

        verify { assignTicket(ticketPrioritized.ticketId, AssignmentStrategy.RULE_BASED) }
    }

    @Test
    fun `should queue a ticket when assignation attempt fails`() {
        val assignmentAttemptFailed = AssignmentAttemptFailed(UUID.randomUUID(), NO_SUITABLE_AGENT_AVAILABLE)

        eventPublisher.publishEvent(assignmentAttemptFailed)

        verify { queueTicket(assignmentAttemptFailed.ticketId) }
    }
}

class TestConfig {
    @Bean
    fun prioritiseTicketService() = mockk<AssignTicketService>(relaxed = true)

    @Bean
    fun queueTicketService() = mockk<QueueTicketService>(relaxed = true)

    @Bean
    fun assignmentStrategy() = AssignmentStrategy.RULE_BASED
}