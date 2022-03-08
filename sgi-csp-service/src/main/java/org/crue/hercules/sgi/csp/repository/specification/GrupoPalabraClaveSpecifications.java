package org.crue.hercules.sgi.csp.repository.specification;

import org.crue.hercules.sgi.csp.model.Grupo;
import org.crue.hercules.sgi.csp.model.GrupoPalabraClave;
import org.crue.hercules.sgi.csp.model.GrupoPalabraClave_;
import org.springframework.data.jpa.domain.Specification;

public class GrupoPalabraClaveSpecifications {

  /**
   * {@link GrupoPalabraClave} de la entidad {@link Grupo} con el id
   * indicado.
   * 
   * @param id identificador de la entidad {@link Grupo}.
   * @return specification para obtener las {@link GrupoPalabraClave} de
   *         la entidad {@link Grupo} con el id indicado.
   */
  public static Specification<GrupoPalabraClave> byGrupoId(Long id) {
    return (root, query, cb) -> {
      return cb.equal(root.get(GrupoPalabraClave_.grupoId), id);
    };
  }
}
