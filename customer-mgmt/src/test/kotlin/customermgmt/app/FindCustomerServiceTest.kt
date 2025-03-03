package customermgmt.app

import customermgmt.domain.CustomerRepository
import customermgmt.fixtures.TestBuilders
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import shared.CustomerDto
import java.util.UUID

class FindCustomerServiceTest {

    private val repository = mockk<CustomerRepository>()

    private val findCustomer = FindCustomerService(repository)

    @Test
    fun `should find ticket priority`() {
        val customer = TestBuilders.buildCustomer()
        every { repository.find(customer.id) } returns customer

        findCustomer(customer.id) shouldBe CustomerDto(
            customer.id,
            customer.fullName,
            customer.email,
            customer.preferredLang,
            CustomerDto.Tier.valueOf(customer.tier.name)
        )
    }

    @Test
    fun `should return null when ticket priority is not found`() {
        every { repository.find(any()) } returns null

        findCustomer(UUID.randomUUID()) shouldBe null
    }
}
