package tickets.infra.outbound

import tickets.fixtures.TestBuilders
import fixtures.containers.Postgres
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import shared.OptimisticLockingException
import java.util.UUID

@Tag("integration")
@TestInstance(PER_CLASS)
class TicketPostgresAdapterTest {

    private val postgres = Postgres(
        flywayLocationSchemasList = listOf(Pair("db/migration/tickets", "ticket_lifecycle")),
    )

    private val repository = TicketPostgresAdapter(postgres.jdbcTemplate)

    @Test
    fun `should save and find ticket`() {
        val ticket = TestBuilders.buildTicket()

        repository.save(ticket)
        val result = repository.find(ticket.id)

        result shouldBe ticket.copy(version = 1)
    }

    @Test
    fun `should not find a ticket when it does not exists`() {
        repository.find(UUID.randomUUID()) shouldBe null
    }

    @Test
    fun `should update ticket`() {
        val ticket = TestBuilders.buildTicket().also { repository.save(it) }.let { repository.find(it.id)!! }
        val updatedTicket = ticket.copy(title = "New title", description = "New description")

        repository.save(updatedTicket)
        val result = repository.find(ticket.id)

        result shouldBe updatedTicket.copy(version = 2)
    }

    @Test
    fun `should throw OptimisticLockingException when updating ticket with wrong version`() {
        val ticket = TestBuilders.buildTicket().also { repository.save(it) }.let { repository.find(it.id)!! }
        val updatedTicket = ticket.copy(title = "New title", description = "New description", version = 3)

        shouldThrow<OptimisticLockingException> { repository.save(updatedTicket) }
    }
}
