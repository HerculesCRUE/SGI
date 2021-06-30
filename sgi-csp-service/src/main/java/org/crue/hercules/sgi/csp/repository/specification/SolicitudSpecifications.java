package org.crue.hercules.sgi.csp.repository.specification;

import java.util.List;

import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.Solicitud_;
import org.springframework.data.jpa.domain.Specification;

public class SolicitudSpecifications {

  /**
   * {@link Solicitud} con Activo a True
   * 
   * @return specification para obtener las {@link Solicitud} activas
   */
  public static Specification<Solicitud> activos() {
    return (root, query, cb) -> {
      return cb.equal(root.get(Solicitud_.activo), Boolean.TRUE);
    };
  }

  /**
   * {@link Solicitud} con un unidadGestionRef incluido en la lista.
   * 
   * @param unidadGestionRefs lista de unidadGestionRefs
   * @return specification para obtener los {@link Convocatoria} cuyo
   *         unidadGestionRef se encuentre entre los recibidos.
   */
  public static Specification<Solicitud> unidadGestionRefIn(List<String> unidadGestionRefs) {
    return (root, query, cb) -> {
      return root.get(Solicitud_.unidadGestionRef).in(unidadGestionRefs);
    };
  }

  /**
   * {@link Solicitud} en las que la persona es el solicitante.
   * 
   * @param personaRef referencia de la persona
   * @return specification para obtener las {@link Solicitud} en las que la
   *         persona es el solicitante.
   */
  public static Specification<Solicitud> bySolicitante(String personaRef) {
    return (root, query, cb) -> {
      return cb.equal(root.get(Solicitud_.solicitanteRef), personaRef);
    };
  }

}
