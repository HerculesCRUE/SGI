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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
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
    // Connection con = ((JdbcConnection)
    // database.getConnection()).getUnderlyingConnection();
    JdbcConnection db_connection = (JdbcConnection) database.getConnection();
    PreparedStatement selectStatement = null;
    PreparedStatement updateStatement = null;
    ResultSet results;

    try {
      StringBuilder select = new StringBuilder();
      select.append("SELECT ");
      select.append(idColumnName);
      select.append(", ");
      select.append(jsonColumnName);
      select.append(" FROM ");
      select.append(tableName);
      if (where != null) {
        select.append(" WHERE ");
        select.append(where);
      }
      log.info(select.toString());
      selectStatement = db_connection.prepareStatement(select.toString());
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
          update.append("UPDATE " + tableName);
          update.append(" SET ");
          update.append(jsonColumnName + " = ? ");
          update.append("WHERE " + idColumnName + " = ?");
          log.info(update.toString());
          updateStatement = db_connection.prepareStatement(update.toString());
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

  private JsonNode applyJslt(JsonNode jsonNode)
      throws CustomChangeException, JsonMappingException, JsonProcessingException {
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
          if (includeEmpty) {
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

  public String getTableName() {
    return tableName;
  }

  public void setTableName(String tableName) {
    this.tableName = tableName;
  }

  public String getJsonColumnName() {
    return jsonColumnName;
  }

  public void setJsonColumnName(String jsonColumnName) {
    this.jsonColumnName = jsonColumnName;
  }

  public String getJsltFile() {
    return jsltFile;
  }

  public void setJsltFile(String jsltFile) {
    this.jsltFile = jsltFile;
  }

  public String getIdColumnName() {
    return idColumnName;
  }

  public void setIdColumnName(String idColumnName) {
    this.idColumnName = idColumnName;
  }

  public String getWhere() {
    return where;
  }

  public void setWhere(String where) {
    this.where = where;
  }

  public Boolean getIncludeEmpty() {
    return includeEmpty;
  }

  public void setIncludeEmpty(Boolean includeEmpty) {
    this.includeEmpty = includeEmpty;
  }

}
