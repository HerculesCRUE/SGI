package org.crue.hercules.sgi.csp.repository.specification;

import org.crue.hercules.sgi.csp.model.Grupo;
import org.crue.hercules.sgi.csp.model.GrupoEquipoInstrumental_;
import org.crue.hercules.sgi.csp.model.GrupoLineaEquipoInstrumental;
import org.crue.hercules.sgi.csp.model.GrupoLineaEquipoInstrumental_;
import org.crue.hercules.sgi.csp.model.GrupoLineaInvestigacion;
import org.crue.hercules.sgi.csp.model.GrupoLineaInvestigacion_;
import org.crue.hercules.sgi.csp.model.Grupo_;
import org.springframework.data.jpa.domain.Specification;

public class GrupoLineaEquipoInstrumentalSpecifications {

  private GrupoLineaEquipoInstrumentalSpecifications() {
  }

  /**
   * {@link GrupoLineaEquipoInstrumental} del {@link Grupo} con el id indicado.
   * 
   * @param grupoId identificador del {@link Grupo}.
   * @return specification para obtener los {@link GrupoLineaEquipoInstrumental}
   *         del
   *         {@link Grupo} con el id indicado.
   */
  public static Specification<GrupoLineaEquipoInstrumental> byGrupoId(Long grupoId) {
    return (root, query, cb) -> cb
        .equal(root.get(GrupoLineaEquipoInstrumental_.grupoLineaInvestigacion).get(GrupoLineaInvestigacion_.grupo)
            .get(Grupo_.id), grupoId);
  }

  /**
   * {@link GrupoLineaEquipoInstrumental} cuya persona Ref sea la recibida.
   * 
   * @param grupoEquipoInstrumentalId identificador
   *                                  {@link GrupoLineaEquipoInstrumental}
   * @return specification para obtener los {@link GrupoLineaEquipoInstrumental}
   *         cuya
   *         persona
   *         Ref sea la recibida.
   */
  public static Specification<GrupoLineaEquipoInstrumental> byGrupoEquipoInstrumentalId(
      Long grupoEquipoInstrumentalId) {
    return (root, query, cb) -> {
      return cb.equal(root.get(GrupoLineaEquipoInstrumental_.grupoEquipoInstrumental).get(GrupoEquipoInstrumental_.id),
          grupoEquipoInstrumentalId);
    };
  }

  /**
   * {@link GrupoLineaEquipoInstrumental} del {@link GrupoLineaInvestigacion} con
   * el id
   * indicado.
   * 
   * @param grupoLineaInvestigacionId identificador del
   *                                  {@link GrupoLineaInvestigacion}.
   * @return specification para obtener los {@link GrupoLineaEquipoInstrumental}
   *         del
   *         {@link GrupoLineaInvestigacion} con el id indicado.
   */
  public static Specification<GrupoLineaEquipoInstrumental> byGrupoLineaInvestigacionId(
      Long grupoLineaInvestigacionId) {
    return (root, query, cb) -> cb
        .equal(root.get(GrupoLineaEquipoInstrumental_.grupoLineaInvestigacion).get(GrupoLineaInvestigacion_.id),
            grupoLineaInvestigacionId);
  }

}
