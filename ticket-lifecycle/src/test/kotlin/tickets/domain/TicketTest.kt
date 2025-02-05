package tickets.domain

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import shared.TicketCreated
import tickets.domain.TicketType.ISSUE
import java.time.Clock
import java.time.Instant
import java.time.LocalDateTime.parse
import java.util.UUID

class TicketTest {

    @Test
    fun `should create a ticket`() {
        val customerId = UUID.randomUUID()
        val ticketId = UUID.randomUUID()
        val clock = Clock.fixed(Instant.parse("2021-01-01T00:00:00Z"), Clock.systemUTC().zone)
        val title = "Access"
        val description = "I can't access to my account"

        val result = Ticket.newTicket(title, description, customerId, ISSUE, generateId = { ticketId }, clock = clock)

        result shouldBe Pair(
            TicketCreated(ticketId, title, description, customerId, "ISSUE"),
            Ticket(ticketId, title, description, customerId, TicketStatus.OPEN, ISSUE, 0, parse("2021-01-01T00:00:00"))
        )
    }
}