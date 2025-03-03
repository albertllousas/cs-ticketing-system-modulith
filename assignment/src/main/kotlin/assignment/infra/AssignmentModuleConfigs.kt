package assignment.infra

import assignment.domain.AssignmentStrategy
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AssignmentModuleConfigs {

    @Bean
    fun assignmentStrategy(): AssignmentStrategy = AssignmentStrategy.RULE_BASED
}
