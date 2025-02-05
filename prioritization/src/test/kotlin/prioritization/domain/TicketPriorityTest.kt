package prioritization.domain

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import prioritization.domain.Customer.Tier.BASIC
import prioritization.domain.Customer.Tier.PREMIUM
import prioritization.domain.Customer.Tier.STANDARD
import prioritization.domain.Priority.HIGH
import prioritization.domain.Priority.LOW
import prioritization.domain.Priority.MEDIUM
import prioritization.domain.Ticket.Type.FEATURE
import prioritization.domain.Ticket.Type.ISSUE
import shared.TicketPrioritized
import java.util.UUID

class TicketPriorityTest {

    @TestFactory
    fun `should prioritise tickets`() =
        listOf(
            Triple(
                Ticket("Account question", "I have a question regarding my account", UUID.randomUUID(), ISSUE),
                Customer(UUID.randomUUID(), BASIC),
                LOW
            ),
            Triple(
                Ticket("App slow", "The main page takes time to load", UUID.randomUUID(), ISSUE),
                Customer(UUID.randomUUID(), STANDARD),
                MEDIUM
            ),
            Triple(
                Ticket("Crash", "The app crashes when I open it", UUID.randomUUID(), ISSUE),
                Customer(UUID.randomUUID(), PREMIUM),
                HIGH
            ),
            Triple(
                Ticket("Error", "I get an error when I try to login", UUID.randomUUID(), ISSUE),
                Customer(UUID.randomUUID(), BASIC),
                HIGH
            ),
            Triple(
                Ticket("Feature request", "I would like to have a new feature", UUID.randomUUID(), FEATURE),
                Customer(UUID.randomUUID(), STANDARD),
                LOW
            ),
            Triple(
                Ticket("Timeout", "Home page is timing out", UUID.randomUUID(), ISSUE),
                Customer(UUID.randomUUID(), STANDARD),
                MEDIUM
            ),
        ).map { (ticket, customer, expectedPriority) ->
            dynamicTest("should prioritise $ticket for $customer as $expectedPriority") {
                val priority = TicketPriority.prioritise(ticket, customer)
                priority shouldBe expectedPriority
            }
        }

    @Test
    fun `should create ticket priority`() {
        val ticketId = UUID.randomUUID()
        val priority = HIGH
        val (event, ticketPriority) = TicketPriority.create(ticketId, priority)

        event shouldBe TicketPrioritized(event.id, ticketId, priority.name)
        ticketPriority shouldBe TicketPriority(ticketPriority.id, ticketId, priority, 0)
    }
}
