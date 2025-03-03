package tickets.fixtures

import tickets.domain.Ticket
import tickets.domain.TicketStatus
import tickets.domain.TicketType
import java.time.LocalDateTime
import java.util.UUID

object TestBuilders {

    fun buildTicket(
        id: UUID = UUID.randomUUID(),
        title: String = "Title",
        description: String = "Description",
        customerId: UUID = UUID.randomUUID(),
        status: TicketStatus = TicketStatus.OPEN,
        type: TicketType = TicketType.ISSUE,
        version: Long = 0,
        created: LocalDateTime = LocalDateTime.now()
    ) = Ticket(id, title, description, customerId, status, type, version, created)
}