package assignment.infra.outbound

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import assignment.domain.AIBasedTicketAssigner
import assignment.domain.Agent
import assignment.domain.AgentAssignments
import assignment.domain.Ticket
import org.springframework.ai.chat.client.ChatClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.springframework.stereotype.Repository
import shared.AssignmentAttemptFailed
import shared.AssignmentAttemptFailed.Reason.*
import shared.TicketAssigned
import java.time.Clock
import java.time.LocalDateTime.now
import java.util.UUID

@Repository
class SpringAITicketAssignerAdapter(
    private val ticketAssignerChatClient: ChatClient,
    @Value("classpath:assignment_prompt.txt") promptResource: Resource,
    private val clock: Clock = Clock.systemUTC()
) : AIBasedTicketAssigner {

    private val prompt = promptResource.inputStream.bufferedReader().readText()

    override fun assign(
        ticket: Ticket,
        agents: List<Agent>,
        currentAssignments: List<AgentAssignments>
    ): Either<AssignmentAttemptFailed, Pair<TicketAssigned, AgentAssignments>> {
        val prompt = promptForPriority(ticket, agents, currentAssignments)
        val content = ticketAssignerChatClient.prompt(prompt).call().content()
        return if (content.isNullOrBlank()) AssignmentAttemptFailed(ticket.id, NO_SUITABLE_AGENT_AVAILABLE).left()
        else {
            val assignedAgentId = UUID.fromString(content)
            Pair(
                TicketAssigned(ticket.id, assignedAgentId),
                currentAssignments.find { it.agentId == assignedAgentId }
                    ?: AgentAssignments.create(assignedAgentId)
                    .add(ticket.id, ticket.status, now(clock))
            ).right()
        }
    }

    private fun sanitise(text: String) = text //TODO: properly done and placed

    private fun promptForPriority(
        ticket: Ticket,
        agents: List<Agent>,
        currentAssignments: List<AgentAssignments>
    ): String = prompt.replace("{ticket_title}", sanitise(ticket.title))
        .replace("{ticket_description}", sanitise(ticket.description))
        .replace("{ticket_priority}", ticket.priority.name)
        .replace("{customer_language}", ticket.customer.lang.language)
        .replace(
            "{available_agents}", agents.map {
                mapOf(
                    "agent_id" to it.id,
                    "langs" to it.languages,
                    "skills" to it.skills
                )
            }.joinToString(", ")
        )
        .replace(
            "{current_assignments}", currentAssignments.map {
                mapOf(
                    "agent_id" to it.agentId,
                    "assigned_tickets" to it.currentTickets.map {
                        mapOf("ticket_id" to it.id, "status" to it.status.name)
                    }
                )
            }.joinToString(", ")
        )
}
