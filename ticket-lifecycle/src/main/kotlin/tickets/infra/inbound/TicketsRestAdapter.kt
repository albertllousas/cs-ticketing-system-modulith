package tickets.infra.inbound

import org.springframework.http.HttpStatus.*
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import tickets.app.CreateTicketService
import tickets.domain.TicketType
import java.util.UUID

@RestController
@RequestMapping("/tickets")
class TicketsRestAdapter(private val createTicket: CreateTicketService) {

    @PostMapping
    fun create(@RequestBody request: CreateTicketHttpRequest, @RequestHeader customerId: UUID) =
        createTicket(request.title, request.description, customerId, request.type)
            .let { ResponseEntity.status(CREATED).body(CreateTicketHttpResponse(it)) }
}

data class CreateTicketHttpRequest(val title: String, val description: String, val type: TicketType)

data class CreateTicketHttpResponse(val id: UUID)
