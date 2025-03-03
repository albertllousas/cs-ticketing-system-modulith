package prioritization.infra.outbound

import prioritization.fixtures.TestBuilders
import fixtures.containers.Postgres
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import prioritization.domain.Priority
import shared.OptimisticLockingException
import java.util.UUID


@Tag("integration")
@TestInstance(PER_CLASS)
class TicketPrioritiesPostgresAdapterTest {

    private val postgres = Postgres(
        flywayLocationSchemasList = listOf(Pair("db/migration/prioritization", "prioritization")),
    )

    private val repository = TicketPrioritiesPostgresAdapter(postgres.jdbcTemplate)

    @Test
    fun `should save and find ticket priority`() {
        val ticketPriority = TestBuilders.buildTicketPriority()

        repository.save(ticketPriority)
        val result = repository.find(ticketPriority.ticketId)

        result shouldBe ticketPriority.copy(version = 1)
    }

    @Test
    fun `should not find a ticket when it does not exists`() {
        repository.find(UUID.randomUUID()) shouldBe null
    }

    @Test
    fun `should update ticket`() {
        val ticketPriority = TestBuilders.buildTicketPriority()
            .also(repository::save)
            .let { repository.find(it.ticketId)!! }
        val updatedTicket = ticketPriority.copy(priority = Priority.HIGH, version = 1)

        repository.save(updatedTicket)
        val result = repository.find(ticketPriority.ticketId)

        result shouldBe updatedTicket.copy(version = 2)
    }

    @Test
    fun `should throw OptimisticLockingException when updating ticket priority with wrong version`() {
        val ticket = TestBuilders.buildTicketPriority()
            .also(repository::save)
            .let { repository.find(it.ticketId)!! }
        val updatedTicket = ticket.copy(version = 3)

        shouldThrow<OptimisticLockingException> { repository.save(updatedTicket) }
    }
}
