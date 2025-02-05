package prioritization.infra.outbound

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import prioritization.domain.Ticket
import prioritization.domain.Ticket.Type.BUG
import shared.ExposedInboundPorts
import shared.TicketDto
import java.util.UUID

class TicketLifecycleModuleAdapterTest {

    private val findTicket = mockk<ExposedInboundPorts.FindTicket>()

    private val ticketLifecycleModuleAdapter = TicketLifecycleModuleAdapter(findTicket)

    @Test
    fun `should find ticket`() {
        val customerId = UUID.randomUUID()
        val ticketId = UUID.randomUUID()
        every {
            findTicket(ticketId)
        } returns TicketDto(ticketId, "title", "description", customerId, TicketDto.Status.OPEN, TicketDto.Type.BUG)

        val result = ticketLifecycleModuleAdapter.find(ticketId)

        result shouldBe Ticket("title", "description", customerId, BUG)
    }

    @Test
    fun `should fail to find ticket`() {
        every { findTicket(any()) } returns null

        shouldThrow<CrossModuleCallException> { ticketLifecycleModuleAdapter.find(UUID.randomUUID()) }
    }
}