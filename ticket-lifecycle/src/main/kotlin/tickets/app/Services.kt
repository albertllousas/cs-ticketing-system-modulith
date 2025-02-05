package tickets.app

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import shared.EventPublisher
import tickets.domain.Ticket
import tickets.domain.TicketRepository
import tickets.domain.TicketType
import java.util.UUID

@Service
class CreateTicketService(private val repository: TicketRepository, private val eventPublisher: EventPublisher) {

    @Transactional
    operator fun invoke(title: String, description: String, customerId: UUID, type: TicketType): UUID =
        Ticket.newTicket(title, description, customerId, type)
            .also { repository.save(it.second) }
            .also { eventPublisher.publish(it.first) }
            .second.id
}

