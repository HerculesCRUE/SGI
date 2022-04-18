package org.crue.hercules.sgi.prc.validation;

import org.crue.hercules.sgi.prc.enums.TipoFuenteImpacto;

public class TipoFuenteImpactoValueValidValidator extends AbstractEnumValueValidValidator<TipoFuenteImpactoValueValid> {

  @Override
  public boolean isValueValid(String value) {
    return null != TipoFuenteImpacto.getByCode(value);
  }

  @Override
  public String getEntityMessage() {
    // TODO I18n si es necesario
    return "TipoFuenteImpacto";
  }
}
