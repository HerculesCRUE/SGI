package org.crue.hercules.sgi.cnf.liquibase;

import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.springframework.util.ResourceUtils;

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

/**
 * Liquibae {@link CustomTaskChange} that update blob resources values if they
 * won't match default_value
 */
@Slf4j
public class UpdateResourcesValue implements CustomTaskChange {

  private ResourceAccessor resourceAccessor;

  private String tableName = "resources";
  private String resourceColumnName = "name";
  private String valueColumnName = "value";
  private String defaultValueColumnName = "default_value";
  private String resourceName;
  private String valueBlobFile;

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
      StringBuilder columnNames = new StringBuilder();
      columnNames.append(resourceColumnName);
      columnNames.append(",");
      columnNames.append(defaultValueColumnName);

      InputStream in = new FileInputStream(ResourceUtils.getFile(valueBlobFile));

      StringBuilder update = new StringBuilder();
      update.append("UPDATE ");
      update.append(
          database.escapeTableName(database.getDefaultCatalogName(), database.getDefaultSchemaName(), tableName));
      update.append(" SET ");
      update.append(database.escapeColumnName(database.getDefaultCatalogName(), database.getDefaultSchemaName(),
          tableName, valueColumnName));
      update.append(" = ? ");
      update.append("WHERE ");
      update.append(database.escapeColumnName(database.getDefaultCatalogName(), database.getDefaultSchemaName(),
          tableName, resourceColumnName));
      update.append(" = ? ");
      update.append(" AND ");
      if (database.getDatabaseProductName().equalsIgnoreCase(OracleDatabase.PRODUCT_NAME)) {
        update.append("dbms_lob.compare(");
        update.append(database.escapeColumnName(database.getDefaultCatalogName(), database.getDefaultSchemaName(),
            tableName, defaultValueColumnName));
        update.append(",");
        update.append(database.escapeColumnName(database.getDefaultCatalogName(), database.getDefaultSchemaName(),
            tableName, valueColumnName));
        update.append(") = 0");
      } else {
        update
            .append(database.escapeColumnName(database.getDefaultCatalogName(), database.getDefaultSchemaName(),
                tableName, defaultValueColumnName));
        update.append("=");
        update.append(database.escapeColumnName(database.getDefaultCatalogName(), database.getDefaultSchemaName(),
            tableName, valueColumnName));
      }
      log.info(update.toString());
      updateStatement = dbConnection.prepareStatement(update.toString());
      log.debug("Applaying column parameter = 1 for column {}", defaultValueColumnName);
      if (database.getDatabaseProductName().equalsIgnoreCase(PostgresDatabase.PRODUCT_NAME)) {
        updateStatement.setBinaryStream(1, in);
      } else {
        updateStatement.setBlob(1, in);
      }

      log.debug("Applaying column parameter = 2 for column {}", resourceColumnName);
      updateStatement.setString(2, resourceName);

      executePreparedStatement(updateStatement);
      updateStatement.close();
      updateStatement = null;
    } catch (Exception e) {
      throw new CustomChangeException("Cannot update", e);
    } finally {
      try {
        if (updateStatement != null)
          updateStatement.close();
      } catch (SQLException e) {
        log.warn("Database error: ", e);
      }
    }
  }

  /**
   * Execute the provided {@link PreparedStatement}.
   * 
   * @param stmt the {@link PreparedStatement} to execute
   * @throws SQLException if there is an error executing the
   *                      {@link PreparedStatement}
   */
  protected void executePreparedStatement(PreparedStatement stmt) throws SQLException {
    // if execute returns false, we can retrieve the affected rows count
    // (true used when resultset is returned)
    if (!stmt.execute()) {
      int partialUpdateCount = stmt.getUpdateCount();
      if (partialUpdateCount != -1)
        updateCount += partialUpdateCount;
    } else {
      int partialUpdateCount = 0;
      // cycle for retrieving row counts from all statements
      do {
        if (!stmt.getMoreResults()) {
          partialUpdateCount = stmt.getUpdateCount();
          if (partialUpdateCount != -1)
            updateCount += partialUpdateCount;
        }
      } while (updateCount != -1);
    }
  }

  /**
   * Get the table name for the columns content to be updated.
   * <code>resources</code> by default.
   * 
   * @return the table name
   */
  public String getTableName() {
    return tableName;
  }

  /**
   * Set the table name for the columns content to be updated.
   * 
   * @param tableName the table name
   */
  public void setTableName(String tableName) {
    this.tableName = tableName;
  }

  /**
   * Get the default value column name. <code>default_value</code> by default.
   * 
   * @return the column name
   */
  public String getDefaultValueColumnName() {
    return defaultValueColumnName;
  }

  /**
   * Set the default value column name.
   * 
   * @param defaultValueColumnName the column name
   */
  public void setDefaultValueColumnName(String defaultValueColumnName) {
    this.defaultValueColumnName = defaultValueColumnName;
  }

  /**
   * Get the value blob file path for the update.
   * 
   * @return the blob file path
   */
  public String getValueBlobFile() {
    return valueBlobFile;
  }

  /**
   * Set the value blob file path for the update.
   * 
   * @param valueBlobFile blob file path
   */
  public void setValueBlobFile(String valueBlobFile) {
    this.valueBlobFile = valueBlobFile;
  }

  /**
   * Get the column name for resource name that identifies uniquely a single
   * record. <code>name</code> by default.
   * 
   * @return the resource name column name
   */
  public String getResourceColumnName() {
    return resourceColumnName;
  }

  /**
   * Get the column name for resource name that identifies uniquely a single
   * record.
   * 
   * @param resourceColumnName the resource name column name
   */
  public void setResourceColumnName(String resourceColumnName) {
    this.resourceColumnName = resourceColumnName;
  }

  /**
   * Get the resource name value where update will be applied.
   * 
   * @return the resource name value
   */
  public String getResourceName() {
    return resourceName;
  }

  /**
   * Set the resource name value where update will be applied.
   * 
   * @param resourceName the resource name value
   */
  public void setResourceName(String resourceName) {
    this.resourceName = resourceName;
  }
}
