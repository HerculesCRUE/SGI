package org.crue.hercules.sgi.csp.repository.specification;

import org.crue.hercules.sgi.csp.model.IncidenciaDocumentacionRequerimiento;
import org.crue.hercules.sgi.csp.model.IncidenciaDocumentacionRequerimiento_;
import org.crue.hercules.sgi.csp.model.RequerimientoJustificacion;
import org.springframework.data.jpa.domain.Specification;

public class IncidenciaDocumentacionRequerimientoSpecifications {

  private IncidenciaDocumentacionRequerimientoSpecifications() {
  }

  /**
   * {@link IncidenciaDocumentacionRequerimiento} que pertenecen al
   * {@link RequerimientoJustificacion} con id indicado.
   * 
   * @param requerimientoJustificacionId id del
   *                                     {@link RequerimientoJustificacion}.
   * @return specification para obtener los
   *         {@link IncidenciaDocumentacionRequerimiento} con
   *         {@link RequerimientoJustificacion}.
   */
  public static Specification<IncidenciaDocumentacionRequerimiento> byRequerimientoJustificacionId(
      Long requerimientoJustificacionId) {
    return (root, query, cb) -> {
      return cb.equal(root.get(IncidenciaDocumentacionRequerimiento_.requerimientoJustificacionId),
          requerimientoJustificacionId);
    };
  }
}
