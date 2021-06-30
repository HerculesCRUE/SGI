package org.crue.hercules.sgi.csp.repository.specification;

import org.crue.hercules.sgi.csp.model.ConvocatoriaHito;
import org.crue.hercules.sgi.csp.model.ConvocatoriaHito_;
import org.crue.hercules.sgi.csp.model.Convocatoria_;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.springframework.data.jpa.domain.Specification;

public class ConvocatoriaHitoSpecifications {

  /**
   * {@link ConvocatoriaHito} de la {@link Convocatoria} con el id indicado.
   * 
   * @param id identificador de la {@link Convocatoria}.
   * @return specification para obtener los {@link ConvocatoriaHito} de la
   *         {@link Convocatoria} con el id indicado.
   */
  public static Specification<ConvocatoriaHito> byConvocatoriaId(Long id) {
    return (root, query, cb) -> {
      return cb.equal(root.get(ConvocatoriaHito_.convocatoria).get(Convocatoria_.id), id);
    };
  }

}
