package prioritization.infra

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import prioritization.domain.PrioritizationStrategy

@Configuration
class PrioritizationModuleConfigs {

    @Bean
    fun prioritizationStrategy(): PrioritizationStrategy = PrioritizationStrategy.RULE_BASED
}
