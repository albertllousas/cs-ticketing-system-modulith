package fixtures

import prioritization.domain.Priority
import prioritization.domain.Priority.LOW
import prioritization.domain.TicketPriority
import java.util.UUID

object TestBuilders {

    fun buildTicketPriority(
        id: UUID = UUID.randomUUID(),
        ticketId: UUID = UUID.randomUUID(),
        priority: Priority = LOW,
        version: Long = 0,
    ) = TicketPriority(id, ticketId, priority, version)
}