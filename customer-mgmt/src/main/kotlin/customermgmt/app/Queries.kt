package customermgmt.app

import customermgmt.domain.Customer
import customermgmt.domain.CustomerRepository
import org.springframework.stereotype.Service
import shared.CustomerDto
import shared.ExposedInboundPorts
import java.util.UUID

@Service
class FindCustomerService(private val repository: CustomerRepository) : ExposedInboundPorts.FindCustomer {

    override operator fun invoke(id: UUID) = repository.find(id)?.toDto()

    private fun Customer.toDto() =
        CustomerDto(id, fullName, email, preferredLang, CustomerDto.Tier.valueOf(tier.name))
}