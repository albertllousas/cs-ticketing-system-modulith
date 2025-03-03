package agentcenter.infra

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import shared.SchemaRestrictedJdbcTemplate
import javax.sql.DataSource

@Configuration
class AgentCenterModuleConfig {

    @Bean
    fun jdbcTemplateForSchema2(dataSource: DataSource) = SchemaRestrictedJdbcTemplate(dataSource, "agent_center")
}
