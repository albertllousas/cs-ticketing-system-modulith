package customermgmt.fixtures

import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication(
    scanBasePackages = ["customermgmt", "shared"]
)
class TestModuleApp