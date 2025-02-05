package assignment.infra.outbound

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import shared.AssignmentAttemptFailed
import shared.AssignmentEvent
import shared.CustomerCreated
import shared.CustomerEvent
import shared.TicketAssigned
import shared.TicketCreated
import shared.TicketEvent
import shared.TicketPrioritized
import shared.TicketPriorityEvent
import shared.TicketQueued
import java.lang.invoke.MethodHandles

@Component
class AssigmentEventLoggerAdapter(private val logger: Logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass())) {

    @EventListener
    fun handle(event: AssignmentEvent) = when(event) {
        is AssignmentAttemptFailed ->
            logger.warn("event:'AssignmentAttemptFailed', ticket-id:'${event.ticketId}', reason:'${event.reason}'")
        is TicketAssigned -> logger.info("event:'TicketAssigned', ticket-id:'${event.ticketId}'")
        is TicketQueued -> logger.info("event:'TicketQueued', ticket-id:'${event.ticketId}'")
    }
}
