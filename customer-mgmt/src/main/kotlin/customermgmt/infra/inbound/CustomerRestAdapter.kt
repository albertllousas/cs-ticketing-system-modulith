package customermgmt.infra.inbound

import customermgmt.app.CreateCustomerService
import customermgmt.domain.Tier
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.Locale
import java.util.UUID

@RestController
@RequestMapping("/customers")
class CustomerRestAdapter(private val createCustomer: CreateCustomerService) {

    @PostMapping
    fun create(@RequestBody request: CreateCustomerHttpRequest) =
        createCustomer(request.email, request.fullName, request.preferredLang, request.tier)
            .let { ResponseEntity.status(CREATED).body(CreateCustomerHttpResponse(it)) }
}

data class CreateCustomerHttpRequest(
    val fullName: String,
    val email: String,
    val preferredLang: Locale,
    val tier: Tier
)

data class CreateCustomerHttpResponse(val id: UUID)
