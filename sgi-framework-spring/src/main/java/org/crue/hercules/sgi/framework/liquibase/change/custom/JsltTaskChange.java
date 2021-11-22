package org.crue.hercules.sgi.framework.liquibase.change.custom;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Set;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.schibsted.spt.data.jslt.Expression;
import com.schibsted.spt.data.jslt.Parser;

import liquibase.change.custom.CustomTaskChange;
import liquibase.database.Database;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.CustomChangeException;
import liquibase.exception.SetupException;
import liquibase.exception.ValidationErrors;
import liquibase.resource.ResourceAccessor;
import lombok.extern.slf4j.Slf4j;

/**
 * Liquibae {@link CustomTaskChange} that allows applying a JSON query and
 * transformation language (JSLT) to specified Database Table Column containing
 * JSON content.
 */
@Slf4j
public class JsltTaskChange implements CustomTaskChange {

  private ResourceAccessor resourceAccessor;
  private String tableName;
  private String idColumnName;
  private String jsonColumnName;
  private String where;
  private String jsltFile;
  private Boolean includeEmpty = true;

  private static ObjectMapper mapper = new ObjectMapper();
  private int updateCount = 0;
  private Expression jsltExpression;

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
    PreparedStatement selectStatement = null;
    PreparedStatement updateStatement = null;
    ResultSet results;

    try {
      StringBuilder select = new StringBuilder();
      StringBuilder columnNames = new StringBuilder();
      columnNames.append(idColumnName);
      columnNames.append(",");
      columnNames.append(jsonColumnName);

      select.append("SELECT ");
      select.append(database.escapeColumnNameList(columnNames.toString()));
      select.append(" FROM ");
      select.append(
          database.escapeTableName(database.getDefaultCatalogName(), database.getDefaultSchemaName(), tableName));
      if (where != null) {
        select.append(" WHERE ");
        select.append(where);
      }
      log.info(select.toString());
      selectStatement = dbConnection.prepareStatement(select.toString());
      results = selectStatement.executeQuery();

      while (results.next()) {
        ResultSetMetaData meta = results.getMetaData();
        int colType = meta.getColumnType(1);
        Object rowid = null;
        if (colType == Types.VARCHAR) {
          rowid = results.getString(idColumnName);
        } else {
          rowid = results.getBigDecimal(idColumnName);
        }
        String json = results.getString(jsonColumnName);
        JsonNode jsonNode = mapper.readTree(json);
        JsonNode newJsonNode = applyJslt(jsonNode);
        if (!jsonNode.equals(newJsonNode)) {
          String newJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(newJsonNode);
          StringBuilder update = new StringBuilder();
          update.append("UPDATE ");
          update.append(
              database.escapeTableName(database.getDefaultCatalogName(), database.getDefaultSchemaName(), tableName));
          update.append(" SET ");
          update.append(database.escapeColumnName(database.getDefaultCatalogName(), database.getDefaultSchemaName(),
              tableName, jsonColumnName));
          update.append(" = ? ");
          update.append("WHERE ");
          update.append(database.escapeColumnName(database.getDefaultCatalogName(), database.getDefaultSchemaName(),
              tableName, idColumnName));
          update.append(" = ?");
          log.info(update.toString());
          updateStatement = dbConnection.prepareStatement(update.toString());
          log.debug("Applaying column parameter = 1 for column {}", jsonColumnName);
          log.debug("value is string = " + newJson);
          updateStatement.setString(1, newJson);
          log.debug("Applaying column parameter = 2 for column {}", idColumnName);
          if (colType == Types.VARCHAR) {
            log.debug("value is string = " + rowid);
            updateStatement.setString(2, (String) rowid);
          } else {
            log.debug("value is numeric = " + rowid);
            updateStatement.setBigDecimal(2, (BigDecimal) rowid);
            rowid = results.getBigDecimal(idColumnName);
          }

          executePreparedStatement(updateStatement);
          updateStatement.close();
          updateStatement = null;
        }
      }
      selectStatement.close();
      selectStatement = null;
    } catch (Exception e) {
      throw new CustomChangeException("Cannot update", e);
    } finally {
      try {
        if (selectStatement != null)
          selectStatement.close();
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

  private JsonNode applyJslt(JsonNode jsonNode) throws CustomChangeException {
    Expression expression = getJsltExpression();
    if (expression != null) {
      return expression.apply(jsonNode);
    } else {
      return jsonNode;
    }
  }

  private Expression getJsltExpression() throws CustomChangeException {
    if (jsltExpression == null) {
      try {
        Set<InputStream> streams = resourceAccessor.getResourcesAsStream(jsltFile);

        if (streams.size() != 1) {
          throw new CustomChangeException("One JSLT file must be provided");
        }

        for (InputStream stream : streams) {
          if (Boolean.TRUE.equals(includeEmpty)) {
            this.jsltExpression = new Parser(new InputStreamReader(stream)).withObjectFilter("true").compile();
          } else {
            this.jsltExpression = new Parser(new InputStreamReader(stream)).compile();
          }
        }
      } catch (Exception e) {
        throw new CustomChangeException(e);
      }
    }
    return jsltExpression;
  }

  /**
   * Get the table name for the columns content to be updated.
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
   * Get the column name for the column content to be updated.
   * 
   * @return the column name
   */
  public String getJsonColumnName() {
    return jsonColumnName;
  }

  /**
   * Set the column name for the column content to be updated.
   * 
   * @param jsonColumnName the column name
   */
  public void setJsonColumnName(String jsonColumnName) {
    this.jsonColumnName = jsonColumnName;
  }

  /**
   * Get the jslt file path for the transformation rules.
   * 
   * @return the jslt file path
   */
  public String getJsltFile() {
    return jsltFile;
  }

  /**
   * Set the jslt file path for the transformation rules.
   * 
   * @param jsltFile the jslt file path
   */
  public void setJsltFile(String jsltFile) {
    this.jsltFile = jsltFile;
  }

  /**
   * Get the column name for the columns that uniquely identifies a single record
   * in the table for the content to be updated.
   * 
   * @return the identifier column name
   */
  public String getIdColumnName() {
    return idColumnName;
  }

  /**
   * Set the column name for the columns that uniquely identifies a single record
   * in the table for the content to be updated.
   * 
   * @param idColumnName the identifier column name
   */
  public void setIdColumnName(String idColumnName) {
    this.idColumnName = idColumnName;
  }

  /**
   * Get the where clause to be used for filtering the rows with content to be
   * updated.
   * 
   * @return the where clause
   */
  public String getWhere() {
    return where;
  }

  /**
   * Set the where clause to be used for filtering the rows with content to be
   * updated.
   * 
   * @param where the where clause
   */
  public void setWhere(String where) {
    this.where = where;
  }

  /**
   * Get if the JSLT transformation should include output object keys where the
   * value is null or an empty object or array.
   * 
   * @return true if empty keys should be included
   */
  public Boolean getIncludeEmpty() {
    return includeEmpty;
  }

  /**
   * Set if the JSLT transformation should include output object keys where the
   * value is null or an empty object or array.
   * 
   * @param includeEmpty true if empty keys should be included
   */
  public void setIncludeEmpty(Boolean includeEmpty) {
    this.includeEmpty = includeEmpty;
  }

}
