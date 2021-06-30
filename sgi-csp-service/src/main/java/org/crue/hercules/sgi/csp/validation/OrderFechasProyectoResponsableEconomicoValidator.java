package org.crue.hercules.sgi.csp.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.crue.hercules.sgi.csp.model.ProyectoResponsableEconomico;

public class OrderFechasProyectoResponsableEconomicoValidator
    implements ConstraintValidator<OrderFechasProyectoResponsableEconomico, ProyectoResponsableEconomico> {

  @Override
  public boolean isValid(ProyectoResponsableEconomico value, ConstraintValidatorContext context) {
    if (value == null) {
      return false;
    }

    if (value.getFechaInicio() == null || value.getFechaFin() == null) {
      return true;
    }

    return !value.getFechaInicio().isAfter(value.getFechaFin());
  }

}
