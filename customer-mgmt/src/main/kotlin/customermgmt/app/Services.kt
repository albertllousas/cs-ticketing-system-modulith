package customermgmt.app

import customermgmt.domain.Customer
import customermgmt.domain.CustomerRepository
import customermgmt.domain.Tier
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import shared.EventPublisher
import java.util.Locale
import java.util.UUID

@Service
class CreateCustomerService(private val repository: CustomerRepository, private val eventPublisher: EventPublisher) {

    @Transactional
    operator fun invoke(email: String, fullName: String, preferredLang: Locale, tier: Tier): UUID =
        Customer.create(email, fullName, preferredLang, tier)
            .also { (agentCreated, agent) ->
                repository.save(agent)
                eventPublisher.publish(agentCreated)
            }.first.id
}
