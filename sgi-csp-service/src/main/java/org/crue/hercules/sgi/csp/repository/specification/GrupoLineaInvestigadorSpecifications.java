package org.crue.hercules.sgi.csp.repository.specification;

import java.time.Instant;

import org.crue.hercules.sgi.csp.model.Grupo;
import org.crue.hercules.sgi.csp.model.GrupoLineaInvestigacion_;
import org.crue.hercules.sgi.csp.model.GrupoLineaInvestigador;
import org.crue.hercules.sgi.csp.model.GrupoLineaInvestigador_;
import org.crue.hercules.sgi.csp.model.Grupo_;
import org.springframework.data.jpa.domain.Specification;

public class GrupoLineaInvestigadorSpecifications {

  private GrupoLineaInvestigadorSpecifications() {
  }

  /**
   * {@link GrupoLineaInvestigador} del {@link Grupo} con el id indicado.
   * 
   * @param grupoId identificador del {@link Grupo}.
   * @return specification para obtener los {@link GrupoLineaInvestigador} del
   *         {@link Grupo} con el id indicado.
   */
  public static Specification<GrupoLineaInvestigador> byGrupoId(Long grupoId) {
    return (root, query, cb) -> cb
        .equal(root.get(GrupoLineaInvestigador_.grupoLineaInvestigacion).get(GrupoLineaInvestigacion_.grupo)
            .get(Grupo_.id), grupoId);
  }

  /**
   * {@link GrupoLineaInvestigador} con fechas solapadas
   * 
   * @param fechaInicio fecha inicio de {@link GrupoLineaInvestigador}
   * @param fechaFin    fecha fin de la {@link GrupoLineaInvestigador}.
   * @return specification para obtener los {@link GrupoLineaInvestigador} con
   *         rango de
   *         fechas solapadas
   */
  public static Specification<GrupoLineaInvestigador> byRangoFechaSolapados(Instant fechaInicio, Instant fechaFin) {
    return (root, query, cb) -> {
      return cb.and(
          cb.or(cb.isNull(root.get(GrupoLineaInvestigador_.fechaInicio)),
              cb.lessThanOrEqualTo(root.get(GrupoLineaInvestigador_.fechaInicio),
                  fechaFin != null ? fechaFin : Instant.parse("2500-01-01T23:59:59Z"))),
          cb.or(cb.isNull(root.get(GrupoLineaInvestigador_.fechaFin)),
              cb.greaterThanOrEqualTo(root.get(GrupoLineaInvestigador_.fechaFin),
                  fechaInicio != null ? fechaInicio : Instant.parse("1900-01-01T00:00:00Z"))));
    };
  }

  /**
   * {@link GrupoLineaInvestigador} cuya persona Ref sea la recibida.
   * 
   * @param personaRef persona ref de {@link GrupoLineaInvestigador}
   * @return specification para obtener los {@link GrupoLineaInvestigador} cuya
   *         persona
   *         Ref sea la recibida.
   */
  public static Specification<GrupoLineaInvestigador> byPersonaRef(String personaRef) {
    return (root, query, cb) -> {
      return cb.equal(root.get(GrupoLineaInvestigador_.personaRef), personaRef);
    };
  }

}
