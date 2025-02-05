package prioritization.infra.outbound

import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Propagation.MANDATORY
import org.springframework.transaction.annotation.Transactional
import prioritization.domain.Priority
import prioritization.domain.TicketPriority
import prioritization.domain.TicketPriorityRepository
import shared.OptimisticLockingException
import java.util.UUID

@Repository
class TicketPrioritiesPostgresAdapter(private val jdbcTemplate: JdbcTemplate) : TicketPriorityRepository {

    @Transactional(propagation = MANDATORY)
    override fun save(ticketPriority: TicketPriority) {
        with(ticketPriority) {
            if (ticketPriority.version == 0L)
                jdbcTemplate.update(
                    """
                INSERT INTO prioritization.ticket_priorities (id, ticket_id, priority, version) 
                VALUES (?,?,?,?) 
                """, id, ticketId, priority.name, 1
                )
            else
                jdbcTemplate.queryForObject(
                    """
                UPDATE prioritization.ticket_priorities
                SET priority = ?, version = version + 1 WHERE id = ?
                RETURNING version
                """, Long::class.java, priority.name, id
                ).also { if (it != version + 1) throw OptimisticLockingException("TicketPriority", id) }
        }
    }

    override fun find(ticketId: UUID): TicketPriority? = try {
        jdbcTemplate.queryForObject(
            """ SELECT * FROM prioritization.ticket_priorities WHERE ticket_id = '$ticketId' """
        ) { rs, _ ->
            TicketPriority(
                id = rs.getObject("id", UUID::class.java),
                ticketId = rs.getObject("ticket_id", UUID::class.java),
                priority = rs.getString("priority").let { Priority.valueOf(it) },
                version = rs.getLong("version")
            )
        }
    } catch (exception: EmptyResultDataAccessException) {
        null
    }
}