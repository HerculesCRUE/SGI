package org.crue.hercules.sgi.framework.liquibase.database;

import liquibase.database.core.H2Database;

public class SgiH2Database extends H2Database {

  @Override
  public int getPriority() {
    return 10;
  }

  @Override
  public boolean isReservedWord(String objectName) {
    // Skip value reserved word
    if (objectName.equalsIgnoreCase("value")) {
      return false;
    }
    return super.isReservedWord(objectName);
  }
}
