package assignment.acceptance

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import shared.TicketAssigned
import shared.TicketPrioritized
import java.util.UUID

@Tag("acceptance")
class AssignTicketToAgentAcceptanceTest : BaseAcceptanceTest() {

    @Test
    fun `should assign a prioritized ticket`() {
        val ticketPrioritized = TicketPrioritized(UUID.randomUUID(), UUID.randomUUID(), "HIGH")

        eventPublisher.publish(ticketPrioritized)

        testEventListener.receivedEvents.map { it::class } shouldBe listOf(
            TicketAssigned::class, TicketPrioritized::class
        )
    }
}
