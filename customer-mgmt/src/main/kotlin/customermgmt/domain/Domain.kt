package customermgmt.domain

import shared.CustomerCreated
import java.util.Locale
import java.util.UUID

enum class Tier { BASIC, STANDARD, PREMIUM }

data class Customer(
    val id: UUID,
    val fullName: String,
    val email: String,
    val preferredLang: Locale,
    val tier: Tier,
    val version: Long
) {

    companion object {
        private val GEN_ID: () -> UUID = { UUID.randomUUID() }

        fun create(
            email: String,
            fullName: String,
            preferredLang: Locale,
            tier: Tier,
            generateId: () -> UUID = GEN_ID
        ): Pair<CustomerCreated, Customer> {
            val id = generateId()
            return Pair(
                CustomerCreated(id, fullName, email, preferredLang.language, tier.name),
                Customer(id, fullName, email, preferredLang, tier, 0)
            )
        }
    }
}
