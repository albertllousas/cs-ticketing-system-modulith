package agentcenter.app

import agentcenter.domain.Agent
import agentcenter.domain.AgentRepository
import agentcenter.domain.CreateAgentError
import agentcenter.domain.SetAvailabilityError
import agentcenter.domain.SetAvailabilityError.SetAvailabilityErrorReason.AGENT_NOT_FOUND
import arrow.core.Either
import arrow.core.left
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import shared.EventPublisher
import java.util.Locale
import java.util.UUID

@Service
class CreateAgentService(
    private val repository: AgentRepository,
    private val eventPublisher: EventPublisher,
    @Value("\${modules.agent-center.valid-languages}")
    private val validLanguages: List<Locale>
) {
    @Transactional
    operator fun invoke(
        email: String,
        fullName: String,
        skills: List<String>,
        languages: List<Locale>
    ): Either<CreateAgentError, UUID> =
        Agent.create(email, fullName, skills, languages, validLanguages)
            .onRight { (agentCreated, agent) ->
                repository.save(agent)
                eventPublisher.publish(agentCreated)
            }.map { it.second.id }
}

@Service
class SetAvailabilityService(private val repository: AgentRepository, private val eventPublisher: EventPublisher) {
    @Transactional
    operator fun invoke(agentId: UUID, connected: Boolean): Either<SetAvailabilityError, Unit> =
        repository.find(agentId)
            ?.let { if (connected) it.connect() else it.disconnect() }
            ?.map { (event, agent) ->
                repository.save(agent)
                eventPublisher.publish(event)
            } ?: SetAvailabilityError(AGENT_NOT_FOUND).left()
}

