package prioritization.acceptance

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import shared.TicketAssigned
import shared.TicketCreated
import shared.TicketPrioritized
import java.util.UUID

@Tag("acceptance")
class PrioritizeTicketAcceptanceTest : BaseAcceptanceTest() {

    @Test
    fun `should prioritise a ticket`() {
        val ticketCreated = TicketCreated(UUID.randomUUID(), "title", "description", UUID.randomUUID(), "LOW")

        eventPublisher.publish(ticketCreated)

        testEventListener.receivedEvents.map { it::class } shouldBe listOf(
            TicketPrioritized::class, TicketCreated::class
        )
    }
}
