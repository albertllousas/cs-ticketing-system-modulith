package assignment.app

import assignment.domain.AIBasedTicketAssigner
import assignment.domain.Agent
import assignment.domain.AgentAssignments
import assignment.domain.AgentAssignmentsRepository
import assignment.domain.AgentFinder
import assignment.domain.AssignmentStrategy
import assignment.domain.AssignmentStrategy.AI_BASED
import assignment.domain.AssignmentStrategy.RULE_BASED
import assignment.domain.TicketFinder
import assignment.domain.TicketQueue
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import shared.EventPublisher
import shared.TicketQueued
import java.util.UUID

@Service
class AssignTicketService(
    private val ticketFinder: TicketFinder,
    private val agentFinder: AgentFinder,
    private val aiBasedTicketAssigner: AIBasedTicketAssigner,
    private val repository: AgentAssignmentsRepository,
    private val eventPublisher: EventPublisher
) {
    @Transactional
    operator fun invoke(ticketId: UUID, strategy: AssignmentStrategy) {
        val ticket = ticketFinder.find(ticketId)
        val availableAgents = agentFinder.findAvailable()
        val currentAssignments = repository.find(availableAgents.map(Agent::id))
        when (strategy) {
            AI_BASED -> aiBasedTicketAssigner.assign(ticket, availableAgents, currentAssignments)
            RULE_BASED -> AgentAssignments.assign(ticket, availableAgents, currentAssignments, maxTicketsPerAgent = 5)
        }.onRight { (ticketAssignedEvent, agentAssignments) ->
            repository.save(agentAssignments)
            eventPublisher.publish(ticketAssignedEvent)
        }.onLeft(eventPublisher::publish)
    }
}

@Service
class QueueTicketService(
    private val ticketFinder: TicketFinder,
    private val queue: TicketQueue,
    private val eventPublisher: EventPublisher
) {
    @Transactional
    operator fun invoke(ticketId: UUID) = ticketFinder.find(ticketId)
        .let { queue.add(ticketId, it.priority) }
        .also { eventPublisher.publish(TicketQueued(ticketId)) }
}
