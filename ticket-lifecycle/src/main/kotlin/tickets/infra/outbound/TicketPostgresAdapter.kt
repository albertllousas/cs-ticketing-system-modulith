package tickets.infra.outbound

import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Propagation.*
import org.springframework.transaction.annotation.Transactional
import shared.OptimisticLockingException
import tickets.domain.Ticket
import tickets.domain.TicketRepository
import tickets.domain.TicketStatus
import tickets.domain.TicketType
import java.util.UUID

@Repository
class TicketPostgresAdapter(private val jdbcTemplate: JdbcTemplate) : TicketRepository {

    @Transactional(propagation = MANDATORY)
    override fun save(ticket: Ticket) {
        with(ticket) {
            if (ticket.version == 0L)
                jdbcTemplate.update(
                    """
                INSERT INTO ticket_lifecycle.tickets (id, customer_id, title, description, status, type, created, version) 
                VALUES (?,?,?,?,?,?,?,?) 
                """, id, customerId, title, description, status.toString(), type.toString(), created, 1
                )
            else
                jdbcTemplate.queryForObject(
                    """
                UPDATE ticket_lifecycle.tickets 
                SET title = ?, description = ?, status = ?, type = ?, version = version + 1 WHERE id = ?
                RETURNING version
                """, Long::class.java, title, description, status.toString(), type.toString(), id
                ).also { if (it != version + 1) throw OptimisticLockingException("Ticket", id) }
        }
    }

    override fun find(id: UUID): Ticket? = try {
        jdbcTemplate.queryForObject(""" SELECT * FROM ticket_lifecycle.tickets WHERE id = '$id' """) { rs, _ ->
            Ticket(
                id = rs.getObject("id", UUID::class.java),
                title = rs.getString("title"),
                description = rs.getString("description"),
                customerId = rs.getObject("customer_id", UUID::class.java),
                status = rs.getString("status").let { TicketStatus.valueOf(it) },
                type = rs.getString("type").let { TicketType.valueOf(it) },
                version = rs.getLong("version"),
                created = rs.getTimestamp("created").toLocalDateTime()
            )
        }
    } catch (exception: EmptyResultDataAccessException) {
        null
    }
}
