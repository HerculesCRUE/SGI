package org.crue.hercules.sgi.pii.repository.specification;

import org.crue.hercules.sgi.pii.model.Invencion;
import org.crue.hercules.sgi.pii.model.InvencionSectorAplicacion;
import org.crue.hercules.sgi.pii.model.InvencionSectorAplicacion_;
import org.springframework.data.jpa.domain.Specification;

public class InvencionSectorAplicacionSpecifications {

  /**
   * {@link InvencionSectorAplicacion} de la {@link Invencion} con el id indicado.
   * 
   * @param id identificador de la {@link Invencion}.
   * @return specification para obtener las {@link InvencionSectorAplicacion} de
   *         la {@link Invencion} con el id indicado.
   */
  public static Specification<InvencionSectorAplicacion> byInvencionId(Long id) {
    return (root, query, cb) -> {
      return cb.equal(root.get(InvencionSectorAplicacion_.invencionId), id);
    };
  }
}
