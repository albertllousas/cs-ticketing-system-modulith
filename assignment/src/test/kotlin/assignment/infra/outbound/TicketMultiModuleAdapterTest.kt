package assignment.infra.outbound

import assignment.domain.Customer
import assignment.domain.Customer.Tier
import assignment.domain.Ticket
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import shared.CustomerDto
import shared.CustomerDto.Tier.PREMIUM
import shared.ExposedInboundPorts
import shared.TicketDto
import shared.TicketDto.Status.OPEN
import shared.TicketDto.Type.BUG
import shared.TicketPriorityDto
import shared.TicketPriorityDto.Priority.HIGH
import java.util.Locale.ENGLISH
import java.util.UUID.randomUUID

class TicketMultiModuleAdapterTest {

    private val findTicket = mockk<ExposedInboundPorts.FindTicket>()

    private val findCustomer = mockk<ExposedInboundPorts.FindCustomer>()

    private val findTicketPriority = mockk<ExposedInboundPorts.FindTicketPriority>()

    private val ticketLifecycleModuleAdapter = TicketMultiModuleAdapter(findTicket, findCustomer, findTicketPriority)

    @Test
    fun `should find ticket`() {
        val customerId = randomUUID()
        val ticketId = randomUUID()
        val ticketPriorityId = randomUUID()
        every { findTicket(ticketId) } returns TicketDto(ticketId, "title", "description", customerId, OPEN, BUG)
        every { findCustomer(customerId) } returns CustomerDto(
            customerId,
            "Jane Doe",
            "jane.doe@gmail.com",
            ENGLISH,
            PREMIUM
        )
        every { findTicketPriority(ticketId) } returns TicketPriorityDto(ticketPriorityId, ticketId, HIGH)

        val result = ticketLifecycleModuleAdapter.find(ticketId)

        result shouldBe Ticket(
            ticketId,
            "title",
            "description",
            Customer(customerId, ENGLISH, Tier.PREMIUM),
            Ticket.Priority.HIGH,
            Ticket.Status.OPEN
        )
    }

    @Test
    fun `should fail to finding a ticket when ticket is not found`() {
        every { findTicket(any()) } returns null

        shouldThrow<CrossModuleCallException> { ticketLifecycleModuleAdapter.find(randomUUID()) }
    }

    @Test
    fun `should fail to finding a ticket when customer is not found`() {
        every { findTicket(any()) } returns TicketDto(randomUUID(), "title", "description", randomUUID(), OPEN, BUG)
        every { findCustomer(any()) } returns null

        shouldThrow<CrossModuleCallException> { ticketLifecycleModuleAdapter.find(randomUUID()) }
    }

    @Test
    fun `should fail to finding a ticket when priority is not found`() {
        every { findTicket(any()) } returns TicketDto(randomUUID(), "title", "description", randomUUID(), OPEN, BUG)
        every { findCustomer(any()) } returns CustomerDto(
            randomUUID(),
            "Jane Doe",
            "jane.doe@gmail.com",
            ENGLISH,
            PREMIUM
        )
        every { findTicketPriority(any()) } returns null

        shouldThrow<CrossModuleCallException> { ticketLifecycleModuleAdapter.find(randomUUID()) }
    }

}
