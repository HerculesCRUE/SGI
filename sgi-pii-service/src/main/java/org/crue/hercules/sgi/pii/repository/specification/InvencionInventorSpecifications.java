package org.crue.hercules.sgi.pii.repository.specification;

import org.crue.hercules.sgi.pii.model.Invencion;
import org.crue.hercules.sgi.pii.model.InvencionInventor;
import org.crue.hercules.sgi.pii.model.InvencionInventor_;
import org.springframework.data.jpa.domain.Specification;

public class InvencionInventorSpecifications {

  /**
   * Devuelve los {@link InvencionInventor} relacionados con la {@link Invencion}
   * con el Id pasado por parámetros.
   * 
   * @param invencionId {@link Long} Id de la {@link Invencion}.
   * @return Specification para obtener los {@link InvencionInventor} relacionados
   *         a la {@link Invencion} pasada por parámetro.
   */
  public static Specification<InvencionInventor> invencionById(Long invencionId) {
    return (root, query, cb) -> {
      return cb.equal(root.get(InvencionInventor_.invencionId), invencionId);
    };
  }
}
