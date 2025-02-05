package assignment.infra.inbound

import assignment.app.AssignTicketService
import assignment.domain.AssignmentStrategy.RULE_BASED
import assignment.domain.TicketQueue
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import java.util.UUID

class AssignQueuedTicketsJobAdapterTest {

    private val queue = mockk<TicketQueue>()

    private val assignTicket = mockk<AssignTicketService>(relaxed = true)

    private val assignQueuedTicketsJob = AssignQueuedTicketsJobAdapter(queue, assignTicket, RULE_BASED)

    @Test
    fun `should assign queued tickets`() {
        val ticketIds = (1..10).map { UUID.randomUUID() }
        every { queue.dequeue(10) } returns ticketIds

        assignQueuedTicketsJob.assignQueuedTickets()

        ticketIds.forEach { ticketId -> verify { assignTicket(ticketId, RULE_BASED) } }
    }
}
