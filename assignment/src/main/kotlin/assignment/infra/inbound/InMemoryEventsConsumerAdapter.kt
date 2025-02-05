package assignment.infra.inbound

import assignment.app.AssignTicketService
import assignment.domain.AssignmentStrategy
import assignment.app.QueueTicketService
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import shared.AssignmentAttemptFailed
import shared.TicketPrioritized

@Component
class InMemoryEventsConsumerAdapter(
    private val assignTicket: AssignTicketService,
    private val queueTicket: QueueTicketService,
    private val strategy: AssignmentStrategy
) {

    @EventListener
    fun listenTo(event: TicketPrioritized) = assignTicket(event.ticketId, strategy)

    @EventListener
    fun listenTo(event: AssignmentAttemptFailed) = queueTicket(event.ticketId)

    // TODO: ticket closed event
}
