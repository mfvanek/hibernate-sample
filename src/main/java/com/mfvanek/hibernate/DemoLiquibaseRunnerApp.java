package com.mfvanek.hibernate;

import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.NoSuchElementException;

public class DemoLiquibaseRunnerApp {

    /**
     * <configuration>
     *   <changeLogFile>src/main/resources/changelog.xml</changeLogFile>
     *   <driver>com.mysql.jdbc.Driver</driver>
     *   <url>jdbc:mysql://localhost:3306/myApp?createDatabaseIfNotExist=true</url>
     *   jdbc:postgresql://localhost:5432/postgres?currentSchema=foo
     *   <username>liquibaseTest</username>
     *   <password>pass</password>
     * </configuration>
     * @param args
     */
    public static void main(String[] args) {
        Connection c = null;
        try {
            //c = DriverManager.getConnection(DataSources.PROPERTIES.getProperty("jdbc.url"),
            //        DataSources.PROPERTIES.getProperty("jdbc.username"),
            //        DataSources.PROPERTIES.getProperty("jdbc.password"));

            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(c));
            //log.info(DataSources.CHANGELOG_MASTER);
            final Liquibase liquibase = new Liquibase("db/changelog/liquibase-changelog.xml", new ClassLoaderResourceAccessor(), database);
            //liquibase.update("main");
            liquibase.update(new Contexts(), new LabelExpression());
        } catch (/*SQLException | */LiquibaseException e) {
            e.printStackTrace();
            throw new NoSuchElementException(e.getMessage());
        } finally {
            if (c != null) {
                try {
                    c.rollback();
                    c.close();
                } catch (SQLException e) {
                    //nothing to do
                }
            }
        }
    }
}
