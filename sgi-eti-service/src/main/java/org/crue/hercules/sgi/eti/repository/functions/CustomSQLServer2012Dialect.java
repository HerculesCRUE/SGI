package org.crue.hercules.sgi.eti.repository.functions;

import org.hibernate.dialect.SQLServer2012Dialect;
import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.type.StandardBasicTypes;

public class CustomSQLServer2012Dialect extends SQLServer2012Dialect {

  public CustomSQLServer2012Dialect() {
    super();
    registerFunction("remove_accents", new EmptySQLFunction("remove_accents"));
    registerFunction("remove_html_tags",
        new SQLFunctionTemplate(StandardBasicTypes.STRING, "stripHtmlFunction(?1)"));
    registerFunction("search_in_value_of_json",
        new SQLFunctionTemplate(StandardBasicTypes.STRING, "getAllJSONValuesFunction(?1)"));
  }
}