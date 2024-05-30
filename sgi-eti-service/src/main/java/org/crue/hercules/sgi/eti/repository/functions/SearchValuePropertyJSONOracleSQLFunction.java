package org.crue.hercules.sgi.eti.repository.functions;

import java.util.List;

import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.type.Type;

public class SearchValuePropertyJSONOracleSQLFunction extends StandardSQLFunction {

  public SearchValuePropertyJSONOracleSQLFunction(String name) {
    super(name);
  }

  @Override
  public String render(Type firstArgumentType, List arguments, SessionFactoryImplementor sessionFactory) {
    return "JSON_VALUE(" + arguments.get(0) + ", '$.*')";
  }
}