package shared

import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication(
    scanBasePackages = ["tickets", "customermgmt", "prioritization", "assignment", "agentcenter"]
)
class RootApp