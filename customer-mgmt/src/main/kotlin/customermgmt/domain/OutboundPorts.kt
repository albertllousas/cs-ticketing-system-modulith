package customermgmt.domain

import java.util.UUID

interface CustomerRepository {
    fun save(customer: Customer)
    fun find(customer: UUID): Customer?
}
