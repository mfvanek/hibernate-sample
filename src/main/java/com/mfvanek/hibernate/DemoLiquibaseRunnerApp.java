package com.mfvanek.hibernate;

import com.mfvanek.hibernate.consts.Const;
import com.mfvanek.hibernate.utils.PropertiesUtil;
import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.Scope;
import liquibase.database.Database;
import liquibase.database.DatabaseConnection;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Slf4j
public class DemoLiquibaseRunnerApp {

    // We can't automatically create a database from Java code
    public static void main(final String[] args) {
        try (Connection connection = getConnection()) {
            createSchema(connection, Const.SCHEMA_NAME);
            updateDatabaseStructure(connection);
        } catch (SQLException | ClassNotFoundException e) {
            log.error(e.getMessage(), e);
        }
    }

    private static Connection getConnection() throws SQLException, ClassNotFoundException {
        final Properties properties = PropertiesUtil.load();
        final String url = properties.getProperty(Const.URL_PROPERTY_NAME);
        final String username = properties.getProperty(Const.USERNAME_PROPERTY_NAME);
        final String password = properties.getProperty(Const.PASSWORD_PROPERTY_NAME);
        Class.forName(properties.getProperty(Const.DRIVER_PROPERTY_NAME));
        return DriverManager.getConnection(url, username, password);
    }

    private static void createSchema(final Connection connection, final String schemaName) {
        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE SCHEMA IF NOT EXISTS " + schemaName);
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
    }

    @SneakyThrows
    private static void updateDatabaseStructure(final Connection connection) {
        final Map<String, Object> config = new HashMap<>();
        Scope.child(config, () -> {
            final DatabaseConnection dbConnection = new JdbcConnection(connection);
            final Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(dbConnection);
            database.setDefaultSchemaName(Const.SCHEMA_NAME);
            database.setLiquibaseSchemaName(Const.SCHEMA_NAME);
            final Liquibase liquibase = new Liquibase(Const.LIQUIBASE_CHANGELOG_FILE, new ClassLoaderResourceAccessor(), database);
            liquibase.update(new Contexts(), new LabelExpression());
        });
    }
}
