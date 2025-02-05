package tickets.domain

import java.util.UUID

interface TicketRepository {

    fun save(ticket: Ticket)

    fun find(id: UUID): Ticket?
}
