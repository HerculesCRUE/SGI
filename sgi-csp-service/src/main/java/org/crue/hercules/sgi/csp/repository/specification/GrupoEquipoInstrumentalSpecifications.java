package org.crue.hercules.sgi.csp.repository.specification;

import org.crue.hercules.sgi.csp.model.Grupo;
import org.crue.hercules.sgi.csp.model.GrupoEquipoInstrumental;
import org.crue.hercules.sgi.csp.model.GrupoEquipoInstrumental_;
import org.springframework.data.jpa.domain.Specification;

public class GrupoEquipoInstrumentalSpecifications {

  private GrupoEquipoInstrumentalSpecifications() {
  }

  /**
   * {@link GrupoEquipoInstrumental} del {@link Grupo} con el id indicado.
   * 
   * @param grupoId identificador del {@link Grupo}.
   * @return specification para obtener los {@link GrupoEquipoInstrumental} del
   *         {@link Grupo} con el id indicado.
   */
  public static Specification<GrupoEquipoInstrumental> byGrupoId(Long grupoId) {
    return (root, query, cb) -> cb.equal(root.get(GrupoEquipoInstrumental_.grupoId), grupoId);
  }

}
