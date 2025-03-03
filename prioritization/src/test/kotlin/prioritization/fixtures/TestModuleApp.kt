package prioritization.fixtures

import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication(
    scanBasePackages = ["prioritization", "shared"]
)
class TestModuleApp