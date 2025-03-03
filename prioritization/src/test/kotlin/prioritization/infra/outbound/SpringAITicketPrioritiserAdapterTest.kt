package prioritization.infra.outbound

import prioritization.fixtures.ai.FakeChatClient
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.springframework.core.io.ClassPathResource
import prioritization.domain.Customer
import prioritization.domain.Customer.Tier.PREMIUM
import prioritization.domain.Priority
import prioritization.domain.Ticket
import prioritization.domain.Ticket.Type.BUG
import java.util.UUID

class SpringAITicketPrioritiserAdapterTest {

    val fakeChatClient = FakeChatClient("HIGH")

    private val prioritizer = SpringAITicketPrioritiserAdapter(
        ticketPrioritiserChatClient = fakeChatClient,
        promptResource = ClassPathResource("prioritization_prompt.txt")
    )

    @Test
    fun `should prioritize ticket using the expected prompt`() {
        val prioritise = prioritizer.prioritise(
            ticket = Ticket(
                "Unable to access account",
                "I am not able to log into my account despite using the correct credentials.",
                UUID.randomUUID(),
                BUG
            ),
            customer = Customer(UUID.randomUUID(), PREMIUM)
        )
        prioritise shouldBe Priority.HIGH
        fakeChatClient.receivedPrompt shouldBe """
            You are an intelligent assistant that prioritizes customer support tickets based on their urgency and importance. Consider the following factors:
            1. Customer Tier: Customers are classified into tiers (BASIC, STANDARD, PREMIUM). Higher-tier customers are more important to prioritize.
            2. Impact: Assess the potential impact or urgency of the ticket based on its title and description.
               - Critical issues that block essential services should have higher priority.
               - Minor or non-urgent requests should have lower priority.
            3. Ticket description: The title and description provide context about the problem. Analyze them carefully.
            4. Ticket Type:
               - ISSUE: Problems preventing the customer from using the service normally.
               - BUG: Software defects that need to be fixed but may not block operations.
               - FEATURE: Feature requests or enhancements that are non-critical.
               - SUPPORT: General inquiries or troubleshooting requests.

            Ticket Information:
            - Customer Tier: PREMIUM
            - Title: Unable to access account
            - Description: I am not able to log into my account despite using the correct credentials.
            - Type: BUG

            Please, respond just with one word, and it should be one of the following values:  LOW, MEDIUM, HIGH
        """.trimIndent()
    }
}
