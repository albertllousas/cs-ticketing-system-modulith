package fixtures.containers

import com.zaxxer.hikari.HikariDataSource
import org.flywaydb.core.Flyway
import org.flywaydb.core.api.configuration.FluentConfiguration
import org.springframework.jdbc.core.JdbcTemplate
import org.testcontainers.containers.Network
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.containers.wait.strategy.Wait
import java.time.Duration
import javax.sql.DataSource

class Postgres(flywayLocationSchemasList: List<Pair<String, String>>) {

    val container: KtPostgreSQLContainer = KtPostgreSQLContainer()
        .withNetwork(Network.newNetwork())
        .withNetworkAliases("localhost")
        .withUsername("ticketingsystem")
        .withPassword("ticketingsystem")
        .withDatabaseName("ticketingsystem")
        .withStartupTimeout(Duration.ofSeconds(60))
        .waitingFor(Wait.forListeningPort())
        .also {
            it.start()
        }

    val datasource: DataSource = HikariDataSource().apply {
        driverClassName = org.postgresql.Driver::class.qualifiedName
        jdbcUrl = container.jdbcUrl
        username = container.username
        password = container.password
    }.also {
        flywayLocationSchemasList.forEach { (location, schema) ->
            val flyway = Flyway(
                FluentConfiguration()
                    .driver(org.postgresql.Driver::class.qualifiedName)
                    .dataSource(container.jdbcUrl, container.username, container.password)
                    .schemas(schema)
                    .locations(location)
                    .cleanDisabled(false)
            )
            flyway.clean()
            flyway.migrate()
        }
    }

    val jdbcTemplate: JdbcTemplate = JdbcTemplate(datasource)
}

class KtPostgreSQLContainer : PostgreSQLContainer<KtPostgreSQLContainer>("postgres:12")
