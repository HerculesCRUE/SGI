package org.crue.hercules.sgi.pii.validation;

import java.math.BigDecimal;
import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.pii.model.Reparto;
import org.crue.hercules.sgi.pii.model.RepartoEquipoInventor;
import org.crue.hercules.sgi.pii.model.RepartoGasto;
import org.crue.hercules.sgi.pii.model.RepartoIngreso;
import org.crue.hercules.sgi.pii.service.RepartoEquipoInventorService;
import org.crue.hercules.sgi.pii.service.RepartoGastoService;
import org.crue.hercules.sgi.pii.service.RepartoIngresoService;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class RepartoIsEjecutableValidator implements ConstraintValidator<RepartoIsEjecutable, Reparto> {
  private final RepartoGastoService repartoGastoService;
  private final RepartoIngresoService repartoIngresoService;
  private final RepartoEquipoInventorService repartoEquipoInventorService;

  public RepartoIsEjecutableValidator(RepartoGastoService repartoGastoService,
      RepartoIngresoService repartoIngresoService, RepartoEquipoInventorService repartoEquipoInventorService) {
    this.repartoGastoService = repartoGastoService;
    this.repartoIngresoService = repartoIngresoService;
    this.repartoEquipoInventorService = repartoEquipoInventorService;
  }

  @Override
  @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
  public boolean isValid(Reparto repartoToValidate, ConstraintValidatorContext context) {
    if (repartoToValidate == null) {
      return false;
    }
    final List<RepartoGasto> gastos = repartoGastoService.findByRepartoId(repartoToValidate.getId(), null);
    final List<RepartoIngreso> ingresos = repartoIngresoService.findByRepartoId(repartoToValidate.getId(), null);
    final List<RepartoEquipoInventor> equipoInventor = repartoEquipoInventorService
        .findByRepartoId(repartoToValidate.getId(), null);

    final boolean returnValue = isImporteUniversidadValid(repartoToValidate,
        calculateImporteUniversidad(gastos, ingresos))
        && isImporteEquipoInventorValid(repartoToValidate, equipoInventor);

    if (!returnValue) {
      addEntityMessageParameter(context);
    }
    return returnValue;
  }

  private BigDecimal calculateImporteUniversidad(List<RepartoGasto> gastos, List<RepartoIngreso> ingresos) {
    return ingresos.stream().map(RepartoIngreso::getImporteARepartir).reduce(new BigDecimal("0.00"), BigDecimal::add)
        .subtract(
            gastos.stream().map(RepartoGasto::getImporteADeducir).reduce(new BigDecimal("0.00"), BigDecimal::add));
  }

  private boolean isImporteUniversidadValid(Reparto repartoToValidate, BigDecimal importeUniversidad) {
    return repartoToValidate.getImporteUniversidad().compareTo(importeUniversidad) == 0
        && importeUniversidad.compareTo(new BigDecimal("0.00")) >= 0;
  }

  private boolean isImporteEquipoInventorValid(Reparto repartoToValidate, List<RepartoEquipoInventor> equipoInventor) {
    final BigDecimal sumaImporteEquipoInventor = equipoInventor.stream()
        .map(inventor -> inventor.getImporteNomina().add(inventor.getImporteProyecto()).add(inventor.getImporteOtros()))
        .reduce(new BigDecimal("0.00"), BigDecimal::add);
    if (repartoToValidate.getImporteEquipoInventor() != null) {
      return repartoToValidate.getImporteUniversidad().compareTo(repartoToValidate.getImporteEquipoInventor()) >= 0
          && sumaImporteEquipoInventor.compareTo(repartoToValidate.getImporteEquipoInventor()) == 0;
    }
    return sumaImporteEquipoInventor.compareTo(repartoToValidate.getImporteUniversidad()) <= 0;
  }

  private void addEntityMessageParameter(ConstraintValidatorContext context) {
    // Add "entity" message parameter this the message-revolved entity name so it
    // can be used in the error message
    HibernateConstraintValidatorContext hibernateContext = context.unwrap(HibernateConstraintValidatorContext.class);
    hibernateContext.addMessageParameter("entity", ApplicationContextSupport.getMessage(Reparto.class));
    // Disable default message to allow binding the message to a property
    hibernateContext.disableDefaultConstraintViolation();
    // Build a custom message for a property using the default message
    hibernateContext.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
        .addPropertyNode(ApplicationContextSupport.getMessage(Reparto.class)).addConstraintViolation();
  }
}
