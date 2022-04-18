package org.crue.hercules.sgi.prc.validation;

import org.crue.hercules.sgi.prc.enums.EpigrafeCVN;

public class EpigrafeCVNValueValidValidator extends AbstractEnumValueValidValidator<EpigrafeCVNValueValid> {

  @Override
  public boolean isValueValid(String value) {
    return null != EpigrafeCVN.getByCode(value);
  }

  @Override
  public String getEntityMessage() {
    // TODO I18n si es necesario
    return "EpigrafeCVN";
  }
}
