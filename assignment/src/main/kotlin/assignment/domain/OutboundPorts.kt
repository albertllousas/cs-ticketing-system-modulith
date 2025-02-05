package assignment.domain

import arrow.core.Either
import shared.AssignmentAttemptFailed
import shared.TicketAssigned
import java.util.UUID

interface AgentAssignmentsRepository {
    fun save(agentAssignments: AgentAssignments)
    fun find(agentIds: List<UUID>): List<AgentAssignments>
    fun find(agentId: UUID): AgentAssignments
}

interface TicketFinder {
    fun find(id: UUID): Ticket
}

interface TicketQueue {
    fun add(ticketId: UUID, priority: Ticket.Priority)
    fun dequeue(batch: Int): List<UUID>
}

interface AgentFinder {
    fun findAvailable(): List<Agent>
}

interface AIBasedTicketAssigner {
    fun assign(
        ticket: Ticket,
        agents: List<Agent>,
        currentAssignments: List<AgentAssignments>
    ): Either<AssignmentAttemptFailed, Pair<TicketAssigned, AgentAssignments>>
}
