package org.crue.hercules.sgi.prc.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.crue.hercules.sgi.prc.dto.ProduccionCientificaApiInput.AcreditacionInput;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

public class UrlOrDocumentoRefAcreditacionValidator
    implements ConstraintValidator<UrlOrDocumentoRefAcreditacion, AcreditacionInput> {

  @Override
  @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
  public boolean isValid(AcreditacionInput value, ConstraintValidatorContext context) {
    if (value == null) {
      return false;
    }

    return isValidAcreditacion(value);
  }

  private boolean isValidAcreditacion(AcreditacionInput value) {
    // Si vienen vacíos url y documentoRef
    if (!StringUtils.hasText(value.getUrl()) &&
        !StringUtils.hasText(value.getDocumentoRef())) {
      return false;
    }

    // Si vienen todos informados
    if (StringUtils.hasText(value.getUrl()) &&
        StringUtils.hasText(value.getDocumentoRef())) {
      return false;
    }

    // Si viene informado o url o documentoRef
    return StringUtils.hasText(value.getUrl()) ||
        StringUtils.hasText(value.getDocumentoRef());
  }
}
