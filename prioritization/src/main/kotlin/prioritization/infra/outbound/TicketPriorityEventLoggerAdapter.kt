package prioritization.infra.outbound

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import shared.TicketCreated
import shared.TicketEvent
import shared.TicketPrioritized
import shared.TicketPriorityEvent
import java.lang.invoke.MethodHandles

@Component
class TicketPriorityEventLoggerAdapter(private val logger: Logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass())) {

    @EventListener
    fun handle(event: TicketPriorityEvent) = when(event) {
        is TicketPrioritized ->
            logger.info("event:'TicketPrioritized', ticket-id:'${event.id}', priority:'${event.priority}'")
    }
}