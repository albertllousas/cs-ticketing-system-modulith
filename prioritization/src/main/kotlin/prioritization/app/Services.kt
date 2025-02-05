package prioritization.app

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import prioritization.domain.PrioritizationStrategy.AI_BASED
import prioritization.domain.PrioritizationStrategy.RULE_BASED
import prioritization.domain.AITicketPrioritiser
import prioritization.domain.CustomerFinder
import prioritization.domain.PrioritizationStrategy
import prioritization.domain.TicketFinder
import prioritization.domain.TicketPriority
import prioritization.domain.TicketPriorityRepository
import shared.EventPublisher
import java.util.UUID

@Service
class PrioritiseTicketService(
    private val ticketFinder: TicketFinder,
    private val customerFinder: CustomerFinder,
    private val aiTicketPrioritiser: AITicketPrioritiser,
    private val repository: TicketPriorityRepository,
    private val eventPublisher: EventPublisher
) {

    @Transactional
    operator fun invoke(ticketId: UUID, strategy: PrioritizationStrategy) {
        ticketFinder.find(ticketId)
            .let { Pair(it, customerFinder.find(it.customerId)) }
            .let { (ticket, customer) ->
                when (strategy) {
                    AI_BASED -> aiTicketPrioritiser.prioritise(ticket, customer)
                    RULE_BASED -> TicketPriority.prioritise(ticket, customer)
                }
            }
            .let { TicketPriority.create(ticketId, it) }
            .let { (tickedPrioritized, priority) ->
                repository.save(priority)
                eventPublisher.publish(tickedPrioritized)
            }
    }
}
