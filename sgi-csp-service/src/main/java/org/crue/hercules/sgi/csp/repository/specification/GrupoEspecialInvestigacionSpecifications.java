package org.crue.hercules.sgi.csp.repository.specification;

import org.crue.hercules.sgi.csp.model.Grupo;
import org.crue.hercules.sgi.csp.model.GrupoEspecialInvestigacion;
import org.crue.hercules.sgi.csp.model.GrupoEspecialInvestigacion_;
import org.springframework.data.jpa.domain.Specification;

public class GrupoEspecialInvestigacionSpecifications {

  private GrupoEspecialInvestigacionSpecifications() {
  }

  /**
   * {@link GrupoEspecialInvestigacion} del {@link Grupo} con el id indicado.
   * 
   * @param grupoId identificador del {@link Grupo}.
   * @return specification para obtener los {@link GrupoEspecialInvestigacion} del
   *         {@link Grupo} con el id indicado.
   */
  public static Specification<GrupoEspecialInvestigacion> byGrupoId(Long grupoId) {
    return (root, query, cb) -> cb.equal(root.get(GrupoEspecialInvestigacion_.grupoId), grupoId);
  }

}
