package shared

import java.util.Locale
import java.util.UUID

sealed interface DomainEvent

sealed interface TicketEvent : DomainEvent

data class TicketCreated(
    val id: UUID,
    val title: String,
    val description: String,
    val customerId: UUID,
    val type: String
) : TicketEvent

sealed interface TicketPriorityEvent : DomainEvent

data class TicketPrioritized(val id: UUID, val ticketId: UUID, val priority: String) : TicketPriorityEvent

sealed interface AssignmentEvent : DomainEvent

data class TicketAssigned(val ticketId: UUID, val agentId: UUID) : AssignmentEvent

data class AssignmentAttemptFailed(val ticketId: UUID, val reason: Reason) : AssignmentEvent {
    enum class Reason { NO_AGENTS_AVAILABLE, NO_SUITABLE_AGENT_AVAILABLE }
}

data class TicketQueued(val ticketId: UUID) : AssignmentEvent

sealed interface AgentEvent : DomainEvent

data class AgentCreated(val id: UUID, val email: String, val fullName: String, val skills: List<String>, val languages: List<Locale>) : AgentEvent

data class AgentConnected(val id: UUID) : AgentEvent

data class AgentDisconnected(val id: UUID) : AgentEvent

sealed interface CustomerEvent : DomainEvent

data class CustomerCreated(val id: UUID, val fullName: String, val email: String, val preferredLang: String, val tier: String) : CustomerEvent
