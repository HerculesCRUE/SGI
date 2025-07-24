package org.crue.hercules.sgi.cnf.liquibase;

import java.io.StringReader;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import liquibase.change.custom.CustomTaskChange;
import liquibase.database.Database;
import liquibase.database.core.OracleDatabase;
import liquibase.database.core.PostgresDatabase;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.CustomChangeException;
import liquibase.exception.SetupException;
import liquibase.exception.ValidationErrors;
import liquibase.resource.ResourceAccessor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UpdateConfigValue implements CustomTaskChange {

  private ResourceAccessor resourceAccessor;

  private String tableName = "config";
  private String configColumnName = "name";
  private String valueColumnName = "value";
  private String defaultValueColumnName = "default_value";
  private String configName;
  private String valueClobContent;

  private int updateCount = 0;

  @Override
  public String getConfirmationMessage() {
    return updateCount + " row(s) affected";
  }

  @Override
  public void setUp() throws SetupException {
    // Do nothing
  }

  @Override
  public void setFileOpener(ResourceAccessor resourceAccessor) {
    this.resourceAccessor = resourceAccessor;
  }

  @Override
  public ValidationErrors validate(Database database) {
    return null;
  }

  @Override
  public void execute(Database database) throws CustomChangeException {
    JdbcConnection dbConnection = (JdbcConnection) database.getConnection();
    PreparedStatement updateStatement = null;

    try {
      StringBuilder update = new StringBuilder();
      update.append("UPDATE ")
          .append(
              database.escapeTableName(database.getDefaultCatalogName(), database.getDefaultSchemaName(), tableName))
          .append(" SET ")
          .append(database.escapeColumnName(database.getDefaultCatalogName(), database.getDefaultSchemaName(),
              tableName, valueColumnName))
          .append(" = ? WHERE ")
          .append(database.escapeColumnName(database.getDefaultCatalogName(), database.getDefaultSchemaName(),
              tableName, configColumnName))
          .append(" = ? AND ");

      if (database.getDatabaseProductName().equalsIgnoreCase(OracleDatabase.PRODUCT_NAME)) {
        update.append("dbms_lob.compare(")
            .append(database.escapeColumnName(database.getDefaultCatalogName(), database.getDefaultSchemaName(),
                tableName, defaultValueColumnName))
            .append(", ")
            .append(database.escapeColumnName(database.getDefaultCatalogName(), database.getDefaultSchemaName(),
                tableName, valueColumnName))
            .append(") = 0");
      } else {
        update
            .append(database.escapeColumnName(database.getDefaultCatalogName(), database.getDefaultSchemaName(),
                tableName, defaultValueColumnName))
            .append(" = ")
            .append(database.escapeColumnName(database.getDefaultCatalogName(), database.getDefaultSchemaName(),
                tableName, valueColumnName));
      }

      log.info("SQL: {}", update.toString());
      updateStatement = dbConnection.prepareStatement(update.toString());

      // Set CLOB content
      log.debug("Setting column parameter = 1 for {}", valueColumnName);
      if (database.getDatabaseProductName().equalsIgnoreCase(PostgresDatabase.PRODUCT_NAME)) {
        updateStatement.setString(1, valueClobContent);
      } else {
        updateStatement.setCharacterStream(1, new StringReader(valueClobContent));
      }

      log.debug("Setting column parameter = 2 for {}", configColumnName);
      updateStatement.setString(2, configName);

      executePreparedStatement(updateStatement);
    } catch (Exception e) {
      throw new CustomChangeException("Cannot update CLOB value", e);
    } finally {
      try {
        if (updateStatement != null)
          updateStatement.close();
      } catch (SQLException e) {
        log.warn("Database error: ", e);
      }
    }
  }

  protected void executePreparedStatement(PreparedStatement stmt) throws SQLException {
    if (!stmt.execute()) {
      int partialUpdateCount = stmt.getUpdateCount();
      if (partialUpdateCount != -1)
        updateCount += partialUpdateCount;
    } else {
      int partialUpdateCount = 0;
      do {
        if (!stmt.getMoreResults()) {
          partialUpdateCount = stmt.getUpdateCount();
          if (partialUpdateCount != -1)
            updateCount += partialUpdateCount;
        }
      } while (partialUpdateCount != -1);
    }
  }

  // Getters and Setters

  public String getTableName() {
    return tableName;
  }

  public void setTableName(String tableName) {
    this.tableName = tableName;
  }

  public String getDefaultValueColumnName() {
    return defaultValueColumnName;
  }

  public void setDefaultValueColumnName(String defaultValueColumnName) {
    this.defaultValueColumnName = defaultValueColumnName;
  }

  public String getValueClobContent() {
    return valueClobContent;
  }

  public void setValueClobContent(String valueClobContent) {
    this.valueClobContent = valueClobContent;
  }

  public String getConfigColumnName() {
    return configColumnName;
  }

  public void setConfigColumnName(String resourceColumnName) {
    this.configColumnName = resourceColumnName;
  }

  public String getConfigName() {
    return configName;
  }

  public void setConfigName(String configName) {
    this.configName = configName;
  }

  public String getValueColumnName() {
    return valueColumnName;
  }

  public void setValueColumnName(String valueColumnName) {
    this.valueColumnName = valueColumnName;
  }
}
