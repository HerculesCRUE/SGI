package org.crue.hercules.sgi.csp.repository.specification;

import javax.persistence.criteria.Join;

import org.crue.hercules.sgi.csp.model.ProyectoProyectoSge;
import org.crue.hercules.sgi.csp.model.ProyectoProyectoSge_;
import org.crue.hercules.sgi.csp.model.RequerimientoJustificacion;
import org.crue.hercules.sgi.csp.model.RequerimientoJustificacion_;
import org.springframework.data.jpa.domain.Specification;

public class RequerimientoJustificacionSpecifications {

  private RequerimientoJustificacionSpecifications() {
  }

  /**
   * {@link RequerimientoJustificacion} pertenecientes
   * al proyectoSgeRef
   * 
   * @param proyectoSgeRef identificador del ProyectoSGE
   * @return specification para obtener los {@link RequerimientoJustificacion}
   *         pertenecientes al proyectoSgeRef .
   */
  public static Specification<RequerimientoJustificacion> byProyectoProyectoSgeProyectoSgeRef(String proyectoSgeRef) {
    return (root, query, cb) -> {
      Join<RequerimientoJustificacion, ProyectoProyectoSge> joinProyectoSge = root
          .join(RequerimientoJustificacion_.proyectoProyectoSge);
      return cb.equal(joinProyectoSge.get(ProyectoProyectoSge_.proyectoSgeRef), proyectoSgeRef);
    };
  }

  /**
   * {@link RequerimientoJustificacion} con {@link RequerimientoJustificacion}
   * previo.
   * 
   * @param requerimientoPrevioId id del {@link RequerimientoJustificacion} previo
   * @return specification para obtener los {@link RequerimientoJustificacion} con
   *         {@link RequerimientoJustificacion}
   *         previo.
   */
  public static Specification<RequerimientoJustificacion> byRequerimientoPrevioId(Long requerimientoPrevioId) {
    return (root, query, cb) -> {
      return cb.equal(root.get(RequerimientoJustificacion_.requerimientoPrevioId), requerimientoPrevioId);
    };
  }
}
