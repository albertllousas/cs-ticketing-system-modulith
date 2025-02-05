package assignment.infra.outbound

import assignment.domain.AgentAssignments
import assignment.domain.AssignedTicket
import assignment.domain.Ticket.Status.IN_PROGRESS
import assignment.domain.Ticket.Status.ON_HOLD
import fixtures.containers.Postgres
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import shared.OptimisticLockingException
import java.time.LocalDateTime.now
import java.util.UUID.randomUUID


@Tag("integration")
@TestInstance(PER_CLASS)
class AgentAssignmentsPostgresAdapterTest {

    private val postgres = Postgres(
        flywayLocationSchemasList = listOf(Pair("db/migration/assignment", "assignment")),
    )

    private val repository = AgentAssignmentsPostgresAdapter(postgres.jdbcTemplate)

    @Test
    fun `should save and find assignments for an agent`() {
        val agentAssignments = AgentAssignments(
            agentId = randomUUID(),
            currentTickets = listOf(
                AssignedTicket(randomUUID(), IN_PROGRESS, now()),
                AssignedTicket(randomUUID(), ON_HOLD, now()),
            ),
            version = 0L
        )

        repository.save(agentAssignments)
        val result = repository.find(agentAssignments.agentId)

        result shouldBe agentAssignments.copy(version = 1)
    }

    @Test
    fun `should find empty assignments for an agent when they do not exists`() {
        val agentId = randomUUID()
        repository.find(agentId) shouldBe AgentAssignments(agentId, emptyList(), 0)
    }

    @Test
    fun `should update assignments for an agent`() {
        val agentAssignments = AgentAssignments(
            agentId = randomUUID(),
            currentTickets = listOf(
                AssignedTicket(randomUUID(), IN_PROGRESS, now()),
                AssignedTicket(randomUUID(), ON_HOLD, now()),
            ),
            version = 0L
        )
            .also(repository::save)
            .let { repository.find(it.agentId)!! }

        val newTicket = AssignedTicket(randomUUID(), ON_HOLD, now())
        val updatedAgentAssignments =
            agentAssignments.copy(currentTickets = agentAssignments.currentTickets + newTicket)

        repository.save(updatedAgentAssignments)
        val result = repository.find(updatedAgentAssignments.agentId)

        result shouldBe updatedAgentAssignments.copy(version = 2)
    }

    @Test
    fun `should throw OptimisticLockingException when updating a customer with wrong version`() {
        val agentAssignments = AgentAssignments(
            agentId = randomUUID(),
            currentTickets = listOf(
                AssignedTicket(randomUUID(), IN_PROGRESS, now()),
                AssignedTicket(randomUUID(), ON_HOLD, now()),
            ),
            version = 0L
        )
            .also(repository::save)
            .let { repository.find(it.agentId)!! }
        val updatedAgentAssignments = agentAssignments.copy(version = 3)

        shouldThrow<OptimisticLockingException> { repository.save(updatedAgentAssignments) }
    }

    @Test
    fun `should find assignments for a list of agents`() {
        val firstAgentAssignments = AgentAssignments(
            agentId = randomUUID(),
            currentTickets = listOf(
                AssignedTicket(randomUUID(), IN_PROGRESS, now()),
                AssignedTicket(randomUUID(), ON_HOLD, now()),
            ),
            version = 0L
        ).also(repository::save)
        val secondAgentAssignments = AgentAssignments(
            agentId = randomUUID(),
            currentTickets = listOf(
                AssignedTicket(randomUUID(), IN_PROGRESS, now()),
                AssignedTicket(randomUUID(), ON_HOLD, now()),
            ),
            version = 0L
        ).also(repository::save)

        val result = repository.find(listOf(firstAgentAssignments.agentId, secondAgentAssignments.agentId))

        result shouldBe listOf(firstAgentAssignments.copy(version = 1), secondAgentAssignments.copy(version = 1))
    }
}
