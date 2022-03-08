package org.crue.hercules.sgi.csp.repository.specification;

import org.crue.hercules.sgi.csp.model.Grupo;
import org.crue.hercules.sgi.csp.model.GrupoEquipo;
import org.crue.hercules.sgi.csp.model.GrupoEquipo_;
import org.springframework.data.jpa.domain.Specification;

public class GrupoEquipoSpecifications {

  private GrupoEquipoSpecifications() {
  }

  /**
   * {@link GrupoEquipo} del {@link Grupo} con el id indicado.
   * 
   * @param grupoId identificador del {@link Grupo}.
   * @return specification para obtener los {@link GrupoEquipo} del
   *         {@link Grupo} con el id indicado.
   */
  public static Specification<GrupoEquipo> byGrupoId(Long grupoId) {
    return (root, query, cb) -> cb.equal(root.get(GrupoEquipo_.grupoId), grupoId);
  }

}
