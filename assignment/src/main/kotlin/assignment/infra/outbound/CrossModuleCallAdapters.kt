package assignment.infra.outbound

import assignment.domain.Agent
import assignment.domain.AgentFinder
import assignment.domain.Customer
import assignment.domain.Ticket
import assignment.domain.TicketFinder
import org.springframework.stereotype.Component
import shared.ExposedInboundPorts
import java.util.UUID

@Component
class TicketMultiModuleAdapter(
    private val findTicket: ExposedInboundPorts.FindTicket,
    private val findCustomer: ExposedInboundPorts.FindCustomer,
    private val findTicketPriority: ExposedInboundPorts.FindTicketPriority

) : TicketFinder {

    override fun find(id: UUID): Ticket {
        val ticket = findTicket(id) ?: (failWith("Ticket '${id}' not found"))
        val customer = findCustomer(ticket.customerId) ?: (failWith("Customer '${ticket.customerId}' not found"))
        val ticketPriority = findTicketPriority(id) ?: (failWith("Ticket priority '${id}' not found"))
        return Ticket(
            ticket.id,
            ticket.title,
            ticket.description,
            Customer(customer.id, customer.preferredLang, Customer.Tier.valueOf(customer.tier.name)),
            Ticket.Priority.valueOf(ticketPriority.priority.name),
            Ticket.Status.valueOf(ticket.status.name),
        )
    }
}

@Component
class AgentCenterModuleAdapter(private val findAvailableAgents: ExposedInboundPorts.FindAvailableAgents) : AgentFinder {
    override fun findAvailable(): List<Agent> = findAvailableAgents().map { Agent(it.id, it.skills, it.languages) }
}

fun failWith(msg: String): Nothing = throw CrossModuleCallException(msg)

data class CrossModuleCallException(private val msg: String) : Exception(msg)
