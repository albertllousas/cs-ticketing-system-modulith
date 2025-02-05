package agentcenter.app

import agentcenter.domain.AgentRepository
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import shared.AgentCreated
import shared.EventPublisher
import java.util.Locale.*

class CreateAgentServiceTest {

    private val repository = mockk<AgentRepository>(relaxed = true)

    private val eventPublisher = mockk<EventPublisher>(relaxed = true)

    private val validLanguages = listOf(ENGLISH, GERMAN, FRENCH)

    private val createAgent = CreateAgentService(repository, eventPublisher, validLanguages)

    @Test
    fun `should orchestrate the creation of an agent`() {
        val result = createAgent(
            "jane.doe@gmail",
            "Jane Doe",
            listOf("Technical background", "anti-financial crime", "accounts"),
            listOf(ENGLISH, GERMAN)
        )

        result.isRight() shouldBe true
        verify {
            repository.save(any())
            eventPublisher.publish(any<AgentCreated>())
        }
    }

    @Test
    fun `should fail orchestrating the creation of an agent if domaij creation fails`() {
        val result = createAgent(
            "jane.doe@gmail",
            "Jane Doe",
            listOf("Technical background", "anti-financial crime", "accounts"),
            listOf(ENGLISH, GERMAN, FRENCH, CHINESE)
        )

        result.isLeft() shouldBe true
        verify(exactly = 0) {
            repository.save(any())
            eventPublisher.publish(any<AgentCreated>())
        }
    }
}