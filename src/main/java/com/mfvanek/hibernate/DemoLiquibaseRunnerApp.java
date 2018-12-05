package com.mfvanek.hibernate;

import com.mfvanek.hibernate.consts.Const;
import com.mfvanek.hibernate.utils.PropertiesUtil;
import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DemoLiquibaseRunnerApp {

    private static final Logger logger = LoggerFactory.getLogger(DemoLiquibaseRunnerApp.class);

    public static void main(String[] args) {
        final Properties properties = PropertiesUtil.load();
        final String sourceUrl = properties.getProperty(Const.URL_PROPERTY_NAME);
        // final String url = sourceUrl + "?createDatabaseIfNotExist=true&currentSchema=" + Const.SCHEMA_NAME; TODO
        final String url = sourceUrl + "?createDatabaseIfNotExist=true&currentSchema=" + Const.SCHEMA_NAME + "2";
        final String username = properties.getProperty(Const.USERNAME_PROPERTY_NAME);
        final String password = properties.getProperty(Const.PASSWORD_PROPERTY_NAME);

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
            final Liquibase liquibase = new Liquibase(Const.LIQUIBASE_CHANGELOG_FILE, new ClassLoaderResourceAccessor(), database);
            liquibase.update(new Contexts(), new LabelExpression());
        } catch (SQLException | LiquibaseException e) {
            logger.error(e.getMessage(), e);
        }
    }
}
