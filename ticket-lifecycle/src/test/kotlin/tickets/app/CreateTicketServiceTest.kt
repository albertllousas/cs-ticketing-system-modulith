package tickets.app

import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import shared.EventPublisher
import shared.TicketCreated
import tickets.domain.Ticket
import tickets.domain.TicketRepository
import tickets.domain.TicketType.ISSUE
import java.util.UUID

class CreateTicketServiceTest {

    private val ticketRepository = mockk<TicketRepository>(relaxed = true)

    private val eventPublisher = mockk<EventPublisher>(relaxed = true)

    private val createTicketService = CreateTicketService(ticketRepository, eventPublisher)

    @Test
    fun `should create a ticket`() {

        createTicketService("Access", "I can't access to my account", UUID.randomUUID(), ISSUE)

        verify { ticketRepository.save(any<Ticket>()) }
        verify { eventPublisher.publish(any<TicketCreated>()) }
    }
}