package tickets.domain

import shared.TicketCreated
import tickets.domain.TicketStatus.OPEN
import java.time.Clock
import java.time.LocalDateTime
import java.time.LocalDateTime.now
import java.util.UUID

enum class TicketStatus { OPEN, IN_PROGRESS, ON_HOLD, RESOLVED, CLOSED }

enum class TicketType { ISSUE, BUG, FEATURE, SUPPORT }

data class Ticket(
    val id: UUID,
    val title: String,
    val description: String,
    val customerId: UUID,
    val status: TicketStatus,
    val type: TicketType,
    val version: Long,
    val created: LocalDateTime
) {

    companion object {

        private val GEN_ID: () -> UUID = { UUID.randomUUID() }

        fun newTicket(
            title: String,
            description: String,
            customerId: UUID,
            type: TicketType,
            generateId: () -> UUID = GEN_ID,
            clock: Clock = Clock.systemUTC()
        ): Pair<TicketCreated, Ticket> {
            val id = generateId()
            return Pair(
                TicketCreated(id, title, description, customerId, type.name),
                Ticket(id, title, description, customerId, OPEN, type, 0, now(clock))
            )
        }
    }
}
