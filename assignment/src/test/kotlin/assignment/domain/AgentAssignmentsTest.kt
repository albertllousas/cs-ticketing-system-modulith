package assignment.domain

import arrow.core.left
import arrow.core.right
import assignment.domain.Customer.Tier.PREMIUM
import assignment.domain.Ticket.Priority.LOW
import assignment.domain.Ticket.Status.OPEN
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import shared.AssignmentAttemptFailed
import shared.AssignmentAttemptFailed.Reason.NO_SUITABLE_AGENT_AVAILABLE
import shared.TicketAssigned
import java.time.Clock
import java.time.Instant
import java.time.LocalDateTime
import java.util.Locale.ENGLISH
import java.util.UUID

class AgentAssignmentsTest {

    private val customer = Customer(UUID.randomUUID(), ENGLISH, PREMIUM)

    private val ticket = Ticket(
        UUID.randomUUID(), "Account question", "I have a question regarding my account", customer, LOW, OPEN,
    )
    private val agent1 = Agent(UUID.randomUUID(), listOf("English"), listOf(ENGLISH))
    private val agent2 = Agent(UUID.randomUUID(), listOf("English"), listOf(ENGLISH))

    private val clock = Clock.fixed(Instant.parse("2021-01-01T00:00:00Z"), Clock.systemUTC().zone)

    @Test
    fun `should assign a ticket to the first agent available with less tickets already assigned`() {
        val agent1Assignments = AgentAssignments.create(agent1.id).add(
            ticketId = UUID.randomUUID(), status = OPEN, assignedAt = LocalDateTime.now(clock)
        )
        val agent2Assignments = AgentAssignments.create(agent2.id)

        val result = AgentAssignments.assign(
            ticket = ticket,
            agents = listOf(agent1, agent2),
            currentAssignments = listOf(agent1Assignments, agent2Assignments),
            clock = clock,
            maxTicketsPerAgent = 5
        )

        result shouldBe Pair(
            TicketAssigned( ticket.id, agent2.id),
            agent2Assignments.copy(currentTickets = listOf(AssignedTicket(ticket.id, OPEN, LocalDateTime.now(clock))))
        ).right()
    }

    @Test
    fun `should assign a ticket to the agent if the agent has no tickets assigned yet`() {
        val result = AgentAssignments.assign(
            ticket = ticket,
            agents = listOf(agent1),
            currentAssignments = emptyList(),
            clock = clock
        )

        result shouldBe Pair(
            TicketAssigned(ticket.id, agent1.id),
            AgentAssignments.create(agent1.id)
                .copy(currentTickets = listOf(AssignedTicket(ticket.id, OPEN, LocalDateTime.now(clock))))
        ).right()
    }

    @Test
    fun `should fail to assign a ticket if current agents have more than max tickets each`() {
        val agent1Assignments = AgentAssignments.create(agent1.id).add(
            ticketId = UUID.randomUUID(), status = OPEN, assignedAt = LocalDateTime.now(clock)
        )
        val agent2Assignments = AgentAssignments.create(agent2.id).add(
            ticketId = UUID.randomUUID(), status = OPEN, assignedAt = LocalDateTime.now(clock)
        )

        val result = AgentAssignments.assign(
            ticket = ticket,
            agents = listOf(agent1, agent2),
            currentAssignments = listOf(agent1Assignments, agent2Assignments),
            clock = clock,
            maxTicketsPerAgent = 1
        )

        result shouldBe AssignmentAttemptFailed(ticket.id, NO_SUITABLE_AGENT_AVAILABLE).left()
    }

    @Test
    fun `should fail to assign a ticket when no agent is available`() {
        val result = AgentAssignments.assign(
            ticket = ticket,
            agents = emptyList(),
            currentAssignments = emptyList(),
            clock = clock,
            maxTicketsPerAgent = 5
        )

        result shouldBe AssignmentAttemptFailed(ticket.id, NO_SUITABLE_AGENT_AVAILABLE).left()
    }

    @Test
    fun `should create agent assignments`() {
        val agentId = UUID.randomUUID()
        val result = AgentAssignments.create(agentId)

        result shouldBe AgentAssignments(agentId, emptyList(), 0)
    }

    @Test
    fun `should add ticket to agent assignments`() {
        val agentId = UUID.randomUUID()
        val agentAssignments = AgentAssignments.create(agentId)
        val ticketId = UUID.randomUUID()

        val result = agentAssignments.add(ticketId, OPEN, LocalDateTime.now(clock))

        result shouldBe AgentAssignments(agentId, listOf(AssignedTicket(ticketId, OPEN, LocalDateTime.now(clock))), 0)
    }
}
