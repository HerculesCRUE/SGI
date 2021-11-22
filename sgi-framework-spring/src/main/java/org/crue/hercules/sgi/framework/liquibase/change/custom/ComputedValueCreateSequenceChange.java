package org.crue.hercules.sgi.framework.liquibase.change.custom;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import liquibase.change.core.CreateSequenceChange;
import liquibase.change.custom.CustomTaskChange;
import liquibase.database.Database;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.CustomChangeException;
import liquibase.exception.DatabaseException;
import liquibase.exception.SetupException;
import liquibase.exception.UnexpectedLiquibaseException;
import liquibase.executor.ExecutorService;
import liquibase.resource.ResourceAccessor;
import liquibase.statement.SqlStatement;
import lombok.extern.slf4j.Slf4j;

/**
 * Liquibae {@link CustomTaskChange} that allows creation secuences based on
 * computed (from database query) values.
 */
@Slf4j
public class ComputedValueCreateSequenceChange extends CreateSequenceChange implements CustomTaskChange {

  private String computedStartValue;
  private String coputedIncrementBy;
  private String computedMaxValue;
  private String computedMinValue;

  /**
   * Gets the SQL to exec for setting the sequence start value.
   * 
   * @return the start value SQL or null if not set
   */
  public String getComputedStartValue() {
    return computedStartValue;
  }

  /**
   * Sets the SQL to exec for setting the sequence start value.
   * 
   * @param computedStartValue the start value SQL
   */
  public void setComputedStartValue(String computedStartValue) {
    this.computedStartValue = computedStartValue;
  }

  /**
   * Gets the SQL to exec for setting the sequence increment value.
   * 
   * @return the increment value SQL or null if not set
   */
  public String getCoputedIncrementBy() {
    return coputedIncrementBy;
  }

  /**
   * Sets the SQL to exec for setting the sequence increment value.
   * 
   * @param coputedIncrementBy the increment value SQL
   */
  public void setCoputedIncrementBy(String coputedIncrementBy) {
    this.coputedIncrementBy = coputedIncrementBy;
  }

  /**
   * Gets the SQL to exec for setting the sequence max value.
   * 
   * @return the max value SQL or null if not set
   */
  public String getComputedMaxValue() {
    return computedMaxValue;
  }

  /**
   * Sets the SQL to exec for setting the sequence max value.
   * 
   * @param computedMaxValue the max value SQL
   */
  public void setComputedMaxValue(String computedMaxValue) {
    this.computedMaxValue = computedMaxValue;
  }

  /**
   * Gets the SQL to exec for setting the sequence min value.
   * 
   * @return the min value SQL or null if not set
   */
  public String getComputedMinValue() {
    return computedMinValue;
  }

  /**
   * Sets the SQL to exec for setting the sequence min value.
   * 
   * @param computedMinValue the min value SQL
   */
  public void setComputedMinValue(String computedMinValue) {
    this.computedMinValue = computedMinValue;
  }

  @Override
  public void execute(Database database) throws CustomChangeException {
    if (computedStartValue != null) {
      setStartValue(computeValue(database, computedStartValue));
    }
    if (coputedIncrementBy != null) {
      setIncrementBy(computeValue(database, coputedIncrementBy));
    }
    if (computedMaxValue != null) {
      setMaxValue(computeValue(database, computedMaxValue));
    }
    if (computedMinValue != null) {
      setMinValue(computeValue(database, computedMinValue));
    }

    SqlStatement[] statements = generateStatements(database);
    for (SqlStatement statement : statements) {
      try {
        ExecutorService.getInstance().getExecutor(database).execute(statement);
      } catch (DatabaseException e) {
        throw new UnexpectedLiquibaseException(e);
      }
    }

  }

  BigInteger computeValue(Database database, String sql) throws CustomChangeException {
    JdbcConnection dbConnection = (JdbcConnection) database.getConnection();
    PreparedStatement selectStatement = null;
    ResultSet results;

    try {
      log.info(sql);
      selectStatement = dbConnection.prepareStatement(sql);
      results = selectStatement.executeQuery();
      if (!results.next()) {
        throw new UnexpectedLiquibaseException("No results found");
      }
      BigDecimal value = results.getBigDecimal(1);
      if (value == null) {
        value = BigDecimal.valueOf(0l);
      }
      return value.toBigInteger();
    } catch (Exception e) {
      throw new CustomChangeException("Cannot create sequence", e);
    } finally {
      try {
        if (selectStatement != null)
          selectStatement.close();
      } catch (SQLException e) {
        log.warn("Database error: ", e);
      }
    }
  }

  @Override
  public void setUp() throws SetupException {
    // Do nothing

  }

  @Override
  public void setFileOpener(ResourceAccessor resourceAccessor) {
    // Do nothing

  }

}
