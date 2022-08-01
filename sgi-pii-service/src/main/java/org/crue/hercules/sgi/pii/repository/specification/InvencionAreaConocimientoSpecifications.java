package org.crue.hercules.sgi.pii.repository.specification;

import org.crue.hercules.sgi.pii.model.Invencion;
import org.crue.hercules.sgi.pii.model.InvencionAreaConocimiento;
import org.crue.hercules.sgi.pii.model.InvencionAreaConocimiento_;
import org.springframework.data.jpa.domain.Specification;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class InvencionAreaConocimientoSpecifications {
  /**
   * {@link InvencionAreaConocimiento} de la {@link Invencion} con el id indicado.
   * 
   * @param id identificador de la {@link Invencion}.
   * @return specification para obtener las {@link InvencionAreaConocimiento} de
   *         la {@link Invencion} con el id indicado.
   */
  public static Specification<InvencionAreaConocimiento> byInvencionId(Long id) {
    return (root, query, cb) -> cb.equal(root.get(InvencionAreaConocimiento_.invencionId), id);
  }
}
