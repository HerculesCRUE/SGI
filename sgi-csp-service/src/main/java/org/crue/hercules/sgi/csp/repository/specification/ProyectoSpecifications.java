package org.crue.hercules.sgi.csp.repository.specification;

import java.util.List;

import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.Proyecto_;
import org.springframework.data.jpa.domain.Specification;

public class ProyectoSpecifications {

  /**
   * {@link Proyecto} con Activo a True
   * 
   * @return specification para obtener las {@link Proyecto} activas
   */
  public static Specification<Proyecto> activos() {
    return (root, query, cb) -> {
      return cb.equal(root.get(Proyecto_.activo), Boolean.TRUE);
    };
  }

  /**
   * {@link Proyecto} con un unidadGestionRef incluido en la lista.
   * 
   * @param unidadGestionRefs lista de unidadGestionRefs
   * @return specification para obtener los {@link Convocatoria} cuyo
   *         unidadGestionRef se encuentre entre los recibidos.
   */
  public static Specification<Proyecto> unidadGestionRefIn(List<String> unidadGestionRefs) {
    return (root, query, cb) -> {
      return root.get(Proyecto_.unidadGestionRef).in(unidadGestionRefs);
    };
  }

}
