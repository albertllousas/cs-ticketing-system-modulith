package prioritization.infra.outbound

import org.springframework.ai.chat.client.ChatClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.springframework.stereotype.Repository
import prioritization.domain.AITicketPrioritiser
import prioritization.domain.Customer
import prioritization.domain.Priority
import prioritization.domain.Ticket

//https://www.danvega.dev/blog/spring-ai-multiple-llms

@Repository
class SpringAITicketPrioritiserAdapter(
    private val ticketPrioritiserChatClient: ChatClient,
    @Value("classpath:prioritization_prompt.txt") promptResource: Resource
) : AITicketPrioritiser {

    private val prompt = promptResource.inputStream.bufferedReader().readText()

    override fun prioritise(ticket: Ticket, customer: Customer): Priority {
        val prompt = promptForPriority(ticket, customer)
        return ticketPrioritiserChatClient.prompt(prompt).call().content().let(Priority::valueOf)
    }

    private fun sanitise(text: String) = text //TODO: properly done and placed

    private fun promptForPriority(ticket: Ticket, customer: Customer): String =
        prompt.replace("{ticket_title}", sanitise(ticket.title))
            .replace("{ticket_description}", sanitise(ticket.description))
            .replace("{ticket_type}", ticket.type.name)
            .replace("{customer_tier}", customer.tier.name)
            .replace("{priority_values}", Priority.entries.joinToString(", ") { it.name })
            .replace("{tier_values}", Customer.Tier.entries.joinToString(", ") { it.name })
}
