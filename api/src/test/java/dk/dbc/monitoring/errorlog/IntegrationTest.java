/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GPLv3
 * See license text in LICENSE.txt or at https://opensource.dbc.dk/licenses/gpl-3.0/
 */

package dk.dbc.monitoring.errorlog;

import dk.dbc.commons.persistence.TransactionScopedPersistenceContext;
import dk.dbc.commons.testcontainers.postgres.DBCPostgreSQLContainer;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.Before;
import org.junit.BeforeClass;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public abstract class IntegrationTest {
    static DBCPostgreSQLContainer ERROR_LOG_DB = startErrorLogDb();
    static DataSource datasource = ERROR_LOG_DB.datasource();
    static EntityManagerFactory entityManagerFactory = createEntityManagerFactory(ERROR_LOG_DB);

    @BeforeClass
    public static void setup() {
        entityManagerFactory = createEntityManagerFactory(ERROR_LOG_DB);
        createDatabase();
    }

    private static DBCPostgreSQLContainer startErrorLogDb() {
        DBCPostgreSQLContainer container = new DBCPostgreSQLContainer().withNetworkAliases("errorlog-db").withReuse(false);
        container.start();
        container.exposeHostPort();
        return container;
    }

    public static void createDatabase() {
        DatabaseMigrator databaseMigrator = new DatabaseMigrator(datasource);
        databaseMigrator.migrate();
    }

    public static EntityManagerFactory createEntityManagerFactory(DBCPostgreSQLContainer dbContainer) {
        return Persistence.createEntityManagerFactory("errorlogIT", dbContainer.entityManagerProperties());
    }

    public void executeScript(File script, Charset encoding) {
        try (
            Connection conn = datasource.getConnection();
            FileInputStream fstream = new FileInputStream(script);
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(new DataInputStream(fstream), encoding))) {
            final StringBuilder scriptLines = new StringBuilder();

            // Read file line by line, and append to StringBuilder
            String line;
            while ((line = reader.readLine()) != null) {
                scriptLines.append(line);
                scriptLines.append("\n");
            }
            conn.prepareStatement(scriptLines.toString()).executeUpdate();
        } catch (SQLException | IOException e) {
            throw new IllegalStateException(e);
        }
    }

    EntityManager entityManager;
    TransactionScopedPersistenceContext transaction;

    @Before
    public void resetDatabase() throws SQLException {
        try (Connection conn = datasource.getConnection();
             Statement statement = conn.createStatement()) {
            statement.executeUpdate("DELETE FROM errorlog");
            statement.executeUpdate("ALTER SEQUENCE errorlog_id_seq RESTART");
            executeScript(new File("src/test/resources/errorlog_testdata.sql"), StandardCharsets.UTF_8);
        }
    }

    @Before
    public void createEntityManager() {
        entityManager = entityManagerFactory.createEntityManager();
        transaction = new TransactionScopedPersistenceContext(entityManager);
    }
}
