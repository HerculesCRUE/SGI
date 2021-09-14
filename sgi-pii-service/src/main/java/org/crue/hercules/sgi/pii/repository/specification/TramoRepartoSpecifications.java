package org.crue.hercules.sgi.pii.repository.specification;

import org.crue.hercules.sgi.pii.model.TramoReparto;
import org.crue.hercules.sgi.pii.model.TramoReparto_;
import org.springframework.data.jpa.domain.Specification;

public class TramoRepartoSpecifications {

  /**
   * {@link TramoReparto} activos.
   * 
   * @return specification para obtener los {@link TramoReparto} activos.
   */
  public static Specification<TramoReparto> activos() {
    return (root, query, cb) -> {
      return cb.equal(root.get(TramoReparto_.activo), Boolean.TRUE);
    };
  }

  /**
   * {@link TramoReparto} activos con tramos solapados al tramo indicado por
   * parámetros
   * 
   * @param desde inicio del tramo.
   * @param hasta fin del tramo.
   * @return specification para obtener los {@link TramoReparto} activos con
   *         tramos solapados.
   */
  public static Specification<TramoReparto> overlappedActiveTramoReparto(Integer desde, Integer hasta) {
    return (root, query, cb) -> {
      return cb.and(cb.equal(root.get(TramoReparto_.activo), Boolean.TRUE),
          cb.and(cb.or(cb.between(root.get(TramoReparto_.desde), desde, hasta),
              cb.between(root.get(TramoReparto_.hasta), desde, hasta),
              cb.and(cb.lessThanOrEqualTo(root.get(TramoReparto_.desde), desde),
                  cb.greaterThanOrEqualTo(root.get(TramoReparto_.hasta), hasta)))));
    };
  }
}
