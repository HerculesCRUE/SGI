package org.crue.hercules.sgi.csp.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.crue.hercules.sgi.csp.model.ProyectoPartida;
import org.springframework.util.StringUtils;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ProyectoPartidaCodigoOrPartidaRefRequiredValidator
    implements ConstraintValidator<ProyectoPartidaCodigoOrPartidaRefRequired, ProyectoPartida> {

  @Override
  public boolean isValid(ProyectoPartida value, ConstraintValidatorContext context) {

    if (value == null) {
      return false;
    }

    return StringUtils.hasText(value.getPartidaRef()) || StringUtils.hasText(value.getCodigo());
  }

}