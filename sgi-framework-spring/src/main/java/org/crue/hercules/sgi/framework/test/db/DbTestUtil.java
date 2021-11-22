package org.crue.hercules.sgi.framework.test.db;

import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Database Test Utility Class.
 */
@Slf4j
public final class DbTestUtil {
  /**
   * Configuration key for the SQL template used to reset a database sequence
   * (test.reset.sql.template).
   */
  public static final String PROPERTY_TEST_RESET_SQL_TEMPLATE = "test.reset.sql.template";

  private DbTestUtil() {
  }

  /**
   * Reset the sequences matching the provided sequence names using the Spring
   * configured {@link DataSource}.
   * 
   * @param applicationContext the Spring {@link ApplicationContext}
   * @param sequenceNames      the sequences to reset
   * @throws SQLException if error occurs
   */
  public static void resetSequence(ApplicationContext applicationContext, String... sequenceNames) throws SQLException {
    DataSource dataSource = applicationContext.getBean(DataSource.class);
    String resetSqlTemplate = getResetSqlTemplate(applicationContext);
    try (Connection dbConnection = dataSource.getConnection()) {
      // Create SQL statements that reset the sequence and invoke it
      for (String resetSqlArgument : sequenceNames) {
        try (Statement statement = dbConnection.createStatement()) {
          String resetSql = String.format(resetSqlTemplate, resetSqlArgument);
          log.info(resetSql);
          statement.execute(resetSql);
        }
      }
    }
  }

  private static String getResetSqlTemplate(ApplicationContext applicationContext) {
    // Read the SQL template from the properties file
    Environment environment = applicationContext.getBean(Environment.class);
    return environment.getRequiredProperty(PROPERTY_TEST_RESET_SQL_TEMPLATE);
  }
}