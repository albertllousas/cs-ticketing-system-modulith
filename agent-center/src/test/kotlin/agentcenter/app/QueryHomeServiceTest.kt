package agentcenter.app

import agentcenter.domain.AgentRepository
import agentcenter.domain.AssignedTicket
import agentcenter.domain.AssignedTicketFinder
import agentcenter.domain.Home
import agentcenter.domain.Priority
import agentcenter.fixtures.TestBuilders
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import java.util.UUID

class QueryHomeServiceTest {

    private val repository = mockk<AgentRepository>()

    private val assignedTicketFinder = mockk<AssignedTicketFinder>()

    private val queryHome = QueryHomeService(repository, assignedTicketFinder)

    @Test
    fun `should get the home data for an agent`() {
        val agentId = UUID.randomUUID()
        val agent = TestBuilders.buildAgent(agentId)
        val assignedTicket = AssignedTicket(UUID.randomUUID(), Priority.HIGH, UUID.randomUUID(), "subject")
        every { repository.find(agentId) } returns agent
        every { assignedTicketFinder.find(agentId) } returns listOf(assignedTicket)

        val result = queryHome(agentId = agentId)

        result shouldBe Home(agent, listOf(assignedTicket))
    }
}
