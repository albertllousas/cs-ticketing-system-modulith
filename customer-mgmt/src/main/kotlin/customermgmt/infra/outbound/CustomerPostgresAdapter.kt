package customermgmt.infra.outbound

import customermgmt.domain.Customer
import customermgmt.domain.CustomerRepository
import customermgmt.domain.Tier
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Propagation.MANDATORY
import org.springframework.transaction.annotation.Transactional
import shared.OptimisticLockingException
import java.util.Locale
import java.util.UUID

@Repository
class CustomerPostgresAdapter(private val jdbcTemplate: JdbcTemplate) : CustomerRepository {

    @Transactional(propagation = MANDATORY)
    override fun save(customer: Customer) {
        with(customer) {
            if (customer.version == 0L)
                jdbcTemplate.update(
                    """
                INSERT INTO customer_mgmt.customers (id, email, full_name, preferred_lang, tier, version) 
                VALUES (?,?,?,?,?,?) 
                """, id, email, fullName, preferredLang.language, tier.name, 1
                )
            else
                jdbcTemplate.queryForObject(
                    """
                UPDATE customer_mgmt.customers
                SET email = ?, full_name = ?, preferred_lang = ?, tier = ?, version = version + 1
                WHERE id = ? 
                RETURNING version
                """, Long::class.java, email, fullName, preferredLang.language, tier.name, id
                ).also { if (it != version + 1) throw OptimisticLockingException("Customer", id) }
        }
    }

    override fun find(customer: UUID): Customer? = try {
        jdbcTemplate.queryForObject(
            """ SELECT * FROM customer_mgmt.customers WHERE id = '$customer' """
        ) { rs, _ ->
            Customer(
                id = rs.getObject("id", UUID::class.java),
                email = rs.getString("email"),
                fullName = rs.getString("full_name"),
                preferredLang = Locale.of(rs.getString("preferred_lang")),
                tier = Tier.valueOf(rs.getString("tier")),
                version = rs.getLong("version")
            )
        }
    } catch (exception: EmptyResultDataAccessException) {
        null
    }
}
