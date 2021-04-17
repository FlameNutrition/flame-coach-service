package com.coach.flame.testing.integration.base

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import java.sql.Connection
import java.sql.SQLException
import javax.sql.DataSource

/**
 * Based in the following explanation and code
 * https://dev.to/henrykeys/don-t-use-transactional-in-tests-40eb
 * https://github.com/Henry-Keys/avoid-transactional-in-tests/blob/without-transactional-replacement/src/test/kotlin/com/example/avoidtransactional/controllers/CartItemsControllerTests.kt
 */
class SQLClean {

    @Autowired
    private lateinit var dataSource: DataSource

    companion object {
        private val LOGGER = LoggerFactory.getLogger(SQLClean::class.java)

        private const val REFERENTIAL_INTEGRITY_DISABLE_QUERY = "SET REFERENTIAL_INTEGRITY FALSE"
        private const val REFERENTIAL_INTEGRITY_ENABLE_QUERY = "SET REFERENTIAL_INTEGRITY TRUE"

        private val TABLES_TO_IGNORE = listOf(
            TableData("databasechangelog"),
            TableData("databasechangeloglock")
        )
    }

    data class TableData(val name: String, val schema: String? = "public") {
        val fullyQualifiedTableName =
            if (schema != null) "$schema.$name" else name
    }

    @Throws(Exception::class)
    fun beforeEach() {
        cleanDatabase(dataSource)
    }

    private fun cleanDatabase(dataSource: DataSource) {
        try {
            dataSource.connection.use { connection ->
                connection.autoCommit = false
                val tablesToClean = loadTablesToClean(connection)
                cleanTablesData(tablesToClean, connection)
                connection.commit()
            }
        } catch (e: SQLException) {
            LOGGER.error(String.format("Failed to clean database due to error: \"%s\"", e.message))
            e.printStackTrace()
        }
    }

    @Throws(SQLException::class)
    private fun loadTablesToClean(connection: Connection): List<TableData> {
        val databaseMetaData = connection.metaData

        val resultSet = databaseMetaData.getTables(
            connection.catalog, null, null, arrayOf("TABLE"))

        val tablesToClean = mutableListOf<TableData>()
        while (resultSet.next()) {
            val tableName = resultSet.getString("TABLE_NAME")
                .split("_")
                .joinToString("_") { value -> value.toLowerCase().capitalize() }

            val table = TableData(
                schema = resultSet.getString("TABLE_SCHEMA"),
                name = tableName
            )

            if (!TABLES_TO_IGNORE.contains(table)) {
                tablesToClean.add(table)
            }
        }

        LOGGER.debug("opr='loadTablesToClean', msg='Tables to clean', tatbles={}", tablesToClean)

        return tablesToClean
    }

    @Throws(SQLException::class)
    private fun cleanTablesData(tablesNames: List<TableData>, connection: Connection) {
        if (tablesNames.isEmpty()) {
            return
        }

        connection.prepareStatement(REFERENTIAL_INTEGRITY_DISABLE_QUERY).execute()

        for (i in tablesNames.indices) {
            val statement = "TRUNCATE TABLE ${tablesNames[i].fullyQualifiedTableName} RESTART IDENTITY"
            connection.prepareStatement(statement).execute()
            LOGGER.debug("opr='cleanTablesData', msg='Statement ran', statement={}", statement)
        }

        connection.prepareStatement(REFERENTIAL_INTEGRITY_ENABLE_QUERY).execute()

    }

}
