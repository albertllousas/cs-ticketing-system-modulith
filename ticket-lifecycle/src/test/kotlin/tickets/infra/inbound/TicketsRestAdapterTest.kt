package tickets.infra.inbound

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import shared.RootApp
import tickets.app.CreateTicketService
import tickets.domain.TicketType.ISSUE
import java.util.UUID

@Tag("integration")
@WebMvcTest(TicketsRestAdapter::class)
@ContextConfiguration(classes = [RootApp::class])
class TicketsRestAdapterTest(@Autowired private val mvc: MockMvc) {

    @MockkBean
    private lateinit var createTicket: CreateTicketService

    @Test
    fun `should create a ticket`() {
        val customerId = UUID.randomUUID()
        val ticketId = UUID.randomUUID()
        every { createTicket.invoke("Access", "I can't access to my account", customerId, ISSUE) } returns ticketId

        val response = mvc.perform(
            post("/tickets")
                .contentType("application/json")
                .header("customerId", customerId.toString())
                .content(""" { "title": "Access", "description": "I can't access to my account", "type": "ISSUE" } """)
        )

        response.andExpect(status().isCreated)
        response.andExpect(content().json(""" { "id": "$ticketId" } """))
    }
}
