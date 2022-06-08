package org.crue.hercules.sgi.csp.validation;

import java.util.Optional;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.crue.hercules.sgi.csp.model.GrupoLineaInvestigacion;
import org.crue.hercules.sgi.csp.model.GrupoLineaInvestigador;
import org.crue.hercules.sgi.csp.repository.GrupoLineaInvestigacionRepository;

public class FechasGrupoLineaInvestigadorWithinGrupoLineaInvestigacionValidator
    implements ConstraintValidator<FechasGrupoLineaInvestigadorWithinGrupoLineaInvestigacion, GrupoLineaInvestigador> {

  private final GrupoLineaInvestigacionRepository repository;

  public FechasGrupoLineaInvestigadorWithinGrupoLineaInvestigacionValidator(
      GrupoLineaInvestigacionRepository repository) {
    this.repository = repository;
  }

  @Override
  public boolean isValid(GrupoLineaInvestigador value, ConstraintValidatorContext context) {

    if (value == null) {
      return false;
    }

    Optional<GrupoLineaInvestigacion> grupoLineaInvestigacion = repository
        .findById(value.getGrupoLineaInvestigacionId());

    if (!grupoLineaInvestigacion.isPresent()) {
      return false;
    }

    if (value.getFechaInicio() != null &&
        !(value.getFechaInicio().isAfter(grupoLineaInvestigacion.get().getFechaInicio())
            || value.getFechaInicio().equals(grupoLineaInvestigacion.get().getFechaInicio()))) {
      return false;
    } else if (value.getFechaFin() != null && grupoLineaInvestigacion.get().getFechaFin() != null
        && !(value.getFechaFin().isBefore(grupoLineaInvestigacion.get().getFechaFin())
            || value.getFechaFin().equals(grupoLineaInvestigacion.get().getFechaFin()))) {
      return false;
    } else {
      return true;
    }

  }

}