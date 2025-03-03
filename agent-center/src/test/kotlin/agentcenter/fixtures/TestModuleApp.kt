package agentcenter.fixtures

import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication(
    scanBasePackages = ["agentcenter", "shared"]
)
class TestModuleApp