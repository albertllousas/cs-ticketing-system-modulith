package tickets.infra.outbound

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import shared.TicketCreated
import shared.TicketEvent
import java.lang.invoke.MethodHandles

@Component
class TicketEventLoggerAdapter(private val logger: Logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass())) {

    @EventListener
    fun reactTo(event: TicketEvent) = when(event) {
        is TicketCreated -> logger.info("event:'TicketCreated', ticket-id:'${event.id}'")
    }
}