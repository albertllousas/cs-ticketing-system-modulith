package fixtures

import customermgmt.domain.Customer
import customermgmt.domain.Tier
import java.util.Locale
import java.util.UUID

object TestBuilders {

    fun buildCustomer(
        id: UUID = UUID.randomUUID(),
        email: String = "jane.doe@gmail.com",
        fullName: String = "Jane Doe",
        preferredLang: Locale = Locale.ENGLISH,
        tier: Tier = Tier.PREMIUM,
        version: Long = 0
    ) = Customer(id, email, fullName, preferredLang, tier, version)
}
