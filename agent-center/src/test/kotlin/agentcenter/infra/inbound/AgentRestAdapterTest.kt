package agentcenter.infra.inbound

import agentcenter.fixtures.TestModuleApp
import agentcenter.app.CreateAgentService
import agentcenter.app.QueryHomeService
import agentcenter.app.SetAvailabilityService
import agentcenter.domain.AssignedTicket
import agentcenter.domain.CreateAgentError
import agentcenter.domain.CreateAgentError.CreateAgentErrorReason.INVALID_LANGUAGE
import agentcenter.domain.Home
import agentcenter.domain.Priority
import agentcenter.fixtures.TestBuilders
import arrow.core.left
import arrow.core.right
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.Locale
import java.util.Locale.ENGLISH
import java.util.UUID

@Tag("integration")
@WebMvcTest(AgentRestAdapter::class)
@ContextConfiguration(classes = [TestModuleApp::class])
class AgentRestAdapterTest(@Autowired private val mvc: MockMvc) {

    @MockkBean
    private lateinit var createAgent: CreateAgentService

    @MockkBean
    private lateinit var setAvailabilityService: SetAvailabilityService

    @MockkBean
    private lateinit var queryHome: QueryHomeService

    @Test
    fun `should create an agent`() {
        val agentId = UUID.randomUUID()
        every {
            createAgent.invoke(
                "jane.doe@gmail.com",
                "Jane Doe",
                listOf("Technical background"),
                listOf(ENGLISH, Locale.GERMAN)
            )
        } returns agentId.right()

        val response = mvc.perform(
            post("/agents")
                .contentType("application/json")
                .content(
                    """ { 
                        |"email": "jane.doe@gmail.com", 
                        |"fullName": "Jane Doe", 
                        |"skills": ["Technical background"], 
                        |"languages": ["en", "de"]
                        |} """
                        .trimMargin()
                )
        )

        response.andExpect(status().isCreated)
        response.andExpect(content().json(""" { "id": "$agentId" } """))
    }

    @Test
    fun `should fail when creating an agent an agent fails`() {
        every {
            createAgent.invoke(
                "jane.doe@gmail.com",
                "Jane Doe",
                listOf("Technical background"),
                listOf(ENGLISH, Locale.GERMAN)
            )
        } returns CreateAgentError(reason = INVALID_LANGUAGE).left()

        val response = mvc.perform(
            post("/agents")
                .contentType("application/json")
                .content(
                    """ { 
                        |"email": "jane.doe@gmail.com", 
                        |"fullName": "Jane Doe", 
                        |"skills": ["Technical background"], 
                        |"languages": ["en", "de"]
                        |} """
                        .trimMargin()
                )
        )

        response.andExpect(status().isBadRequest)
        response.andExpect(content().json(""" { "reason": "INVALID_LANGUAGE" } """))
    }

    @Test
    fun `should set an agent online`() {
        val agentId = UUID.randomUUID()
        every { setAvailabilityService.invoke(agentId, true) } returns Unit.right()

        val response = mvc.perform(patch("/agents/$agentId/online").contentType("application/json"))

        response.andExpect(status().isOk)
    }

    @Test
    fun `should set an agent offline`() {
        val agentId = UUID.randomUUID()
        every { setAvailabilityService.invoke(agentId, false) } returns Unit.right()

        val response = mvc.perform(patch("/agents/$agentId/offline").contentType("application/json"))

        response.andExpect(status().isOk)
    }

    @Test
    fun `should get an agent home`() {
        val agentId = UUID.randomUUID()
        val agent = TestBuilders.buildAgent(agentId)
        val ticket = AssignedTicket(UUID.randomUUID(), Priority.MEDIUM, UUID.randomUUID(), "subject")
        every { queryHome(agentId) } returns Home(agent, listOf(ticket))

        val response = mvc.perform(get("/agents/$agentId/home"))

        response.andExpect(status().isOk)
        response.andExpect(
            content().json(
                """ { 
                    |"agentName": "Jane Doe",
                    |"assignments": [
                    |  { "ticketId": "${ticket.ticketId}", "priority": "MEDIUM", "customer": "${ticket.customer}", "subject": "subject" }
                    |]
                    |} """.trimMargin()
            )
        )
    }
}
