package org.crue.hercules.sgi.csp.repository.specification;

import org.crue.hercules.sgi.csp.model.GastoRequerimientoJustificacion;
import org.crue.hercules.sgi.csp.model.GastoRequerimientoJustificacion_;
import org.crue.hercules.sgi.csp.model.RequerimientoJustificacion;
import org.springframework.data.jpa.domain.Specification;

public class GastoRequerimientoJustificacionSpecifications {

  private GastoRequerimientoJustificacionSpecifications() {
  }

  /**
   * {@link GastoRequerimientoJustificacion} que pertenecen al
   * {@link RequerimientoJustificacion} con id indicado.
   * 
   * @param requerimientoJustificacionId id del
   *                                     {@link RequerimientoJustificacion}.
   * @return specification para obtener los
   *         {@link GastoRequerimientoJustificacion} con
   *         {@link RequerimientoJustificacion}.
   */
  public static Specification<GastoRequerimientoJustificacion> byRequerimientoJustificacionId(
      Long requerimientoJustificacionId) {
    return (root, query, cb) -> {
      return cb.equal(root.get(GastoRequerimientoJustificacion_.requerimientoJustificacionId),
          requerimientoJustificacionId);
    };
  }
}
