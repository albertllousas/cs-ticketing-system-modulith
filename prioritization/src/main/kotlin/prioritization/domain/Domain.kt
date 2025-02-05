package prioritization.domain

import prioritization.domain.Customer.Tier.BASIC
import prioritization.domain.Customer.Tier.PREMIUM
import prioritization.domain.Customer.Tier.STANDARD
import prioritization.domain.Ticket.Type.BUG
import prioritization.domain.Ticket.Type.FEATURE
import prioritization.domain.Ticket.Type.ISSUE
import prioritization.domain.Ticket.Type.SUPPORT
import shared.TicketPrioritized
import java.util.UUID

enum class PrioritizationStrategy { AI_BASED, RULE_BASED }

enum class Priority { LOW, MEDIUM, HIGH }

data class TicketPriority(val id: UUID, val ticketId: UUID, val priority: Priority, val version: Long) {
    companion object {

        private val GEN_ID: () -> UUID = { UUID.randomUUID() }

        // could be parameterized
        private val keywords = mapOf(
            "access" to 100, "login" to 100, "crash" to 90, "error" to 80, "failure" to 80, "timeout" to 70,
            "password" to 60, "security" to 60, "slow" to 50, "request" to 40, "support" to 30, "feature" to 20,
            "help" to 20, "question" to 10
        )

        fun prioritise(ticket: Ticket, customer: Customer): Priority {
            val priority = when (customer.tier) {
                BASIC -> 20
                STANDARD -> 50
                PREMIUM -> 100
            } + when (ticket.type) {
                ISSUE -> 100
                BUG -> 50
                FEATURE -> 20
                SUPPORT -> 10
            } + keywords.entries.sumOf { (word, weight) ->
                if (ticket.title.contains(word, ignoreCase = true)
                    || ticket.description.contains(word, ignoreCase = true)
                ) weight
                else 0
            }
            return when (priority) {
                in 0..150 -> Priority.LOW
                in 151..250 -> Priority.MEDIUM
                else -> Priority.HIGH
            }
        }

        fun create(
            ticketId: UUID,
            priority: Priority,
            generateId: () -> UUID = GEN_ID
        ): Pair<TicketPrioritized, TicketPriority> {
            val newId = generateId()
            return Pair(
                TicketPrioritized(newId, ticketId, priority.name),
                TicketPriority(UUID.randomUUID(), ticketId, priority, 0)
            )
        }
    }
}

data class Customer(val id: UUID, val tier: Tier) {
    enum class Tier { BASIC, STANDARD, PREMIUM }
}

data class Ticket(val title: String, val description: String, val customerId: UUID, val type: Type) {
    enum class Type { ISSUE, BUG, FEATURE, SUPPORT }
}
