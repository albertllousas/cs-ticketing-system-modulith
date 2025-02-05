package agentcenter.app

import agentcenter.domain.Agent
import agentcenter.domain.AgentRepository
import agentcenter.domain.AssignedTicketFinder
import agentcenter.domain.Home
import org.springframework.stereotype.Service
import shared.AgentDto
import shared.ExposedInboundPorts
import java.util.UUID

@Service
class FindAvailableAgentsService(private val repository: AgentRepository) : ExposedInboundPorts.FindAvailableAgents {

    override operator fun invoke() = repository.findAvailable().map { it.toDto() }
}

@Service
class FindAgentService(private val repository: AgentRepository) : ExposedInboundPorts.FindAgent {

    override operator fun invoke(id: UUID) = repository.find(id)?.toDto()
}

private fun Agent.toDto() = AgentDto(id, email, fullName, skills, languages, AgentDto.Status.valueOf(status.name))

@Service
class QueryHomeService(private val repository: AgentRepository, private val assignedTicketFinder: AssignedTicketFinder) {

    operator fun invoke(agentId: UUID): Home? =
        repository.find(agentId)?.let { Home(it, assignedTicketFinder.find(agentId)) }
}
