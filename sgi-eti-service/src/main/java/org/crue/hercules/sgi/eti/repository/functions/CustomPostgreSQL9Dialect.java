package org.crue.hercules.sgi.eti.repository.functions;

import org.hibernate.dialect.PostgreSQL94Dialect;

public class CustomPostgreSQL9Dialect extends PostgreSQL94Dialect {

  public CustomPostgreSQL9Dialect() {
    super();
    registerFunction("remove_accents", new RemoveAccentsOracleAndPostgreSQLFunction("remove_accents"));
    registerFunction("remove_html_tags", new RemoveHTMLPostgreSQLFunction("remove_html_tags"));
    registerFunction("search_in_value_of_json",
        new EmptySQLFunction("search_in_value_of_json"));
  }
}