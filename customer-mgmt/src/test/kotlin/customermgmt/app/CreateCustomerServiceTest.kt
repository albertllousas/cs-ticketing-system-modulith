package customermgmt.app

import customermgmt.domain.Customer
import customermgmt.domain.CustomerRepository
import customermgmt.domain.Tier
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import shared.EventPublisher
import shared.TicketCreated
import java.util.Locale

class CreateCustomerServiceTest {

    private val repository = mockk<CustomerRepository>(relaxed = true)

    private val eventPublisher = mockk<EventPublisher>(relaxed = true)

    private val createCustomer = CreateCustomerService(repository, eventPublisher)

    @Test
    fun `should create a ticket`() {

        createCustomer("Jane Doe", "jane.doe@gmail.com", Locale.ENGLISH, Tier.PREMIUM)

        verify { repository.save(any<Customer>()) }
        verify { eventPublisher.publish(any<TicketCreated>()) }
    }
}