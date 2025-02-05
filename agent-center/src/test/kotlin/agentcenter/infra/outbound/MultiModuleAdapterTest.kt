package agentcenter.infra.outbound

import agentcenter.domain.AssignedTicket
import agentcenter.domain.Priority
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import shared.AgentAssignmentsDto
import shared.ExposedInboundPorts
import shared.TicketDto
import shared.TicketDto.Status.OPEN
import shared.TicketDto.Type.ISSUE
import shared.TicketPriorityDto
import java.util.UUID.randomUUID

class MultiModuleAdapterTest {

    private val findTicket = mockk<ExposedInboundPorts.FindTicket>()

    private val findAssignments = mockk<ExposedInboundPorts.FindAssignments>()

    private val findTicketPriority = mockk<ExposedInboundPorts.FindTicketPriority>()

    private val multiModuleAdapter = MultiModuleAdapter(findTicket, findAssignments, findTicketPriority)

    @Test
    fun `should find assigned tickets for an agent`() {
        val agentId = randomUUID()
        val agentAssignments = AgentAssignmentsDto(agentId, listOf(randomUUID()))
        val ticketDto = TicketDto(randomUUID(), "title", "description", randomUUID(), OPEN, ISSUE)
        val ticketPriorityDto = TicketPriorityDto(randomUUID(), ticketDto.id, TicketPriorityDto.Priority.HIGH)
        every { findAssignments(agentId) } returns agentAssignments
        every { findTicket(agentAssignments.currentTickets[0]) } returns ticketDto
        every { findTicketPriority(agentAssignments.currentTickets[0]) } returns ticketPriorityDto

        val result = multiModuleAdapter.find(agentId)

        result shouldBe listOf(AssignedTicket(ticketDto.id, Priority.HIGH, ticketDto.customerId, ticketDto.title))
    }

    @Test
    fun `should throw exception if agent assignments not found`() {
        val agentId = randomUUID()
        every { findAssignments(agentId) } returns null

        shouldThrow<CrossModuleCallException> { multiModuleAdapter.find(agentId) }
    }

    @Test
    fun `should throw exception if ticket not found`() {
        val agentId = randomUUID()
        val agentAssignments = AgentAssignmentsDto(agentId, listOf(randomUUID()))
        every { findAssignments(agentId) } returns agentAssignments
        every { findTicket(agentAssignments.currentTickets[0]) } returns null

        shouldThrow<CrossModuleCallException> { multiModuleAdapter.find(agentId) }
    }

    @Test
    fun `should throw exception if ticket priority not found`() {
        val agentId = randomUUID()
        val agentAssignments = AgentAssignmentsDto(agentId, listOf(randomUUID()))
        val ticketDto = TicketDto(randomUUID(), "title", "description", randomUUID(), OPEN, ISSUE)
        every { findAssignments(agentId) } returns agentAssignments
        every { findTicket(agentAssignments.currentTickets[0]) } returns ticketDto
        every { findTicketPriority(agentAssignments.currentTickets[0]) } returns null

        shouldThrow<CrossModuleCallException> { multiModuleAdapter.find(agentId) }
    }
}
