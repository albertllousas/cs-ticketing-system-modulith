package assignment.infra.inbound

import assignment.app.AssignTicketService
import assignment.domain.AssignmentStrategy
import assignment.domain.TicketQueue
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class AssignQueuedTicketsJobAdapter(
    private val queue: TicketQueue,
    private val assignTicket: AssignTicketService,
    private val strategy: AssignmentStrategy
) {

    @Scheduled(fixedDelay = 500)
    @Transactional
    fun assignQueuedTickets() {
        queue.dequeue(10).forEach { assignTicket(it, strategy) }
    }
}
