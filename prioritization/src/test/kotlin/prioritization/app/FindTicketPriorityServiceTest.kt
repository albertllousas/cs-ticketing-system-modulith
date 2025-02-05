package prioritization.app

import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import prioritization.domain.Priority
import prioritization.domain.TicketPriority
import prioritization.domain.TicketPriorityRepository
import shared.TicketPriorityDto
import java.util.UUID

class FindTicketPriorityServiceTest {

    private val repository = mockk<TicketPriorityRepository>()

    private val findTicketPriority = FindTicketPriorityService(repository)

    @Test
    fun `should find ticket priority`() {
        val ticketPriority = TicketPriority(UUID.randomUUID(), UUID.randomUUID(), Priority.HIGH, 0L)
        every { repository.find(ticketPriority.ticketId) } returns ticketPriority

        findTicketPriority(ticketPriority.ticketId) shouldBe TicketPriorityDto(
            ticketPriority.id, ticketPriority.ticketId, TicketPriorityDto.Priority.HIGH
        )
    }

    @Test
    fun `should return null when ticket priority is not found`() {
        every { repository.find(any()) } returns null

        findTicketPriority(UUID.randomUUID()) shouldBe null
    }
}
