package org.crue.hercules.sgi.pii.repository.specification;

import org.crue.hercules.sgi.pii.model.Invencion;
import org.crue.hercules.sgi.pii.model.InvencionPalabraClave;
import org.crue.hercules.sgi.pii.model.InvencionPalabraClave_;
import org.springframework.data.jpa.domain.Specification;

public class InvencionPalabraClaveSpecifications {

  /**
   * {@link InvencionPalabraClave} de la {@link Invencion} con el id indicado.
   * 
   * @param id identificador de la {@link Invencion}.
   * @return specification para obtener las {@link InvencionPalabraClave} de
   *         la {@link Invencion} con el id indicado.
   */
  public static Specification<InvencionPalabraClave> byInvencionId(Long id) {
    return (root, query, cb) -> {
      return cb.equal(root.get(InvencionPalabraClave_.invencionId), id);
    };
  }
}
