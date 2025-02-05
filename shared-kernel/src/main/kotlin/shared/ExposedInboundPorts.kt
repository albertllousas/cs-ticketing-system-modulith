package shared

import java.util.Locale
import java.util.UUID

// Exposed inbound ports for cross service calls
object ExposedInboundPorts {

    interface FindTicket {
        operator fun invoke(id: UUID): TicketDto?
    }

    interface FindTicketPriority {
        operator fun invoke(ticketId: UUID): TicketPriorityDto?
    }

    interface FindCustomer {
        operator fun invoke(id: UUID): CustomerDto?
    }

    interface FindAssignments {
        operator fun invoke(agentIds: List<UUID>): List<AgentAssignmentsDto>
        operator fun invoke(agentId: UUID): AgentAssignmentsDto?
    }

    interface FindAgent {
        operator fun invoke(id: UUID) : AgentDto?
    }

    interface FindAvailableAgents {
        operator fun invoke() : List<AgentDto>
    }
}

data class TicketDto(val id: UUID, val title: String, val description: String, val customerId: UUID, val status: Status, val type: Type) {
    enum class Status { OPEN, IN_PROGRESS, ON_HOLD, RESOLVED, CLOSED }
    enum class Type { ISSUE, BUG, FEATURE, SUPPORT }
}

data class TicketPriorityDto(val id: UUID, val ticketId: UUID, val priority: Priority) {
    enum class Priority { LOW, MEDIUM, HIGH }
}

data class CustomerDto(val id: UUID, val fullName: String, val email: String, val preferredLang: Locale, val tier: Tier) {
    enum class Tier { BASIC, STANDARD, PREMIUM }
}

data class AgentAssignmentsDto(val agentId: UUID, val currentTickets: List<UUID>)

data class AgentDto(val id: UUID, val email: String, val fullName: String, val skills: List<String>, val languages: List<Locale>, val status: Status) {
    enum class Status { ONLINE, OFFLINE }
}