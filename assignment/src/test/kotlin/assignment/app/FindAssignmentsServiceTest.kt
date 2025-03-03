package assignment.app

import assignment.domain.AgentAssignmentsRepository
import assignment.fixtures.TestBuilders
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import shared.AgentAssignmentsDto
import java.util.UUID

class FindAssignmentsServiceTest {

    private val repository = mockk<AgentAssignmentsRepository>()

    private val findAssignments = FindAssignmentsService(repository)

    @Test
    fun `should find assignments for an agent`() {
        val agentId = UUID.randomUUID()
        val agentAssignments = TestBuilders.buildAgentAssignments(agentId = agentId)
        every { repository.find(agentId) } returns agentAssignments

        findAssignments(agentId) shouldBe AgentAssignmentsDto(
            agentId = agentId,
            currentTickets = agentAssignments.currentTickets.map { it.id }
        )
    }

    @Test
    fun `should find assignments for a multiple agents`() {
        val fstAssignments = TestBuilders.buildAgentAssignments()
        val sndAssignments = TestBuilders.buildAgentAssignments()
        every { repository.find(any<List<UUID>>()) } returns listOf(fstAssignments, sndAssignments)

        findAssignments(listOf(UUID.randomUUID(), UUID.randomUUID())) shouldBe listOf(
            AgentAssignmentsDto(fstAssignments.agentId, fstAssignments.currentTickets.map { it.id }),
            AgentAssignmentsDto(sndAssignments.agentId, sndAssignments.currentTickets.map { it.id }),
        )
    }
}