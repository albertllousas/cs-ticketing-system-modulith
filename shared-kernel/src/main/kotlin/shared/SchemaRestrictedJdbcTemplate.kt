package shared

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.StatementCallback
import javax.sql.DataSource

class SchemaRestrictedJdbcTemplate(dataSource: DataSource, private val schema: String) : JdbcTemplate(dataSource) {

    override fun <T> execute(callback: StatementCallback<T>): T =
        // Set the schema at the start of every query/operation
        super.execute(StatementCallback { statement ->
            statement.execute("SET search_path TO $schema")
            callback.doInStatement(statement)
        })
}
