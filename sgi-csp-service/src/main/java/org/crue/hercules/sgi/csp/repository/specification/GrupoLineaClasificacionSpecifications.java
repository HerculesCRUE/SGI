package org.crue.hercules.sgi.csp.repository.specification;

import org.crue.hercules.sgi.csp.model.GrupoLineaClasificacion;
import org.crue.hercules.sgi.csp.model.GrupoLineaClasificacion_;
import org.crue.hercules.sgi.csp.model.GrupoLineaInvestigacion;
import org.crue.hercules.sgi.csp.model.GrupoLineaInvestigacion_;
import org.springframework.data.jpa.domain.Specification;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GrupoLineaClasificacionSpecifications {

  /**
   * {@link GrupoLineaClasificacion} del {@link GrupoLineaInvestigacion} con el id
   * indicado.
   * 
   * @param id identificador del {@link GrupoLineaInvestigacion}.
   * @return specification para obtener los {@link GrupoLineaClasificacion} de la
   *         {@link GrupoLineaInvestigacion} con el id indicado.
   */
  public static Specification<GrupoLineaClasificacion> byGrupoLineaInvestigacionId(Long id) {
    return (root, query, cb) -> cb
        .equal(root.get(GrupoLineaClasificacion_.grupoLineaInvestigacion).get(GrupoLineaInvestigacion_.id), id);
  }

}