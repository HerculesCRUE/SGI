package org.crue.hercules.sgi.csp.validation;

import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.crue.hercules.sgi.csp.model.GrupoEquipo;
import org.crue.hercules.sgi.csp.model.GrupoLineaInvestigador;
import org.crue.hercules.sgi.csp.repository.GrupoLineaInvestigadorRepository;
import org.crue.hercules.sgi.csp.repository.specification.GrupoLineaInvestigadorSpecifications;
import org.springframework.data.jpa.domain.Specification;

public class NotGrupoLineaInvestigadorInGrupoEquipoValidator
    implements ConstraintValidator<NotGrupoLineaInvestigadorInGrupoEquipo, GrupoEquipo> {

  private final GrupoLineaInvestigadorRepository grupoLineaInvestigadorRepository;

  public NotGrupoLineaInvestigadorInGrupoEquipoValidator(GrupoLineaInvestigadorRepository repository) {
    this.grupoLineaInvestigadorRepository = repository;
  }

  @Override
  public boolean isValid(GrupoEquipo value, ConstraintValidatorContext context) {

    if (value == null) {
      return false;
    }
    Specification<GrupoLineaInvestigador> specByGrupoId = GrupoLineaInvestigadorSpecifications
        .byGrupoId(value.getGrupoId());

    Specification<GrupoLineaInvestigador> specByRangoFechaSolapados = GrupoLineaInvestigadorSpecifications
        .byRangoFechaSolapados(value.getFechaInicio(), value.getFechaFin());

    Specification<GrupoLineaInvestigador> specByPersonaRef = GrupoLineaInvestigadorSpecifications
        .byPersonaRef(value.getPersonaRef());

    Specification<GrupoLineaInvestigador> specs = Specification.where(specByGrupoId).and(specByRangoFechaSolapados)
        .and(specByRangoFechaSolapados).and(specByPersonaRef);
    List<GrupoLineaInvestigador> grupoLineaInvestigador = this.grupoLineaInvestigadorRepository.findAll(specs);

    if (!grupoLineaInvestigador.isEmpty()) {
      return false;
    }

    return true;
  }

}