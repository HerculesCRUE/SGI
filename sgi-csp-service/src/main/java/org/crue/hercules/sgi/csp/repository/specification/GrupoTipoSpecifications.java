package org.crue.hercules.sgi.csp.repository.specification;

import org.crue.hercules.sgi.csp.model.Grupo;
import org.crue.hercules.sgi.csp.model.GrupoTipo;
import org.crue.hercules.sgi.csp.model.GrupoTipo_;
import org.springframework.data.jpa.domain.Specification;

public class GrupoTipoSpecifications {

  private GrupoTipoSpecifications() {
  }

  /**
   * {@link GrupoTipo} del {@link Grupo} con el id indicado.
   * 
   * @param grupoId identificador del {@link Grupo}.
   * @return specification para obtener los {@link GrupoTipo} del
   *         {@link Grupo} con el id indicado.
   */
  public static Specification<GrupoTipo> byGrupoId(Long grupoId) {
    return (root, query, cb) -> cb.equal(root.get(GrupoTipo_.grupoId), grupoId);
  }

}
