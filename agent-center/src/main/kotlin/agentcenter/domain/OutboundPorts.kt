package agentcenter.domain

import java.util.UUID

interface AgentRepository {
    fun findAvailable(): List<Agent>
    fun save(agent: Agent)
    fun find(agentId: UUID): Agent?
}

interface AssignedTicketFinder {
    fun find(agentId: UUID): List<AssignedTicket>
}