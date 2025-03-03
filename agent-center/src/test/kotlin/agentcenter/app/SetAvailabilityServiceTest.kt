package agentcenter.app

import agentcenter.domain.AgentRepository
import agentcenter.domain.AgentStatus.OFFLINE
import agentcenter.domain.SetAvailabilityError
import agentcenter.domain.SetAvailabilityError.SetAvailabilityErrorReason.AGENT_NOT_FOUND
import agentcenter.fixtures.TestBuilders
import arrow.core.left
import arrow.core.right
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import shared.AgentConnected
import shared.EventPublisher
import java.util.UUID

class SetAvailabilityServiceTest {

    private val repository = mockk<AgentRepository>(relaxed = true)

    private val eventPublisher = mockk<EventPublisher>(relaxed = true)

    private val setAvailability = SetAvailabilityService(repository, eventPublisher)

    @Test
    fun `should orchestrate the setting of availability of an agent`() {
        val agentId = UUID.randomUUID()
        val agent = TestBuilders.buildAgent(agentId, status = OFFLINE)
        every { repository.find(agentId) } returns agent

        val result = setAvailability(agentId, connected = true)

        result shouldBe Unit.right()
        verify {
            repository.save(any())
            eventPublisher.publish(any<AgentConnected>())
        }
    }

    @Test
    fun `should fail orchestrating the setting of availability of an agent if agent not found`() {
        val agentId = UUID.randomUUID()
        every { repository.find(agentId) } returns null

        val result = setAvailability(agentId, connected = true)

        result shouldBe SetAvailabilityError(AGENT_NOT_FOUND).left()
        verify(exactly = 0) {
            repository.save(any())
            eventPublisher.publish(any())
        }
    }
}