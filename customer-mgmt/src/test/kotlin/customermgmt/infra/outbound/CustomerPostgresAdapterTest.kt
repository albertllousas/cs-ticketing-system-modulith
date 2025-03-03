package customermgmt.infra.outbound

import customermgmt.fixtures.TestBuilders
import fixtures.containers.Postgres
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import shared.OptimisticLockingException
import java.util.UUID

@Tag("integration")
@TestInstance(PER_CLASS)
class CustomerPostgresAdapterTest {

    private val postgres = Postgres(
        flywayLocationSchemasList = listOf(Pair("db/migration/customermgmt", "customer_mgmt")),
    )

    private val repository = CustomerPostgresAdapter(postgres.jdbcTemplate)

    @Test
    fun `should save and find a customer`() {
        val customer = TestBuilders.buildCustomer()

        repository.save(customer)
        val result = repository.find(customer.id)

        result shouldBe customer.copy(version = 1)
    }

    @Test
    fun `should not find a customer when it does not exists`() {
        repository.find(UUID.randomUUID()) shouldBe null
    }

    @Test
    fun `should update a customer`() {
        val customer = TestBuilders.buildCustomer()
            .also(repository::save)
            .let { repository.find(it.id)!! }
        val updatedCustomer = customer.copy(fullName = "John Doe", version = 1)

        repository.save(updatedCustomer)
        val result = repository.find(customer.id)

        result shouldBe updatedCustomer.copy(version = 2)
    }

    @Test
    fun `should throw OptimisticLockingException when updating a customer with wrong version`() {
        val customer = TestBuilders.buildCustomer()
            .also(repository::save)
            .let { repository.find(it.id)!! }
        val updatedCustomer = customer.copy(version = 3)

        shouldThrow<OptimisticLockingException> { repository.save(updatedCustomer) }
    }
}
