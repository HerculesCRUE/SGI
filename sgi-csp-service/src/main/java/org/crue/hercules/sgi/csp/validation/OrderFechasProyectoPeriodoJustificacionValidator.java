package org.crue.hercules.sgi.csp.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.crue.hercules.sgi.csp.model.ProyectoPeriodoJustificacion;

public class OrderFechasProyectoPeriodoJustificacionValidator
    implements ConstraintValidator<OrderFechasProyectoPeriodoJustificacion, ProyectoPeriodoJustificacion> {

  @Override
  public boolean isValid(ProyectoPeriodoJustificacion value, ConstraintValidatorContext context) {
    if (value == null) {
      return false;
    }

    if (value.getFechaInicio() == null || value.getFechaFin() == null) {
      return true;
    }

    return !value.getFechaInicio().isAfter(value.getFechaFin());
  }

}