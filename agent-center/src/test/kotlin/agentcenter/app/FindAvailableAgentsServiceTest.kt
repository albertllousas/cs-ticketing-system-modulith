package agentcenter.app

import agentcenter.domain.AgentRepository
import agentcenter.infra.outbound.fixtures.TestBuilders
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import shared.AgentDto

class FindAvailableAgentsServiceTest {

    private val repository = mockk<AgentRepository>(relaxed = true)

    private val findAvailableAgentsService = FindAvailableAgentsService(repository)

    @Test
    fun `should find available agents`() {
        val agent = TestBuilders.buildAgent()
        every { repository.findAvailable() } returns listOf(agent)

        val result = findAvailableAgentsService()

        result shouldBe listOf(
            AgentDto(
                agent.id,
                agent.email,
                agent.fullName,
                agent.skills,
                agent.languages,
                AgentDto.Status.valueOf(agent.status.name)
            )
        )
    }
}