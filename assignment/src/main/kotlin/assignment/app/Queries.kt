package assignment.app

import assignment.domain.AgentAssignments
import assignment.domain.AgentAssignmentsRepository
import org.springframework.stereotype.Service
import shared.AgentAssignmentsDto
import shared.ExposedInboundPorts
import java.util.UUID

@Service
class FindAssignmentsService(private val repository: AgentAssignmentsRepository) : ExposedInboundPorts.FindAssignments {

    override operator fun invoke(agentIds: List<UUID>)  = repository.find(agentIds).map { it.toDto() }

    override operator fun invoke(agentId: UUID) = repository.find(agentId)?.toDto()

    private fun AgentAssignments.toDto() = AgentAssignmentsDto(agentId, currentTickets.map { it.id })
}
