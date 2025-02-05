package prioritization.infra.outbound

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import prioritization.domain.Customer
import shared.CustomerDto
import shared.ExposedInboundPorts
import java.util.Locale
import java.util.UUID

class CustomerModuleAdapterTest {

    private val findCustomer = mockk<ExposedInboundPorts.FindCustomer>()

    private val customerModuleAdapter = CustomerModuleAdapter(findCustomer)

    @Test
    fun `should find a customer`() {
        val customerId = UUID.randomUUID()
        every {
            findCustomer(customerId)
        } returns CustomerDto(customerId, "Jane Doe", "jane.doe@gmail.com", Locale.ENGLISH, CustomerDto.Tier.PREMIUM)

        val result = customerModuleAdapter.find(customerId)

        result shouldBe Customer(customerId, Customer.Tier.PREMIUM)
    }

    @Test
    fun `should fail to find ticket`() {
        every { findCustomer(any()) } returns null

        shouldThrow<CrossModuleCallException> { customerModuleAdapter.find(UUID.randomUUID()) }
    }
}