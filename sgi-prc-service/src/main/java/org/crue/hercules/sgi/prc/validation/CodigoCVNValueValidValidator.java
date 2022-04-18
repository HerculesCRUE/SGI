package org.crue.hercules.sgi.prc.validation;

import org.crue.hercules.sgi.prc.enums.CodigoCVN;

public class CodigoCVNValueValidValidator extends AbstractEnumValueValidValidator<CodigoCVNValueValid> {

  @Override
  public boolean isValueValid(String value) {
    return null != CodigoCVN.getByCode(value);
  }

  @Override
  public String getEntityMessage() {
    // TODO I18n si es necesario
    return "CodigoCVN";
  }
}
