package assignment.app

import assignment.domain.Customer
import assignment.domain.Customer.Tier.PREMIUM
import assignment.domain.Ticket
import assignment.domain.Ticket.Priority.LOW
import assignment.domain.Ticket.Status.OPEN
import assignment.domain.TicketFinder
import assignment.domain.TicketQueue
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import shared.EventPublisher
import shared.TicketQueued
import java.util.Locale.ENGLISH
import java.util.UUID.randomUUID

class QueueTicketServiceTest {

    private val ticketFinder = mockk<TicketFinder>()

    private val queue = mockk<TicketQueue>(relaxed = true)

    private val eventPublisher = mockk<EventPublisher>(relaxed = true)

    private val queueTicket = QueueTicketService(ticketFinder, queue, eventPublisher)

    @Test
    fun `should queue a ticket`() {
        val customer = Customer(randomUUID(), ENGLISH, PREMIUM)
        val ticket = Ticket(
            randomUUID(), "Account question", "I have a question regarding my account", customer, LOW, OPEN,
        )
        every { ticketFinder.find(ticket.id) } returns ticket

        queueTicket(ticket.id)

        verify {
            queue.add(ticket.id, ticket.priority)
            eventPublisher.publish(any<TicketQueued>())
        }
    }
}
