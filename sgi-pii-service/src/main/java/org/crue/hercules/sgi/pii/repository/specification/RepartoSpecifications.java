package org.crue.hercules.sgi.pii.repository.specification;

import org.crue.hercules.sgi.pii.model.Invencion;
import org.crue.hercules.sgi.pii.model.Reparto;
import org.crue.hercules.sgi.pii.model.Reparto_;
import org.springframework.data.jpa.domain.Specification;

public class RepartoSpecifications {
  /**
   * {@link Reparto} de la {@link Invencion} con el id indicado.
   * 
   * @param id identificador de la {@link Invencion}.
   * @return specification para obtener los {@link Reparto} de la
   *         {@link Invencion} con el id indicado.
   */
  public static Specification<Reparto> byInvencionId(Long id) {
    return (root, query, cb) -> {
      return cb.equal(root.get(Reparto_.invencionId), id);
    };
  }
}
