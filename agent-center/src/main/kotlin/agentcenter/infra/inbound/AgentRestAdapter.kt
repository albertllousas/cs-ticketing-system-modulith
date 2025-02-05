package agentcenter.infra.inbound

import agentcenter.app.CreateAgentService
import agentcenter.app.QueryHomeService
import agentcenter.app.SetAvailabilityService
import agentcenter.domain.SetAvailabilityError.SetAvailabilityErrorReason.AGENT_NOT_FOUND
import agentcenter.domain.SetAvailabilityError.SetAvailabilityErrorReason.NO_CHANGES
import agentcenter.infra.inbound.PriorityHttpResponse.valueOf
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.*
import org.springframework.http.ProblemDetail.forStatusAndDetail
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.Locale
import java.util.UUID

@RestController
@RequestMapping("/agents")
class AgentRestAdapter(
    private val createAgent: CreateAgentService,
    private val setAvailabilityService: SetAvailabilityService,
    private val queryHome: QueryHomeService
) {

    @PostMapping
    fun create(@RequestBody request: CreateAgentHttpRequest) =
        createAgent(request.email, request.fullName, request.skills, request.languages)
            .fold(
                ifLeft = { ResponseEntity.of(forStatusAndDetail(BAD_REQUEST, it.reason.toString())) },
                ifRight = { ResponseEntity.status(CREATED).body(CreateAgentHttpResponse(it)) }
            )

    @PatchMapping("/{agentId}/online")
    fun setOnline(@PathVariable agentId: UUID) =
        setAvailabilityService(agentId, connected = true)
            .fold(
                ifLeft = {
                    when (it.reason) {
                        NO_CHANGES -> ResponseEntity.noContent().build<Unit>()
                        AGENT_NOT_FOUND -> ResponseEntity.notFound().build()
                    }
                },
                ifRight = { ok().build() }
            )

    @PatchMapping("/{agentId}/offline")
    fun setOffline(@PathVariable agentId: UUID) =
        setAvailabilityService(agentId, connected = false)
            .fold(
                ifLeft = {
                    when (it.reason) {
                        NO_CHANGES -> ResponseEntity.noContent().build<Unit>()
                        AGENT_NOT_FOUND -> ResponseEntity.notFound().build()
                    }
                },
                ifRight = { ok().build() }
            )

    @GetMapping("/{agentId}/home")
    fun home(@PathVariable agentId: UUID) =
        queryHome(agentId)
            ?.let {
                HomeHttpResponse(
                    it.agent.fullName,
                    it.assignments.map { ticket ->
                        AssignedTicketHttpResponse(ticket.ticketId, valueOf(ticket.priority.name), ticket.customer, ticket.subject)
                    }
                )
            }
            ?: ResponseEntity.notFound()
}

data class CreateAgentHttpRequest(
    val email: String,
    val fullName: String,
    val skills: List<String>,
    val languages: List<Locale>
)

data class CreateAgentHttpResponse(val id: UUID)

data class SetOnlineHttpRequest(val agentId: UUID, val online: Boolean)

data class HomeHttpResponse(val agentName: String, val assignments: List<AssignedTicketHttpResponse>)

data class AssignedTicketHttpResponse(
    val ticketId: UUID,
    val priority: PriorityHttpResponse,
    val customer: UUID,
    val subject: String
)

enum class PriorityHttpResponse { LOW, MEDIUM, HIGH }
