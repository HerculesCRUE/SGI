package org.crue.hercules.sgi.eer.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.crue.hercules.sgi.eer.model.Empresa;
import org.crue.hercules.sgi.eer.model.Empresa.EstadoEmpresa;

public class ValidateEstadoValidator
    implements ConstraintValidator<ValidateEstado, Empresa> {

  @Override
  public boolean isValid(Empresa value, ConstraintValidatorContext context) {

    if (value == null) {
      return false;
    }

    if (value.getEntidadRef() == null && value.getEstado() == EstadoEmpresa.ACTIVA) {
      return false;
    } else {
      return true;
    }

  }

}