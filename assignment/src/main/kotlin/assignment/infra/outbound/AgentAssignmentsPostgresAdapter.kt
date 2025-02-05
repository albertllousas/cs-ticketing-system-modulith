package assignment.infra.outbound

import assignment.domain.AgentAssignments
import assignment.domain.AgentAssignmentsRepository
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Propagation.MANDATORY
import org.springframework.transaction.annotation.Transactional
import shared.OptimisticLockingException
import java.util.UUID

@Repository
class AgentAssignmentsPostgresAdapter(
    private val jdbcTemplate: JdbcTemplate,
    private val mapper: ObjectMapper = jacksonObjectMapper().registerModule(JavaTimeModule())
)  : AgentAssignmentsRepository {

    @Transactional(propagation = MANDATORY)
    override fun save(agentAssignments: AgentAssignments) {
        with(agentAssignments) {
            if(agentAssignments.version == 0L ) {
                jdbcTemplate.update(
                    """
                    INSERT INTO assignment.agent_assignments (agent_id, tickets, version) 
                    VALUES (?,?::jsonb,?) 
                    """, agentId, mapper.writeValueAsString(currentTickets), 1
                )
            } else {
                jdbcTemplate.queryForObject(
                    """
                    UPDATE assignment.agent_assignments
                    SET  tickets = ?::jsonb, version = version + 1
                    WHERE agent_id = ? 
                    RETURNING version
                    """, Long::class.java, mapper.writeValueAsString(currentTickets), agentId
                ).also { if (it != version + 1) throw OptimisticLockingException("AgentAssignments", agentId) }
            }
        }
    }

    override fun find(agentIds: List<UUID>): List<AgentAssignments> = agentIds.mapNotNull { find(it) }


    override fun find(agentId: UUID): AgentAssignments = try {
        jdbcTemplate.queryForObject(
            """ SELECT * FROM assignment.agent_assignments WHERE agent_id = '$agentId' """
        ) { rs, _ ->
            AgentAssignments(
                agentId = rs.getObject("agent_id", UUID::class.java),
                currentTickets = mapper.readValue(rs.getBytes("tickets")),
                version = rs.getLong("version")
            )
        }
    } catch (exception: EmptyResultDataAccessException) {
        AgentAssignments(agentId, emptyList(), 0)
    }
}