package prioritization.app

import org.springframework.stereotype.Service
import prioritization.domain.TicketPriority
import prioritization.domain.TicketPriorityRepository
import shared.ExposedInboundPorts
import shared.TicketPriorityDto
import java.util.UUID

@Service
class FindTicketPriorityService(private val repository: TicketPriorityRepository) : ExposedInboundPorts.FindTicketPriority {

    override fun invoke(ticketId: UUID): TicketPriorityDto? = repository.find(ticketId)?.toDto()

    private fun TicketPriority.toDto() =
        TicketPriorityDto(id, ticketId, TicketPriorityDto.Priority.valueOf(priority.name))
}