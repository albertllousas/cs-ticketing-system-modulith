package prioritization.app

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import prioritization.domain.AITicketPrioritiser
import prioritization.domain.Customer
import prioritization.domain.Customer.Tier.PREMIUM
import prioritization.domain.CustomerFinder
import prioritization.domain.PrioritizationStrategy.AI_BASED
import prioritization.domain.PrioritizationStrategy.RULE_BASED
import prioritization.domain.Priority
import prioritization.domain.Ticket
import prioritization.domain.Ticket.Type.ISSUE
import prioritization.domain.TicketFinder
import prioritization.domain.TicketPriorityRepository
import shared.EventPublisher
import shared.TicketPrioritized
import java.util.UUID

class PrioritiseTicketServiceTest {

    private val ticketFinder = mockk<TicketFinder>()

    private val customerFinder = mockk<CustomerFinder>()

    private val aiTicketPrioritiser = mockk<AITicketPrioritiser>()

    private val ticketPriorityRepository = mockk<TicketPriorityRepository>(relaxed = true)

    private val eventPublisher = mockk<EventPublisher>(relaxed = true)

    private val prioritiseTicket = PrioritiseTicketService(
        ticketFinder, customerFinder, aiTicketPrioritiser, ticketPriorityRepository, eventPublisher
    )

    @Test
    fun `should orchestrate the prioritisation of a ticket using AI based strategy`() {
        val ticketId = UUID.randomUUID()
        val customerId = UUID.randomUUID()
        val ticket = Ticket("Access", "I can't access to my account", customerId, ISSUE)
        val customer = Customer(customerId, PREMIUM)
        every { ticketFinder.find(ticketId) } returns ticket
        every { customerFinder.find(customerId) } returns customer
        every { aiTicketPrioritiser.prioritise(ticket, customer) } returns Priority.HIGH

        prioritiseTicket(ticketId, AI_BASED)

        verify {
            ticketPriorityRepository.save(any())
            eventPublisher.publish(any<TicketPrioritized>())
        }
    }

    @Test
    fun `should orchestrate the prioritisation of a ticket using rule based strategy`() {
        val ticketId = UUID.randomUUID()
        val customerId = UUID.randomUUID()
        val ticket = Ticket("Access", "I can't access to my account", customerId, ISSUE)
        val customer = Customer(customerId, PREMIUM)
        every { ticketFinder.find(ticketId) } returns ticket
        every { customerFinder.find(customerId) } returns customer

        prioritiseTicket(ticketId, RULE_BASED)

        verify {
            ticketPriorityRepository.save(any())
            eventPublisher.publish(any<TicketPrioritized>())
        }
    }
}