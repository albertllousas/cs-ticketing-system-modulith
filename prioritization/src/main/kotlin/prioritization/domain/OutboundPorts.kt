package prioritization.domain

import java.util.UUID

interface TicketPriorityRepository {
    fun save(ticketPriority: TicketPriority)
    fun find(ticketId: UUID): TicketPriority?
}

interface TicketFinder {
    fun find(id: UUID): Ticket
}

interface CustomerFinder{
    fun find(id: UUID): Customer
}

interface AITicketPrioritiser {
    fun prioritise(ticket: Ticket, customer: Customer): Priority
}
