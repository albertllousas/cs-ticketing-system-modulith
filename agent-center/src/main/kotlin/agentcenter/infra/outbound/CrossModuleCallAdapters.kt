package agentcenter.infra.outbound

import agentcenter.domain.AssignedTicket
import agentcenter.domain.AssignedTicketFinder
import agentcenter.domain.Priority
import org.springframework.stereotype.Component
import shared.ExposedInboundPorts
import java.util.UUID

@Component
class MultiModuleAdapter(
    private val findTicket: ExposedInboundPorts.FindTicket,
    private val findAssignments: ExposedInboundPorts.FindAssignments,
    private val findTicketPriority: ExposedInboundPorts.FindTicketPriority
) : AssignedTicketFinder {

    override fun find(agentId: UUID): List<AssignedTicket> {
        val agentAssignments = findAssignments(agentId) ?: (failWith("Agent assignments for '${agentId}' not found"))
        val tickets = agentAssignments.currentTickets.map { ticketId ->
            findTicket(ticketId) ?: (failWith("Ticket '${ticketId}' not found"))
        }
        val ticketPriorities = agentAssignments.currentTickets.map { ticketId ->
            findTicketPriority(ticketId) ?: (failWith("Ticket priority '${ticketId}' not found"))
        }
        return tickets.zip(ticketPriorities).map { (ticket, priority) ->
            AssignedTicket(ticket.id, Priority.valueOf(priority.priority.name), ticket.customerId, ticket.title)
        }
    }
}

fun failWith(msg: String): Nothing = throw CrossModuleCallException(msg)

data class CrossModuleCallException(private val msg: String) : Exception(msg)
