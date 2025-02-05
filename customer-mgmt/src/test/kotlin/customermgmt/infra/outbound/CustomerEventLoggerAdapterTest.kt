package customermgmt.infra.outbound

import io.mockk.spyk
import io.mockk.verify
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.slf4j.Logger
import org.slf4j.helpers.NOPLogger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Bean
import shared.CustomerCreated
import java.util.UUID

@Tag("integration")
@SpringBootTest(classes = [CustomerEventLoggerAdapter::class, TestConfig::class])
class CustomerEventLoggerAdapterTest {

    @Autowired
    private lateinit var eventPublisher: ApplicationEventPublisher

    @Autowired
    private lateinit var logger: Logger

    @Test
    fun `should write a log when a new customer is created`() {
        val event = CustomerCreated(UUID.randomUUID(), "John Doe", "john.doe@gmail.com", "ENGLISH", "PREMIUM")

        eventPublisher.publishEvent(event)

        verify { logger.info("event:'CustomerCreated', customer-id:'${event.id}'") }
    }
}

class TestConfig {
    @Bean
    fun logger(): Logger = spyk(NOPLogger.NOP_LOGGER)
}