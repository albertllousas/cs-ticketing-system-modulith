package prioritization.infra.inbound

import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import prioritization.app.PrioritiseTicketService
import prioritization.domain.PrioritizationStrategy
import shared.TicketCreated

@Component
class InMemoryEventsConsumerAdapter(
    private val prioritiseTicketService: PrioritiseTicketService,
    private val strategy: PrioritizationStrategy
) {

    @EventListener
    fun listenTo(event: TicketCreated) = prioritiseTicketService(event.id, strategy)
    // errors handle, like twice prioritisation, etc
}
