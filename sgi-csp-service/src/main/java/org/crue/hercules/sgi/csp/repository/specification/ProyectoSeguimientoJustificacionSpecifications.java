package org.crue.hercules.sgi.csp.repository.specification;

import javax.persistence.criteria.Join;

import org.crue.hercules.sgi.csp.model.ProyectoProyectoSge;
import org.crue.hercules.sgi.csp.model.ProyectoProyectoSge_;
import org.crue.hercules.sgi.csp.model.ProyectoSeguimientoJustificacion;
import org.crue.hercules.sgi.csp.model.ProyectoSeguimientoJustificacion_;
import org.springframework.data.jpa.domain.Specification;

public class ProyectoSeguimientoJustificacionSpecifications {

  private ProyectoSeguimientoJustificacionSpecifications() {
  }

  /**
   * {@link ProyectoSeguimientoJustificacion} pertenecientes
   * al proyectoSgeRef
   * 
   * @param proyectoSgeRef identificador del ProyectoSGE
   * @return specification para obtener los
   *         {@link ProyectoSeguimientoJustificacion}
   *         pertenecientes al proyectoSgeRef .
   */
  public static Specification<ProyectoSeguimientoJustificacion> byProyectoProyectoSgeProyectoSgeRef(
      String proyectoSgeRef) {
    return (root, query, cb) -> {
      Join<ProyectoSeguimientoJustificacion, ProyectoProyectoSge> joinProyectoSge = root
          .join(ProyectoSeguimientoJustificacion_.proyectoProyectoSge);
      return cb.equal(joinProyectoSge.get(ProyectoProyectoSge_.proyectoSgeRef), proyectoSgeRef);
    };
  }
}
