package fixtures.ai

import assignment.domain.AgentAssignments
import assignment.domain.AssignedTicket
import assignment.domain.Ticket
import java.time.LocalDateTime.now
import java.util.UUID

object TestBuilders {

    fun buildAgentAssignments(
        agentId: UUID = UUID.randomUUID(),
        currentTickets: List<AssignedTicket> = listOf(
            AssignedTicket(UUID.randomUUID(), Ticket.Status.OPEN, now()),
            AssignedTicket(UUID.randomUUID(), Ticket.Status.CLOSED, now())
        ),
        version: Long = 0L
    ) = AgentAssignments(agentId, currentTickets, version)
}
