package prioritization.infra.outbound

import org.springframework.stereotype.Component
import prioritization.domain.Customer
import prioritization.domain.CustomerFinder
import prioritization.domain.Ticket
import prioritization.domain.TicketFinder
import shared.ExposedInboundPorts
import java.util.UUID

@Component
class TicketLifecycleModuleAdapter(private val findTicket: ExposedInboundPorts.FindTicket) : TicketFinder {

    override fun find(id: UUID): Ticket = findTicket(id)
        ?.let { Ticket(it.title, it.description, it.customerId, Ticket.Type.valueOf(it.type.name)) }
        ?: (failWith("Ticket '${id}' not found"))
}

@Component
class CustomerModuleAdapter(private val findCustomer: ExposedInboundPorts.FindCustomer) : CustomerFinder {

    override fun find(id: UUID): Customer = findCustomer(id)
        ?.let { Customer(it.id, Customer.Tier.valueOf(it.tier.name)) }
        ?: (failWith("Customer '${id}' not found"))
}


fun failWith(msg: String): Nothing = throw CrossModuleCallException(msg)

data class CrossModuleCallException(private val msg: String) : Exception(msg)
