package tickets.app

import tickets.fixtures.TestBuilders
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import shared.TicketDto
import tickets.domain.TicketRepository
import java.util.UUID

class FindTicketServiceTest {

    private val repository = mockk<TicketRepository>()

    private val findTicket = FindTicketService(repository)

    @Test
    fun `should find ticket priority`() {
        val ticket = TestBuilders.buildTicket()
        every { repository.find(ticket.id) } returns ticket

        findTicket(ticket.id) shouldBe TicketDto(
            ticket.id,
            ticket.title,
            ticket.description,
            ticket.customerId,
            TicketDto.Status.valueOf(ticket.status.name),
            TicketDto.Type.valueOf(ticket.type.name)
        )
    }

    @Test
    fun `should return null when ticket priority is not found`() {
        every { repository.find(any()) } returns null

        findTicket(UUID.randomUUID()) shouldBe null
    }
}