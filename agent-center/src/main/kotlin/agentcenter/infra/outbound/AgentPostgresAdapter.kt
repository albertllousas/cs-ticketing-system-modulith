package agentcenter.infra.outbound

import agentcenter.domain.Agent
import agentcenter.domain.AgentRepository
import agentcenter.domain.AgentStatus
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import shared.OptimisticLockingException
import java.sql.ResultSet
import java.util.Locale
import java.util.UUID

@Repository
class AgentPostgresAdapter(private val jdbcTemplate: JdbcTemplate) : AgentRepository {

    override fun save(agent: Agent): Unit = with(agent) {
        if (agent.version == 0L)
            jdbcTemplate.update(
                """
                INSERT INTO agent_center.agents (id, email, full_name, skills, languages, status, version) 
                VALUES (?,?,?,?,?,?,?) 
                """, id, email, fullName, skills.toTypedArray(), languages.map { it.language }.toTypedArray(), status.name, 1
            )
        else
            jdbcTemplate.queryForObject(
                """
                UPDATE agent_center.agents
                SET email = ?, full_name = ?, skills = ?, languages = ?, status = ?, version = version + 1
                WHERE id = ? 
                RETURNING version
                """, Long::class.java, email, fullName, skills.toTypedArray(), languages.map { it.language }.toTypedArray(), status.name, id
            ).also { if (it != version + 1) throw OptimisticLockingException("Agent", id) }
    }

    override fun findAvailable(): List<Agent> = jdbcTemplate.query(
        """ SELECT * FROM agent_center.agents WHERE status = 'ONLINE' """, mapToDomain
    ).filterNotNull()


    override fun find(agentId: UUID): Agent? = try {
        jdbcTemplate.queryForObject(""" SELECT * FROM agent_center.agents WHERE id = '$agentId' """, mapToDomain)
    } catch (exception: EmptyResultDataAccessException) {
        null
    }

    private val mapToDomain: (ResultSet, rowNum: Int) -> Agent? = { rs, _ ->
        Agent(
            id = rs.getObject("id", UUID::class.java),
            email = rs.getString("email"),
            fullName = rs.getString("full_name"),
            skills = (rs.getArray("skills").array as Array<String>).toList(),
            languages = (rs.getArray("languages").array as Array<String>).toList().map { Locale.of(it) },
            status = AgentStatus.valueOf(rs.getString("status")),
            version = rs.getLong("version")
        )
    }
}