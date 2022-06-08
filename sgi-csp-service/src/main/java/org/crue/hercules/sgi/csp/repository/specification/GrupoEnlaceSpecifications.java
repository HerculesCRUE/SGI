package org.crue.hercules.sgi.csp.repository.specification;

import org.crue.hercules.sgi.csp.model.Grupo;
import org.crue.hercules.sgi.csp.model.GrupoEnlace;
import org.crue.hercules.sgi.csp.model.GrupoEnlace_;
import org.springframework.data.jpa.domain.Specification;

public class GrupoEnlaceSpecifications {

  private GrupoEnlaceSpecifications() {
  }

  /**
   * {@link GrupoEnlace} del {@link Grupo} con el id indicado.
   * 
   * @param grupoId identificador del {@link Grupo}.
   * @return specification para obtener los {@link GrupoEnlace} del
   *         {@link Grupo} con el id indicado.
   */
  public static Specification<GrupoEnlace> byGrupoId(Long grupoId) {
    return (root, query, cb) -> cb.equal(root.get(GrupoEnlace_.grupoId), grupoId);
  }

}
