package org.crue.hercules.sgi.csp.validation;

import java.util.Optional;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.crue.hercules.sgi.csp.model.Grupo;
import org.crue.hercules.sgi.csp.model.GrupoEquipo;
import org.crue.hercules.sgi.csp.repository.GrupoRepository;

public class FechasGrupoEquipoWithinGrupoValidator
    implements ConstraintValidator<FechasGrupoEquipoWithinGrupo, GrupoEquipo> {

  private final GrupoRepository repository;

  public FechasGrupoEquipoWithinGrupoValidator(GrupoRepository repository) {
    this.repository = repository;
  }

  @Override
  public boolean isValid(GrupoEquipo value, ConstraintValidatorContext context) {

    if (value == null) {
      return false;
    }

    Optional<Grupo> grupo = repository.findById(value.getGrupoId());

    if (!grupo.isPresent()) {
      return false;
    }

    if (value.getFechaInicio() != null &&
        !(value.getFechaInicio().isAfter(grupo.get().getFechaInicio())
            || value.getFechaInicio().equals(grupo.get().getFechaInicio()))) {
      return false;
    } else if (value.getFechaFin() != null && grupo.get().getFechaFin() != null
        && !(value.getFechaFin().isBefore(grupo.get().getFechaFin())
            || value.getFechaFin().equals(grupo.get().getFechaFin()))) {
      return false;
    } else {
      return true;
    }

  }

}