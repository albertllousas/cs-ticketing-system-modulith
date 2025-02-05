package assignment.infra.outbound

import assignment.domain.Ticket.Priority
import fixtures.containers.Postgres
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import java.util.UUID.randomUUID

@Tag("integration")
@TestInstance(PER_CLASS)
class TicketQueuePostgresAdapterTest {

    private val postgres = Postgres(
        flywayLocationSchemasList = listOf(Pair("db/migration/assignment", "assignment")),
    )

    private val repository = TicketQueuePostgresAdapter(postgres.jdbcTemplate)

    @Test
    fun `should queue a ticket`() {
        val ticketId = randomUUID()
        repository.add(ticketId, Priority.HIGH)

        val result = repository.dequeue(1)

        result shouldBe listOf(ticketId)
    }

    @Test
    fun `should dequeue a tickets by priority and date`() {
        val midPriority = randomUUID()
        val lowPriority = randomUUID()
        val firstHighPriority = randomUUID()
        val secondHighPriority = randomUUID()
        repository.add(midPriority, Priority.MEDIUM)
        repository.add(lowPriority, Priority.LOW)
        repository.add(firstHighPriority, Priority.HIGH)
        repository.add(secondHighPriority, Priority.HIGH)

        val result = repository.dequeue(10)

        result shouldBe listOf(secondHighPriority, firstHighPriority, midPriority, lowPriority)
    }
}