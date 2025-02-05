package assignment.infra.outbound

import arrow.core.left
import arrow.core.right
import assignment.domain.Agent
import assignment.domain.AgentAssignments
import assignment.domain.AssignedTicket
import assignment.domain.Customer
import assignment.domain.Customer.Tier.PREMIUM
import assignment.domain.Ticket
import assignment.domain.Ticket.Priority.*
import assignment.domain.Ticket.Status
import assignment.domain.Ticket.Status.*
import fixtures.ai.FakeChatClient
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldMatch
import org.junit.jupiter.api.Test
import org.springframework.core.io.ClassPathResource
import shared.AssignmentAttemptFailed
import shared.AssignmentAttemptFailed.Reason.NO_SUITABLE_AGENT_AVAILABLE
import shared.TicketAssigned
import java.time.LocalDateTime.now
import java.util.Locale
import java.util.Locale.*
import java.util.UUID
import java.util.UUID.*

class SpringAITicketAssignerAdapterTest {

    val fakeChatClient = FakeChatClient("")

    private val assigner = SpringAITicketAssignerAdapter(
        ticketAssignerChatClient = fakeChatClient,
        promptResource = ClassPathResource("assignment_prompt.txt")
    )

    @Test
    fun `should prioritize ticket using the expected prompt`() {
        val llmAnswer = randomUUID().toString()
        val agentId = fromString("c24099c8-6a20-4e99-a838-0534f763ba96")
        val ticket = Ticket(
            agentId,
            "Unable to access account",
            "I am not able to log into my account despite using the correct credentials.",
            Customer(randomUUID(), ENGLISH, PREMIUM),
            HIGH,
            OPEN
        )
        val agent = Agent(fromString("9681a079-d2bf-4275-b2b3-21be98fff67c"), listOf("Accounting"), listOf(ENGLISH, JAPANESE))
        val assignedTicket = AssignedTicket(agentId, OPEN, now())
        val agentAssignments = listOf(AgentAssignments(agent.id, listOf(assignedTicket), 1))
        fakeChatClient.fixedAnswer = llmAnswer

        val result = assigner.assign(
            ticket = ticket,
            agents = listOf(agent),
            currentAssignments = agentAssignments
        )

        result shouldBe  Pair(
            TicketAssigned(ticket.id, agentId),
            AgentAssignments(agentId, listOf(assignedTicket), 1)

        ).right()
        fakeChatClient.receivedPrompt shouldBe """
You are an intelligent ticket assignment system. Your task is to assign the following ticket to the most suitable agent based on their skills, language capabilities, and current workload.

Ticket Details:
- Title: Unable to access account
- Description: I am not able to log into my account despite using the correct credentials.
- Priority: HIGH
- Customer Language: en

Available Agents:
{agent_id=9681a079-d2bf-4275-b2b3-21be98fff67c, langs=[en, ja], skills=[Accounting]}

Current Assignments:
{agent_id=9681a079-d2bf-4275-b2b3-21be98fff67c, assigned_tickets=[{ticket_id=c24099c8-6a20-4e99-a838-0534f763ba96, status=OPEN}]}

Please analyze the ticket details and match it with the agent who has the required skills and language abilities, while balancing workload.

Please, respond just with the id of the agent or blank if no agent is suitable for this ticket.""".trimIndent()
    }

    @Test
    fun `should fail prioritizing when the llms is not able to assign an agent`() {
        val agentId = fromString("c24099c8-6a20-4e99-a838-0534f763ba96")
        val ticket = Ticket(agentId, "title", "description", Customer(randomUUID(), ENGLISH, PREMIUM), HIGH, OPEN)
        val agent = Agent(fromString("9681a079-d2bf-4275-b2b3-21be98fff67c"), listOf("Accounting"), listOf(ENGLISH, JAPANESE))
        val assignedTicket = AssignedTicket(agentId, OPEN, now())
        val agentAssignments = listOf(AgentAssignments(agent.id, listOf(assignedTicket), 1))
        fakeChatClient.fixedAnswer = ""

        val result = assigner.assign(ticket = ticket, agents = listOf(agent), currentAssignments = agentAssignments)

        result shouldBe AssignmentAttemptFailed(ticket.id, NO_SUITABLE_AGENT_AVAILABLE).left()
    }
}
