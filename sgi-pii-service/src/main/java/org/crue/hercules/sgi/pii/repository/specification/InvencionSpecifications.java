package org.crue.hercules.sgi.pii.repository.specification;

import org.crue.hercules.sgi.pii.model.Invencion;
import org.crue.hercules.sgi.pii.model.Invencion_;
import org.springframework.data.jpa.domain.Specification;

public class InvencionSpecifications {

  /**
   * {@link Invencion} activos.
   * 
   * @return specification para obtener los {@link Invencion} activos.
   */
  public static Specification<Invencion> activos() {
    return (root, query, cb) -> {
      return cb.equal(root.get(Invencion_.activo), Boolean.TRUE);
    };
  }

  /**
   * Solo {@link Invencion} distintas.
   * 
   * @return specification para obtener las {@link Invencion} distintas solamente.
   */
  public static Specification<Invencion> distinct() {
    return (root, query, cb) -> {
      query.distinct(true);
      return null;
    };
  }

}
