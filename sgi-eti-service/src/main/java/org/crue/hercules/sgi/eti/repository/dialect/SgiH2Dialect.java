package org.crue.hercules.sgi.eti.repository.dialect;

import org.crue.hercules.sgi.eti.repository.functions.EmptySQLFunction;
import org.crue.hercules.sgi.eti.repository.functions.RemoveAccentsOracleAndPostgreSQLFunction;
import org.crue.hercules.sgi.eti.repository.functions.RemoveHTMLPostgreSQLFunction;
import org.hibernate.dialect.H2Dialect;

public class SgiH2Dialect extends H2Dialect {

  public SgiH2Dialect() {
    super();
    registerFunction("remove_accents", new RemoveAccentsOracleAndPostgreSQLFunction("remove_accents"));
    registerFunction("remove_html_tags", new RemoveHTMLPostgreSQLFunction("remove_html_tags"));
    registerFunction("search_in_value_of_json",
        new EmptySQLFunction("search_in_value_of_json"));
  }
}
