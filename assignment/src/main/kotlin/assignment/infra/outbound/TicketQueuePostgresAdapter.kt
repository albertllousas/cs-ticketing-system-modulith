package assignment.infra.outbound

import assignment.domain.Ticket
import assignment.domain.TicketQueue
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class TicketQueuePostgresAdapter(
    private val jdbcTemplate: JdbcTemplate,
) : TicketQueue {
    override fun add(ticketId: UUID, priority: Ticket.Priority) {
        jdbcTemplate.update(
            """ INSERT INTO assignment.ticket_queue (ticket_id, priority) VALUES (?,?) """,
            ticketId,
            priority.name
        )
    }

    override fun dequeue(batch: Int): List<UUID> =
    jdbcTemplate.queryForList(
        """
        WITH selected_tickets AS (
            SELECT ticket_id 
            FROM assignment.ticket_queue 
            ORDER BY 
                CASE priority 
                    WHEN 'HIGH' THEN 3 
                    WHEN 'MEDIUM' THEN 2 
                    WHEN 'LOW' THEN 1 
                END DESC, 
                created_at DESC
            LIMIT ? 
            FOR UPDATE SKIP LOCKED
        ),
        deleted_tickets AS (
            DELETE FROM assignment.ticket_queue
            WHERE ticket_id IN (SELECT ticket_id FROM selected_tickets)
            RETURNING ticket_id, priority, created_at
        )
        SELECT ticket_id FROM deleted_tickets
        ORDER BY 
            CASE priority 
                WHEN 'HIGH' THEN 3 
                WHEN 'MEDIUM' THEN 2 
                WHEN 'LOW' THEN 1 
            END DESC, 
            created_at DESC
        """,
            UUID::class.java,
            batch
        )

}
