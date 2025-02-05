package customermgmt.domain

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import shared.CustomerCreated
import java.util.Locale
import java.util.UUID

class CustomerTest {

    @Test
    fun `should create a customer`() {
        val customerId = UUID.randomUUID()
        val (event, customer) = Customer.create(
            "jane.doe@gmail.com",
            "Jane Doe",
            Locale.ENGLISH,
            Tier.PREMIUM,
            { customerId })
        event shouldBe CustomerCreated(customerId, "Jane Doe", "jane.doe@gmail.com", "en", "PREMIUM")
        customer shouldBe Customer(customerId, "Jane Doe", "jane.doe@gmail.com", Locale.ENGLISH, Tier.PREMIUM, 0L)
    }
}
