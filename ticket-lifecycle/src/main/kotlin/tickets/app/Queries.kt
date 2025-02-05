package tickets.app

import org.springframework.stereotype.Service
import shared.ExposedInboundPorts
import shared.TicketDto
import tickets.domain.Ticket
import tickets.domain.TicketRepository
import java.util.UUID

@Service
class FindTicketService(private val repository: TicketRepository) : ExposedInboundPorts.FindTicket {

    override operator fun invoke(id: UUID): TicketDto? = repository.find(id)?.toDto()

    private fun Ticket.toDto() = TicketDto(
        id, title, description, customerId, TicketDto.Status.valueOf(status.name), TicketDto.Type.valueOf(type.name)
    )
}