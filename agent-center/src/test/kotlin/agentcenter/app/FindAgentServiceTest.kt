package agentcenter.app

import agentcenter.domain.AgentRepository
import agentcenter.infra.outbound.fixtures.TestBuilders
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import shared.AgentDto

class FindAgentServiceTest {

    private val repository = mockk<AgentRepository>(relaxed = true)

    private val findAgentService = FindAgentService(repository)

    @Test
    fun `should find an agent`() {
        val agent = TestBuilders.buildAgent()
        every { repository.find(agent.id) } returns agent

        val result = findAgentService(agent.id)

        result shouldBe AgentDto(
            agent.id,
            agent.email,
            agent.fullName,
            agent.skills,
            agent.languages,
            AgentDto.Status.valueOf(agent.status.name)
        )
    }
}