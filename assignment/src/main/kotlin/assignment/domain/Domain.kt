package assignment.domain

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import assignment.domain.Ticket.Status
import shared.AssignmentAttemptFailed
import shared.AssignmentAttemptFailed.Reason.NO_SUITABLE_AGENT_AVAILABLE
import shared.TicketAssigned
import java.time.Clock
import java.time.LocalDateTime
import java.util.Locale
import java.util.UUID

enum class AssignmentStrategy { AI_BASED, RULE_BASED }

data class AgentAssignments(val agentId: UUID, val currentTickets: List<AssignedTicket>, val version: Long) {

    companion object {
        /* Simple assignment strategy based on the number of tickets assigned to each agent,
        * could be improved matching languages, skills, but it is already done by the AI assigment
        */
        fun assign(
            ticket: Ticket,
            agents: List<Agent>,
            currentAssignments: List<AgentAssignments>,
            clock: Clock = Clock.systemUTC(),
            maxTicketsPerAgent: Int = 5
        ): Either<AssignmentAttemptFailed, Pair<TicketAssigned, AgentAssignments>> {
            val missingAssignments = agents
                .filterNot { agent -> currentAssignments.any { agent.id == it.agentId } }
                .map { create(it.id) }
            val assignments = currentAssignments + missingAssignments
            return agents.filter { agent ->
                assignments.find { it.agentId == agent.id && it.currentTickets.size < maxTicketsPerAgent } != null
            }.minByOrNull { agent ->
                assignments.find { it.agentId == agent.id }?.currentTickets?.size ?: 0
            }?.let { agentToAssign ->
                (assignments.find { it.agentId == agentToAssign.id } ?: create(agentToAssign.id))
                    .add(ticketId = ticket.id, status = ticket.status, assignedAt = LocalDateTime.now(clock))
            }?.let { updatedAssignment ->
                Pair(TicketAssigned(ticket.id, updatedAssignment.agentId), updatedAssignment).right()
            } ?: AssignmentAttemptFailed(ticket.id, NO_SUITABLE_AGENT_AVAILABLE).left()
        }

        fun create(assignedAgentId: UUID): AgentAssignments = AgentAssignments(assignedAgentId, emptyList(), 0)
    }

    fun add(ticketId: UUID, status: Status, assignedAt: LocalDateTime) = copy(
        currentTickets = currentTickets + AssignedTicket(ticketId, status, assignedAt)
    )
}

data class Agent(val id: UUID, val skills: List<String>, val languages: List<Locale>)

data class Customer(val id: UUID, val lang: Locale, val tier: Tier) {
    enum class Tier { BASIC, STANDARD, PREMIUM }
}

data class AssignedTicket(val id: UUID, val status: Status, val assignedAt: LocalDateTime)

data class Ticket(
    val id: UUID,
    val title: String,
    val description: String,
    val customer: Customer,
    val priority: Priority,
    val status: Status
) {
    enum class Priority { LOW, MEDIUM, HIGH }
    enum class Status { OPEN, IN_PROGRESS, ON_HOLD, RESOLVED, CLOSED }
}
