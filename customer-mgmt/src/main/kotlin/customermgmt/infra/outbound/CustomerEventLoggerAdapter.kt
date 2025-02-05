package customermgmt.infra.outbound

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import shared.CustomerCreated
import shared.CustomerEvent
import shared.TicketCreated
import shared.TicketEvent
import shared.TicketPrioritized
import shared.TicketPriorityEvent
import java.lang.invoke.MethodHandles

@Component
class CustomerEventLoggerAdapter(private val logger: Logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass())) {

    @EventListener
    fun handle(event: CustomerEvent) = when(event) {
        is CustomerCreated -> logger.info("event:'CustomerCreated', customer-id:'${event.id}'")
    }
}