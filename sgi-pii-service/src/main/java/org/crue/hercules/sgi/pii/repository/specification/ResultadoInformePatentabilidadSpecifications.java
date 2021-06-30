package org.crue.hercules.sgi.pii.repository.specification;

import org.crue.hercules.sgi.pii.model.ResultadoInformePatentabilidad_;
import org.crue.hercules.sgi.pii.model.ResultadoInformePatentabilidad;
import org.springframework.data.jpa.domain.Specification;

public class ResultadoInformePatentabilidadSpecifications {

  /**
   * {@link ResultadoInformePatentabilidad} activos.
   * 
   * @return Specification para obtener los {@link ResultadoInformePatentabilidad}
   *         activos.
   */
  public static Specification<ResultadoInformePatentabilidad> activos() {
    return (root, query, cb) -> {
      return cb.equal(root.get(ResultadoInformePatentabilidad_.activo), Boolean.TRUE);
    };
  }

}