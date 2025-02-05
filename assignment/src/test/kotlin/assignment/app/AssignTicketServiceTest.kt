package assignment.app

import arrow.core.right
import assignment.domain.AIBasedTicketAssigner
import assignment.domain.Agent
import assignment.domain.AgentAssignments
import assignment.domain.AgentAssignmentsRepository
import assignment.domain.AgentFinder
import assignment.domain.AssignmentStrategy
import assignment.domain.Customer
import assignment.domain.Customer.Tier.PREMIUM
import assignment.domain.Ticket
import assignment.domain.Ticket.Priority.LOW
import assignment.domain.Ticket.Status.OPEN
import assignment.domain.TicketFinder
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import shared.AssignmentAttemptFailed
import shared.EventPublisher
import shared.TicketAssigned
import java.util.Locale.ENGLISH
import java.util.UUID

class AssignTicketServiceTest {

    private val ticketFinder = mockk<TicketFinder>()

    private val agentFinder = mockk<AgentFinder>()

    private val aiBasedTicketAssigner = mockk<AIBasedTicketAssigner>()

    private val repository = mockk<AgentAssignmentsRepository>(relaxed = true)

    private val eventPublisher = mockk<EventPublisher>(relaxed = true)

    private val assignTicket = AssignTicketService(
        ticketFinder, agentFinder, aiBasedTicketAssigner, repository, eventPublisher
    )

    @Test
    fun `should assign ticket using AI based strategy`() {
        val ticketId = UUID.randomUUID()
        val customer = Customer(UUID.randomUUID(), ENGLISH, PREMIUM)
        val ticket = Ticket(
            UUID.randomUUID(), "Account question", "I have a question regarding my account", customer, LOW, OPEN,
        )
        val agent1 = Agent(UUID.randomUUID(), listOf("English"), listOf(ENGLISH))
        val agent2 = Agent(UUID.randomUUID(), listOf("English"), listOf(ENGLISH))
        every { ticketFinder.find(ticketId) } returns ticket
        every { agentFinder.findAvailable() } returns listOf(agent1, agent2)
        every { repository.find(listOf(agent1.id, agent2.id)) } returns emptyList()
        every { aiBasedTicketAssigner.assign(ticket, listOf(agent1, agent2), emptyList()) } returns
            Pair(TicketAssigned(ticket.id, agent1.id), AgentAssignments.create(agent1.id)).right()

        assignTicket(ticketId, AssignmentStrategy.AI_BASED)

        verify {
            repository.save(any<AgentAssignments>())
            eventPublisher.publish(any<TicketAssigned>())
        }
    }

    @Test
    fun `should assign ticket using the rule based strategy`() {
        val ticketId = UUID.randomUUID()
        val customer = Customer(UUID.randomUUID(), ENGLISH, PREMIUM)
        val ticket = Ticket(
            UUID.randomUUID(), "Account question", "I have a question regarding my account", customer, LOW, OPEN,
        )
        val agent1 = Agent(UUID.randomUUID(), listOf("English"), listOf(ENGLISH))
        val agent2 = Agent(UUID.randomUUID(), listOf("English"), listOf(ENGLISH))
        every { ticketFinder.find(ticketId) } returns ticket
        every { agentFinder.findAvailable() } returns listOf(agent1, agent2)
        every { repository.find(listOf(agent1.id, agent2.id)) } returns emptyList()

        assignTicket(ticketId, AssignmentStrategy.RULE_BASED)

        verify {
            repository.save(any<AgentAssignments>())
            eventPublisher.publish(any<TicketAssigned>())
        }
    }

    @Test
    fun `should not assign ticket if no agent is available`() {
        val ticketId = UUID.randomUUID()
        val customer = Customer(UUID.randomUUID(), ENGLISH, PREMIUM)
        val ticket = Ticket(
            UUID.randomUUID(), "Account question", "I have a question regarding my account", customer, LOW, OPEN,
        )
        every { ticketFinder.find(ticketId) } returns ticket
        every { agentFinder.findAvailable() } returns emptyList()

        assignTicket(ticketId, AssignmentStrategy.RULE_BASED)

        verify {
            eventPublisher.publish(any<AssignmentAttemptFailed>())
        }
    }
}
