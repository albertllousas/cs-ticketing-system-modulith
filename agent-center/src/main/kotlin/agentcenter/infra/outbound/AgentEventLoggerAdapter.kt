package agentcenter.infra.outbound

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import shared.AgentConnected
import shared.AgentCreated
import shared.AgentDisconnected
import shared.AgentEvent
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
class AgentEventLoggerAdapter(private val logger: Logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass())) {

    @EventListener
    fun handle(event: AgentEvent) = when(event) {
        is AgentConnected -> logger.info("event:'AgentConnected', agent-id:'${event.id}'")
        is AgentCreated -> logger.info("event:'AgentCreated', agent-id:'${event.id}'")
        is AgentDisconnected -> logger.info("event:'AgentDisconnected', agent-id:'${event.id}'")
    }
}
