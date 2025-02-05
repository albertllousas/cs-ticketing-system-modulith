package agentcenter.domain

import agentcenter.domain.AgentStatus.OFFLINE
import agentcenter.domain.AgentStatus.ONLINE
import agentcenter.domain.CreateAgentError.CreateAgentErrorReason.INVALID_LANGUAGE
import agentcenter.domain.SetAvailabilityError.SetAvailabilityErrorReason.NO_CHANGES
import agentcenter.infra.outbound.fixtures.TestBuilders
import arrow.core.left
import arrow.core.right
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import shared.AgentConnected
import shared.AgentCreated
import shared.AgentDisconnected
import java.util.Locale.ENGLISH
import java.util.Locale.FRENCH
import java.util.Locale.GERMAN
import java.util.UUID

class AgentTest {

    private val uuid = UUID.randomUUID()

    @Test
    fun `should create an agent`() {
        val result = Agent.create(
            email = "jane.doe@gmail",
            fullName = "Jane Doe",
            skills = listOf("Technical background", "anti-financial crime", "accounts"),
            languages = listOf(ENGLISH, GERMAN),
            validLanguages = listOf(ENGLISH, GERMAN, FRENCH),
            generateId = { uuid },
        )

        result shouldBe Pair(
            AgentCreated(
                id = uuid,
                email = "jane.doe@gmail",
                fullName = "Jane Doe",
                skills = listOf("Technical background", "anti-financial crime", "accounts"),
                languages = listOf(ENGLISH, GERMAN)
            ),
            Agent(
                id = uuid,
                email = "jane.doe@gmail",
                fullName = "Jane Doe",
                skills = listOf("Technical background", "anti-financial crime", "accounts"),
                languages = listOf(ENGLISH, GERMAN),
                status = OFFLINE,
                version = 0
            )
        ).right()
    }

    @Test
    fun `should fail creating an agent if language is invalid`() {
        val result = Agent.create(
            email = "jane.doe@gmail",
            fullName = "Jane Doe",
            skills = listOf("Technical background", "anti-financial crime", "accounts"),
            languages = listOf(ENGLISH, GERMAN),
            validLanguages = listOf(ENGLISH, FRENCH),
            generateId = { uuid },
        )

        result shouldBe CreateAgentError(INVALID_LANGUAGE).left()
    }

    @Test
    fun `should connect an agent`() {
        val agent = TestBuilders.buildAgent(id = uuid, status = OFFLINE)

        val result = agent.connect()

        result shouldBe Pair(AgentConnected(id = uuid), agent.copy(status = ONLINE)).right()
    }

    @Test
    fun `should not connect an agent if already online`() {
        val agent = TestBuilders.buildAgent(id = uuid, status = ONLINE)

        val result = agent.connect()

        result shouldBe SetAvailabilityError(NO_CHANGES).left()
    }

    @Test
    fun `should disconnect an agent`() {
        val agent = TestBuilders.buildAgent(id = uuid, status = ONLINE)

        val result = agent.disconnect()

        result shouldBe Pair(AgentDisconnected(id = uuid), agent.copy(status = OFFLINE)).right()
    }

    @Test
    fun `should not disconnect an agent if already offline`() {
        val agent = TestBuilders.buildAgent(id = uuid, status = OFFLINE)

        val result = agent.disconnect()

        result shouldBe SetAvailabilityError(NO_CHANGES).left()
    }
}