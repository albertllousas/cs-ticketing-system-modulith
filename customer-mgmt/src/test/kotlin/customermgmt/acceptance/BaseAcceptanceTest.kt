package customermgmt.acceptance

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import customermgmt.acceptance.BaseAcceptanceTest.Initializer
import customermgmt.fixtures.TestModuleApp
import fixtures.TestEventListener
import fixtures.containers.Postgres
import io.restassured.RestAssured
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_CLASS
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = RANDOM_PORT)
@ContextConfiguration(
    initializers = [Initializer::class],
    classes = [TestModuleApp::class, TestEventListenerConfig::class]
)
@DirtiesContext(classMode = AFTER_CLASS)
abstract class BaseAcceptanceTest {

    init {
        RestAssured.defaultParser = io.restassured.parsing.Parser.JSON
    }

    @Autowired
    protected lateinit var testEventListener: TestEventListener

    @LocalServerPort
    protected val servicePort: Int = 0

    protected val mapper = jacksonObjectMapper().registerModule(JavaTimeModule())

    companion object {
        val postgres: Postgres = Postgres(
            flywayLocationSchemasList = listOf(Pair("db/migration/customermgmt", "customer_mgmt")),
        )
    }

    @BeforeEach
    fun setUp() {
        postgres.container.start()
        testEventListener.clear()
    }

    @AfterEach
    fun tearDown() {
        postgres.container.stop()
    }

    class Initializer : ApplicationContextInitializer<ConfigurableApplicationContext> {

        override fun initialize(configurableApplicationContext: ConfigurableApplicationContext) {
            TestPropertyValues.of(
                "spring.datasource.url=" + postgres.container.jdbcUrl,
                "spring.datasource.password=" + postgres.container.password,
                "spring.datasource.username=" + postgres.container.username,
                "spring.flyway.url=" + postgres.container.jdbcUrl,
                "spring.flyway.password=" + postgres.container.password,
                "spring.flyway.user=" + postgres.container.username,
            ).applyTo(configurableApplicationContext.environment)
        }
    }
}

@TestConfiguration
class TestEventListenerConfig {
    @Bean
    fun testEventListener() = TestEventListener()
}
