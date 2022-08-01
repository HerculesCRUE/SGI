package org.crue.hercules.sgi.pii.repository.specification;

import org.crue.hercules.sgi.pii.model.Invencion;
import org.crue.hercules.sgi.pii.model.InvencionIngreso;
import org.crue.hercules.sgi.pii.model.InvencionIngreso_;
import org.springframework.data.jpa.domain.Specification;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class InvencionIngresoSpecifications {

  /**
   * {@link InvencionIngreso} de la {@link Invencion} con el id indicado.
   * 
   * @param id identificador de la {@link Invencion}.
   * @return specification para obtener las {@link InvencionIngreso} de la
   *         {@link Invencion} con el id indicado.
   */
  public static Specification<InvencionIngreso> byInvencionId(Long id) {
    return (root, query, cb) -> cb.equal(root.get(InvencionIngreso_.invencionId), id);
  }
}
