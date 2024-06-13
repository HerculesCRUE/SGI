package org.crue.hercules.sgi.eti.repository.functions;

import org.hibernate.dialect.Oracle12cDialect;
import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.type.StandardBasicTypes;

public class CustomOracle12cDialect extends Oracle12cDialect {

  public CustomOracle12cDialect() {
    super();
    registerFunction("remove_accents",
        new SQLFunctionTemplate(StandardBasicTypes.STRING, "ETI_SGI.REMOVE_ACCENTS(?1)"));
    registerFunction("remove_html_tags",
        new SQLFunctionTemplate(StandardBasicTypes.STRING, "ETI_SGI.STRIP_HTML_FUNCTION(?1)"));
    registerFunction("search_in_value_of_json",
        new SQLFunctionTemplate(StandardBasicTypes.STRING, "ETI_SGI.GET_ALL_JSON_VALUES_FUNCTION(?1)"));
  }
}