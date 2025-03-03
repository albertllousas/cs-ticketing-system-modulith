package tickets.fixtures

import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication(
    scanBasePackages = ["tickets", "shared"]
)
class TestModuleApp