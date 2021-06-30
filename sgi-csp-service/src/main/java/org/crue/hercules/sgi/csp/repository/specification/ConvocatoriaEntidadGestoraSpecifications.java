package org.crue.hercules.sgi.csp.repository.specification;

import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaEntidadGestora;
import org.crue.hercules.sgi.csp.model.ConvocatoriaEntidadGestora_;
import org.crue.hercules.sgi.csp.model.Convocatoria_;
import org.springframework.data.jpa.domain.Specification;

public class ConvocatoriaEntidadGestoraSpecifications {

  /**
   * {@link ConvocatoriaEntidadGestora} de la {@link Convocatoria} con el id
   * indicado.
   * 
   * @param id identificador de la {@link Convocatoria}.
   * @return specification para obtener los {@link ConvocatoriaEntidadGestora} de
   *         la {@link Convocatoria} con el id indicado.
   */
  public static Specification<ConvocatoriaEntidadGestora> byConvocatoriaId(Long id) {
    return (root, query, cb) -> {
      return cb.equal(root.get(ConvocatoriaEntidadGestora_.convocatoria).get(Convocatoria_.id), id);
    };
  }

}