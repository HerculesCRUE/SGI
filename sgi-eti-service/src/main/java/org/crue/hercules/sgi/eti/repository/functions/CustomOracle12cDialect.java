package org.crue.hercules.sgi.eti.repository.functions;

import org.hibernate.dialect.Oracle12cDialect;

public class CustomOracle12cDialect extends Oracle12cDialect {

  public CustomOracle12cDialect() {
    super();
    registerFunction("remove_accents", new RemoveAccentsOracleAndPostgreSQLFunction("remove_accents"));
    registerFunction("remove_html_tags", new RemoveHTMLOracleSQLFunction("remove_html_tags"));
    registerFunction("search_in_value_of_json",
        new SearchValuePropertyJSONOracleSQLFunction("search_in_value_of_json"));
  }
}