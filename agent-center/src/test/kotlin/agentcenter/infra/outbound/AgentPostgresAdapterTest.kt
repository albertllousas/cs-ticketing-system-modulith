package agentcenter.infra.outbound

import agentcenter.fixtures.TestBuilders
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
class AgentPostgresAdapterTest {

    private val postgres = Postgres(
        flywayLocationSchemasList = listOf(Pair("db/migration/agent_center", "agent_center")),
    )

    private val repository = AgentPostgresAdapter(postgres.jdbcTemplate)

    @Test
    fun `should save and find a customer`() {
        val agent = TestBuilders.buildAgent()

        repository.save(agent)
        val result = repository.find(agent.id)

        result shouldBe agent.copy(version = 1)
    }

    @Test
    fun `should not find an agent when it does not exists`() {
        repository.find(UUID.randomUUID()) shouldBe null
    }

    @Test
    fun `should update an agent`() {
        val agent = TestBuilders.buildAgent()
            .also(repository::save)
            .let { repository.find(it.id)!! }
        val updatedAgent = agent.copy(fullName = "John Doe", version = 1)

        repository.save(updatedAgent)
        val result = repository.find(agent.id)

        result shouldBe updatedAgent.copy(version = 2)
    }

    @Test
    fun `should throw OptimisticLockingException when updating a customer with wrong version`() {
        val agent = TestBuilders.buildAgent()
            .also(repository::save)
            .let { repository.find(it.id)!! }
        val updatedAgent = agent.copy(version = 3)

        shouldThrow<OptimisticLockingException> { repository.save(updatedAgent) }
    }
}
