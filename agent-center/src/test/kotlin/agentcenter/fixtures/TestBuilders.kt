package agentcenter.fixtures

import agentcenter.domain.Agent
import agentcenter.domain.AgentStatus
import java.util.Locale
import java.util.UUID

object TestBuilders {

    fun buildAgent(
        id: UUID = UUID.randomUUID(),
        fullName: String = "Jane Doe",
        email: String = "jane.doe@gmail.com",
        skills: List<String> = listOf("Customer support"),
        languages: List<Locale> = listOf(Locale.ENGLISH),
        status: AgentStatus = AgentStatus.ONLINE,
        version: Long = 0,
    ) = Agent(id, email, fullName, skills, languages, status, version)
}
