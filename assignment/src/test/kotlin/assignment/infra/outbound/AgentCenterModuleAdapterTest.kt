package assignment.infra.outbound

import assignment.domain.Agent
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import shared.AgentDto
import shared.ExposedInboundPorts
import java.util.Locale
import java.util.UUID

class AgentCenterModuleAdapterTest {

    private val findAvailableAgents = mockk<ExposedInboundPorts.FindAvailableAgents>()

    private val agentCenterModuleAdapter = AgentCenterModuleAdapter(findAvailableAgents)

    @Test
    fun `should find available agents`() {
        val agent = AgentDto(
            UUID.randomUUID(),
            "patrick.lane@gmail.com",
            "Patrick Lane",
            listOf("CS"),
            listOf(Locale.ENGLISH),
            AgentDto.Status.ONLINE
        )
        val agents = listOf(agent)
        every { findAvailableAgents() } returns agents

        val result = agentCenterModuleAdapter.findAvailable()

        result shouldBe listOf(Agent(agent.id, agent.skills, agent.languages))
    }
}
