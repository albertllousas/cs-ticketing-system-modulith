package agentcenter.domain

import agentcenter.domain.AgentStatus.OFFLINE
import agentcenter.domain.AgentStatus.ONLINE
import agentcenter.domain.CreateAgentError.CreateAgentErrorReason.INVALID_LANGUAGE
import agentcenter.domain.SetAvailabilityError.SetAvailabilityErrorReason.NO_CHANGES
import arrow.core.Either
import arrow.core.left
import arrow.core.right
import shared.AgentConnected
import shared.AgentCreated
import shared.AgentDisconnected
import java.util.Locale
import java.util.UUID

enum class AgentStatus { ONLINE, OFFLINE }

data class Agent(
    val id: UUID,
    val email: String,
    val fullName: String,
    val skills: List<String>,
    val languages: List<Locale>,
    val status: AgentStatus,
    val version: Long
) {
    fun connect(): Either<SetAvailabilityError, Pair<AgentConnected, Agent>> =
        if (status == ONLINE) SetAvailabilityError(NO_CHANGES).left()
        else Pair(AgentConnected(id), copy(status = ONLINE)).right()

    fun disconnect(): Either<SetAvailabilityError, Pair<AgentDisconnected, Agent>> =
        if (status == OFFLINE) SetAvailabilityError(NO_CHANGES).left()
        else Pair(AgentDisconnected(id), copy(status = OFFLINE)).right()

    companion object {
        private val GEN_ID: () -> UUID = { UUID.randomUUID() }

        fun create(
            email: String,
            fullName: String,
            skills: List<String>,
            languages: List<Locale>,
            validLanguages: List<Locale> = emptyList(),
            generateId: () -> UUID = GEN_ID
        ): Either<CreateAgentError, Pair<AgentCreated, Agent>> {
            if (!validLanguages.containsAll(languages)) return CreateAgentError(INVALID_LANGUAGE).left()
            val id = generateId()
            return Pair(
                AgentCreated(id, email, fullName, skills, languages),
                Agent(id, email, fullName, skills, languages, OFFLINE, 0)
            ).right()
        }
    }
}

data class Home(val agent: Agent, val assignments: List<AssignedTicket>)

data class AssignedTicket(val ticketId: UUID, val priority: Priority, val customer: UUID, val subject: String)

enum class Priority { LOW, MEDIUM, HIGH }

data class CreateAgentError(val reason: CreateAgentErrorReason) {
    enum class CreateAgentErrorReason { INVALID_LANGUAGE }
}

data class SetAvailabilityError(val reason: SetAvailabilityErrorReason) {
    enum class SetAvailabilityErrorReason { AGENT_NOT_FOUND, NO_CHANGES }
}
