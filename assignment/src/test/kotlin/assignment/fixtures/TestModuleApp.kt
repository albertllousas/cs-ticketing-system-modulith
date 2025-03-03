package assignment.fixtures

import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication(
    scanBasePackages = ["assignment", "shared"]
)
class TestModuleApp